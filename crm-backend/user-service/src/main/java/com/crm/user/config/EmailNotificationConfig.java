package com.crm.user.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Email Notification Configuration
 * 
 * Service class used to send mail notifications to specific recipients.
 * Handles email configuration and sending functionality.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Slf4j
@Component
public class EmailNotificationConfig {

    @Value("${app.sendgrid-apikey}")
    private String sendgridApikey;

    @Value("${app.mail-from}")
    private String mailFrom;

    @Value("${app.mail-host}")
    private String mailHost;

    @Value("${app.mail-port}")
    private String mailPort;

    @Value("${app.mail-user}")
    private String mailUser;

    @Value("${app.mail-password}")
    private String mailPassword;

    @Value("${app.mail-send}")
    private boolean mailSend;

    @Value("${app.mail-recipients}")
    private String recipientAddresses;

    @Value("${app.mail-heading}")
    private String heading;

    /**
     * Send email notification
     * 
     * @param body the email body content
     * @param subject the email subject
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendEmailNotification(String body, String subject) {
        try {
            if (mailSend) {
                Properties properties = new Properties();
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mail.smtp.host", mailHost);
                properties.put("mail.smtp.port", mailPort);
                properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
                properties.put("mail.smtp.ssl.trust", mailHost);

                Authenticator auth = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailUser, mailPassword);
                    }
                };
                
                Session session = Session.getInstance(properties, auth);
                MimeMessage mimeMessage = new MimeMessage(session);
                mimeMessage.addHeader("Content-Type", "text/html; charset=UTF-8");
                mimeMessage.addHeader("Format", "flowed");
                mimeMessage.addHeader("Content-Transfer-Encoding", "8bit");
                mimeMessage.setFrom(mailFrom);
                mimeMessage.setReplyTo(InternetAddress.parse(mailFrom, false));
                mimeMessage.setSubject(heading + " - " + subject, "UTF-8");
                mimeMessage.setContent(body, "text/html; charset=UTF-8");
                mimeMessage.setSentDate(new Date());
                mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress
                        .parse(StringUtils.isNotBlank(recipientAddresses)
                                ? getRecipientAddresses() : "", false));
                Transport.send(mimeMessage);
                
                log.info("Email notification sent successfully to: {}", getRecipientAddresses());
            }
            return true;
        } catch (Exception e) {
            log.error("Error sending email notification: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Send email notification to specific recipient
     * 
     * @param body the email body content
     * @param subject the email subject
     * @param recipientEmail the recipient email address
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendEmailNotification(String body, String subject, String recipientEmail) {
        try {
            if (mailSend && StringUtils.isNotBlank(recipientEmail)) {
                Properties properties = new Properties();
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mail.smtp.host", mailHost);
                properties.put("mail.smtp.port", mailPort);
                properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
                properties.put("mail.smtp.ssl.trust", mailHost);

                Authenticator auth = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailUser, mailPassword);
                    }
                };
                
                Session session = Session.getInstance(properties, auth);
                MimeMessage mimeMessage = new MimeMessage(session);
                mimeMessage.addHeader("Content-Type", "text/html; charset=UTF-8");
                mimeMessage.addHeader("Format", "flowed");
                mimeMessage.addHeader("Content-Transfer-Encoding", "8bit");
                mimeMessage.setFrom(mailFrom);
                mimeMessage.setReplyTo(InternetAddress.parse(mailFrom, false));
                mimeMessage.setSubject(heading + " - " + subject, "UTF-8");
                mimeMessage.setContent(body, "text/html; charset=UTF-8");
                mimeMessage.setSentDate(new Date());
                mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress
                        .parse(recipientEmail, false));
                Transport.send(mimeMessage);
                
                log.info("Email notification sent successfully to: {}", recipientEmail);
            }
            return true;
        } catch (Exception e) {
            log.error("Error sending email notification to {}: {}", recipientEmail, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get recipient addresses
     * 
     * @return the recipient addresses
     */
    public String getRecipientAddresses() {
        return recipientAddresses.trim();
    }
} 