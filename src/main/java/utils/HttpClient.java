package utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Simple HTTP Client utility to replace REST Assured
 * Eliminates all Hamcrest dependencies
 */
public class HttpClient {
    
    private static CloseableHttpClient client = HttpClients.createDefault();
    
    /**
     * Execute GET request and verify status code
     */
    public static void get(String url, int expectedStatusCode) {
        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            
            if (statusCode != expectedStatusCode) {
                throw new AssertionError("Expected status code " + expectedStatusCode + " but got " + statusCode);
            }
            
            // Consume response to free resources
            EntityUtils.consume(response.getEntity());
            
        } catch (IOException e) {
            throw new RuntimeException("HTTP GET request failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Execute POST request with JSON body and verify status code
     */
    public static void post(String url, String jsonBody, int expectedStatusCode) {
        try {
            HttpPost request = new HttpPost(url);
            request.setHeader("Content-Type", "application/json");
            
            if (jsonBody != null && !jsonBody.isEmpty()) {
                StringEntity entity = new StringEntity(jsonBody);
                request.setEntity(entity);
            }
            
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            
            if (statusCode != expectedStatusCode) {
                throw new AssertionError("Expected status code " + expectedStatusCode + " but got " + statusCode);
            }
            
            // Consume response to free resources
            EntityUtils.consume(response.getEntity());
            
        } catch (IOException e) {
            throw new RuntimeException("HTTP POST request failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Close the HTTP client
     */
    public static void close() {
        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            // Ignore close errors
        }
    }
}
