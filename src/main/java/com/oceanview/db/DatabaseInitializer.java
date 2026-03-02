package com.oceanview.db;

import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Runs on application startup (via InitServlet).
 * Creates missing tables and columns — safe to run multiple times.
 *
 * Key strategies:
 *  1. CREATE TABLE IF NOT EXISTS  — safe for fresh installs
 *  2. addColumnIfMissing           — adds columns absent from old schemas
 *  3. convertColumnToVarchar       — fixes INT columns that must store strings
 *  4. makeColumnNullable           — fixes explicit old columns
 *  5. makeAllExtraColumnsNullable  — catches every other NOT-NULL-no-default
 *                                    column in old tables automatically
 */
public class DatabaseInitializer {

    private static final Logger LOG = Logger.getLogger(DatabaseInitializer.class.getName());

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            LOG.info("DatabaseInitializer: starting schema setup...");
            // Drop all CHECK constraints from old schema FIRST — they can block INSERTs
            for (String t : new String[]{"bill", "reservation", "room", "room_type",
                                         "guest", "payment", "payment_method", "user"}) {
                dropAllCheckConstraints(conn, t);
            }
            createUserTable(conn);
            createRoomTypeTable(conn);
            createRoomTable(conn);
            createGuestTable(conn);
            createReservationTable(conn);
            createBillTable(conn);
            createPaymentMethodTable(conn);
            createPaymentTable(conn);
            seedDefaultData(conn);
            resetRoomAvailability(conn);
            recalculateZeroBills(conn);
            LOG.info("DatabaseInitializer: schema ready.");
        } catch (Exception e) {
            LOG.severe("DatabaseInitializer failed: " + e.getMessage());
        }
    }

    // ── Table creation ───────────────────────────────────────────

    private static void createUserTable(Connection conn) throws SQLException {
        exec(conn,
            "CREATE TABLE IF NOT EXISTS `user` (" +
            "  user_id       INT AUTO_INCREMENT PRIMARY KEY," +
            "  username      VARCHAR(50)  NOT NULL UNIQUE," +
            "  password_hash VARCHAR(255) NOT NULL DEFAULT ''," +
            "  full_name     VARCHAR(100) NOT NULL DEFAULT ''," +
            "  email         VARCHAR(100)," +
            "  phone         VARCHAR(20)," +
            "  role          VARCHAR(10)  NOT NULL DEFAULT 'STAFF'," +
            "  is_active     TINYINT(1)   NOT NULL DEFAULT 1," +
            "  created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP" +
            ")");

        addColumnIfMissing(conn, "user", "password_hash", "VARCHAR(255) NOT NULL DEFAULT ''");
        addColumnIfMissing(conn, "user", "full_name",     "VARCHAR(100) NOT NULL DEFAULT ''");
        addColumnIfMissing(conn, "user", "email",         "VARCHAR(100)");
        addColumnIfMissing(conn, "user", "phone",         "VARCHAR(20)");
        addColumnIfMissing(conn, "user", "role",          "VARCHAR(10) NOT NULL DEFAULT 'STAFF'");
        addColumnIfMissing(conn, "user", "is_active",     "TINYINT(1) NOT NULL DEFAULT 1");
        addColumnIfMissing(conn, "user", "created_at",    "DATETIME DEFAULT CURRENT_TIMESTAMP");

        // Old schema may use 'password' instead of 'password_hash'
        if (columnExists(conn, "user", "password")) {
            exec(conn, "UPDATE `user` SET password_hash = `password` WHERE password_hash = '' OR password_hash IS NULL");
            makeColumnNullable(conn, "user", "password", "VARCHAR(255)");
        }

        // Old schema may store role as integer (0=STAFF, 1=ADMIN, 2=MANAGER) — convert to string
        exec(conn, "UPDATE `user` SET role = 'ADMIN' WHERE role = '1'");
        exec(conn, "UPDATE `user` SET role = 'STAFF' WHERE role = '0' OR role = '2' OR role = ''");
        // Ensure admin account (SHA2 password) also gets the correct role string
        exec(conn, "UPDATE `user` SET role = 'ADMIN' WHERE username = 'admin' AND (role IS NULL OR role NOT IN ('ADMIN','STAFF'))");

        // Neutralise every remaining NOT-NULL-no-default column we don't supply in INSERTs
        makeAllExtraColumnsNullable(conn, "user",
            "user_id", "username", "password_hash", "full_name", "email",
            "phone", "role", "is_active", "created_at");
    }

    private static void createRoomTypeTable(Connection conn) throws SQLException {
        exec(conn,
            "CREATE TABLE IF NOT EXISTS room_type (" +
            "  type_id     INT AUTO_INCREMENT PRIMARY KEY," +
            "  type_name   VARCHAR(50)   NOT NULL DEFAULT ''," +
            "  base_price  DECIMAL(10,2) NOT NULL DEFAULT 0," +
            "  description VARCHAR(255)" +
            ")");

        // Old schema may use 'room_type_id' as PK name — add type_id if missing
        if (!columnExists(conn, "room_type", "type_id")) {
            addColumnIfMissing(conn, "room_type", "type_id", "INT NOT NULL DEFAULT 0");
            if (columnExists(conn, "room_type", "room_type_id")) {
                exec(conn, "UPDATE room_type SET type_id = room_type_id");
            }
        }

        addColumnIfMissing(conn, "room_type", "type_name",   "VARCHAR(50) NOT NULL DEFAULT ''");
        addColumnIfMissing(conn, "room_type", "base_price",  "DECIMAL(10,2) NOT NULL DEFAULT 0");
        addColumnIfMissing(conn, "room_type", "description", "VARCHAR(255)");

        // Old schema may use 'name' instead of 'type_name'
        for (String col : new String[]{"name", "room_type_name", "type"}) {
            if (columnExists(conn, "room_type", col)) {
                exec(conn, "UPDATE room_type SET type_name = `" + col + "` WHERE type_name = '' OR type_name IS NULL");
                makeColumnNullable(conn, "room_type", col, "VARCHAR(255)");
            }
        }

        // Old schema may use different column names for price
        for (String col : new String[]{"price", "rate", "price_per_night", "cost", "daily_rate"}) {
            if (columnExists(conn, "room_type", col)) {
                exec(conn, "UPDATE room_type SET base_price = `" + col + "` WHERE base_price = 0 OR base_price IS NULL");
                makeColumnNullable(conn, "room_type", col, "DECIMAL(10,2)");
            }
        }

        // Apply configured prices for known room type names where price is still 0
        exec(conn, "UPDATE room_type SET base_price = 100.00 WHERE LOWER(type_name) LIKE '%standard%' AND (base_price = 0 OR base_price IS NULL)");
        exec(conn, "UPDATE room_type SET base_price =  75.00 WHERE LOWER(type_name) LIKE '%deluxe%'   AND (base_price = 0 OR base_price IS NULL)");
        exec(conn, "UPDATE room_type SET base_price =  50.00 WHERE LOWER(type_name) LIKE '%suite%'    AND (base_price = 0 OR base_price IS NULL)");

        makeAllExtraColumnsNullable(conn, "room_type",
            "type_id", "room_type_id", "type_name", "base_price", "description");
    }

    private static void createRoomTable(Connection conn) throws SQLException {
        exec(conn,
            "CREATE TABLE IF NOT EXISTS room (" +
            "  room_id       INT AUTO_INCREMENT PRIMARY KEY," +
            "  room_number   VARCHAR(10)  NOT NULL DEFAULT ''," +
            "  type_id       INT          NOT NULL DEFAULT 1," +
            "  floor_number  INT          DEFAULT 1," +
            "  max_occupancy INT          DEFAULT 2," +
            "  is_available  TINYINT(1)   DEFAULT 1" +
            ")");

        addColumnIfMissing(conn, "room", "room_number",   "VARCHAR(10) NOT NULL DEFAULT ''");
        addColumnIfMissing(conn, "room", "floor_number",  "INT DEFAULT 1");
        addColumnIfMissing(conn, "room", "max_occupancy", "INT DEFAULT 2");
        addColumnIfMissing(conn, "room", "is_available",  "TINYINT(1) DEFAULT 1");

        // Old schema might use 'room_type_id' instead of 'type_id'
        if (!columnExists(conn, "room", "type_id")) {
            addColumnIfMissing(conn, "room", "type_id", "INT NOT NULL DEFAULT 1");
            if (columnExists(conn, "room", "room_type_id")) {
                exec(conn, "UPDATE room SET type_id = room_type_id");
            }
        }

        // Old schema may use 'number' or 'no' instead of 'room_number'
        for (String col : new String[]{"number", "no", "room_no", "room_num"}) {
            if (columnExists(conn, "room", col)) {
                exec(conn, "UPDATE room SET room_number = `" + col + "` WHERE room_number = '' OR room_number IS NULL");
                makeColumnNullable(conn, "room", col, "VARCHAR(20)");
            }
        }

        makeAllExtraColumnsNullable(conn, "room",
            "room_id", "room_number", "type_id", "room_type_id",
            "floor_number", "max_occupancy", "is_available");
    }

    private static void createGuestTable(Connection conn) throws SQLException {
        exec(conn,
            "CREATE TABLE IF NOT EXISTS guest (" +
            "  guest_id     INT AUTO_INCREMENT PRIMARY KEY," +
            "  full_name    VARCHAR(100) NOT NULL DEFAULT ''," +
            "  phone        VARCHAR(20)," +
            "  email        VARCHAR(100)," +
            "  nic_passport VARCHAR(50)," +
            "  address      VARCHAR(255)" +
            ")");

        addColumnIfMissing(conn, "guest", "full_name",    "VARCHAR(100) NOT NULL DEFAULT ''");
        addColumnIfMissing(conn, "guest", "email",        "VARCHAR(100)");
        addColumnIfMissing(conn, "guest", "phone",        "VARCHAR(20)");
        addColumnIfMissing(conn, "guest", "nic_passport", "VARCHAR(50)");
        addColumnIfMissing(conn, "guest", "address",      "VARCHAR(255)");

        // Migrate old name column variants → full_name, then make nullable
        for (String col : new String[]{"name", "guest_name", "guest_full_name", "first_name", "last_name",
                                       "full_name_old", "guestname"}) {
            if (columnExists(conn, "guest", col)) {
                exec(conn, "UPDATE guest SET full_name = `" + col + "` WHERE full_name = '' OR full_name IS NULL");
                makeColumnNullable(conn, "guest", col, "VARCHAR(255)");
            }
        }

        // Migrate old phone column variants → phone, then make nullable
        for (String col : new String[]{"contact_no", "contact_number", "mobile", "mobile_no",
                                       "phone_no", "telephone", "tel_no", "cell_no", "cell", "hp"}) {
            if (columnExists(conn, "guest", col)) {
                exec(conn, "UPDATE guest SET phone = `" + col + "` WHERE phone IS NULL OR phone = ''");
                makeColumnNullable(conn, "guest", col, "VARCHAR(50)");
            }
        }

        // Migrate old NIC/passport column variants → nic_passport, then make nullable
        for (String col : new String[]{"nic", "passport_no", "id_no", "national_id", "passport", "ic_no"}) {
            if (columnExists(conn, "guest", col)) {
                exec(conn, "UPDATE guest SET nic_passport = `" + col + "` WHERE nic_passport IS NULL OR nic_passport = ''");
                makeColumnNullable(conn, "guest", col, "VARCHAR(100)");
            }
        }

        makeAllExtraColumnsNullable(conn, "guest",
            "guest_id", "full_name", "phone", "email", "nic_passport", "address");
    }

    private static void createReservationTable(Connection conn) throws SQLException {
        exec(conn,
            "CREATE TABLE IF NOT EXISTS reservation (" +
            "  reservation_id   INT AUTO_INCREMENT PRIMARY KEY," +
            "  reservation_no   VARCHAR(30)  NOT NULL DEFAULT ''," +
            "  guest_id         INT          NOT NULL DEFAULT 0," +
            "  room_id          INT          NOT NULL DEFAULT 0," +
            "  user_id          INT          NOT NULL DEFAULT 0," +
            "  check_in         DATE         NOT NULL DEFAULT '2000-01-01'," +
            "  check_out        DATE         NOT NULL DEFAULT '2000-01-01'," +
            "  num_adults       INT          DEFAULT 1," +
            "  num_children     INT          DEFAULT 0," +
            "  status           VARCHAR(20)  DEFAULT 'CONFIRMED'," +
            "  special_requests VARCHAR(500)," +
            "  created_at       DATETIME     DEFAULT CURRENT_TIMESTAMP" +
            ")");

        // Old schema may use 'id' as PK name — add reservation_id if missing
        if (!columnExists(conn, "reservation", "reservation_id")) {
            addColumnIfMissing(conn, "reservation", "reservation_id", "INT NOT NULL DEFAULT 0");
            if (columnExists(conn, "reservation", "id")) {
                exec(conn, "UPDATE reservation SET reservation_id = id");
            }
        }

        addColumnIfMissing(conn, "reservation", "reservation_no",   "VARCHAR(30) NOT NULL DEFAULT ''");
        // Old schema may have reservation_no as INT — convert to VARCHAR for 'OVR...' strings
        convertColumnToVarchar(conn, "reservation", "reservation_no", 30);

        addColumnIfMissing(conn, "reservation", "guest_id",         "INT NOT NULL DEFAULT 0");
        addColumnIfMissing(conn, "reservation", "room_id",          "INT NOT NULL DEFAULT 0");
        addColumnIfMissing(conn, "reservation", "user_id",          "INT NOT NULL DEFAULT 0");
        addColumnIfMissing(conn, "reservation", "check_in",         "DATE NOT NULL DEFAULT '2000-01-01'");
        addColumnIfMissing(conn, "reservation", "check_out",        "DATE NOT NULL DEFAULT '2000-01-01'");
        addColumnIfMissing(conn, "reservation", "num_adults",       "INT DEFAULT 1");
        addColumnIfMissing(conn, "reservation", "num_children",     "INT DEFAULT 0");
        addColumnIfMissing(conn, "reservation", "special_requests", "VARCHAR(500)");
        addColumnIfMissing(conn, "reservation", "status",           "VARCHAR(20) DEFAULT 'CONFIRMED'");
        addColumnIfMissing(conn, "reservation", "created_at",       "DATETIME DEFAULT CURRENT_TIMESTAMP");

        // Migrate old check-in column variants → check_in
        for (String col : new String[]{"checkin_date", "check_in_date", "arrival_date", "date_from", "from_date"}) {
            if (columnExists(conn, "reservation", col)) {
                exec(conn, "UPDATE reservation SET check_in = `" + col + "` WHERE check_in = '2000-01-01' OR check_in IS NULL");
                makeColumnNullable(conn, "reservation", col, "DATE");
            }
        }
        // Migrate old check-out column variants → check_out
        for (String col : new String[]{"checkout_date", "check_out_date", "departure_date", "date_to", "to_date"}) {
            if (columnExists(conn, "reservation", col)) {
                exec(conn, "UPDATE reservation SET check_out = `" + col + "` WHERE check_out = '2000-01-01' OR check_out IS NULL");
                makeColumnNullable(conn, "reservation", col, "DATE");
            }
        }
        // Migrate old staff/user column variants → user_id
        for (String col : new String[]{"staff_id", "employee_id", "booked_by", "created_by"}) {
            if (columnExists(conn, "reservation", col)) {
                exec(conn, "UPDATE reservation SET user_id = `" + col + "` WHERE user_id = 0 OR user_id IS NULL");
                makeColumnNullable(conn, "reservation", col, "INT");
            }
        }
        // Migrate old reservation number column variants → reservation_no
        for (String col : new String[]{"reservation_number", "booking_no", "booking_number", "res_no", "booking_ref"}) {
            if (columnExists(conn, "reservation", col)) {
                exec(conn, "UPDATE reservation SET reservation_no = `" + col + "` WHERE reservation_no = '' OR reservation_no IS NULL");
                makeColumnNullable(conn, "reservation", col, "VARCHAR(50)");
            }
        }

        // Normalise old status values to the canonical uppercase set used by the new schema
        exec(conn, "UPDATE reservation SET status = 'CONFIRMED'   WHERE status IN ('ACTIVE','active','Confirmed','confirmed','BOOKED','booked','PENDING','pending','NEW','new')");
        exec(conn, "UPDATE reservation SET status = 'CHECKED_IN'  WHERE status IN ('CHECKED IN','checked_in','checkin','CHECK_IN','CheckedIn','CHECKIN')");
        exec(conn, "UPDATE reservation SET status = 'CHECKED_OUT' WHERE status IN ('CHECKED OUT','checked_out','checkout','CHECK_OUT','CheckedOut','CHECKOUT','COMPLETED','completed')");
        exec(conn, "UPDATE reservation SET status = 'CANCELLED'   WHERE status IN ('CANCELED','canceled','CANCEL','cancel','cancelled','Cancelled')");

        makeAllExtraColumnsNullable(conn, "reservation",
            "reservation_id", "id", "reservation_no", "guest_id", "room_id", "user_id",
            "check_in", "check_out", "num_adults", "num_children",
            "status", "special_requests", "created_at");
    }

    private static void createBillTable(Connection conn) throws SQLException {
        exec(conn,
            "CREATE TABLE IF NOT EXISTS bill (" +
            "  bill_id        INT AUTO_INCREMENT PRIMARY KEY," +
            "  reservation_id INT           NOT NULL DEFAULT 0," +
            "  total_amount   DECIMAL(10,2) NOT NULL DEFAULT 0," +
            "  tax_amount     DECIMAL(10,2) DEFAULT 0," +
            "  discount       DECIMAL(10,2) DEFAULT 0," +
            "  balance_due    DECIMAL(10,2) NOT NULL DEFAULT 0," +
            "  generated_at   DATETIME      DEFAULT CURRENT_TIMESTAMP" +
            ")");

        addColumnIfMissing(conn, "bill", "reservation_id", "INT NOT NULL DEFAULT 0");
        addColumnIfMissing(conn, "bill", "total_amount",   "DECIMAL(10,2) NOT NULL DEFAULT 0");
        addColumnIfMissing(conn, "bill", "tax_amount",     "DECIMAL(10,2) DEFAULT 0");
        addColumnIfMissing(conn, "bill", "discount",       "DECIMAL(10,2) DEFAULT 0");
        addColumnIfMissing(conn, "bill", "balance_due",    "DECIMAL(10,2) NOT NULL DEFAULT 0");
        addColumnIfMissing(conn, "bill", "generated_at",   "DATETIME DEFAULT CURRENT_TIMESTAMP");

        // Migrate old total column variants → total_amount
        for (String col : new String[]{"amount", "total", "grand_total", "bill_amount", "total_cost"}) {
            if (columnExists(conn, "bill", col)) {
                exec(conn, "UPDATE bill SET total_amount = `" + col + "` WHERE total_amount = 0 OR total_amount IS NULL");
                makeColumnNullable(conn, "bill", col, "DECIMAL(10,2)");
            }
        }

        // Old schema may link via reservation_number string — resolve to integer reservation_id
        for (String col : new String[]{"reservation_number", "reservation_no", "booking_no"}) {
            if (columnExists(conn, "bill", col) && columnExists(conn, "reservation", "reservation_no")) {
                try {
                    exec(conn,
                        "UPDATE bill b JOIN reservation r ON b.`" + col + "` = r.reservation_no " +
                        "SET b.reservation_id = r.reservation_id " +
                        "WHERE b.reservation_id = 0 OR b.reservation_id IS NULL");
                } catch (SQLException ignore) { /* column type mismatch is ok */ }
                makeColumnNullable(conn, "bill", col, "VARCHAR(50)");
            }
        }

        makeAllExtraColumnsNullable(conn, "bill",
            "bill_id", "reservation_id", "total_amount", "tax_amount",
            "discount", "balance_due", "generated_at");
    }

    private static void createPaymentMethodTable(Connection conn) throws SQLException {
        exec(conn,
            "CREATE TABLE IF NOT EXISTS payment_method (" +
            "  method_id   INT AUTO_INCREMENT PRIMARY KEY," +
            "  method_name VARCHAR(50) NOT NULL DEFAULT ''" +
            ")");

        addColumnIfMissing(conn, "payment_method", "method_name", "VARCHAR(50) NOT NULL DEFAULT ''");
        addColumnIfMissing(conn, "payment_method", "is_active",   "TINYINT(1) NOT NULL DEFAULT 1");

        // Migrate old 'name' → method_name
        for (String col : new String[]{"name", "payment_type", "type", "method"}) {
            if (columnExists(conn, "payment_method", col)) {
                exec(conn, "UPDATE payment_method SET method_name = `" + col + "` WHERE method_name = '' OR method_name IS NULL");
                makeColumnNullable(conn, "payment_method", col, "VARCHAR(255)");
            }
        }

        makeAllExtraColumnsNullable(conn, "payment_method", "method_id", "method_name", "is_active");
    }

    private static void createPaymentTable(Connection conn) throws SQLException {
        exec(conn,
            "CREATE TABLE IF NOT EXISTS payment (" +
            "  payment_id   INT AUTO_INCREMENT PRIMARY KEY," +
            "  bill_id      INT           NOT NULL DEFAULT 0," +
            "  method_id    INT           NOT NULL DEFAULT 1," +
            "  amount       DECIMAL(10,2) NOT NULL DEFAULT 0," +
            "  reference_no VARCHAR(100)," +
            "  payment_date DATETIME      DEFAULT CURRENT_TIMESTAMP," +
            "  recorded_by  INT           NOT NULL DEFAULT 1" +
            ")");

        addColumnIfMissing(conn, "payment", "bill_id",      "INT NOT NULL DEFAULT 0");
        addColumnIfMissing(conn, "payment", "method_id",    "INT NOT NULL DEFAULT 1");
        addColumnIfMissing(conn, "payment", "amount",       "DECIMAL(10,2) NOT NULL DEFAULT 0");
        addColumnIfMissing(conn, "payment", "reference_no", "VARCHAR(100)");
        addColumnIfMissing(conn, "payment", "payment_date", "DATETIME DEFAULT CURRENT_TIMESTAMP");
        addColumnIfMissing(conn, "payment", "recorded_by",  "INT NOT NULL DEFAULT 1");

        // Migrate old amount column variants (amount_paid is the common old column name)
        for (String col : new String[]{"amount_paid", "paid_amount", "payment_amount", "total"}) {
            if (columnExists(conn, "payment", col)) {
                exec(conn, "UPDATE payment SET amount = `" + col + "` WHERE amount = 0 OR amount IS NULL");
                makeColumnNullable(conn, "payment", col, "DECIMAL(10,2)");
            }
        }

        // Migrate old recorded_by column variants (received_by is the common old column name)
        for (String col : new String[]{"received_by", "staff_id", "cashier_id"}) {
            if (columnExists(conn, "payment", col)) {
                exec(conn, "UPDATE payment SET recorded_by = `" + col + "` WHERE (recorded_by = 0 OR recorded_by IS NULL) AND `" + col + "` IS NOT NULL");
                makeColumnNullable(conn, "payment", col, "INT");
            }
        }

        // Migrate old payment_date column variants (paid_at is the common old column name)
        for (String col : new String[]{"paid_at", "transaction_date", "date_paid"}) {
            if (columnExists(conn, "payment", col)) {
                exec(conn, "UPDATE payment SET payment_date = `" + col + "` WHERE payment_date IS NULL AND `" + col + "` IS NOT NULL");
                makeColumnNullable(conn, "payment", col, "DATETIME");
            }
        }

        makeAllExtraColumnsNullable(conn, "payment",
            "payment_id", "bill_id", "method_id", "amount",
            "reference_no", "payment_date", "recorded_by");
    }

    // ── Seed data ────────────────────────────────────────────────

    private static void seedDefaultData(Connection conn) throws SQLException {
        // Admin account (only if no admin exists)
        String checkAdmin = "SELECT COUNT(*) FROM `user` WHERE role='ADMIN'";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(checkAdmin)) {
            if (rs.next() && rs.getInt(1) == 0) {
                exec(conn, "INSERT INTO `user` (username, password_hash, full_name, role, is_active) " +
                           "VALUES ('admin', SHA2('admin123',256), 'Administrator', 'ADMIN', 1)");
                LOG.info("DatabaseInitializer: default admin created (admin/admin123)");
            }
        }

        // Room types + sample rooms (only if empty)
        String checkTypes = "SELECT COUNT(*) FROM room_type";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(checkTypes)) {
            if (rs.next() && rs.getInt(1) == 0) {
                exec(conn, "INSERT INTO room_type (type_name, base_price, description) VALUES " +
                           "('Standard', 100.00, 'Comfortable standard room'), " +
                           "('Deluxe',    75.00, 'Spacious deluxe room with ocean view'), " +
                           "('Suite',     50.00, 'Luxury suite with private balcony')");
                exec(conn, "INSERT INTO room (room_number, type_id, floor_number, max_occupancy) VALUES " +
                           "('101',1,1,2),('102',1,1,2),('103',1,1,3)," +
                           "('201',2,2,2),('202',2,2,3)," +
                           "('301',3,3,4),('302',3,3,4)");
            }
        }

        // Payment methods (only if empty)
        String checkMethods = "SELECT COUNT(*) FROM payment_method";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(checkMethods)) {
            if (rs.next() && rs.getInt(1) == 0) {
                exec(conn, "INSERT INTO payment_method (method_name) VALUES " +
                           "('Cash'),('Credit Card'),('Bank Transfer'),('Online Payment')");
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────

    private static void exec(Connection conn, String sql) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    private static boolean columnExists(Connection conn, String table, String column) {
        String sql = "SELECT COUNT(*) FROM information_schema.columns " +
                     "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, table);
            ps.setString(2, column);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private static String getColumnDataType(Connection conn, String table, String column) {
        String sql = "SELECT DATA_TYPE FROM information_schema.columns " +
                     "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, table);
            ps.setString(2, column);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1).toLowerCase() : "";
            }
        } catch (SQLException e) {
            return "";
        }
    }

    private static void addColumnIfMissing(Connection conn, String table, String column, String definition)
            throws SQLException {
        if (!columnExists(conn, table, column)) {
            try {
                exec(conn, "ALTER TABLE `" + table + "` ADD COLUMN `" + column + "` " + definition);
                LOG.info("DatabaseInitializer: added column " + table + "." + column);
            } catch (SQLException e) {
                LOG.warning("Could not add " + table + "." + column + ": " + e.getMessage());
            }
        }
    }

    /** Makes a column nullable so INSERTs that omit it no longer fail. */
    private static void makeColumnNullable(Connection conn, String table, String column, String type) {
        try {
            exec(conn, "ALTER TABLE `" + table + "` MODIFY COLUMN `" + column + "` " + type + " NULL DEFAULT NULL");
            LOG.info("DatabaseInitializer: made " + table + "." + column + " nullable");
        } catch (SQLException e) {
            LOG.warning("Could not make " + table + "." + column + " nullable: " + e.getMessage());
        }
    }

    /**
     * Automatically finds every NOT-NULL column with no DEFAULT in the given table
     * that is NOT in the knownColumns set, and makes it nullable.
     * This is the catch-all for any old schema column we haven't explicitly handled.
     */
    private static void makeAllExtraColumnsNullable(Connection conn, String table, String... knownColumns) {
        Set<String> known = new HashSet<>(Arrays.asList(knownColumns));
        String sql = "SELECT COLUMN_NAME, COLUMN_TYPE FROM information_schema.columns " +
                     "WHERE table_schema = DATABASE() AND table_name = ? " +
                     "AND IS_NULLABLE = 'NO' AND COLUMN_DEFAULT IS NULL " +
                     "AND EXTRA NOT LIKE '%auto_increment%'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, table);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String col  = rs.getString("COLUMN_NAME");
                    String type = rs.getString("COLUMN_TYPE");
                    if (!known.contains(col)) {
                        makeColumnNullable(conn, table, col, type);
                    }
                }
            }
        } catch (SQLException e) {
            LOG.warning("makeAllExtraColumnsNullable failed for " + table + ": " + e.getMessage());
        }
    }

    /** Converts an INT column to VARCHAR when the code needs to store string values. */
    private static void convertColumnToVarchar(Connection conn, String table, String column, int length) {
        String dt = getColumnDataType(conn, table, column);
        if (dt.equals("int") || dt.equals("bigint") || dt.equals("tinyint")
                || dt.equals("smallint") || dt.equals("mediumint")) {
            try {
                exec(conn, "ALTER TABLE `" + table + "` MODIFY COLUMN `" + column +
                           "` VARCHAR(" + length + ") NOT NULL DEFAULT ''");
                LOG.info("DatabaseInitializer: converted " + table + "." + column + " INT→VARCHAR(" + length + ")");
            } catch (SQLException e) {
                LOG.warning("Could not convert " + table + "." + column + " to VARCHAR: " + e.getMessage());
            }
        }
    }

    /**
     * Resets is_available = 1 for rooms that have no active (non-cancelled, non-checked-out)
     * reservations. Old schemas often left all rooms as is_available = 0 after a guest checked
     * out without resetting the flag. Also sets is_available for rooms where it is NULL.
     */
    private static void resetRoomAvailability(Connection conn) {
        try {
            // Mark rooms with no active overlapping future reservations as available
            exec(conn,
                "UPDATE room SET is_available = 1 " +
                "WHERE is_available IS NULL OR is_available = 0");
            // Then mark rooms that currently have an active (CONFIRMED or CHECKED_IN) reservation
            // covering today as unavailable
            exec(conn,
                "UPDATE room r " +
                "JOIN reservation res ON r.room_id = res.room_id " +
                "SET r.is_available = 0 " +
                "WHERE res.status IN ('CONFIRMED','CHECKED_IN') " +
                "AND res.check_in <= CURDATE() AND res.check_out > CURDATE()");
            LOG.info("DatabaseInitializer: room availability reset complete.");
        } catch (SQLException e) {
            LOG.warning("resetRoomAvailability failed: " + e.getMessage());
        }
    }

    /**
     * Recalculates bill totals for any bill whose total_amount is 0 or NULL but whose
     * linked reservation has a room with a non-zero base_price.
     * This fixes bills created by old schema code that stored $0 amounts.
     * Formula: nights × base_price × 1.10 (10% tax) — same as ReservationService.
     */
    private static void recalculateZeroBills(Connection conn) {
        try {
            exec(conn,
                "UPDATE bill b " +
                "JOIN reservation r  ON b.reservation_id = r.reservation_id " +
                "JOIN room rm        ON r.room_id = rm.room_id " +
                "JOIN room_type rt   ON rm.type_id = rt.type_id " +
                "SET " +
                "  b.total_amount = ROUND(DATEDIFF(r.check_out, r.check_in) * rt.base_price * 1.10, 2), " +
                "  b.tax_amount   = ROUND(DATEDIFF(r.check_out, r.check_in) * rt.base_price * 0.10, 2), " +
                "  b.balance_due  = ROUND(DATEDIFF(r.check_out, r.check_in) * rt.base_price * 1.10, 2) " +
                "WHERE (b.total_amount = 0 OR b.total_amount IS NULL) " +
                "  AND rt.base_price > 0 " +
                "  AND DATEDIFF(r.check_out, r.check_in) > 0");
            LOG.info("DatabaseInitializer: zero-bill recalculation complete.");
        } catch (SQLException e) {
            LOG.warning("recalculateZeroBills failed: " + e.getMessage());
        }
    }

    /**
     * Drops all CHECK constraints on a table (MySQL 8.0+).
     * Old schemas often have CHECK constraints like CK_TOTAL_AMOUNT that
     * reject valid INSERTs from new code. Safe to call even if no checks exist.
     */
    private static void dropAllCheckConstraints(Connection conn, String table) {
        String sql = "SELECT CONSTRAINT_NAME FROM information_schema.TABLE_CONSTRAINTS " +
                     "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND CONSTRAINT_TYPE = 'CHECK'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, table);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("CONSTRAINT_NAME");
                    try {
                        exec(conn, "ALTER TABLE `" + table + "` DROP CHECK `" + name + "`");
                        LOG.info("DatabaseInitializer: dropped CHECK constraint " + table + "." + name);
                    } catch (SQLException e) {
                        // MySQL 5.7 ignores CHECK constraints — DROP CHECK not supported, safe to ignore
                        LOG.fine("Could not drop CHECK " + table + "." + name + ": " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            LOG.warning("dropAllCheckConstraints failed for " + table + ": " + e.getMessage());
        }
    }
}
