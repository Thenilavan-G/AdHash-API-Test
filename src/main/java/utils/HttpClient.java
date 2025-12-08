package utils;

import java.io.IOException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

/**
 * Simple HTTP Client utility to replace REST Assured
 * Eliminates all Hamcrest dependencies and handles SSL certificate issues
 */
public class HttpClient {

    // Timeout settings (in milliseconds)
    private static final int CONNECTION_TIMEOUT = 30000;  // 30 seconds to establish connection
    private static final int SOCKET_TIMEOUT = 60000;      // 60 seconds to wait for data
    private static final int CONNECTION_REQUEST_TIMEOUT = 30000;  // 30 seconds to get connection from pool

    private static CloseableHttpClient client = createHttpClient();

    // Thread-local storage for API call details
    private static ThreadLocal<ApiCallDetails> currentApiCall = new ThreadLocal<>();

    // Inner class to store API call details
    public static class ApiCallDetails {
        public String url;
        public int statusCode;
        public String errorMessage;

        public ApiCallDetails(String url, int statusCode, String errorMessage) {
            this.url = url;
            this.statusCode = statusCode;
            this.errorMessage = errorMessage;
        }
    }

    /**
     * Create HTTP client with SSL certificate handling and TIMEOUTS
     * Timeouts prevent the tests from hanging indefinitely on unresponsive endpoints
     */
    private static CloseableHttpClient createHttpClient() {
        try {
            // Configure request timeouts to prevent hanging
            RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECTION_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .build();

            SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(new TrustSelfSignedStrategy())
                .build();

            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                (hostname, session) -> true  // Accept all hostnames
            );

            return HttpClientBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .setDefaultRequestConfig(requestConfig)
                .build();

        } catch (Exception e) {
            // Fallback to default client with timeouts if SSL setup fails
            RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECTION_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .build();
            return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .build();
        }
    }
    
    /**
     * Execute GET request and verify status code
     */
    public static void get(String url, int expectedStatusCode) {
        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            // Store API call details
            currentApiCall.set(new ApiCallDetails(url, statusCode, null));

            if (statusCode != expectedStatusCode) {
                String errorMsg = "Expected status code " + expectedStatusCode + " but got " + statusCode;
                currentApiCall.set(new ApiCallDetails(url, statusCode, errorMsg));
                throw new AssertionError(errorMsg);
            }

            // Consume response to free resources
            EntityUtils.consume(response.getEntity());

        } catch (IOException e) {
            String errorMsg = "HTTP GET request failed: " + e.getMessage();
            currentApiCall.set(new ApiCallDetails(url, 0, errorMsg));
            throw new RuntimeException(errorMsg, e);
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

            // Store API call details
            currentApiCall.set(new ApiCallDetails(url, statusCode, null));

            if (statusCode != expectedStatusCode) {
                String errorMsg = "Expected status code " + expectedStatusCode + " but got " + statusCode;
                currentApiCall.set(new ApiCallDetails(url, statusCode, errorMsg));
                throw new AssertionError(errorMsg);
            }

            // Consume response to free resources
            EntityUtils.consume(response.getEntity());

        } catch (IOException e) {
            String errorMsg = "HTTP POST request failed: " + e.getMessage();
            currentApiCall.set(new ApiCallDetails(url, 0, errorMsg));
            throw new RuntimeException(errorMsg, e);
        }
    }
    
    /**
     * Get the current API call details for the current thread
     */
    public static ApiCallDetails getCurrentApiCallDetails() {
        return currentApiCall.get();
    }

    /**
     * Clear the current API call details for the current thread
     */
    public static void clearCurrentApiCallDetails() {
        currentApiCall.remove();
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
