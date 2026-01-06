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
            
            // Add HTML part with card-based UI
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
     * Create HTML email body with card-based UI design
     */
    private String createEmailBody(String additionalMessage, String htmlReportPath) {
        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z").format(new Date());
        int totalTests = SimpleHtmlReportGenerator.getTotalTests();
        int passedTests = SimpleHtmlReportGenerator.getPassedTests();
        int failedTests = SimpleHtmlReportGenerator.getFailedTests();
        int certErrorCount = SimpleHtmlReportGenerator.getCertificateErrorCount();
        java.util.List<SimpleHtmlReportGenerator.TestMethodDetails> sslIssues = SimpleHtmlReportGenerator.getTestsWithCertificateErrors();

        boolean allPassed = failedTests == 0;
        String headerColor = allPassed ? "background:linear-gradient(135deg,#4CAF50,#2E7D32);" : "background:linear-gradient(135deg,#F44336,#C62828);";
        String statusBanner = allPassed
            ? "<div style=\"background:#4CAF50;color:#fff;text-align:center;padding:12px;border-radius:8px;font-weight:bold;\">✓ ALL TESTS PASSED</div>"
            : "<div style=\"background:#F44336;color:#fff;text-align:center;padding:12px;border-radius:8px;font-weight:bold;\">✗ " + failedTests + " TEST(S) FAILED</div>";
        String rateColor = allPassed ? "#4CAF50" : "#F44336";
        double successRate = totalTests > 0 ? (passedTests * 100.0 / totalTests) : 0;

        // Build SSL warning section
        StringBuilder sslHtml = new StringBuilder();
        if (certErrorCount > 0) {
            sslHtml.append("<div style=\"background:#FFF3E0;border-radius:8px;padding:15px;margin-top:15px;border-left:4px solid #FF9800;\">");
            sslHtml.append("<div style=\"font-weight:bold;color:#E65100;margin-bottom:10px;\">🔒 SSL CERTIFICATE WARNING - ").append(certErrorCount).append(" Issue(s)</div>");
            for (SimpleHtmlReportGenerator.TestMethodDetails test : sslIssues) {
                String projectName = extractProjectName(test.methodName);
                sslHtml.append("<div style=\"padding:8px 0;border-bottom:1px solid #FFE0B2;\"><strong style=\"color:#E65100;\">⚠ ").append(projectName).append("</strong>");
                if (test.apiUrl != null) {
                    sslHtml.append("<br><a href=\"").append(test.apiUrl).append("\" style=\"color:#FF6D00;font-size:12px;word-break:break-all;\">").append(test.apiUrl).append("</a>");
                }
                sslHtml.append("</div>");
            }
            sslHtml.append("</div>");
        }

        // Build card-based HTML email
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        body.append("<body style=\"margin:0;padding:20px;background:#f5f5f5;font-family:Arial,sans-serif;\">");
        body.append("<table width=\"600\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#fff;border-radius:12px;box-shadow:0 2px 8px rgba(0,0,0,0.1);\">");

        // Header
        body.append("<tr><td style=\"").append(headerColor).append("padding:25px;text-align:center;border-radius:12px 12px 0 0;\">");
        body.append("<h1 style=\"margin:0;color:#fff;font-size:22px;\">🎯 AdHash API Test Report</h1>");
        body.append("<p style=\"margin:8px 0 0;color:rgba(255,255,255,0.9);font-size:13px;\">").append(timestamp).append("</p>");
        body.append("</td></tr>");

        // Stats Cards
        body.append("<tr><td style=\"padding:20px;\">");
        body.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"8\"><tr>");
        body.append("<td width=\"33%\" align=\"center\" style=\"background:#E3F2FD;padding:15px;border-radius:8px;\"><div style=\"font-size:28px;font-weight:bold;color:#1976D2;\">").append(totalTests).append("</div><div style=\"font-size:11px;color:#666;margin-top:4px;\">Total Tests</div></td>");
        body.append("<td width=\"33%\" align=\"center\" style=\"background:#E8F5E9;padding:15px;border-radius:8px;\"><div style=\"font-size:28px;font-weight:bold;color:#388E3C;\">✓ ").append(passedTests).append("</div><div style=\"font-size:11px;color:#666;margin-top:4px;\">Passed</div></td>");
        body.append("<td width=\"33%\" align=\"center\" style=\"background:#FFEBEE;padding:15px;border-radius:8px;\"><div style=\"font-size:28px;font-weight:bold;color:#D32F2F;\">✗ ").append(failedTests).append("</div><div style=\"font-size:11px;color:#666;margin-top:4px;\">Failed</div></td>");
        body.append("</tr></table>");

        // Success Rate
        body.append("<div style=\"text-align:center;margin:20px 0;\"><div style=\"font-size:42px;font-weight:bold;color:").append(rateColor).append(";\">").append(String.format("%.1f", successRate)).append("%</div><div style=\"font-size:12px;color:#666;\">Success Rate</div></div>");

        // Status Banner
        body.append(statusBanner);

        // SSL Warning
        body.append(sslHtml);

        // Attachment Note
        body.append("<div style=\"background:#E8F5E9;border-radius:8px;padding:12px;margin-top:15px;border-left:4px solid #4CAF50;\"><strong style=\"color:#2E7D32;\">📎 Detailed report attached</strong><div style=\"font-size:12px;color:#558B2F;margin-top:4px;\">Open the HTML attachment for complete test details.</div></div>");

        body.append("</td></tr>");

        // Footer
        body.append("<tr><td style=\"background:#FAFAFA;padding:15px;text-align:center;border-radius:0 0 12px 12px;border-top:1px solid #EEE;\"><p style=\"margin:0;font-size:11px;color:#999;\">AdHash API Test Automation | ").append(timestamp).append("</p></td></tr>");

        body.append("</table></body></html>");

        return body.toString();
    }

    /**
     * Extract project name from method name (e.g., "pm_AdHash_Website" -> "AdHash Website")
     */
    private String extractProjectName(String methodName) {
        if (methodName == null) return "Unknown";
        String name = methodName.replaceFirst("^[a-z]+_", "");
        return name.replace("_", " ");
    }

    /**
     * Send simple notification email
     */
    public void sendNotification(String[] toEmails, String subject, String message) {
        sendTestReport(toEmails, subject, null, message);
    }
}
