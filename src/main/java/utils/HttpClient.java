package utils;

import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;

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
        public String httpMethod;
        public String requestBody;
        public String responseBody;
        public String certificateError;  // SSL certificate error message

        public ApiCallDetails(String url, int statusCode, String errorMessage) {
            this.url = url;
            this.statusCode = statusCode;
            this.errorMessage = errorMessage;
            this.httpMethod = "GET";
            this.requestBody = null;
            this.responseBody = null;
            this.certificateError = null;
        }

        public ApiCallDetails(String url, int statusCode, String errorMessage, String httpMethod, String requestBody, String responseBody) {
            this.url = url;
            this.statusCode = statusCode;
            this.errorMessage = errorMessage;
            this.httpMethod = httpMethod;
            this.requestBody = requestBody;
            this.responseBody = responseBody;
            this.certificateError = null;
        }

        public ApiCallDetails(String url, int statusCode, String errorMessage, String httpMethod, String requestBody, String responseBody, String certificateError) {
            this.url = url;
            this.statusCode = statusCode;
            this.errorMessage = errorMessage;
            this.httpMethod = httpMethod;
            this.requestBody = requestBody;
            this.responseBody = responseBody;
            this.certificateError = certificateError;
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
     * Check SSL certificate validity for a URL
     * Returns error message if certificate is invalid, null if valid
     */
    private static String checkCertificateValidity(String urlString) {
        if (!urlString.startsWith("https://")) {
            return null; // Not HTTPS, no certificate to check
        }

        try {
            URL url = new URL(urlString);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.connect();

            Certificate[] certs = conn.getServerCertificates();
            if (certs != null && certs.length > 0) {
                for (Certificate cert : certs) {
                    if (cert instanceof X509Certificate) {
                        X509Certificate x509 = (X509Certificate) cert;
                        Date now = new Date();
                        Date notAfter = x509.getNotAfter();
                        Date notBefore = x509.getNotBefore();

                        // Check if certificate is expired
                        if (now.after(notAfter)) {
                            long daysExpired = (now.getTime() - notAfter.getTime()) / (1000 * 60 * 60 * 24);
                            conn.disconnect();
                            return "⚠️ SSL CERTIFICATE EXPIRED! Certificate expired " + daysExpired + " days ago (Expired: " + notAfter + ")";
                        }

                        // Check if certificate is not yet valid
                        if (now.before(notBefore)) {
                            conn.disconnect();
                            return "⚠️ SSL CERTIFICATE NOT YET VALID! Valid from: " + notBefore;
                        }

                        // Check if certificate is expiring soon (within 30 days)
                        long daysUntilExpiry = (notAfter.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
                        if (daysUntilExpiry <= 30) {
                            conn.disconnect();
                            return "⚠️ SSL CERTIFICATE EXPIRING SOON! Expires in " + daysUntilExpiry + " days (Expires: " + notAfter + ")";
                        }
                    }
                }
            }
            conn.disconnect();
            return null; // Certificate is valid
        } catch (javax.net.ssl.SSLHandshakeException e) {
            return "⚠️ SSL CERTIFICATE ERROR: " + e.getMessage();
        } catch (javax.net.ssl.SSLException e) {
            return "⚠️ SSL ERROR: " + e.getMessage();
        } catch (Exception e) {
            // Certificate check failed but don't block the API call
            return null;
        }
    }

    /**
     * Execute GET request and verify status code
     */
    public static void get(String url, int expectedStatusCode) {
        // Check SSL certificate validity first
        String certError = checkCertificateValidity(url);

        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            // Get response body (limit to prevent memory issues with large responses)
            String responseBody = getResponseBody(response);

            // Store API call details with response and certificate error
            currentApiCall.set(new ApiCallDetails(url, statusCode, null, "GET", null, responseBody, certError));

            if (statusCode != expectedStatusCode) {
                String errorMsg = "Expected status code " + expectedStatusCode + " but got " + statusCode;
                currentApiCall.set(new ApiCallDetails(url, statusCode, errorMsg, "GET", null, responseBody, certError));
                throw new AssertionError(errorMsg);
            }

        } catch (IOException e) {
            String errorMsg = "HTTP GET request failed: " + e.getMessage();
            // Check if it's an SSL certificate issue
            if (e.getMessage() != null && (e.getMessage().contains("SSL") || e.getMessage().contains("certificate") || e.getMessage().contains("PKIX"))) {
                certError = "⚠️ SSL CERTIFICATE ERROR: " + e.getMessage();
            }
            currentApiCall.set(new ApiCallDetails(url, 0, errorMsg, "GET", null, null, certError));
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Execute POST request with JSON body and verify status code
     */
    public static void post(String url, String jsonBody, int expectedStatusCode) {
        // Check SSL certificate validity first
        String certError = checkCertificateValidity(url);

        try {
            HttpPost request = new HttpPost(url);
            request.setHeader("Content-Type", "application/json");

            if (jsonBody != null && !jsonBody.isEmpty()) {
                StringEntity entity = new StringEntity(jsonBody);
                request.setEntity(entity);
            }

            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            // Get response body (limit to prevent memory issues with large responses)
            String responseBody = getResponseBody(response);

            // Store API call details with request and response and certificate error
            currentApiCall.set(new ApiCallDetails(url, statusCode, null, "POST", jsonBody, responseBody, certError));

            if (statusCode != expectedStatusCode) {
                String errorMsg = "Expected status code " + expectedStatusCode + " but got " + statusCode;
                currentApiCall.set(new ApiCallDetails(url, statusCode, errorMsg, "POST", jsonBody, responseBody, certError));
                throw new AssertionError(errorMsg);
            }

        } catch (IOException e) {
            String errorMsg = "HTTP POST request failed: " + e.getMessage();
            // Check if it's an SSL certificate issue
            if (e.getMessage() != null && (e.getMessage().contains("SSL") || e.getMessage().contains("certificate") || e.getMessage().contains("PKIX"))) {
                certError = "⚠️ SSL CERTIFICATE ERROR: " + e.getMessage();
            }
            currentApiCall.set(new ApiCallDetails(url, 0, errorMsg, "POST", jsonBody, null, certError));
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Get response body as string (limited to prevent memory issues)
     */
    private static String getResponseBody(HttpResponse response) {
        try {
            if (response.getEntity() != null) {
                String body = EntityUtils.toString(response.getEntity(), "UTF-8");
                // Limit response body to 2000 chars to prevent huge HTML pages in report
                if (body != null && body.length() > 2000) {
                    return body.substring(0, 2000) + "... [truncated]";
                }
                return body;
            }
        } catch (Exception e) {
            return "[Error reading response: " + e.getMessage() + "]";
        }
        return null;
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
