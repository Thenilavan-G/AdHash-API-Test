package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * EmailService class to send HTML test reports via email
 * Supports Gmail, Outlook, and other SMTP providers
 */
public class EmailService {
    
    private String smtpHost;
    private String smtpPort;
    private String username;
    private String password;
    private boolean useSSL;
    private boolean useTLS;
    
    /**
     * Constructor for Gmail configuration
     */
    public static EmailService forGmail(String username, String password) {
        EmailService service = new EmailService();
        service.smtpHost = "smtp.gmail.com";
        service.smtpPort = "587";
        service.username = username;
        service.password = password;
        service.useSSL = false;
        service.useTLS = true;
        return service;
    }
    
    /**
     * Constructor for Outlook configuration
     */
    public static EmailService forOutlook(String username, String password) {
        EmailService service = new EmailService();
        service.smtpHost = "smtp-mail.outlook.com";
        service.smtpPort = "587";
        service.username = username;
        service.password = password;
        service.useSSL = false;
        service.useTLS = true;
        return service;
    }

    /**
     * Constructor for Zoho configuration
     */
    public static EmailService forZoho(String username, String password) {
        EmailService service = new EmailService();
        service.smtpHost = "smtp.zoho.com";
        service.smtpPort = "587";
        service.username = username;
        service.password = password;
        service.useSSL = false;
        service.useTLS = true;
        return service;
    }
    
    /**
     * Constructor for custom SMTP configuration
     */
    public static EmailService forCustomSMTP(String smtpHost, String smtpPort, String username, String password, boolean useSSL, boolean useTLS) {
        EmailService service = new EmailService();
        service.smtpHost = smtpHost;
        service.smtpPort = smtpPort;
        service.username = username;
        service.password = password;
        service.useSSL = useSSL;
        service.useTLS = useTLS;
        return service;
    }
    
    /**
     * Send HTML test report via email
     */
    public void sendTestReport(String[] toEmails, String subject, String htmlReportPath, String additionalMessage) {
        try {
            // Setup mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", "true");
            
            if (useTLS) {
                props.put("mail.smtp.starttls.enable", "true");
            }
            if (useSSL) {
                props.put("mail.smtp.ssl.enable", "true");
            }
            
            // Create session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            
            // Set recipients
            InternetAddress[] addresses = new InternetAddress[toEmails.length];
            for (int i = 0; i < toEmails.length; i++) {
                addresses[i] = new InternetAddress(toEmails[i]);
            }
            message.setRecipients(Message.RecipientType.TO, addresses);
            
            // Set subject with timestamp
            message.setSubject(subject + " - " + new Date());
            
            // Create multipart message
            Multipart multipart = new MimeMultipart();
            
            // Add text part
            BodyPart messageBodyPart = new MimeBodyPart();
            String emailBody = createEmailBody(additionalMessage, htmlReportPath);
            messageBodyPart.setContent(emailBody, "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);
            
            // Add HTML report as attachment
            if (htmlReportPath != null && new File(htmlReportPath).exists()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(htmlReportPath);
                attachmentPart.setFileName("TestReport_" + System.currentTimeMillis() + ".html");
                multipart.addBodyPart(attachmentPart);
            }
            
            // Set content
            message.setContent(multipart);
            
            // Send message
            Transport.send(message);
            
            System.out.println("✅ Email sent successfully to: " + String.join(", ", toEmails));
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create HTML email body - converts HTML report to email-friendly format
     * Email clients don't support JavaScript, so we remove expand/collapse functionality
     * and show a clean table format that matches the HTML report style
     */
    private String createEmailBody(String additionalMessage, String htmlReportPath) {
        // Try to read and modify HTML report for email compatibility
        if (htmlReportPath != null && new File(htmlReportPath).exists()) {
            try {
                String htmlContent = new String(Files.readAllBytes(Paths.get(htmlReportPath)),
                    java.nio.charset.StandardCharsets.UTF_8);

                // Make HTML email-compatible by:
                // 1. Remove JavaScript (doesn't work in emails)
                htmlContent = htmlContent.replaceAll("(?s)<script[^>]*>.*?</script>", "");

                // 2. Remove expand button column header
                htmlContent = htmlContent.replaceAll("<th class=\"expand-col\"></th>", "");

                // 3. Remove expand button cells (single line)
                htmlContent = htmlContent.replaceAll("<td class=\"expand-col\">.*?</td>\\s*", "");

                // 4. Remove hidden details rows (multi-line - they would just clutter the email)
                // Each details-row contains the expandable API details that won't work without JS
                htmlContent = htmlContent.replaceAll("(?s)<tr class=\"details-row\"[^>]*>.*?</tr>\\s*", "");

                // 5. Update colspan from 6 to 5 since we removed the expand column
                htmlContent = htmlContent.replaceAll("colspan=\"6\"", "colspan=\"5\"");

                return htmlContent;
            } catch (IOException e) {
                // Fall back to simple email body if HTML report cannot be read
            }
        }

        // Fallback simple email body if HTML report is not available
        StringBuilder body = new StringBuilder();
        body.append("<html><head><meta charset=\"UTF-8\"></head><body>");
        body.append("<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;\">");
        body.append("<h2 style=\"color: #333;\">🎯 AdHash API Test Report</h2>");
        body.append("<p><strong>Test Execution Completed:</strong> ").append(new Date()).append("</p>");

        if (additionalMessage != null && !additionalMessage.trim().isEmpty()) {
            body.append("<p>").append(additionalMessage).append("</p>");
        }

        body.append("<p style=\"color: #ff9800;\">⚠️ HTML report file not found. Please check the attachment.</p>");
        body.append("<hr style=\"border: none; border-top: 1px solid #e0e0e0; margin: 20px 0;\">");
        body.append("<p style=\"color: #666; font-size: 12px;\"><em>This is an automated email from AdHash API Test Suite.</em></p>");
        body.append("</div></body></html>");

        return body.toString();
    }
    
    /**
     * Send simple notification email
     */
    public void sendNotification(String[] toEmails, String subject, String message) {
        sendTestReport(toEmails, subject, null, message);
    }
}
