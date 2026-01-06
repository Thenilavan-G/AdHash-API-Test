package utils;

// Simple HTML Report Generator - Updated with expandable details

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Simple HTML Report Generator for AdHash API Tests
 * Creates a clean, minimal HTML report with just pass/fail counts and charts
 */
public class SimpleHtmlReportGenerator {
    
    private static String reportPath;
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;
    private static int skippedTests = 0;

    // Lists to store test method details with API information
    private static List<TestMethodDetails> passedTestMethods = new ArrayList<>();
    private static List<TestMethodDetails> failedTestMethods = new ArrayList<>();
    private static List<TestMethodDetails> skippedTestMethods = new ArrayList<>();

    // Inner class to store test method details
    public static class TestMethodDetails {
        public String methodName;
        public String apiUrl;
        public int statusCode;
        public String errorMessage;
        public String httpMethod;
        public String requestBody;
        public String responseBody;
        public String certificateError;  // SSL certificate error message

        public TestMethodDetails(String methodName, String apiUrl, int statusCode, String errorMessage) {
            this.methodName = methodName;
            this.apiUrl = apiUrl;
            this.statusCode = statusCode;
            this.errorMessage = errorMessage;
            this.httpMethod = "GET";
            this.requestBody = null;
            this.responseBody = null;
            this.certificateError = null;
        }

        public TestMethodDetails(String methodName, String apiUrl, int statusCode, String errorMessage,
                                  String httpMethod, String requestBody, String responseBody) {
            this.methodName = methodName;
            this.apiUrl = apiUrl;
            this.statusCode = statusCode;
            this.errorMessage = errorMessage;
            this.httpMethod = httpMethod;
            this.requestBody = requestBody;
            this.responseBody = responseBody;
            this.certificateError = null;
        }

        public TestMethodDetails(String methodName, String apiUrl, int statusCode, String errorMessage,
                                  String httpMethod, String requestBody, String responseBody, String certificateError) {
            this.methodName = methodName;
            this.apiUrl = apiUrl;
            this.statusCode = statusCode;
            this.errorMessage = errorMessage;
            this.httpMethod = httpMethod;
            this.requestBody = requestBody;
            this.responseBody = responseBody;
            this.certificateError = certificateError;
        }
    }
    
    /**
     * Initialize the simple report
     */
    public static void initializeReport() {
        // Reset all counters and lists
        reset();

        // Create reports directory if it doesn't exist
        File reportsDir = new File("simple-reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }

        // Generate report file name with timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        reportPath = "simple-reports/AdHash_API_Simple_Report_" + timestamp + ".html";
    }
    
    /**
     * Record test result with test method name and API details (legacy method)
     */
    public static void recordTestResult(String status, String testMethodName, String apiUrl, int statusCode, String errorMessage) {
        recordTestResult(status, testMethodName, apiUrl, statusCode, errorMessage, "GET", null, null, null);
    }

    /**
     * Record test result with full API details including request/response (legacy method)
     */
    public static void recordTestResult(String status, String testMethodName, String apiUrl, int statusCode,
                                         String errorMessage, String httpMethod, String requestBody, String responseBody) {
        recordTestResult(status, testMethodName, apiUrl, statusCode, errorMessage, httpMethod, requestBody, responseBody, null);
    }

    /**
     * Record test result with full API details including request/response and certificate error
     */
    public static void recordTestResult(String status, String testMethodName, String apiUrl, int statusCode,
                                         String errorMessage, String httpMethod, String requestBody, String responseBody,
                                         String certificateError) {
        totalTests++;
        TestMethodDetails details = new TestMethodDetails(testMethodName, apiUrl, statusCode, errorMessage,
                                                           httpMethod, requestBody, responseBody, certificateError);

        switch (status.toLowerCase()) {
            case "pass":
                passedTests++;
                passedTestMethods.add(details);
                break;
            case "fail":
                failedTests++;
                failedTestMethods.add(details);
                break;
            case "skip":
                skippedTests++;
                skippedTestMethods.add(details);
                break;
        }
    }
    
    /**
     * Generate the final HTML report
     */
    public static void generateReport() {
        try {
            // Use UTF-8 encoding explicitly to ensure proper display of Unicode characters
            java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(reportPath),
                java.nio.charset.StandardCharsets.UTF_8
            );

            // Calculate success rate
            double successRate = totalTests > 0 ? (passedTests * 100.0 / totalTests) : 0;

            // Generate HTML content
            writer.write(generateHtmlContent(successRate));
            writer.close();

            System.out.println("📊 Simple HTML Report generated: " + reportPath);

        } catch (IOException e) {
            System.err.println("Error generating simple HTML report: " + e.getMessage());
        }
    }
    
