package utils;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import java.io.File;
import java.util.Arrays;

/**
 * TestNG Listener to automatically send HTML test reports via email
 * Triggers after all tests are completed
 */
public class TestReportListener implements ITestListener, ISuiteListener {
    
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private int skippedTests = 0;
    
    @Override
    public void onTestSuccess(ITestResult result) {
        passedTests++;
        System.out.println("✅ PASSED: " + result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        failedTests++;
        System.out.println("❌ FAILED: " + result.getMethod().getMethodName());
        System.out.println("   Error: " + result.getThrowable().getMessage());
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        skippedTests++;
        System.out.println("⏭️ SKIPPED: " + result.getMethod().getMethodName());
    }
    
    @Override
    public void onFinish(ISuite suite) {
        totalTests = passedTests + failedTests + skippedTests;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎯 TEST EXECUTION COMPLETED - AdHash API Suite");
        System.out.println("=".repeat(60));
        System.out.println("📊 RESULTS SUMMARY:");
        System.out.println("   Total Tests: " + totalTests);
        System.out.println("   ✅ Passed: " + passedTests);
        System.out.println("   ❌ Failed: " + failedTests);
        System.out.println("   ⏭️ Skipped: " + skippedTests);
        System.out.println("   📈 Success Rate: " + (totalTests > 0 ? (passedTests * 100.0 / totalTests) : 0) + "%");
        System.out.println("=".repeat(60));
        
        // Send email report
        sendEmailReport();
    }
    
    /**
     * Send email report with HTML attachment
     */
    private void sendEmailReport() {
        try {
            // Check if email is enabled
            String emailEnabled = System.getProperty("email.enabled", System.getenv("EMAIL_ENABLED"));
            if (!"true".equalsIgnoreCase(emailEnabled)) {
                System.out.println("📧 Email reporting is disabled. Set EMAIL_ENABLED=true to enable.");
                return;
            }
            
            // Get email configuration
            String emailProvider = System.getProperty("email.provider", System.getenv("EMAIL_PROVIDER"));
            String emailUsername = System.getProperty("email.username", System.getenv("EMAIL_USERNAME"));
            String emailPassword = System.getProperty("email.password", System.getenv("EMAIL_PASSWORD"));
            String emailRecipients = System.getProperty("email.recipients", System.getenv("EMAIL_RECIPIENTS"));
            
            if (emailUsername == null || emailPassword == null || emailRecipients == null) {
                System.out.println("⚠️ Email configuration missing. Please set EMAIL_USERNAME, EMAIL_PASSWORD, and EMAIL_RECIPIENTS");
                return;
            }
            
            // Create email service
            EmailService emailService;
            if ("outlook".equalsIgnoreCase(emailProvider)) {
                emailService = EmailService.forOutlook(emailUsername, emailPassword);
            } else {
                // Default to Gmail
                emailService = EmailService.forGmail(emailUsername, emailPassword);
            }
            
            // Find HTML report file
            String htmlReportPath = findHtmlReport();
            
            // Prepare email details
            String[] recipients = emailRecipients.split(",");
            for (int i = 0; i < recipients.length; i++) {
                recipients[i] = recipients[i].trim();
            }
            
            String subject = "AdHash API Test Report - " + getTestStatus();
            String message = createEmailMessage();
            
            // Send email
            System.out.println("📧 Sending email report to: " + Arrays.toString(recipients));
            emailService.sendTestReport(recipients, subject, htmlReportPath, message);
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send email report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Find the HTML report file
     */
    private String findHtmlReport() {
        // Check common report locations
        String[] possiblePaths = {
            "target/surefire-reports/emailable-report.html",
            "target/surefire-reports/index.html",
            "test-output/emailable-report.html",
            "test-output/index.html"
        };
        
        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists()) {
                System.out.println("📄 Found HTML report: " + file.getAbsolutePath());
                return file.getAbsolutePath();
            }
        }
        
        System.out.println("⚠️ HTML report not found in standard locations");
        return null;
    }
    
    /**
     * Get overall test status
     */
    private String getTestStatus() {
        if (failedTests > 0) {
            return "FAILED (" + failedTests + " failures)";
        } else if (skippedTests > 0) {
            return "PASSED with SKIPS (" + skippedTests + " skipped)";
        } else {
            return "ALL PASSED";
        }
    }
    
    /**
     * Create detailed email message
     */
    private String createEmailMessage() {
        StringBuilder message = new StringBuilder();
        message.append("<h3>🎯 AdHash API Test Execution Summary</h3>");
        message.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
        message.append("<tr style='background-color: #f2f2f2;'>");
        message.append("<th style='padding: 8px;'>Metric</th>");
        message.append("<th style='padding: 8px;'>Count</th>");
        message.append("<th style='padding: 8px;'>Percentage</th>");
        message.append("</tr>");
        
        message.append("<tr>");
        message.append("<td style='padding: 8px;'>Total Tests</td>");
        message.append("<td style='padding: 8px;'>").append(totalTests).append("</td>");
        message.append("<td style='padding: 8px;'>100%</td>");
        message.append("</tr>");
        
        message.append("<tr style='background-color: #d4edda;'>");
        message.append("<td style='padding: 8px;'>✅ Passed</td>");
        message.append("<td style='padding: 8px;'>").append(passedTests).append("</td>");
        message.append("<td style='padding: 8px;'>").append(totalTests > 0 ? String.format("%.1f%%", passedTests * 100.0 / totalTests) : "0%").append("</td>");
        message.append("</tr>");
        
        if (failedTests > 0) {
            message.append("<tr style='background-color: #f8d7da;'>");
            message.append("<td style='padding: 8px;'>❌ Failed</td>");
            message.append("<td style='padding: 8px;'>").append(failedTests).append("</td>");
            message.append("<td style='padding: 8px;'>").append(totalTests > 0 ? String.format("%.1f%%", failedTests * 100.0 / totalTests) : "0%").append("</td>");
            message.append("</tr>");
        }
        
        if (skippedTests > 0) {
            message.append("<tr style='background-color: #fff3cd;'>");
            message.append("<td style='padding: 8px;'>⏭️ Skipped</td>");
            message.append("<td style='padding: 8px;'>").append(skippedTests).append("</td>");
            message.append("<td style='padding: 8px;'>").append(totalTests > 0 ? String.format("%.1f%%", skippedTests * 100.0 / totalTests) : "0%").append("</td>");
            message.append("</tr>");
        }
        
        message.append("</table>");
        message.append("<p><strong>Overall Status:</strong> ").append(getTestStatus()).append("</p>");
        message.append("<p><em>Detailed HTML report is attached to this email.</em></p>");
        
        return message.toString();
    }
}
