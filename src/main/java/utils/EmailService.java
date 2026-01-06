package utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
     * Create HTML email body - generates a modern, clean email-friendly format
     * Shows status banner, SSL warnings, summary stats, and project groupings
     */
    private String createEmailBody(String additionalMessage, String htmlReportPath) {
        StringBuilder body = new StringBuilder();

        // Get test data from SimpleHtmlReportGenerator
        int totalTests = SimpleHtmlReportGenerator.getTotalTests();
        int passedTests = SimpleHtmlReportGenerator.getPassedTests();
        int failedTests = SimpleHtmlReportGenerator.getFailedTests();
        int certErrorCount = SimpleHtmlReportGenerator.getCertificateErrorCount();
        List<TestMethodDetails> allTests = SimpleHtmlReportGenerator.getAllTestMethods();
        List<TestMethodDetails> testsWithCertErrors = SimpleHtmlReportGenerator.getTestsWithCertificateErrors();

        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z").format(new Date());
        boolean allPassed = failedTests == 0;
        double successRate = totalTests > 0 ? (passedTests * 100.0 / totalTests) : 0;

        // Email body with table-based layout for maximum email client compatibility
        body.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        body.append("<body style=\"margin:0;padding:0;background-color:#f0f2f5;\">");
        body.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#f0f2f5;padding:20px 0;\">");
        body.append("<tr><td align=\"center\">");
        body.append("<table width=\"650\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#ffffff;border-radius:12px;box-shadow:0 2px 8px rgba(0,0,0,0.1);\">");

        // Header with gradient background
        body.append("<tr><td style=\"background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);padding:30px;border-radius:12px 12px 0 0;text-align:center;\">");
        body.append("<h1 style=\"color:#ffffff;margin:0;font-family:'Segoe UI',Arial,sans-serif;font-size:28px;font-weight:600;\">🎯 AdHash API Test Report</h1>");
        body.append("<p style=\"color:rgba(255,255,255,0.9);margin:10px 0 0 0;font-family:'Segoe UI',Arial,sans-serif;font-size:14px;\">").append(timestamp).append("</p>");
        body.append("</td></tr>");

        // Stats Section
        body.append("<tr><td style=\"padding:30px;\">");
        body.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">");
        body.append("<tr>");
        // Total Tests
        body.append("<td width=\"33%\" align=\"center\" style=\"padding:15px;\">");
        body.append("<div style=\"background:#e3f2fd;border-radius:10px;padding:20px;\">");
        body.append("<div style=\"font-size:36px;font-weight:bold;color:#1976d2;font-family:'Segoe UI',Arial,sans-serif;\">").append(totalTests).append("</div>");
        body.append("<div style=\"font-size:12px;color:#666;font-family:'Segoe UI',Arial,sans-serif;margin-top:5px;\">Total Tests</div>");
        body.append("</div></td>");
        // Passed
        body.append("<td width=\"33%\" align=\"center\" style=\"padding:15px;\">");
        body.append("<div style=\"background:#e8f5e9;border-radius:10px;padding:20px;\">");
        body.append("<div style=\"font-size:36px;font-weight:bold;color:#4caf50;font-family:'Segoe UI',Arial,sans-serif;\">✓ ").append(passedTests).append("</div>");
        body.append("<div style=\"font-size:12px;color:#4caf50;font-family:'Segoe UI',Arial,sans-serif;margin-top:5px;\">Passed</div>");
        body.append("</div></td>");
        // Failed
        body.append("<td width=\"33%\" align=\"center\" style=\"padding:15px;\">");
        body.append("<div style=\"background:#ffebee;border-radius:10px;padding:20px;\">");
        body.append("<div style=\"font-size:36px;font-weight:bold;color:#f44336;font-family:'Segoe UI',Arial,sans-serif;\">✗ ").append(failedTests).append("</div>");
        body.append("<div style=\"font-size:12px;color:#f44336;font-family:'Segoe UI',Arial,sans-serif;margin-top:5px;\">Failed</div>");
        body.append("</div></td>");
        body.append("</tr></table>");

        // Success Rate
        String rateColor = successRate == 100 ? "#4caf50" : successRate >= 80 ? "#ff9800" : "#f44336";
        body.append("<div style=\"text-align:center;margin:20px 0;\">");
        body.append("<div style=\"font-size:48px;font-weight:bold;color:").append(rateColor).append(";font-family:'Segoe UI',Arial,sans-serif;\">").append(String.format("%.1f%%", successRate)).append("</div>");
        body.append("<div style=\"font-size:14px;color:#666;font-family:'Segoe UI',Arial,sans-serif;\">Success Rate</div>");
        body.append("</div>");

        // Status Banner
        if (allPassed) {
            body.append("<div style=\"background:#4caf50;color:#ffffff;text-align:center;padding:15px;border-radius:8px;margin:15px 0;\">");
            body.append("<span style=\"font-size:18px;font-weight:bold;font-family:'Segoe UI',Arial,sans-serif;\">✓ ALL TESTS PASSED</span>");
            body.append("</div>");
        } else {
            body.append("<div style=\"background:#f44336;color:#ffffff;text-align:center;padding:15px;border-radius:8px;margin:15px 0;\">");
            body.append("<span style=\"font-size:18px;font-weight:bold;font-family:'Segoe UI',Arial,sans-serif;\">✗ TESTS FAILED (").append(failedTests).append(" of ").append(totalTests).append(")</span>");
            body.append("</div>");
        }

        // SSL Certificate Warning Section (PROMINENT)
        if (certErrorCount > 0) {
            body.append("<div style=\"background:#ff9800;color:#ffffff;text-align:center;padding:15px;border-radius:8px;margin:15px 0;\">");
            body.append("<span style=\"font-size:18px;font-weight:bold;font-family:'Segoe UI',Arial,sans-serif;\">🔒 SSL CERTIFICATE WARNING: ").append(certErrorCount).append(" endpoint(s) have issues!</span>");
            body.append("</div>");

            // Certificate Error Details
            body.append("<div style=\"background:#fff8e1;border:2px solid #ff9800;border-radius:8px;padding:20px;margin:15px 0;\">");
            body.append("<h3 style=\"color:#e65100;margin:0 0 15px 0;font-family:'Segoe UI',Arial,sans-serif;font-size:16px;\">🔒 SSL Certificate Issues Detected</h3>");
            body.append("<p style=\"color:#e65100;margin:0 0 15px 0;font-family:'Segoe UI',Arial,sans-serif;font-size:13px;\">The following endpoints have SSL certificate problems that require immediate attention:</p>");
            body.append("<table width=\"100%\" cellpadding=\"8\" cellspacing=\"0\" style=\"border-collapse:collapse;\">");
            for (TestMethodDetails test : testsWithCertErrors) {
                String projectName = extractProjectName(test.methodName);
                body.append("<tr style=\"border-bottom:1px solid #ffcc80;\">");
                body.append("<td style=\"padding:10px;font-family:'Segoe UI',Arial,sans-serif;\">");
                body.append("<strong style=\"color:#e65100;\">⚠️ ").append(projectName).append("</strong><br>");
                if (test.apiUrl != null) {
                    body.append("<a href=\"").append(test.apiUrl).append("\" style=\"color:#f57c00;font-size:12px;\">").append(test.apiUrl).append("</a><br>");
                }
                body.append("<span style=\"color:#bf360c;font-size:11px;\">").append(test.certificateError != null ? test.certificateError : "Certificate validation failed").append("</span>");
                body.append("</td></tr>");
            }
            body.append("</table></div>");
        }

        // Attachment Note
        body.append("<div style=\"background:#e8f5e9;border-left:4px solid #4caf50;padding:15px;margin:20px 0;border-radius:0 8px 8px 0;\">");
        body.append("<strong style=\"color:#2e7d32;font-family:'Segoe UI',Arial,sans-serif;\">📎 Detailed HTML report is attached</strong><br>");
        body.append("<span style=\"color:#558b2f;font-family:'Segoe UI',Arial,sans-serif;font-size:13px;\">Open the attachment to view complete test results with expandable details.</span>");
        body.append("</div>");

        body.append("</td></tr>");

        // Projects Section
        body.append("<tr><td style=\"padding:0 30px 30px 30px;\">");
        body.append("<h3 style=\"color:#333;font-family:'Segoe UI',Arial,sans-serif;margin:0 0 15px 0;font-size:16px;\">🚀 AdHash Projects Tested</h3>");

        // Group by project
        Map<String, Set<String>> projectFunctions = new java.util.LinkedHashMap<>();
        for (TestMethodDetails test : allTests) {
            String project = extractProjectName(test.methodName);
            String function = extractFunctionName(test.methodName);
            projectFunctions.computeIfAbsent(project, k -> new java.util.LinkedHashSet<>()).add(function);
        }

        // Display projects in a clean grid
        body.append("<table width=\"100%\" cellpadding=\"5\" cellspacing=\"0\">");
        int colCount = 0;
        for (Map.Entry<String, Set<String>> entry : projectFunctions.entrySet()) {
            if (colCount % 2 == 0) body.append("<tr>");
            body.append("<td width=\"50%\" style=\"padding:8px;\">");
            body.append("<div style=\"background:#f5f5f5;border-radius:6px;padding:12px;font-family:'Segoe UI',Arial,sans-serif;\">");
            body.append("<strong style=\"color:#333;\">▸ ").append(entry.getKey()).append("</strong>");
            body.append("<div style=\"color:#666;font-size:11px;margin-top:4px;\">(").append(String.join(", ", entry.getValue())).append(")</div>");
            body.append("</div></td>");
            colCount++;
            if (colCount % 2 == 0) body.append("</tr>");
        }
        if (colCount % 2 != 0) body.append("<td></td></tr>");
        body.append("</table>");
        body.append("</td></tr>");

        // Footer
        body.append("<tr><td style=\"background:#f5f5f5;padding:20px;text-align:center;border-radius:0 0 12px 12px;\">");
        body.append("<p style=\"margin:0;color:#999;font-family:'Segoe UI',Arial,sans-serif;font-size:11px;\">Generated by AdHash API Test Automation</p>");
        body.append("<p style=\"margin:5px 0 0 0;color:#bbb;font-family:'Segoe UI',Arial,sans-serif;font-size:10px;\">").append(timestamp).append("</p>");
        body.append("</td></tr>");

        body.append("</table></td></tr></table>");
        body.append("</body></html>");

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