    /**
     * Generate HTML content - Certificate Expiry style with expandable table format
     */
    private static String generateHtmlContent(double successRate) {
        StringBuilder html = new StringBuilder();

        // Get timestamp in IST
        SimpleDateFormat istFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        istFormat.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Kolkata"));
        String timestamp = istFormat.format(new Date()) + " IST";

        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("    <title>AdHash API Test Report</title>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <style>\n");
        html.append(generateCSS(successRate));
        html.append("    </style>\n</head>\n<body>\n");
        html.append("    <div class=\"container\">\n");

        // Header
        html.append("        <div class=\"header\">\n");
        html.append("            <h1 class=\"title\">🎯 AdHash API Test Report</h1>\n");
        html.append("            <p class=\"timestamp\">").append(timestamp).append("</p>\n");
        html.append("        </div>\n");

        // Statistics boxes
        html.append("        <div class=\"stats\">\n");
        html.append("            <div class=\"stat-box total\"><div class=\"stat-number\">").append(totalTests).append("</div><div class=\"stat-label\">Total Tests</div></div>\n");
        html.append("            <div class=\"stat-box passed\"><div class=\"stat-number\">✓ ").append(passedTests).append("</div><div class=\"stat-label\">Passed</div></div>\n");
        html.append("            <div class=\"stat-box failed\"><div class=\"stat-number\">✗ ").append(failedTests).append("</div><div class=\"stat-label\">Failed</div></div>\n");
        html.append("        </div>\n");

        // Success Rate
        html.append("        <div class=\"success-rate\"><div class=\"success-rate-number\">").append(String.format("%.1f%%", successRate)).append("</div><div class=\"success-rate-label\">Success Rate</div></div>\n");

        // Status Banner
        String bannerClass = failedTests > 0 ? "failed" : skippedTests > 0 ? "warning" : "success";
        String bannerText = failedTests > 0 ? "✗ TESTS FAILED" : skippedTests > 0 ? "⚠ TESTS PASSED WITH SKIPS" : "✓ ALL TESTS PASSED";
        html.append("        <div class=\"status-banner ").append(bannerClass).append("\">").append(bannerText).append("</div>\n");

        // SSL Certificate Warning Banner (if any certificate errors)
        int certErrorCount = getCertificateErrorCount();
        if (certErrorCount > 0) {
            html.append("        <div class=\"status-banner warning\" style=\"margin-top: 10px;\">🔒 SSL CERTIFICATE WARNING: ")
                .append(certErrorCount).append(" endpoint(s) have certificate issues!</div>\n");

            // Certificate Error Details Section
            html.append("        <div style=\"background-color: #fff3e0; border: 2px solid #ff9800; border-radius: 6px; padding: 15px; margin: 15px 0;\">\n");
            html.append("            <h4 style=\"color: #e65100; margin: 0 0 10px 0;\">🔒 SSL Certificate Issues Detected</h4>\n");
            html.append("            <p style=\"color: #e65100; margin-bottom: 10px;\">The following endpoints have SSL certificate problems:</p>\n");
            html.append("            <ul style=\"margin: 0; padding-left: 20px;\">\n");
            for (TestMethodDetails test : getTestsWithCertificateErrors()) {
                String projectName = extractProjectName(test.methodName);
                html.append("                <li style=\"color: #e65100; margin-bottom: 8px;\"><strong>")
                    .append(projectName).append("</strong>");
                if (test.apiUrl != null) {
                    html.append(" - <a href=\"").append(test.apiUrl).append("\" style=\"color: #e65100;\">").append(test.apiUrl).append("</a>");
                }
                html.append("<br><span style=\"font-size: 12px;\">").append(escapeHtml(test.certificateError)).append("</span></li>\n");
            }
            html.append("            </ul>\n");
            html.append("        </div>\n");
        }

        // Table with all tests
        html.append("        <div class=\"table-container\">\n");
        html.append("            <table>\n");
        html.append("                <thead><tr><th class=\"sno\">S.No</th><th>Project Name</th><th>Function</th><th>Website URL</th><th>Status</th><th class=\"expand-col\"></th></tr></thead>\n");
        html.append("                <tbody>\n");

        int sno = 1;
        // Add failed tests first
        for (TestMethodDetails method : failedTestMethods) {
            html.append(generateTableRow(sno++, method, "fail"));
        }
        // Add passed tests
        for (TestMethodDetails method : passedTestMethods) {
            html.append(generateTableRow(sno++, method, "pass"));
        }
        // Add skipped tests
        for (TestMethodDetails method : skippedTestMethods) {
            html.append(generateTableRow(sno++, method, "skip"));
        }

        html.append("                </tbody>\n            </table>\n        </div>\n");
        html.append("        <div class=\"footer\"><p>Generated by AdHash API Test Suite</p></div>\n");
        html.append("    </div>\n");
        html.append(generateJavaScript());
        html.append("</body>\n</html>\n");

        return html.toString();
    }

