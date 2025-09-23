package utils;

import java.io.File;
import java.util.Arrays;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

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
    public void onTestStart(ITestResult result) {
        // Create test in Extent Report
        String testName = result.getMethod().getMethodName();
        String description = "AdHash API Test: " + testName.replace("pm_", "").replace("_", " ");
        ExtentReportManager.createTest(testName, description);

        // Assign categories based on test name
        if (testName.contains("Login")) {
            ExtentReportManager.assignCategory("Authentication");
        } else if (testName.contains("UI")) {
            ExtentReportManager.assignCategory("UI Tests");
        } else {
            ExtentReportManager.assignCategory("API Tests");
        }

        ExtentReportManager.assignAuthor("AdHash QA Team");
        ExtentReportManager.logInfo("Starting test: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        passedTests++;
        String testName = result.getMethod().getMethodName();
        System.out.println("✅ PASSED: " + testName);

        // Log to Extent Report
        ExtentReportManager.logPass("✅ Test passed successfully");
        ExtentReportManager.logInfo("Execution time: " + (result.getEndMillis() - result.getStartMillis()) + "ms");
        ExtentReportManager.endTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failedTests++;
        String testName = result.getMethod().getMethodName();
        System.out.println("❌ FAILED: " + testName);
        System.out.println("   Error: " + result.getThrowable().getMessage());

        // Log to Extent Report
        ExtentReportManager.logFail("❌ Test failed: " + result.getThrowable().getMessage());
        ExtentReportManager.logInfo("Execution time: " + (result.getEndMillis() - result.getStartMillis()) + "ms");
        ExtentReportManager.endTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        skippedTests++;
        String testName = result.getMethod().getMethodName();
        System.out.println("⏭️ SKIPPED: " + testName);

        // Log to Extent Report
        ExtentReportManager.logSkip("⏭️ Test was skipped");
        ExtentReportManager.logInfo("Reason: " + (result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown"));
        ExtentReportManager.endTest();
    }
    
    @Override
    public void onStart(ISuite suite) {
        // Initialize Extent Reports at the beginning
        ExtentReportManager.initializeReport();
        System.out.println("📊 Extent Reports initialized successfully");
    }

    @Override
    public void onFinish(ISuite suite) {
        totalTests = passedTests + failedTests + skippedTests;

        // Finalize Extent Report
        ExtentReportManager.flushReport();
        System.out.println("📊 Extent Report generated: " + ExtentReportManager.getReportPath());

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
            // Check if email is enabled - check system properties first (from Maven -D), then environment variables
            String emailEnabled = System.getProperty("email.enabled");
            if (emailEnabled == null || emailEnabled.trim().isEmpty()) {
                emailEnabled = System.getenv("EMAIL_ENABLED");
            }

            System.out.println("🔍 Email configuration check:");
            System.out.println("   email.enabled sys prop: " + System.getProperty("email.enabled"));
            System.out.println("   EMAIL_ENABLED env var: " + System.getenv("EMAIL_ENABLED"));
            System.out.println("   Final emailEnabled value: " + emailEnabled);

            if (!"true".equalsIgnoreCase(emailEnabled)) {
                System.out.println("📧 Email reporting is disabled. Set EMAIL_ENABLED=true to enable.");
                return;
            }

            // Get email configuration - check system properties first (from Maven -D), then environment variables
            String emailProvider = System.getProperty("email.provider");
            if (emailProvider == null || emailProvider.trim().isEmpty()) {
                emailProvider = System.getenv("EMAIL_PROVIDER");
            }

            String emailUsername = System.getProperty("email.username");
            if (emailUsername == null || emailUsername.trim().isEmpty()) {
                emailUsername = System.getenv("EMAIL_USERNAME");
            }

            String emailPassword = System.getProperty("email.password");
            if (emailPassword == null || emailPassword.trim().isEmpty()) {
                emailPassword = System.getenv("EMAIL_PASSWORD");
            }

            String emailRecipients = System.getProperty("email.recipients");
            if (emailRecipients == null || emailRecipients.trim().isEmpty()) {
                emailRecipients = System.getenv("EMAIL_RECIPIENTS");
            }

            System.out.println("📧 Email configuration values:");
            System.out.println("   Provider: " + emailProvider);
            System.out.println("   Username: " + emailUsername);
            System.out.println("   Recipients: " + emailRecipients);
            System.out.println("   Password: " + (emailPassword != null ? "[SET]" : "[NOT SET]"));
            
            if (emailUsername == null || emailPassword == null || emailRecipients == null) {
                System.out.println("⚠️ Email configuration missing. Please set EMAIL_USERNAME, EMAIL_PASSWORD, and EMAIL_RECIPIENTS");
                return;
            }
            
            // Create email service
            EmailService emailService;
            if ("outlook".equalsIgnoreCase(emailProvider)) {
                emailService = EmailService.forOutlook(emailUsername, emailPassword);
            } else if ("zoho".equalsIgnoreCase(emailProvider)) {
                emailService = EmailService.forZoho(emailUsername, emailPassword);
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
     * Find the HTML report file (TestNG, Surefire, or Extent Reports)
     */
    private String findHtmlReport() {
        // First, check if we have the Extent Report path from ExtentReportManager
        String extentReportPath = ExtentReportManager.getReportPath();
        if (extentReportPath != null) {
            File extentFile = new File(extentReportPath);
            if (extentFile.exists()) {
                System.out.println("📄 Found Extent HTML report: " + extentFile.getAbsolutePath());
                System.out.println("   📊 Report Type: Extent Reports (Professional)");
                return extentFile.getAbsolutePath();
            }
        }

        // Fallback to check common report locations
        String[] possiblePaths = {
            // Extent Reports locations
            "extent-reports/AdHash_API_Report_*.html",
            "test-output/ExtentReports.html",
            "test-output/extent-reports.html",
            "test-output/extent.html",
            "reports/extent-reports.html",
            "reports/ExtentReports.html",
            // TestNG/Surefire locations (fallback)
            "target/surefire-reports/emailable-report.html",
            "target/surefire-reports/index.html",
            "test-output/emailable-report.html",
            "test-output/index.html"
        };

        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists()) {
                System.out.println("📄 Found HTML report: " + file.getAbsolutePath());
                if (path.toLowerCase().contains("extent")) {
                    System.out.println("   📊 Report Type: Extent Reports");
                } else {
                    System.out.println("   📊 Report Type: TestNG/Surefire (Basic)");
                }
                return file.getAbsolutePath();
            }
        }

        System.out.println("⚠️ HTML report not found in standard locations");
        System.out.println("   Checked: Extent Reports, TestNG, and Surefire report locations");
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
     * Create simple email message with just pass/fail counts
     */
    private String createEmailMessage() {
        StringBuilder message = new StringBuilder();
        message.append("<h3>🎯 AdHash API Test Execution Summary</h3>");
        message.append("<p>");
        message.append("<strong>Total Tests:</strong> ").append(totalTests).append("<br>");
        message.append("<strong>✅ Passed:</strong> ").append(passedTests).append("<br>");

        if (failedTests > 0) {
            message.append("<strong>❌ Failed:</strong> ").append(failedTests).append("<br>");
        }

        if (skippedTests > 0) {
            message.append("<strong>⏭️ Skipped:</strong> ").append(skippedTests).append("<br>");
        }

        message.append("<strong>📈 Success Rate:</strong> ").append(totalTests > 0 ? String.format("%.1f%%", passedTests * 100.0 / totalTests) : "0%");
        message.append("</p>");
        message.append("<p><strong>Overall Status:</strong> ").append(getTestStatus()).append("</p>");
        message.append("<p><em>Detailed HTML report is attached to this email.</em></p>");

        return message.toString();
    }
}
