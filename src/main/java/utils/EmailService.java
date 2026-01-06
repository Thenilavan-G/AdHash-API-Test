package utils;

import java.io.File;
import java.text.SimpleDateFormat;
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
     * Create simple email body - just a brief message, HTML report is attached
     */
    private String createEmailBody(String additionalMessage, String htmlReportPath) {
        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z").format(new Date());
        int totalTests = SimpleHtmlReportGenerator.getTotalTests();
        int passedTests = SimpleHtmlReportGenerator.getPassedTests();
        int failedTests = SimpleHtmlReportGenerator.getFailedTests();
        int certErrorCount = SimpleHtmlReportGenerator.getCertificateErrorCount();

        String status = failedTests == 0 ? "ALL TESTS PASSED" : failedTests + " TEST(S) FAILED";

        StringBuilder body = new StringBuilder();
        body.append("AdHash API Test Report - ").append(timestamp).append("\n\n");
        body.append("Total: ").append(totalTests).append(" | Passed: ").append(passedTests).append(" | Failed: ").append(failedTests).append("\n");
        body.append("Status: ").append(status).append("\n");

        if (certErrorCount > 0) {
            body.append("\n⚠️ SSL CERTIFICATE WARNING: ").append(certErrorCount).append(" endpoint(s) have SSL certificate issues!\n");
            body.append("Please check the attached HTML report for details.\n");
        }

        body.append("\nPlease open the attached HTML report for detailed results.");

        return body.toString();
    }

    /**
     * Send simple notification email
     */
    public void sendNotification(String[] toEmails, String subject, String message) {
        sendTestReport(toEmails, subject, null, message);
    }
}