    /**
     * Generate CSS styles
     */
    private static String generateCSS(double successRate) {
        String successColor = successRate >= 90 ? "#4caf50" : successRate >= 70 ? "#ff9800" : "#f44336";
        return "body { font-family: 'Segoe UI', Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }\n" +
               ".container { max-width: 1100px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n" +
               ".header { text-align: center; margin-bottom: 20px; }\n" +
               ".title { color: #333; font-size: 24px; margin-bottom: 5px; }\n" +
               ".timestamp { color: #666; font-size: 14px; text-decoration: underline; }\n" +
               ".stats { display: flex; justify-content: center; gap: 20px; margin: 25px 0; }\n" +
               ".stat-box { text-align: center; padding: 15px 30px; border-radius: 8px; min-width: 100px; border: 1px solid #e0e0e0; }\n" +
               ".stat-number { font-size: 32px; font-weight: bold; }\n" +
               ".stat-label { font-size: 12px; color: #666; margin-top: 5px; }\n" +
               ".total { background-color: #e3f2fd; } .total .stat-number { color: #1976d2; }\n" +
               ".passed { background-color: #e8f5e9; } .passed .stat-number, .passed .stat-label { color: #4caf50; }\n" +
               ".failed { background-color: #ffebee; } .failed .stat-number, .failed .stat-label { color: #f44336; }\n" +
               ".success-rate { text-align: center; margin: 20px 0; }\n" +
               ".success-rate-number { font-size: 42px; font-weight: bold; color: " + successColor + "; }\n" +
               ".success-rate-label { font-size: 14px; color: #666; }\n" +
               ".status-banner { text-align: center; margin: 20px 0; padding: 12px; border-radius: 6px; font-size: 16px; font-weight: bold; }\n" +
               ".status-banner.success { background-color: #4caf50; color: white; }\n" +
               ".status-banner.failed { background-color: #f44336; color: white; }\n" +
               ".status-banner.warning { background-color: #ff9800; color: white; }\n" +
               ".table-container { margin: 25px 0; overflow-x: auto; }\n" +
               "table { width: 100%; border-collapse: collapse; font-size: 13px; }\n" +
               "th { background-color: #455a64; color: white; padding: 10px 8px; text-align: left; font-weight: 600; }\n" +
               "td { padding: 10px 8px; border-bottom: 1px solid #e0e0e0; vertical-align: middle; }\n" +
               "tr:hover { background-color: #f5f5f5; }\n" +
               ".sno { width: 50px; text-align: center; }\n" +
               ".expand-col { width: 40px; text-align: center; }\n" +
               ".url a { color: #1976d2; text-decoration: none; word-break: break-all; }\n" +
               ".url a:hover { text-decoration: underline; }\n" +
               ".status-ok { color: #4caf50; font-weight: bold; }\n" +
               ".status-fail { color: #f44336; font-weight: bold; }\n" +
               ".status-skip { color: #ff9800; font-weight: bold; }\n" +
               ".expand-btn { cursor: pointer; background: #e0e0e0; border: none; border-radius: 4px; padding: 5px 10px; font-size: 12px; }\n" +
               ".expand-btn:hover { background: #bdbdbd; }\n" +
               ".details-row { display: none; }\n" +
               ".details-row.show { display: table-row; }\n" +
               ".details-cell { background-color: #fafafa; padding: 15px !important; }\n" +
               ".api-details { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }\n" +
               ".detail-box { background: white; border: 1px solid #e0e0e0; border-radius: 6px; padding: 12px; }\n" +
               ".detail-label { font-weight: bold; color: #455a64; margin-bottom: 5px; font-size: 11px; text-transform: uppercase; }\n" +
               ".detail-value { font-family: 'Consolas', monospace; font-size: 12px; word-break: break-all; max-height: 150px; overflow-y: auto; white-space: pre-wrap; }\n" +
               ".method-badge { display: inline-block; padding: 2px 8px; border-radius: 3px; font-size: 11px; font-weight: bold; margin-right: 5px; }\n" +
               ".method-get { background: #e3f2fd; color: #1976d2; }\n" +
               ".method-post { background: #fff3e0; color: #f57c00; }\n" +
               ".status-code { display: inline-block; padding: 2px 8px; border-radius: 3px; font-size: 11px; font-weight: bold; }\n" +
               ".status-2xx { background: #e8f5e9; color: #388e3c; }\n" +
               ".status-4xx { background: #fff3e0; color: #f57c00; }\n" +
               ".status-5xx { background: #ffebee; color: #d32f2f; }\n" +
               ".function-tag { background: #e8eaf6; color: #3f51b5; padding: 2px 6px; border-radius: 3px; font-size: 11px; }\n" +
               ".footer { text-align: center; margin-top: 20px; color: #999; font-size: 11px; }\n";
    }

