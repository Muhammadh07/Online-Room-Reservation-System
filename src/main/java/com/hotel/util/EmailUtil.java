package com.hotel.util;

import java.util.Properties;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * EmailUtil — sends email notifications (Observer support).
 * Configure SMTP credentials in the constants below.
 */
public final class EmailUtil {

    private static final Logger LOG = Logger.getLogger(EmailUtil.class.getName());

    // ---- Configure your SMTP here ----
    private static final String SMTP_HOST     = "smtp.gmail.com";
    private static final String SMTP_PORT     = "587";
    private static final String SMTP_USER     = "hotel@example.com";
    private static final String SMTP_PASSWORD = "your-app-password";
    private static final String FROM_NAME     = "Grand Hotel";
    // ----------------------------------

    private EmailUtil() {}

    public static boolean send(String toEmail, String subject, String body) {
        if (toEmail == null || toEmail.isBlank()) return false;
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth",            "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host",            SMTP_HOST);
            props.put("mail.smtp.port",            SMTP_PORT);

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
                }
            });

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(SMTP_USER, FROM_NAME));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject(subject);
            msg.setContent(body, "text/html; charset=utf-8");
            Transport.send(msg);
            LOG.info("Email sent to: " + toEmail);
            return true;
        } catch (Exception e) {
            LOG.warning("Email failed to " + toEmail + ": " + e.getMessage());
            return false;
        }
    }

    public static String buildReservationEmail(String guestName, String resNumber,
                                               String room, String checkin, String checkout) {
        return "<html><body style='font-family:Arial;'>"
                + "<h2 style='color:#1a73e8;'>Reservation Confirmed</h2>"
                + "<p>Dear <b>" + guestName + "</b>,</p>"
                + "<p>Your reservation has been confirmed. Details below:</p>"
                + "<table style='border-collapse:collapse;width:400px;'>"
                + "<tr><td style='padding:8px;background:#f1f3f4;'><b>Reservation #</b></td><td style='padding:8px;'>" + resNumber + "</td></tr>"
                + "<tr><td style='padding:8px;background:#f1f3f4;'><b>Room</b></td><td style='padding:8px;'>" + room + "</td></tr>"
                + "<tr><td style='padding:8px;background:#f1f3f4;'><b>Check-in</b></td><td style='padding:8px;'>" + checkin + "</td></tr>"
                + "<tr><td style='padding:8px;background:#f1f3f4;'><b>Check-out</b></td><td style='padding:8px;'>" + checkout + "</td></tr>"
                + "</table>"
                + "<p>Thank you for choosing Grand Hotel!</p>"
                + "</body></html>";
    }
}