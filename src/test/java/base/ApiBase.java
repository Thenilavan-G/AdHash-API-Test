package base;

import org.testng.annotations.BeforeClass;

public class ApiBase {

    @BeforeClass
    public void setup() {
        // Base setup for API tests
        System.out.println("Setting up API test environment...");
    }
}
