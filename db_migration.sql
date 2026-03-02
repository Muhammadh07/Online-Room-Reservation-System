-- ============================================================
-- Ocean View Resort — Complete Database Setup Script
-- Database: ocean_view_resort
-- Run this in MySQL Workbench or any MySQL client.
-- Safe to re-run: disables FK checks, drops all tables, recreates from scratch.
-- Default login after running: username=admin  password=admin123
-- ============================================================

CREATE DATABASE IF NOT EXISTS ocean_view_resort;
USE ocean_view_resort;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS bill;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS guest;
DROP TABLE IF EXISTS room;
DROP TABLE IF EXISTS room_type;
DROP TABLE IF EXISTS payment_method;
DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;

-- ── Users (staff / admin accounts) ──────────────────────────
CREATE TABLE `user` (
  user_id       INT AUTO_INCREMENT PRIMARY KEY,
  username      VARCHAR(50)  NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name     VARCHAR(100) NOT NULL DEFAULT '',
  email         VARCHAR(100),
  phone         VARCHAR(20),
  role          ENUM('ADMIN','STAFF') NOT NULL DEFAULT 'STAFF',
  is_active     TINYINT(1) NOT NULL DEFAULT 1,
  created_at    DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ── Room types ───────────────────────────────────────────────
CREATE TABLE room_type (
  type_id     INT AUTO_INCREMENT PRIMARY KEY,
  type_name   VARCHAR(50)     NOT NULL UNIQUE,
  base_price  DECIMAL(10,2)   NOT NULL,
  description VARCHAR(255)
);

-- ── Rooms ────────────────────────────────────────────────────
CREATE TABLE room (
  room_id       INT AUTO_INCREMENT PRIMARY KEY,
  room_number   VARCHAR(10)   NOT NULL UNIQUE,
  type_id       INT           NOT NULL,
  floor_number  INT           DEFAULT 1,
  max_occupancy INT           DEFAULT 2,
  is_available  TINYINT(1)    DEFAULT 1,
  FOREIGN KEY (type_id) REFERENCES room_type(type_id)
);

-- ── Guests ───────────────────────────────────────────────────
CREATE TABLE guest (
  guest_id     INT AUTO_INCREMENT PRIMARY KEY,
  full_name    VARCHAR(100)  NOT NULL,
  phone        VARCHAR(20)   NOT NULL,
  email        VARCHAR(100),
  nic_passport VARCHAR(50),
  address      VARCHAR(255)
);

-- ── Reservations ─────────────────────────────────────────────
CREATE TABLE reservation (
  reservation_id  INT AUTO_INCREMENT PRIMARY KEY,
  reservation_no  VARCHAR(30)   NOT NULL UNIQUE,
  guest_id        INT           NOT NULL,
  room_id         INT           NOT NULL,
  user_id         INT           NOT NULL,
  check_in        DATE          NOT NULL,
  check_out       DATE          NOT NULL,
  num_adults      INT           DEFAULT 1,
  num_children    INT           DEFAULT 0,
  status          VARCHAR(20)   DEFAULT 'CONFIRMED',
  special_requests VARCHAR(500),
  created_at      DATETIME      DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (guest_id) REFERENCES guest(guest_id),
  FOREIGN KEY (room_id)  REFERENCES room(room_id),
  FOREIGN KEY (user_id)  REFERENCES `user`(user_id)
);

-- ── Bills ────────────────────────────────────────────────────
CREATE TABLE bill (
  bill_id        INT AUTO_INCREMENT PRIMARY KEY,
  reservation_id INT           NOT NULL UNIQUE,
  total_amount   DECIMAL(10,2) NOT NULL,
  tax_amount     DECIMAL(10,2) DEFAULT 0,
  discount       DECIMAL(10,2) DEFAULT 0,
  balance_due    DECIMAL(10,2) NOT NULL,
  generated_at   DATETIME      DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id)
);

-- ── Payment methods ──────────────────────────────────────────
CREATE TABLE payment_method (
  method_id   INT AUTO_INCREMENT PRIMARY KEY,
  method_name VARCHAR(50) NOT NULL UNIQUE
);

-- ── Payments ─────────────────────────────────────────────────
CREATE TABLE payment (
  payment_id   INT AUTO_INCREMENT PRIMARY KEY,
  bill_id      INT           NOT NULL,
  method_id    INT           NOT NULL,
  amount       DECIMAL(10,2) NOT NULL,
  reference_no VARCHAR(100),
  payment_date DATETIME      DEFAULT CURRENT_TIMESTAMP,
  recorded_by  INT           NOT NULL,
  FOREIGN KEY (bill_id)     REFERENCES bill(bill_id),
  FOREIGN KEY (method_id)   REFERENCES payment_method(method_id),
  FOREIGN KEY (recorded_by) REFERENCES `user`(user_id)
);

-- ============================================================
-- Seed Data
-- ============================================================

-- Default admin account  (password: admin123)
INSERT INTO `user` (username, password_hash, full_name, role, is_active) VALUES
('admin', SHA2('admin123', 256), 'Administrator', 'ADMIN', 1);

-- Room types
INSERT INTO room_type (type_name, base_price, description) VALUES
('Standard', 80.00,  'Comfortable standard room with garden view'),
('Deluxe',   130.00, 'Spacious deluxe room with ocean view'),
('Suite',    220.00, 'Luxury suite with private balcony and ocean view');

-- Rooms
INSERT INTO room (room_number, type_id, floor_number, max_occupancy) VALUES
('101', 1, 1, 2), ('102', 1, 1, 2), ('103', 1, 1, 3),
('201', 2, 2, 2), ('202', 2, 2, 3),
('301', 3, 3, 4), ('302', 3, 3, 4);

-- Payment methods
INSERT INTO payment_method (method_name) VALUES
('Cash'), ('Credit Card'), ('Bank Transfer'), ('Online Payment');
