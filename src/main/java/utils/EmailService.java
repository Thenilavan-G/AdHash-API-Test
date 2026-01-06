package utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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

import utils.SimpleHtmlReportGenerator.TestMethodDetails;

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
     * Create HTML email body - simple, clean email format
     * Shows stats, SSL warnings prominently, and simple project list
     */
    private String createEmailBody(String additionalMessage, String htmlReportPath) {
        StringBuilder body = new StringBuilder();

        int totalTests = SimpleHtmlReportGenerator.getTotalTests();
        int passedTests = SimpleHtmlReportGenerator.getPassedTests();
        int failedTests = SimpleHtmlReportGenerator.getFailedTests();
        int certErrorCount = SimpleHtmlReportGenerator.getCertificateErrorCount();
        List<TestMethodDetails> allTests = SimpleHtmlReportGenerator.getAllTestMethods();
        List<TestMethodDetails> testsWithCertErrors = SimpleHtmlReportGenerator.getTestsWithCertificateErrors();

        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z").format(new Date());
        boolean allPassed = failedTests == 0;
        double successRate = totalTests > 0 ? (passedTests * 100.0 / totalTests) : 0;

        body.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        body.append("<body style=\"margin:0;padding:20px;background:#f5f5f5;font-family:Arial,sans-serif;\">");
        body.append("<table width=\"600\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#fff;border-radius:8px;\">");

        // Header
        body.append("<tr><td style=\"background:#2196F3;color:#fff;padding:25px;text-align:center;border-radius:8px 8px 0 0;\">");
        body.append("<h1 style=\"margin:0;font-size:24px;\">AdHash API Test Report</h1>");
        body.append("<p style=\"margin:8px 0 0;opacity:0.9;font-size:13px;\">").append(timestamp).append("</p>");
        body.append("</td></tr>");

        // Stats Row
        body.append("<tr><td style=\"padding:25px;\">");
        body.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"10\">");
        body.append("<tr>");
        body.append("<td width=\"33%\" align=\"center\" style=\"background:#E3F2FD;padding:20px;border-radius:8px;\">");
        body.append("<div style=\"font-size:32px;font-weight:bold;color:#1976D2;\">").append(totalTests).append("</div>");
        body.append("<div style=\"font-size:12px;color:#666;margin-top:5px;\">Total Tests</div></td>");
        body.append("<td width=\"33%\" align=\"center\" style=\"background:#E8F5E9;padding:20px;border-radius:8px;\">");
        body.append("<div style=\"font-size:32px;font-weight:bold;color:#4CAF50;\">").append(passedTests).append("</div>");
        body.append("<div style=\"font-size:12px;color:#4CAF50;margin-top:5px;\">Passed</div></td>");
        body.append("<td width=\"33%\" align=\"center\" style=\"background:#FFEBEE;padding:20px;border-radius:8px;\">");
        body.append("<div style=\"font-size:32px;font-weight:bold;color:#F44336;\">").append(failedTests).append("</div>");
        body.append("<div style=\"font-size:12px;color:#F44336;margin-top:5px;\">Failed</div></td>");
        body.append("</tr></table>");

        // Success Rate
        String rateColor = successRate == 100 ? "#4CAF50" : successRate >= 80 ? "#FF9800" : "#F44336";
        body.append("<div style=\"text-align:center;margin:25px 0 15px;\">");
        body.append("<span style=\"font-size:42px;font-weight:bold;color:").append(rateColor).append(";\">").append(String.format("%.1f%%", successRate)).append("</span>");
        body.append("<div style=\"font-size:13px;color:#666;\">Success Rate</div></div>");

        // Status Banner
        if (allPassed) {
            body.append("<div style=\"background:#4CAF50;color:#fff;text-align:center;padding:12px;border-radius:6px;font-weight:bold;\">&#10004; ALL TESTS PASSED</div>");
        } else {
            body.append("<div style=\"background:#F44336;color:#fff;text-align:center;padding:12px;border-radius:6px;font-weight:bold;\">&#10008; ").append(failedTests).append(" TEST(S) FAILED</div>");
        }

        // SSL Certificate Warning Section
        if (certErrorCount > 0) {
            body.append("<div style=\"margin-top:15px;background:#FFF3E0;border:2px solid #FF9800;border-radius:6px;padding:15px;\">");
            body.append("<div style=\"background:#FF9800;color:#fff;padding:10px;margin:-15px -15px 15px;border-radius:4px 4px 0 0;font-weight:bold;text-align:center;\">");
            body.append("&#128274; SSL CERTIFICATE WARNING - ").append(certErrorCount).append(" Issue(s) Found</div>");
            body.append("<table width=\"100%\" cellpadding=\"8\" cellspacing=\"0\" style=\"font-size:13px;\">");
            body.append("<tr style=\"background:#FFE0B2;\"><th align=\"left\" style=\"padding:10px;color:#E65100;\">Project</th><th align=\"left\" style=\"padding:10px;color:#E65100;\">API URL</th></tr>");
            for (TestMethodDetails test : testsWithCertErrors) {
                String projectName = extractProjectName(test.methodName);
                body.append("<tr style=\"border-bottom:1px solid #FFCC80;\">");
                body.append("<td style=\"padding:10px;color:#BF360C;font-weight:bold;\">&#9888; ").append(projectName).append("</td>");
                body.append("<td style=\"padding:10px;\"><a href=\"").append(test.apiUrl != null ? test.apiUrl : "#").append("\" style=\"color:#E65100;font-size:11px;\">").append(test.apiUrl != null ? test.apiUrl : "N/A").append("</a></td>");
                body.append("</tr>");
            }
            body.append("</table>");
            body.append("<p style=\"margin:10px 0 0;font-size:11px;color:#E65100;\">These endpoints have SSL certificate validation issues. Please renew or fix the certificates.</p>");
            body.append("</div>");
        }

        // Attachment Note
        body.append("<div style=\"margin-top:20px;background:#E8F5E9;border-left:4px solid #4CAF50;padding:12px;border-radius:0 6px 6px 0;\">");
        body.append("<strong style=\"color:#2E7D32;\">&#128206; Detailed report attached</strong>");
        body.append("<div style=\"font-size:12px;color:#558B2F;margin-top:4px;\">Open the HTML attachment for complete test details.</div></div>");

        body.append("</td></tr>");

        // Simple Project Summary (no cards)
        body.append("<tr><td style=\"padding:0 25px 25px;\">");
        body.append("<div style=\"font-size:14px;font-weight:bold;color:#333;margin-bottom:10px;\">&#128640; Projects Tested (").append(totalTests).append(" APIs)</div>");

        Set<String> projectNames = new java.util.LinkedHashSet<>();
        for (TestMethodDetails test : allTests) {
            projectNames.add(extractProjectName(test.methodName));
        }
        body.append("<div style=\"font-size:12px;color:#666;line-height:1.8;\">");
        body.append(String.join(" &bull; ", projectNames));
        body.append("</div></td></tr>");

        // Footer
        body.append("<tr><td style=\"background:#FAFAFA;padding:15px;text-align:center;border-radius:0 0 8px 8px;border-top:1px solid #EEE;\">");
        body.append("<p style=\"margin:0;font-size:11px;color:#999;\">AdHash API Test Automation | ").append(timestamp).append("</p>");
        body.append("</td></tr>");

        body.append("</table></body></html>");
        return body.toString();
    }

    /**
     * Extract project name from method name (e.g., "pm_AdHash_Website" -> "AdHash")
     */
    private String extractProjectName(String methodName) {
        if (methodName == null) return "Unknown";
        // Remove prefix like "pm_" or "gm_"
        String name = methodName.replaceFirst("^[a-z]+_", "");
        // Extract project name (first part before underscore)
        String[] parts = name.split("_");
        if (parts.length >= 1) {
            return parts[0];
        }
        return name;
    }

    /**
     * Extract function name from method name (e.g., "pm_AdHash_Website" -> "Website")
     */
    private String extractFunctionName(String methodName) {
        if (methodName == null) return "Unknown";
        // Remove prefix like "pm_" or "gm_"
        String name = methodName.replaceFirst("^[a-z]+_", "");
        // Extract function name (everything after first underscore)
        int idx = name.indexOf('_');
        if (idx > 0 && idx < name.length() - 1) {
            return name.substring(idx + 1).replace("_", " ");
        }
        return name;
    }
    
    /**
     * Send simple notification email
     */
    public void sendNotification(String[] toEmails, String subject, String message) {
        sendTestReport(toEmails, subject, null, message);
    }
}