    /**
     * Generate a table row with expandable details
     */
    private static String generateTableRow(int sno, TestMethodDetails method, String status) {
        StringBuilder row = new StringBuilder();
        String rowId = "row-" + sno;
        String[] parsed = parseMethodName(method.methodName);
        String projectName = parsed[0];
        String functionType = parsed[1];

        // Main row
        String statusClass = status.equals("pass") ? "status-ok" : status.equals("fail") ? "status-fail" : "status-skip";
        String statusText = status.equals("pass") ? "✓ OK" : status.equals("fail") ? "✗ Failed" : "⏭ Skipped";

        row.append("<tr>\n");
        row.append("  <td class=\"sno\">").append(sno).append("</td>\n");
        row.append("  <td>").append(escapeHtml(projectName)).append("</td>\n");
        row.append("  <td><span class=\"function-tag\">").append(escapeHtml(functionType)).append("</span></td>\n");
        row.append("  <td class=\"url\"><a href=\"").append(method.apiUrl != null ? escapeHtml(method.apiUrl) : "#").append("\" target=\"_blank\">").append(method.apiUrl != null ? escapeHtml(method.apiUrl) : "N/A").append("</a></td>\n");
        row.append("  <td class=\"").append(statusClass).append("\">").append(statusText).append("</td>\n");
        row.append("  <td class=\"expand-col\"><button class=\"expand-btn\" onclick=\"toggleDetails('").append(rowId).append("')\">▼</button></td>\n");
        row.append("</tr>\n");

        // Details row (hidden by default)
        row.append("<tr class=\"details-row\" id=\"").append(rowId).append("\">\n");
        row.append("  <td colspan=\"6\" class=\"details-cell\">\n");
        row.append("    <div class=\"api-details\">\n");

        // Method & Status Code box
        row.append("      <div class=\"detail-box\">\n");
        row.append("        <div class=\"detail-label\">HTTP Method & Status</div>\n");
        row.append("        <div class=\"detail-value\">\n");
        String methodClass = "POST".equals(method.httpMethod) ? "method-post" : "method-get";
        row.append("          <span class=\"method-badge ").append(methodClass).append("\">").append(method.httpMethod != null ? method.httpMethod : "GET").append("</span>\n");
        String statusCodeClass = method.statusCode >= 200 && method.statusCode < 300 ? "status-2xx" : method.statusCode >= 400 && method.statusCode < 500 ? "status-4xx" : "status-5xx";
        row.append("          <span class=\"status-code ").append(statusCodeClass).append("\">Status: ").append(method.statusCode).append("</span>\n");
        row.append("        </div>\n");
        row.append("      </div>\n");

        // Certificate error box (if any) - PROMINENTLY DISPLAYED
        if (method.certificateError != null && !method.certificateError.isEmpty()) {
            row.append("      <div class=\"detail-box\" style=\"grid-column: 1 / -1; background-color: #fff3e0; border: 2px solid #ff9800;\">\n");
            row.append("        <div class=\"detail-label\" style=\"color: #e65100;\">🔒 SSL Certificate Warning</div>\n");
            row.append("        <div class=\"detail-value\" style=\"color: #e65100; font-weight: bold;\">").append(escapeHtml(method.certificateError)).append("</div>\n");
            row.append("      </div>\n");
        }

        // Error message box (if any)
        if (method.errorMessage != null && !method.errorMessage.isEmpty()) {
            row.append("      <div class=\"detail-box\">\n");
            row.append("        <div class=\"detail-label\">Error Message</div>\n");
            row.append("        <div class=\"detail-value\" style=\"color: #d32f2f;\">").append(escapeHtml(method.errorMessage)).append("</div>\n");
            row.append("      </div>\n");
        }

        // Request body box (if any)
        if (method.requestBody != null && !method.requestBody.isEmpty()) {
            row.append("      <div class=\"detail-box\">\n");
            row.append("        <div class=\"detail-label\">Request Body</div>\n");
            row.append("        <div class=\"detail-value\">").append(escapeHtml(method.requestBody)).append("</div>\n");
            row.append("      </div>\n");
        }

        // Response body box (if any)
        if (method.responseBody != null && !method.responseBody.isEmpty()) {
            row.append("      <div class=\"detail-box\">\n");
            row.append("        <div class=\"detail-label\">Response Body</div>\n");
            row.append("        <div class=\"detail-value\">").append(escapeHtml(method.responseBody)).append("</div>\n");
            row.append("      </div>\n");
        }

        row.append("    </div>\n");
        row.append("  </td>\n");
        row.append("</tr>\n");

        return row.toString();
    }

