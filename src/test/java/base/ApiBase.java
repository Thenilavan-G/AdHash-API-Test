package base;

import org.testng.annotations.BeforeClass;
import io.restassured.RestAssured;

public class ApiBase {
    
    @BeforeClass
    public void setup() {
        // Set base configuration for RestAssured
        RestAssured.useRelaxedHTTPSValidation();
        
        // Set default timeout
        RestAssured.config = RestAssured.config()
            .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", 30000)
                .setParam("http.socket.timeout", 30000));
    }
}
