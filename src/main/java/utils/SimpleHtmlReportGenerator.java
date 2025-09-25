package utils;

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

        public TestMethodDetails(String methodName, String apiUrl, int statusCode, String errorMessage) {
            this.methodName = methodName;
            this.apiUrl = apiUrl;
            this.statusCode = statusCode;
            this.errorMessage = errorMessage;
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
     * Record test result with test method name and API details
     */
    public static void recordTestResult(String status, String testMethodName, String apiUrl, int statusCode, String errorMessage) {
        totalTests++;
        TestMethodDetails details = new TestMethodDetails(testMethodName, apiUrl, statusCode, errorMessage);

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
     * Generate HTML content
     */
    private static String generateHtmlContent(double successRate) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("    <title>AdHash API Test Report</title>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <style>\n");
        html.append("        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n");
        html.append("        .container { max-width: 800px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n");
        html.append("        .header { text-align: center; margin-bottom: 30px; }\n");
        html.append("        .title { color: #333; font-size: 28px; margin-bottom: 10px; }\n");
        html.append("        .subtitle { color: #666; font-size: 16px; }\n");
        html.append("        .stats { display: flex; justify-content: space-around; margin: 30px 0; }\n");
        html.append("        .stat-box { text-align: center; padding: 20px; border-radius: 8px; min-width: 120px; }\n");
        html.append("        .stat-number { font-size: 36px; font-weight: bold; margin-bottom: 5px; }\n");
        html.append("        .stat-label { font-size: 14px; color: #666; }\n");
        html.append("        .total { background-color: #e3f2fd; color: #1976d2; }\n");
        html.append("        .passed { background-color: #e8f5e8; color: #4caf50; }\n");
        html.append("        .failed { background-color: #ffebee; color: #f44336; }\n");
        html.append("        .skipped { background-color: #fff3e0; color: #ff9800; }\n");
        html.append("        .success-rate { text-align: center; margin: 30px 0; }\n");
        html.append("        .success-rate-number { font-size: 48px; font-weight: bold; color: ").append(successRate >= 90 ? "#4caf50" : successRate >= 70 ? "#ff9800" : "#f44336").append("; }\n");
        html.append("        .chart-container { text-align: center; margin: 30px 0; }\n");
        html.append("        .status { text-align: center; margin: 20px 0; padding: 15px; border-radius: 8px; font-size: 18px; font-weight: bold; }\n");
        html.append("        .status.success { background-color: #e8f5e8; color: #4caf50; }\n");
        html.append("        .status.warning { background-color: #fff3e0; color: #ff9800; }\n");
        html.append("        .status.error { background-color: #ffebee; color: #f44336; }\n");
        html.append("        .test-methods { margin: 30px 0; }\n");
        html.append("        .test-methods h3 { text-align: center; color: #333; margin-bottom: 20px; }\n");
        html.append("        .method-section { margin: 20px 0; }\n");
        html.append("        .method-section h4 { margin-bottom: 10px; }\n");
        html.append("        .method-list { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 8px; }\n");
        html.append("        .method-item { padding: 8px 12px; border-radius: 4px; font-size: 14px; margin-bottom: 4px; }\n");
        html.append("        .expandable { cursor: pointer; transition: all 0.3s ease; }\n");
        html.append("        .expandable:hover { transform: translateY(-1px); box-shadow: 0 2px 8px rgba(0,0,0,0.1); }\n");
        html.append("        .method-name { display: inline-block; flex: 1; }\n");
        html.append("        .expand-icon { float: right; transition: transform 0.3s ease; font-size: 12px; }\n");
        html.append("        .method-details { margin-top: 8px; padding: 8px; background: rgba(255,255,255,0.5); border-radius: 4px; }\n");
        html.append("        .detail-item { margin: 4px 0; font-size: 12px; }\n");
        html.append("        .error-message { color: #d32f2f; font-weight: bold; }\n");
        html.append("        .passed-method { background-color: #e8f5e8; color: #2e7d32; border-left: 4px solid #4caf50; }\n");
        html.append("        .failed-method { background-color: #ffebee; color: #c62828; border-left: 4px solid #f44336; }\n");
        html.append("        .skipped-method { background-color: #fff3e0; color: #ef6c00; border-left: 4px solid #ff9800; }\n");
        html.append("        .footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("    <div class=\"container\">\n");
        html.append("        <div class=\"header\">\n");
        html.append("            <h1 class=\"title\">🎯 AdHash API Test Report</h1>\n");
        html.append("            <p class=\"subtitle\">").append(new SimpleDateFormat("EEEE, MMMM dd, yyyy 'at' hh:mm a").format(new Date())).append("</p>\n");
        html.append("        </div>\n");
        
        // Statistics
        html.append("        <div class=\"stats\">\n");
        html.append("            <div class=\"stat-box total\">\n");
        html.append("                <div class=\"stat-number\">").append(totalTests).append("</div>\n");
        html.append("                <div class=\"stat-label\">Total Tests</div>\n");
        html.append("            </div>\n");
        html.append("            <div class=\"stat-box passed\">\n");
        html.append("                <div class=\"stat-number\">").append(passedTests).append("</div>\n");
        html.append("                <div class=\"stat-label\">✅ Passed</div>\n");
        html.append("            </div>\n");
        html.append("            <div class=\"stat-box failed\">\n");
        html.append("                <div class=\"stat-number\">").append(failedTests).append("</div>\n");
        html.append("                <div class=\"stat-label\">❌ Failed</div>\n");
        html.append("            </div>\n");
        
        if (skippedTests > 0) {
            html.append("            <div class=\"stat-box skipped\">\n");
            html.append("                <div class=\"stat-number\">").append(skippedTests).append("</div>\n");
            html.append("                <div class=\"stat-label\">⏭️ Skipped</div>\n");
            html.append("            </div>\n");
        }
        
        html.append("        </div>\n");
        
        // Success Rate
        html.append("        <div class=\"success-rate\">\n");
        html.append("            <div class=\"success-rate-number\">").append(String.format("%.1f%%", successRate)).append("</div>\n");
        html.append("            <div class=\"stat-label\">Success Rate</div>\n");
        html.append("        </div>\n");
        
        // Overall Status
        String statusClass = failedTests > 0 ? "error" : skippedTests > 0 ? "warning" : "success";
        String statusText = failedTests > 0 ? "❌ TESTS FAILED" : skippedTests > 0 ? "⚠️ TESTS PASSED WITH SKIPS" : "✅ ALL TESTS PASSED";
        
        html.append("        <div class=\"status ").append(statusClass).append("\">\n");
        html.append("            ").append(statusText).append("\n");
        html.append("        </div>\n");

        // Add test methods section
        html.append("        <div class=\"test-methods\">\n");
        html.append("            <h3>📋 Test Methods</h3>\n");

        // Passed tests
        if (!passedTestMethods.isEmpty()) {
            html.append("            <div class=\"method-section\">\n");
            html.append("                <h4 style=\"color: #4caf50;\">✅ Passed Tests (").append(passedTests).append(")</h4>\n");
            html.append("                <div class=\"method-list\">\n");
            for (TestMethodDetails method : passedTestMethods) {
                String displayName = method.methodName.replace("pm_", "").replace("_", " ");
                String methodId = "passed_" + method.methodName;
                html.append("                    <div class=\"method-item passed-method expandable\" onclick=\"toggleDetails('").append(methodId).append("')\">\n");
                html.append("                        <span class=\"method-name\">").append(displayName).append("</span>\n");
                html.append("                        <span class=\"expand-icon\">▼</span>\n");
                html.append("                        <div class=\"method-details\" id=\"").append(methodId).append("\" style=\"display: none;\">\n");
                html.append("                            <div class=\"detail-item\"><strong>API URL:</strong> ").append(method.apiUrl != null ? method.apiUrl : "N/A").append("</div>\n");
                html.append("                            <div class=\"detail-item\"><strong>Status Code:</strong> ").append(method.statusCode > 0 ? method.statusCode : "N/A").append("</div>\n");
                html.append("                        </div>\n");
                html.append("                    </div>\n");
            }
            html.append("                </div>\n");
            html.append("            </div>\n");
        }

        // Failed tests
        if (!failedTestMethods.isEmpty()) {
            html.append("            <div class=\"method-section\">\n");
            html.append("                <h4 style=\"color: #f44336;\">❌ Failed Tests (").append(failedTests).append(")</h4>\n");
            html.append("                <div class=\"method-list\">\n");
            for (TestMethodDetails method : failedTestMethods) {
                String displayName = method.methodName.replace("pm_", "").replace("_", " ");
                String methodId = "failed_" + method.methodName;
                html.append("                    <div class=\"method-item failed-method expandable\" onclick=\"toggleDetails('").append(methodId).append("')\">\n");
                html.append("                        <span class=\"method-name\">").append(displayName).append("</span>\n");
                html.append("                        <span class=\"expand-icon\">▼</span>\n");
                html.append("                        <div class=\"method-details\" id=\"").append(methodId).append("\" style=\"display: none;\">\n");
                html.append("                            <div class=\"detail-item\"><strong>API URL:</strong> ").append(method.apiUrl != null ? method.apiUrl : "N/A").append("</div>\n");
                html.append("                            <div class=\"detail-item\"><strong>Status Code:</strong> ").append(method.statusCode > 0 ? method.statusCode : "N/A").append("</div>\n");
                if (method.errorMessage != null && !method.errorMessage.isEmpty()) {
                    html.append("                            <div class=\"detail-item error-message\"><strong>Error:</strong> ").append(method.errorMessage).append("</div>\n");
                }
                html.append("                        </div>\n");
                html.append("                    </div>\n");
            }
            html.append("                </div>\n");
            html.append("            </div>\n");
        }

        // Skipped tests
        if (!skippedTestMethods.isEmpty()) {
            html.append("            <div class=\"method-section\">\n");
            html.append("                <h4 style=\"color: #ff9800;\">⏭️ Skipped Tests (").append(skippedTests).append(")</h4>\n");
            html.append("                <div class=\"method-list\">\n");
            for (TestMethodDetails method : skippedTestMethods) {
                String displayName = method.methodName.replace("pm_", "").replace("_", " ");
                String methodId = "skipped_" + method.methodName;
                html.append("                    <div class=\"method-item skipped-method expandable\" onclick=\"toggleDetails('").append(methodId).append("')\">\n");
                html.append("                        <span class=\"method-name\">").append(displayName).append("</span>\n");
                html.append("                        <span class=\"expand-icon\">▼</span>\n");
                html.append("                        <div class=\"method-details\" id=\"").append(methodId).append("\" style=\"display: none;\">\n");
                html.append("                            <div class=\"detail-item\"><strong>API URL:</strong> ").append(method.apiUrl != null ? method.apiUrl : "N/A").append("</div>\n");
                html.append("                            <div class=\"detail-item\"><strong>Status Code:</strong> ").append(method.statusCode > 0 ? method.statusCode : "N/A").append("</div>\n");
                html.append("                        </div>\n");
                html.append("                    </div>\n");
            }
            html.append("                </div>\n");
            html.append("            </div>\n");
        }

        html.append("        </div>\n");

        html.append("        <div class=\"footer\">\n");
        html.append("            <p>Generated by AdHash API Test Suite</p>\n");
        html.append("        </div>\n");
        html.append("    </div>\n");
        html.append("    \n");
        html.append("    <script>\n");
        html.append("        function toggleDetails(methodId) {\n");
        html.append("            var details = document.getElementById(methodId);\n");
        html.append("            var icon = details.parentElement.querySelector('.expand-icon');\n");
        html.append("            \n");
        html.append("            if (details.style.display === 'none') {\n");
        html.append("                details.style.display = 'block';\n");
        html.append("                icon.innerHTML = '▲';\n");
        html.append("                icon.style.transform = 'rotate(180deg)';\n");
        html.append("            } else {\n");
        html.append("                details.style.display = 'none';\n");
        html.append("                icon.innerHTML = '▼';\n");
        html.append("                icon.style.transform = 'rotate(0deg)';\n");
        html.append("            }\n");
        html.append("        }\n");
        html.append("    </script>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        
        return html.toString();
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
}