    /**
     * Generate JavaScript for expand/collapse functionality
     */
    private static String generateJavaScript() {
        return "<script>\n" +
               "function toggleDetails(rowId) {\n" +
               "  var row = document.getElementById(rowId);\n" +
               "  var btn = row.previousElementSibling.querySelector('.expand-btn');\n" +
               "  if (row.classList.contains('show')) {\n" +
               "    row.classList.remove('show');\n" +
               "    btn.textContent = '▼';\n" +
               "  } else {\n" +
               "    row.classList.add('show');\n" +
               "    btn.textContent = '▲';\n" +
               "  }\n" +
               "}\n" +
               "</script>\n";
    }

    /**
     * Parse method name to extract project name and function type
     */
    private static String[] parseMethodName(String methodName) {
        // Remove pm_ prefix
        String name = methodName.replace("pm_", "");

        // Common function types to look for
        String[] functionTypes = {"SuperAdmin_Login_UI", "SuperAdmin_Login", "Admin_Login_UI", "Admin_Login",
                                   "Login_UI", "Login_OTP", "Login_PhoneNumber", "Login_Number", "Login",
                                   "App_Login", "Web_Login_UI", "Web_Login", "CPA_Login_UI", "CPA_Login",
                                   "PAR_Login_UI", "PAR_Login", "PAR_CRM_Login_UI", "PAR_CRM_Login",
                                   "Participant_Login_UI", "Participant_Login", "Doctor_Login_UI", "Doctor_Login",
                                   "Doctor_SuperAdmin_Login_UI", "Doctor_SuperAdmin_Login",
                                   "Form_UI", "Market_Web", "Website", "UI", "Loader"};

        String projectName = name;
        String functionType = "API";

        for (String ft : functionTypes) {
            if (name.endsWith("_" + ft) || name.equals(ft)) {
                int idx = name.lastIndexOf("_" + ft);
                if (idx > 0) {
                    projectName = name.substring(0, idx).replace("_", " ");
                } else {
                    projectName = name.replace("_", " ");
                }
                functionType = ft.replace("_", " ");
                break;
            }
        }

        // Clean up project name
        projectName = projectName.replace("_", " ").trim();
        if (projectName.isEmpty()) {
            projectName = name.replace("_", " ");
        }

        return new String[]{projectName, functionType};
    }

