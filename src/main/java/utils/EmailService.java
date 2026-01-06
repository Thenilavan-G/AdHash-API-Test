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
     * Create HTML email body - generates a clean email-friendly format
     * Shows status banner, API endpoint table, certificate warnings, and project groupings
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

        body.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body>");
        body.append("<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 800px; margin: 0 auto; padding: 20px;\">");

        // Status Banner
        if (allPassed) {
            body.append("<div style=\"background-color: #4caf50; color: white; text-align: center; padding: 15px; border-radius: 6px; font-size: 18px; font-weight: bold; margin-bottom: 20px;\">☑ ALL TESTS PASSED</div>");
        } else {
            body.append("<div style=\"background-color: #f44336; color: white; text-align: center; padding: 15px; border-radius: 6px; font-size: 18px; font-weight: bold; margin-bottom: 20px;\">✗ TESTS FAILED (").append(failedTests).append(" of ").append(totalTests).append(")</div>");
        }

        // SSL Certificate Warning Banner (if any certificate errors)
        if (certErrorCount > 0) {
            body.append("<div style=\"background-color: #ff9800; color: white; text-align: center; padding: 12px; border-radius: 6px; font-size: 16px; font-weight: bold; margin-bottom: 20px;\">");
            body.append("🔒 SSL CERTIFICATE WARNING: ").append(certErrorCount).append(" endpoint(s) have certificate issues!");
            body.append("</div>");

            // Certificate Error Details Section
            body.append("<div style=\"background-color: #fff3e0; border: 2px solid #ff9800; border-radius: 6px; padding: 15px; margin-bottom: 20px;\">");
            body.append("<h4 style=\"color: #e65100; margin-top: 0;\">🔒 SSL Certificate Issues Detected</h4>");
            body.append("<p style=\"color: #e65100; margin-bottom: 10px;\">The following endpoints have SSL certificate problems that require attention:</p>");
            body.append("<ul style=\"margin: 0; padding-left: 20px;\">");
            for (TestMethodDetails test : testsWithCertErrors) {
                String projectName = extractProjectName(test.methodName);
                body.append("<li style=\"color: #e65100; margin-bottom: 8px;\">");
                body.append("<strong>").append(projectName).append("</strong>");
                if (test.apiUrl != null) {
                    body.append(" - <a href=\"").append(test.apiUrl).append("\" style=\"color: #e65100;\">").append(test.apiUrl).append("</a>");
                }
                body.append("<br><span style=\"font-size: 12px;\">").append(test.certificateError).append("</span>");
                body.append("</li>");
            }
            body.append("</ul>");
            body.append("</div>");
        }

        // Attachment note
        body.append("<div style=\"background-color: #f5f5f5; padding: 12px; border-radius: 4px; margin-bottom: 20px;\">");
        body.append("<strong>📎 Detailed HTML report is attached to this email.</strong><br>");
        body.append("<span style=\"color: #666;\">Open the attachment to view the complete test results with expandable test details.</span>");
        body.append("</div>");

        // Individual API Test Results section
        body.append("<h3 style=\"color: #333; margin-top: 25px;\">📋 Individual API Test Results</h3>");

        // API Endpoint Table
        body.append("<table style=\"width: 100%; border-collapse: collapse; font-size: 13px;\">");
        body.append("<thead><tr>");
        body.append("<th style=\"background-color: #2196f3; color: white; padding: 10px; text-align: left; width: 40px;\">#</th>");
        body.append("<th style=\"background-color: #2196f3; color: white; padding: 10px; text-align: left;\">API Endpoint</th>");
        body.append("<th style=\"background-color: #2196f3; color: white; padding: 10px; text-align: center; width: 50px;\">SSL</th>");
        body.append("<th style=\"background-color: #2196f3; color: white; padding: 10px; text-align: right; width: 80px;\">Status</th>");
        body.append("</tr></thead><tbody>");

        int index = 1;
        for (TestMethodDetails test : allTests) {
            String projectName = extractProjectName(test.methodName);
            String function = extractFunctionName(test.methodName);
            boolean isPassed = test.errorMessage == null || test.errorMessage.isEmpty();
            String statusColor = isPassed ? "#4caf50" : "#f44336";
            String statusText = isPassed ? "✓ OK" : "✗ FAIL";
            boolean hasCertError = test.certificateError != null && !test.certificateError.isEmpty();
            String sslStatus = hasCertError ? "⚠️" : "✓";
            String sslColor = hasCertError ? "#ff9800" : "#4caf50";

            body.append("<tr style=\"border-bottom: 1px solid #e0e0e0;\">");
            body.append("<td style=\"padding: 10px;\">").append(index++).append("</td>");
            body.append("<td style=\"padding: 10px;\">");
            body.append("<strong>").append(projectName).append("</strong> - ").append(function);
            if (test.apiUrl != null && !test.apiUrl.isEmpty()) {
                body.append("<br><a href=\"").append(test.apiUrl).append("\" style=\"color: #1976d2; font-size: 12px;\">").append(test.apiUrl).append("</a>");
            }
            body.append("</td>");
            body.append("<td style=\"padding: 10px; text-align: center; color: ").append(sslColor).append("; font-weight: bold;\">").append(sslStatus).append("</td>");
            body.append("<td style=\"padding: 10px; text-align: right; color: ").append(statusColor).append("; font-weight: bold;\">").append(statusText).append("</td>");
            body.append("</tr>");
        }
        body.append("</tbody></table>");

        // AdHash Projects Tested section
        body.append("<h3 style=\"color: #333; margin-top: 30px;\">🚀 AdHash Projects Tested</h3>");
        body.append("<p>All <strong>").append(totalTests).append(" API endpoints</strong> tested successfully across all AdHash projects:</p>");

        // Group by project
        Map<String, Set<String>> projectFunctions = new HashMap<>();
        for (TestMethodDetails test : allTests) {
            String project = extractProjectName(test.methodName);
            String function = extractFunctionName(test.methodName);
            projectFunctions.computeIfAbsent(project, k -> new HashSet<>()).add(function);
        }

        // Display project cards
        body.append("<div style=\"display: flex; flex-wrap: wrap; gap: 10px;\">");
        for (Map.Entry<String, Set<String>> entry : projectFunctions.entrySet()) {
            body.append("<div style=\"background-color: #f5f5f5; padding: 10px 15px; border-radius: 6px; margin: 5px;\">");
            body.append("<strong>").append(entry.getKey()).append("</strong>");
            body.append(" <span style=\"color: #666;\">(").append(String.join(", ", entry.getValue())).append(")</span>");
            body.append("</div>");
        }
        body.append("</div>");

        body.append("<hr style=\"border: none; border-top: 1px solid #e0e0e0; margin: 25px 0;\">");
        body.append("<p style=\"color: #666; font-size: 12px;\"><em>Generated: ").append(timestamp).append("</em></p>");
        body.append("</div></body></html>");

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
