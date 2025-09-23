package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Extent Reports Manager for AdHash API Test Suite
 * Generates professional HTML reports with charts and detailed test information
 */
public class ExtentReportManager {
    
    private static ExtentReports extent;
    private static ExtentTest test;
    private static String reportPath;
    
    /**
     * Initialize Extent Reports
     */
    public static void initializeReport() {
        if (extent == null) {
            // Create reports directory if it doesn't exist
            File reportsDir = new File("extent-reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }
            
            // Generate report file name with timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            reportPath = "extent-reports/AdHash_API_Report_" + timestamp + ".html";
            
            // Configure Extent Spark Reporter
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            
            // Configure report settings
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("AdHash API Test Report");
            sparkReporter.config().setReportName("AdHash API Automation Results");
            sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
            
            // Initialize ExtentReports
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            
            // Set system information
            extent.setSystemInfo("Project", "AdHash API Test Suite");
            extent.setSystemInfo("Environment", "Production");
            extent.setSystemInfo("Total APIs", "60");
            extent.setSystemInfo("Test Type", "API Automation");
            extent.setSystemInfo("Framework", "TestNG + HttpClient");
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("User", System.getProperty("user.name"));
        }
    }
    
    /**
     * Create a new test in the report
     */
    public static ExtentTest createTest(String testName, String description) {
        test = extent.createTest(testName, description);
        return test;
    }
    
    /**
     * Log test step information
     */
    public static void logInfo(String message) {
        if (test != null) {
            test.log(Status.INFO, message);
        }
    }
    
    /**
     * Log test pass
     */
    public static void logPass(String message) {
        if (test != null) {
            test.log(Status.PASS, message);
        }
    }
    
    /**
     * Log test fail
     */
    public static void logFail(String message) {
        if (test != null) {
            test.log(Status.FAIL, message);
        }
    }
    
    /**
     * Log test skip
     */
    public static void logSkip(String message) {
        if (test != null) {
            test.log(Status.SKIP, message);
        }
    }
    
    /**
     * Log test warning
     */
    public static void logWarning(String message) {
        if (test != null) {
            test.log(Status.WARNING, message);
        }
    }
    
    /**
     * Assign category to test
     */
    public static void assignCategory(String category) {
        if (test != null) {
            test.assignCategory(category);
        }
    }
    
    /**
     * Assign author to test
     */
    public static void assignAuthor(String author) {
        if (test != null) {
            test.assignAuthor(author);
        }
    }
    
    /**
     * Flush the report
     */
    public static void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }
    
    /**
     * Get the report file path
     */
    public static String getReportPath() {
        return reportPath;
    }
    
    /**
     * Get current test instance
     */
    public static ExtentTest getCurrentTest() {
        return test;
    }
    
    /**
     * End current test
     */
    public static void endTest() {
        test = null;
    }
}