    /**
     * Extract project name from method name
     */
    private static String extractProjectName(String methodName) {
        // Remove pm_ prefix and convert underscores to spaces
        String name = methodName.replace("pm_", "");
        // Take the first part before any underscore as project name
        String[] parts = name.split("_");
        if (parts.length >= 2) {
            return parts[0] + " " + parts[1];
        }
        return name.replace("_", " ");
    }

    /**
     * Escape HTML special characters
     */
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
    
    /**
     * Get the report file path
     */
    public static String getReportPath() {
        return reportPath;
    }
    
    /**
     * Reset counters and lists
     */
    public static void reset() {
        totalTests = 0;
        passedTests = 0;
        failedTests = 0;
        skippedTests = 0;
        passedTestMethods.clear();
        failedTestMethods.clear();
        skippedTestMethods.clear();
    }

    /**
     * Get all test methods (passed + failed + skipped)
     */
    public static List<TestMethodDetails> getAllTestMethods() {
        List<TestMethodDetails> allMethods = new ArrayList<>();
        allMethods.addAll(passedTestMethods);
        allMethods.addAll(failedTestMethods);
        allMethods.addAll(skippedTestMethods);
        return allMethods;
    }

    /**
     * Get passed test methods
     */
    public static List<TestMethodDetails> getPassedTestMethods() {
        return passedTestMethods;
    }

    /**
     * Get failed test methods
     */
    public static List<TestMethodDetails> getFailedTestMethods() {
        return failedTestMethods;
    }

    /**
     * Get test statistics
     */
    public static int getTotalTests() { return totalTests; }
    public static int getPassedTests() { return passedTests; }
    public static int getFailedTests() { return failedTests; }

    /**
     * Get all tests with certificate errors
     */
    public static List<TestMethodDetails> getTestsWithCertificateErrors() {
        List<TestMethodDetails> testsWithCertErrors = new ArrayList<>();
        for (TestMethodDetails test : getAllTestMethods()) {
            if (test.certificateError != null && !test.certificateError.isEmpty()) {
                testsWithCertErrors.add(test);
            }
        }
        return testsWithCertErrors;
    }

    /**
     * Get count of tests with certificate errors
     */
    public static int getCertificateErrorCount() {
        return getTestsWithCertificateErrors().size();
    }
}
