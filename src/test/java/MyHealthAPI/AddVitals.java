package MyHealthAPI;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AddVitals {
    List<String> number;
    List<String> heartRateDate;
    List<String> bpDate;
    List<String> oxygenDate;
    List<String> bloodGlucoseDate;
    List<String> bloodGlucoseCurrentDate;

    public List<String> encryptedRequests = new ArrayList<>();
    public List<String> encryptedResponses = new ArrayList<>();
    public static List<String> accessTokens = new ArrayList<>();
    public List<String> hrEncryptedRequests = new ArrayList<>();
    public List<String> hrEncryptedResponses = new ArrayList<>();
    public List<String> bpEncryptedRequests = new ArrayList<>();
    public List<String> bpEncryptedResponses = new ArrayList<>();
    public List<String> oxyEncryptedRequests = new ArrayList<>();
    public List<String> oxyEncryptedResponses = new ArrayList<>();
    public List<String> bgEncryptedRequests = new ArrayList<>();
    public List<String> bgEncryptedResponses = new ArrayList<>();
    public List<String> hrDateList = new ArrayList<>();
    public List<String> bpDateList = new ArrayList<>();
    public List<String> oxyDateList = new ArrayList<>();
    public List<String> bgDateList = new ArrayList<>();
    public List<String> bgCurrentDateList = new ArrayList<>();
    public List<String> hrRandomValues = new ArrayList<>();
    public List<String> bpsRandomValues = new ArrayList<>();
    public List<String> bpdRandomValues = new ArrayList<>();
    public List<String> oxyRandomValues = new ArrayList<>();
    public List<String> bgRandomValues = new ArrayList<>();
    public List<String> bloodGlucoseMeal = new ArrayList<>();

    //Set the key value and vector value
    String key = "xfnr3PVyckouBZxW"; // 128 bit key
    String initVector = "xfnr3PVyckouBZxW"; // 16 bytes IV

    //Get the mobile numbers from Excel and generate dates automatically using IST
    @BeforeTest
    public void beforeTest() {
        PatientReqRead addPatient = new PatientReqRead();
        number = addPatient.getNumberList();

        // Generate dates automatically using IST timezone instead of reading from Excel
        generateDatesUsingIST();
    }

    //Generate dates automatically using IST timezone - CURRENT DATE ONLY with COMMON TIME
    private void generateDatesUsingIST() {
        // Define IST timezone
        ZoneId istZone = ZoneId.of("Asia/Kolkata");

        // Get current date and time in IST
        LocalDateTime currentIST = LocalDateTime.now(istZone);

        // Generate current date for all vital types with COMMON TIME (10:00 AM)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Common time for all vitals - current date at 10:00 AM
        LocalDateTime commonDateTime = currentIST.withHour(10).withMinute(0).withSecond(0).withNano(0);
        String commonDateTimeString = commonDateTime.format(formatter);

        // For Heart Rate - current date at 10:00 AM
        heartRateDate = new ArrayList<>();
        heartRateDate.add(commonDateTimeString);

        // For Blood Pressure - current date at 10:00 AM
        bpDate = new ArrayList<>();
        bpDate.add(commonDateTimeString);

        // For Oxygen - current date at 10:00 AM
        oxygenDate = new ArrayList<>();
        oxygenDate.add(commonDateTimeString);

        // For Blood Glucose - current date at 10:00 AM
        bloodGlucoseDate = new ArrayList<>();
        bloodGlucoseDate.add(commonDateTimeString);

        // For Blood Glucose Current - current date at 10:00 AM
        bloodGlucoseCurrentDate = new ArrayList<>();
        bloodGlucoseCurrentDate.add(commonDateTimeString);

        System.out.println("Generated dates using IST timezone for CURRENT DATE ONLY with COMMON TIME:");
        System.out.println("Heart Rate date: " + heartRateDate.get(0));
        System.out.println("Blood Pressure date: " + bpDate.get(0));
        System.out.println("Oxygen date: " + oxygenDate.get(0));
        System.out.println("Blood Glucose date: " + bloodGlucoseDate.get(0));
        System.out.println("Blood Glucose Current date: " + bloodGlucoseCurrentDate.get(0));
    }

    @BeforeClass
    public void beforeClass() {
        // Define IST timezone
        ZoneId istZone = ZoneId.of("Asia/Kolkata");

        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        System.out.println("heartRateDate : " + heartRateDate);
        System.out.println("heartRateDate Size: " + heartRateDate.size());

        // Generate ONE timestamp for Heart Rate for current date
        LocalDateTime hrDateTime = LocalDateTime.parse(heartRateDate.get(0), formatter);
        long hrTimestamp = hrDateTime.atZone(istZone).toEpochSecond();
        System.out.println("HR Timestamp for current date: " + hrTimestamp);
        hrDateList.add(String.valueOf(hrTimestamp));

        System.out.println("HR Date List :" + hrDateList);
        System.out.println("HR Date Size :" + hrDateList.size());

        // Generate ONE timestamp for Blood Pressure for current date
        LocalDateTime bpDateTime = LocalDateTime.parse(bpDate.get(0), formatter);
        long bpTimestamp = bpDateTime.atZone(istZone).toEpochSecond();
        System.out.println("BP Timestamp for current date: " + bpTimestamp);
        bpDateList.add(String.valueOf(bpTimestamp));

        System.out.println("BP Date List :" + bpDateList);
        System.out.println("BP Date Size :" + bpDateList.size());

        // Generate ONE timestamp for Oxygen for current date
        LocalDateTime oxyDateTime = LocalDateTime.parse(oxygenDate.get(0), formatter);
        long oxyTimestamp = oxyDateTime.atZone(istZone).toEpochSecond();
        System.out.println("OXY Timestamp for current date: " + oxyTimestamp);
        oxyDateList.add(String.valueOf(oxyTimestamp));

        System.out.println("OXY Date List :" + oxyDateList);
        System.out.println("OXY Date Size :" + oxyDateList.size());

        // Generate ONE timestamp for Blood Glucose for current date
        LocalDateTime bgDateTime = LocalDateTime.parse(bloodGlucoseDate.get(0), formatter);
        long bgTimestamp = bgDateTime.atZone(istZone).toEpochSecond();
        System.out.println("BG Timestamp for current date: " + bgTimestamp);
        bgDateList.add(String.valueOf(bgTimestamp));

        // Generate ONE timestamp for Blood Glucose Current for current date
        LocalDateTime bgCurrentDateTime = LocalDateTime.parse(bloodGlucoseCurrentDate.get(0), formatter);
        long bgCurrentTimestamp = bgCurrentDateTime.atZone(istZone).toEpochSecond();
        System.out.println("BG-C Timestamp for current date: " + bgCurrentTimestamp);
        bgCurrentDateList.add(String.valueOf(bgCurrentTimestamp));

        System.out.println("BG Date List :" + bgDateList);
        System.out.println("BG Date Size :" + bgDateList.size());

        System.out.println("BG-C Date List :" + bgCurrentDateList);
        System.out.println("BG-C Date Size :" + bgCurrentDateList.size());

    }

    //Decrypt the encrypted list
    @Test(priority = 1)
    public void loginEncReq() {
        //Iterate the rows based on post id size
        for (int i = 1; i <= number.size(); i++) {
            //Decrypt the encrypted list using keys
            String encryptedRequest = encrypt(key, initVector, "{\n" +
                    "\t\"fcmtoken\": \"f2QibZmCuEhevrIhsqIwc9:APA91bG_s6xWQ9UKFNVE4an3YJplXaLxJjgQzREVx5atPBu4SAIY9SD-_RME3P9oz75AMzLEDTjt9wy6CTGXX376HJMPpv_gVKd7cYpS5qBe_r3ACHff2icFOp3Dq-oeaMYSr0AQJozb\",\n" +
                    "\t\"devicetype\": \"1\",\n" +
                    "\t\"osversion\": \"16.2\",\n" +
                    "\t\"isVerified\": \"1\",\n" +
                    "\t\"mobilenumber\": \"" + number.get(i - 1) + "\",\n" +
                    "\t\"devicetoken\": \"\",\n" +
                    "\t\"appversion\": \"1.0\",\n" +
                    "\t\"manufacturer\": \"Apple\",\n" +
                    "\t\"fname\": \"\",\n" +
                    "\t\"lname\": \"\",\n" +
                    "\t\"profilepic\": \"\",\n" +
                    "\t\"email\": \"\",\n" +
                    "\t\"buildversion\": \"1.0.1.0\"\n" +
                    "}"
            );

            // Print or use the encrypted request
            System.out.println("Encrypted request: " + encryptedRequest);

            // Store the encrypted request in the list
            encryptedRequests.add(encryptedRequest);
        }
    }

    //Call the add clap api
    @Test(priority = 2)
    public void loginEncRes() {
        //Iterate the encrypted list based on size
        for (int i = 1; i <= encryptedRequests.size(); i++) {
            //Using the restAssured framework
            RequestSpecification request = RestAssured.given();
            RestAssured.useRelaxedHTTPSValidation();

            //Set the request parameters
            request.header("accept", "*/*");
            request.header("Content-Type", "application/json");
            request.header("Authorization", "Bearer ");
            request.body("{\"type\":\" " + encryptedRequests.get(i - 1) + " \"}");

            //Call the API and store the response
            Response response = request.post("https://mobileapi.kaicare.ai/api/Login");

            //Print the response body
            System.out.println("Encrypted status code: " + i + " : " + response.getStatusCode());
            System.out.println("Encrypted response: " + i + " : " + response.getBody().asString());

            // Store the encrypted request in the list
            encryptedResponses.add(response.getBody().asString());
        }

        System.out.println("Response list: " + encryptedResponses);
        System.out.println("Response size: " + encryptedResponses.size());
    }

/*
    @Test(priority = 3)
    public void loginDecRes() {
        //Iterate the rows based on encrypted list
        for (int i = 1; i <= encryptedResponses.size(); i++) {
            //Store each encrypted value
            String encryptedString = encryptedResponses.get(i - 1);
            //Decrypt the stored encrypted value using keys
            String decryptedString = decrypt(encryptedString, key, initVector);
            //System.out.println("Decrypted : " + decryptedString);

            assert decryptedString != null;
            JsonObject jsonObject = JsonParser.parseString(decryptedString).getAsJsonObject();
            String accessToken = jsonObject.getAsJsonObject("Value")
                    .getAsJsonObject("data")
                    .get("accessToken")
                    .getAsString();

            //Print the response body
            System.out.println("Access token: " + i + " : " + accessToken);

            //Store the encrypted request in the list
            accessTokens.add(accessToken);
        }

        System.out.println("Access tokens list: " + accessTokens);
        System.out.println("Access tokens size: " + accessTokens.size());
    }
*/


    @Test(priority = 3)
    public void loginDecRes() {
        // iterate safely with 0-based index
        for (int idx = 0; idx < encryptedResponses.size(); idx++) {
            String encrypted = encryptedResponses.get(idx);

            System.out.println("Processing response " + idx + ": " + encrypted);

            // First check if the response is already JSON (not encrypted)
            String decrypted = null;
            JsonElement parsed = null;

            try {
                // Try to parse as JSON directly first
                parsed = JsonParser.parseString(encrypted);
                decrypted = encrypted; // It's already decrypted
                System.out.println("Response " + idx + " is already in JSON format");
            } catch (JsonSyntaxException e) {
                // If it's not JSON, try to decrypt it
                System.out.println("Response " + idx + " appears to be encrypted, attempting decryption");
                decrypted = decrypt(encrypted, key, initVector);

                if (decrypted != null) {
                    try {
                        parsed = JsonParser.parseString(decrypted);
                    } catch (JsonSyntaxException e2) {
                        Assert.fail("Decrypted text is not valid JSON for item index " + idx + ":\n" + decrypted, e2);
                        return;
                    }
                } else {
                    Assert.fail("Decryption returned null for item index " + idx + ". Original response: " + encrypted);
                    return;
                }
            }

            JsonObject root = parsed.isJsonObject() ? parsed.getAsJsonObject() : new JsonObject();

            // Try several known locations / key aliases
            String accessToken = findToken(root);

            Assert.assertNotNull(
                    accessToken,
                    "Could not find access token for item index " + idx + ". JSON:\n" + decrypted
            );

            System.out.println("Access token: " + (idx + 1) + " : " + accessToken);
            accessTokens.add(accessToken);
        }

        System.out.println("Access tokens list: " + accessTokens);
        System.out.println("Access tokens size: " + accessTokens.size());
    }

    /* ---------- helpers ---------- */

    // Finds the token at top-level, under data/Data, or under Value/value (and Value.data)
    private static String findToken(JsonObject root) {
        String t;

        // top-level: { "accessToken": "..."} or { "token": "..."} or { "jwt": "..." }
        if ((t = getString(root, "accessToken", "token", "jwt")) != null) return t;

        // { "data": { "accessToken": "..." } }
        JsonObject data = getObject(root, "data", "Data");
        if (data != null && (t = getString(data, "accessToken", "token", "jwt")) != null) return t;

        // { "Value": { "accessToken": "..." } } or { "Value": { "data": { "accessToken": "..." } } }
        JsonObject value = getObject(root, "Value", "value");
        if (value != null) {
            if ((t = getString(value, "accessToken", "token", "jwt")) != null) return t;
            JsonObject vdata = getObject(value, "data", "Data");
            if (vdata != null && (t = getString(vdata, "accessToken", "token", "jwt")) != null) return t;
        }

        return null;
    }

    private static JsonObject getObject(JsonObject parent, String... names) {
        if (parent == null) return null;
        for (String n : names) {
            if (parent.has(n) && parent.get(n).isJsonObject()) return parent.getAsJsonObject(n);
        }
        return null;
    }

    private static String getString(JsonObject parent, String... names) {
        if (parent == null) return null;
        for (String n : names) {
            if (parent.has(n) && parent.get(n).isJsonPrimitive()) return parent.get(n).getAsString();
        }
        return null;
    }

    //Decrypt the encrypted list
    // @Test(priority = 4)
    public void hrEncReq() {
        //Set the key value and vector value
        String key = "xfnr3PVyckouBZxW"; // 128 bit key
        String initVector = "xfnr3PVyckouBZxW"; // 16 bytes IV

        System.out.println("hrDateList : " + hrDateList);
        System.out.println("hrDateList : " + hrDateList.size());

/*
        //Iterate the rows based on post id size
        for (int i = 1; i <= hrDateList.size(); i++) {
            //Decrypt the encrypted list using keys
            String encryptedRequest = encrypt(key, initVector, "{\n" +
                    "\t\"timeOfCheck\": 0,\n" +
                    "\t\"currentDate\": 0,\n" +
                    "\t\"filterType\": [1],\n" +
                    "\t\"type\": 0,\n" +
                    "\t\"value\": [\"" + hrRandomValues.get(i - 1) + "\"],\n" +
                    "\t\"vitalId\": 2,\n" +
                    "\t\"date\": " + hrDateList.get(i - 1) + "\n" +
                    "}"
            );
*/

        for (int j = 1; j <= accessTokens.size(); j++) {

            //Iterate the rows based on post id size
            for (int i = 1; i <= hrDateList.size(); i++) {

                Random random = new Random();
                int randomValue = random.nextInt(111) + 60; // Generates random integer between 0 and 110, then adds 60
                String stringValue = String.valueOf(randomValue); // Convert integer to string
                //hrRandomValues.add(stringValue);
                System.out.println("HR String Value: " + i + " : " + stringValue);

                //Decrypt the encrypted list using keys
                String encryptedRequest = encrypt(key, initVector, "{\n" +
                        "\t\"timeOfCheck\": 0,\n" +
                        "\t\"currentDate\": 0,\n" +
                        "\t\"filterType\": [1],\n" +
                        "\t\"type\": 0,\n" +
                        "\t\"value\": [\"" + stringValue + "\"],\n" +
                        "\t\"vitalId\": 2,\n" +
                        "\t\"date\": " + hrDateList.get(i - 1) + "\n" +
                        "}"
                );

                // Print or use the encrypted request
                System.out.println("HR Encrypted request: " + encryptedRequest);

                // Store the encrypted request in the list
                hrEncryptedRequests.add(encryptedRequest);
            }
        }
    }

    //Call the add clap api
    @Test(priority = 5)
    public void hrEncRes() {

        System.out.println("hrDateList : " + hrDateList);
        System.out.println("hrDateList : " + hrDateList.size());

        //Iterate the encrypted list based on size
        for (int j = 1; j <= accessTokens.size(); j++) {

            for (int i = 1; i <= hrDateList.size(); i++) {

                Random random = new Random();
                int randomValue = random.nextInt(111) + 60; // Generates random integer between 0 and 110, then adds 60
                String stringValue = String.valueOf(randomValue); // Convert integer to string
                //hrRandomValues.add(stringValue);
                System.out.println("HR String Value: " + i + " : " + stringValue);

                //Decrypt the encrypted list using keys
                String encryptedRequest = encrypt(key, initVector, "{\n" +
                        "\t\"timeOfCheck\": 0,\n" +
                        "\t\"currentDate\": 0,\n" +
                        "\t\"filterType\": [1],\n" +
                        "\t\"type\": 0,\n" +
                        "\t\"value\": [\"" + stringValue + "\"],\n" +
                        "\t\"vitalId\": 2,\n" +
                        "\t\"date\": " + hrDateList.get(i - 1) + "\n" +
                        "}"
                );

                // Print or use the encrypted request
                System.out.println("HR Encrypted request: " + encryptedRequest);

                // Store the encrypted request in the list
                //hrEncryptedRequests.add(encryptedRequest);

                //Using the restAssured framework
                RequestSpecification request = RestAssured.given();
                RestAssured.useRelaxedHTTPSValidation();

                //Set the request parameters
                request.header("accept", "*/*");
                request.header("Content-Type", "application/json");
                request.header("Authorization", "Bearer " + accessTokens.get(j - 1));

                //for (int i = 1; i <= hrEncryptedRequests.size(); i++) {
                //request.body("{\"type\":\" " + hrEncryptedRequests.get(i - 1) + " \"}");
                request.body("{\"type\":\" " + encryptedRequest + " \"}");

                //Call the API and store the response
                Response response = request.post("https://mobileapi.kaicare.ai/api/Vital/saveVital");

                //Print the response body
                System.out.println("HR Encrypted status code: " + i + " : " + response.getStatusCode());
                System.out.println("HR Encrypted response: " + i + " : " + response.getBody().asString());

                // Store the encrypted request in the list
                hrEncryptedResponses.add(response.getBody().asString());
                //}
            }
        }

        System.out.println("HR Response list: " + hrEncryptedResponses);
        System.out.println("HR Response size: " + hrEncryptedResponses.size());
    }

    //@Test(priority = 6)
    public void bpEncReq() {
        for (int j = 1; j <= accessTokens.size(); j++) {

            //Iterate the rows based on post id size
            for (int i = 1; i <= bpDateList.size(); i++) {

                Random random1 = new Random();
                int randomValue = random1.nextInt(81) + 100; // Generates random integer between 0 and 80, then adds 100
                String stringValueS = String.valueOf(randomValue); // Convert integer to string
                //bpsRandomValues.add(stringValueS);
                System.out.println("BP String ValueS: " + i + " : " + stringValueS);

                Random random2 = new Random();
                int randomValue1 = random2.nextInt(51) + 60; // Generates random integer between 0 and 50, then adds 60
                String stringValueD = String.valueOf(randomValue1); // Convert integer to string
                //bpdRandomValues.add(stringValueD);
                System.out.println("BP String ValueD: " + i + " : " + stringValueD);

/*
            //Decrypt the encrypted list using keys
            String encryptedRequest = encrypt(key, initVector, "{\n" +
                    "\t\"timeOfCheck\": 0,\n" +
                    "\t\"currentDate\": 0,\n" +
                    "\t\"filterType\": [1],\n" +
                    "\t\"type\": 0,\n" +
                    "\t\"value\": [\" " + bpsRandomValues.get(i - 1) + "/" + bpdRandomValues.get(i - 1) + " \"],\n" +
                    "\t\"vitalId\": 5,\n" +
                    "\t\"date\": " + bpDateList.get(i - 1) + "\n" +
                    "}"
            );
*/

                //Decrypt the encrypted list using keys
                String encryptedRequest = encrypt(key, initVector, "{\n" +
                        "\t\"timeOfCheck\": 0,\n" +
                        "\t\"currentDate\": 0,\n" +
                        "\t\"filterType\": [1],\n" +
                        "\t\"type\": 0,\n" +
                        "\t\"value\": [\" " + stringValueS + "/" + stringValueD + " \"],\n" +
                        "\t\"vitalId\": 5,\n" +
                        "\t\"date\": " + bpDateList.get(i - 1) + "\n" +
                        "}"
                );

                // Print or use the encrypted request
                System.out.println("BP Encrypted request: " + encryptedRequest);

                // Store the encrypted request in the list
                bpEncryptedRequests.add(encryptedRequest);
            }
        }
    }

    //Call the add clap api
    @Test(priority = 7)
    public void bpEncRes() {
        //Iterate the encrypted list based on size
        for (int j = 1; j <= accessTokens.size(); j++) {

            //Iterate the rows based on post id size
            for (int i = 1; i <= bpDateList.size(); i++) {

                Random random1 = new Random();
                //int randomValue = random1.nextInt(81) + 100; // Generates random integer between 0 and 80, then adds 100
                int randomValue = random1.nextInt(41) + 140; // Generates random integer between 0 and 80, then adds 100
                String stringValueS = String.valueOf(randomValue); // Convert integer to string
                //bpsRandomValues.add(stringValueS);
                System.out.println("BP String ValueS: " + i + " : " + stringValueS);

                Random random2 = new Random();
                //int randomValue1 = random2.nextInt(51) + 60; // Generates random integer between 0 and 50, then adds 60
                int randomValue1 = random2.nextInt(21) + 90; // Generates random integer between 0 and 50, then adds 60
                String stringValueD = String.valueOf(randomValue1); // Convert integer to string
                //bpdRandomValues.add(stringValueD);
                System.out.println("BP String ValueD: " + i + " : " + stringValueD);

                //Decrypt the encrypted list using keys
                String encryptedRequest = encrypt(key, initVector, "{\n" +
                        "\t\"timeOfCheck\": 0,\n" +
                        "\t\"currentDate\": 0,\n" +
                        "\t\"filterType\": [1],\n" +
                        "\t\"type\": 0,\n" +
                        "\t\"value\": [\" " + stringValueS + "/" + stringValueD + " \"],\n" +
                        "\t\"vitalId\": 5,\n" +
                        "\t\"date\": " + bpDateList.get(i - 1) + "\n" +
                        "}"
                );

                // Print or use the encrypted request
                System.out.println("BP Encrypted request: " + encryptedRequest);

                // Store the encrypted request in the list
                //bpEncryptedRequests.add(encryptedRequest);

                //Using the restAssured framework
                RequestSpecification request = RestAssured.given();
                RestAssured.useRelaxedHTTPSValidation();

                //Set the request parameters
                request.header("accept", "*/*");
                request.header("Content-Type", "application/json");
                request.header("Authorization", "Bearer " + accessTokens.get(j - 1));

                //for (int i = 1; i <= bpEncryptedRequests.size(); i++) {
                //request.body("{\"type\":\" " + bpEncryptedRequests.get(i - 1) + " \"}");
                request.body("{\"type\":\" " + encryptedRequest + " \"}");

                //Call the API and store the response
                Response response = request.post("https://mobileapi.kaicare.ai/api/Vital/saveVital");

                //Print the response body
                System.out.println("BP Encrypted status code: " + i + " : " + response.getStatusCode());
                System.out.println("BP Encrypted response: " + i + " : " + response.getBody().asString());

                // Store the encrypted request in the list
                bpEncryptedResponses.add(response.getBody().asString());
            }
        }

        System.out.println("BP Response list: " + bpEncryptedResponses);
        System.out.println("BP Response size: " + bpEncryptedResponses.size());
    }

    //@Test(priority = 8)
    public void oxyEncReq() {
        for (int j = 1; j <= accessTokens.size(); j++) {

            //Iterate the rows based on post id size
            for (int i = 1; i <= oxyDateList.size(); i++) {

                // Create an instance of the Random class
                Random random3 = new Random();
                int randomValue = random3.nextInt(11) + 90; // Generates random integer between 0 and 10, then adds 90
                String stringValue = String.valueOf(randomValue); // Convert integer to string
                //oxyRandomValues.add(stringValue);
                System.out.println("Oxy String Value: " + i + " : " + stringValue);

/*
            //Decrypt the encrypted list using keys
            String encryptedRequest = encrypt(key, initVector, "{\n" +
                    "\t\"timeOfCheck\": 0,\n" +
                    "\t\"currentDate\": 0,\n" +
                    "\t\"filterType\": [1],\n" +
                    "\t\"type\": 0,\n" +
                    "\t\"value\": [\"" + oxyRandomValues.get(i - 1) + "\"],\n" +
                    "\t\"vitalId\": 4,\n" +
                    "\t\"date\": " + oxyDateList.get(i - 1) + "\n" +
                    "}"
            );
*/

                //Decrypt the encrypted list using keys
                String encryptedRequest = encrypt(key, initVector, "{\n" +
                        "\t\"timeOfCheck\": 0,\n" +
                        "\t\"currentDate\": 0,\n" +
                        "\t\"filterType\": [1],\n" +
                        "\t\"type\": 0,\n" +
                        "\t\"value\": [\"" + stringValue + "\"],\n" +
                        "\t\"vitalId\": 4,\n" +
                        "\t\"date\": " + oxyDateList.get(i - 1) + "\n" +
                        "}"
                );

                // Print or use the encrypted request
                System.out.println("OXY Encrypted request: " + encryptedRequest);

                // Store the encrypted request in the list
                oxyEncryptedRequests.add(encryptedRequest);
            }
        }
    }

    //Call the add clap api
    @Test(priority = 9)
    public void oxyEncRes() {
        //Iterate the encrypted list based on size
        for (int j = 1; j <= accessTokens.size(); j++) {

            for (int i = 1; i <= oxyDateList.size(); i++) {

                // Create an instance of the Random class
                Random random3 = new Random();
                int randomValue = random3.nextInt(11) + 90; // Generates random integer between 0 and 10, then adds 90
                String stringValue = String.valueOf(randomValue); // Convert integer to string
                //oxyRandomValues.add(stringValue);
                System.out.println("Oxy String Value: " + i + " : " + stringValue);

                //Decrypt the encrypted list using keys
                String encryptedRequest = encrypt(key, initVector, "{\n" +
                        "\t\"timeOfCheck\": 0,\n" +
                        "\t\"currentDate\": 0,\n" +
                        "\t\"filterType\": [1],\n" +
                        "\t\"type\": 0,\n" +
                        "\t\"value\": [\"" + stringValue + "\"],\n" +
                        "\t\"vitalId\": 4,\n" +
                        "\t\"date\": " + oxyDateList.get(i - 1) + "\n" +
                        "}"
                );

                // Print or use the encrypted request
                System.out.println("OXY Encrypted request: " + encryptedRequest);

                // Store the encrypted request in the list
                //oxyEncryptedRequests.add(encryptedRequest);

                //Using the restAssured framework
                RequestSpecification request = RestAssured.given();
                RestAssured.useRelaxedHTTPSValidation();

                //Set the request parameters
                request.header("accept", "*/*");
                request.header("Content-Type", "application/json");
                request.header("Authorization", "Bearer " + accessTokens.get(j - 1));

                //for (int i = 1; i <= oxyEncryptedRequests.size(); i++) {
                //request.body("{\"type\":\" " + oxyEncryptedRequests.get(i - 1) + " \"}");
                request.body("{\"type\":\" " + encryptedRequest + " \"}");

                //Call the API and store the response
                Response response = request.post("https://mobileapi.kaicare.ai/api/Vital/saveVital");

                //Print the response body
                System.out.println("OXY Encrypted status code: " + i + " : " + response.getStatusCode());
                System.out.println("OXY Encrypted response: " + i + " : " + response.getBody().asString());

                // Store the encrypted request in the list
                oxyEncryptedResponses.add(response.getBody().asString());
            }
        }

        System.out.println("OXY Response list: " + oxyEncryptedResponses);
        System.out.println("OXY Response size: " + oxyEncryptedResponses.size());
    }

   // @Test(priority = 10)
    public void bgEncReq() {
        for (int j = 1; j <= accessTokens.size(); j++) {

            //Iterate the rows based on post id size
            for (int i = 1; i <= bgDateList.size(); i++) {

                Random random4 = new Random();
                int randomValue = random4.nextInt(201) + 100; // Generates random integer between 0 and 200, then adds 100
                String stringValue = String.valueOf(randomValue); // Convert integer to string
                //bgRandomValues.add(stringValue);
                System.out.println("BG String Value: " + i + " : " + stringValue);

                // Given timestamp
                long timestamp = Long.parseLong(bgDateList.get(i - 1));
                System.out.println("BG-E Long: " + i + " : " + timestamp);

                // Convert the timestamp to a ZonedDateTime object
                ZonedDateTime zonedDateTime = Instant.ofEpochSecond(timestamp)
                        .atZone(ZoneId.systemDefault());

                // Define the date-time format
                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                // Format the ZonedDateTime object using the formatter
                String formattedDateTime = zonedDateTime.format(formatter1);

                // Print the formatted date-time
                System.out.println("BG-E Formatted date-time: " + formattedDateTime);

                // Parse the formatted date-time string to a LocalDateTime object
                LocalDateTime localDateTime = LocalDateTime.parse(formattedDateTime, formatter1);

                // Extract the time (hh:mm) from the LocalDateTime object
                LocalTime localTime = localDateTime.toLocalTime();

                // Format the extracted time using a time formatter
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                String extractedTime = localTime.format(timeFormatter);

                // Print the extracted time
                System.out.println("BG-E Extracted time: " + extractedTime);

                // Parse the extracted time
                LocalTime parsedTime = LocalTime.parse(extractedTime);

                // Reduce 4 hours from the extracted time
                LocalTime reducedTime = parsedTime.plusHours(28);

                // Output the reduced time
                System.out.println("Reduced time: " + reducedTime);

                // Parse the boundaries
                LocalTime startTime = LocalTime.of(6, 0); // 06:00
                LocalTime endTime = LocalTime.of(9, 30);  // 09:30

                // Check if the extracted time is between the boundaries
                boolean isBetween = !reducedTime.isBefore(startTime) && !reducedTime.isAfter(endTime);
                //boolean isBetween = !parsedTime.isBefore(startTime) && !parsedTime.isAfter(endTime);

                // Output the result
                if (isBetween) {
                    System.out.println("The extracted time is between 06:00 and 09:30.");
                    bloodGlucoseMeal.add(0, "1");

/*
                //Decrypt the encrypted list using keys
                String encryptedRequest = encrypt(key, initVector, "{\n" +
                        "\t\"timeOfCheck\": 0,\n" +
                        "\t\"currentDate\": " + bgCurrentDateList.get(i - 1) + ",\n" +
                        "\t\"filterType\": [1],\n" +
                        "\t\"type\": 0,\n" +
                        "\t\"value\": [\"" + bgRandomValues.get(i - 1) + "\"],\n" +
                        "\t\"glucoseId\": " + bloodGlucoseMeal.get(0) + ",\n" +
                        "\t\"vitalId\": 7,\n" +
                        "\t\"date\": " + bgDateList.get(i - 1) + "\n" +
                        "}"
                );
*/

                    //Decrypt the encrypted list using keys
                    String encryptedRequest = encrypt(key, initVector, "{\n" +
                            "\t\"timeOfCheck\": 0,\n" +
                            "\t\"currentDate\": " + bgCurrentDateList.get(i - 1) + ",\n" +
                            "\t\"filterType\": [1],\n" +
                            "\t\"type\": 0,\n" +
                            "\t\"value\": [\"" + stringValue + "\"],\n" +
                            "\t\"glucoseId\": " + bloodGlucoseMeal.get(0) + ",\n" +
                            "\t\"vitalId\": 7,\n" +
                            "\t\"date\": " + bgDateList.get(i - 1) + "\n" +
                            "}"
                    );

                    // Print or use the encrypted request
                    System.out.println("BG Encrypted request: " + encryptedRequest);

                    // Store the encrypted request in the list
                    bgEncryptedRequests.add(encryptedRequest);

                } else {
                    System.out.println("The extracted time is not between 06:00 and 09:30.");
                    // Parse the boundaries
                    LocalTime startTime1 = LocalTime.of(11, 0); // 06:00
                    LocalTime endTime1 = LocalTime.of(14, 0);  // 09:30

                    // Parse the extracted time
                    LocalTime parsedTime1 = LocalTime.parse(extractedTime);

                    // Reduce 4 hours from the extracted time
                    LocalTime reducedTime1 = parsedTime1.plusHours(28);

                    // Output the reduced time
                    System.out.println("Reduced time 1: " + reducedTime1);

                    // Check if the extracted time is between the boundaries
                    boolean isBetween1 = !reducedTime1.isBefore(startTime1) && !reducedTime1.isAfter(endTime1);
                    //boolean isBetween1 = !parsedTime1.isBefore(startTime1) && !parsedTime1.isAfter(endTime1);

                    // Output the result
                    if (isBetween1) {
                        System.out.println("The extracted time is between 11:00 and 14:00.");
                        bloodGlucoseMeal.add(0, "2");

/*
                    //Decrypt the encrypted list using keys
                    String encryptedRequest = encrypt(key, initVector, "{\n" +
                            "\t\"timeOfCheck\": 0,\n" +
                            "\t\"currentDate\": " + bgCurrentDateList.get(i - 1) + ",\n" +
                            "\t\"filterType\": [1],\n" +
                            "\t\"type\": 0,\n" +
                            "\t\"value\": [\"" + bgRandomValues.get(i - 1) + "\"],\n" +
                            "\t\"glucoseId\": " + bloodGlucoseMeal.get(0) + ",\n" +
                            "\t\"vitalId\": 7,\n" +
                            "\t\"date\": " + bgDateList.get(i - 1) + "\n" +
                            "}"
                    );
*/

                        //Decrypt the encrypted list using keys
                        String encryptedRequest = encrypt(key, initVector, "{\n" +
                                "\t\"timeOfCheck\": 0,\n" +
                                "\t\"currentDate\": " + bgCurrentDateList.get(i - 1) + ",\n" +
                                "\t\"filterType\": [1],\n" +
                                "\t\"type\": 0,\n" +
                                "\t\"value\": [\"" + stringValue + "\"],\n" +
                                "\t\"glucoseId\": " + bloodGlucoseMeal.get(0) + ",\n" +
                                "\t\"vitalId\": 7,\n" +
                                "\t\"date\": " + bgDateList.get(i - 1) + "\n" +
                                "}"
                        );

                        // Print or use the encrypted request
                        System.out.println("BG Encrypted request: " + encryptedRequest);

                        // Store the encrypted request in the list
                        bgEncryptedRequests.add(encryptedRequest);

                    } else {
                        System.out.println("The extracted time is not between 11:00 and 14:00.");

                        // Parse the boundaries
                        LocalTime startTime2 = LocalTime.of(17, 0); // 06:00
                        LocalTime endTime2 = LocalTime.of(19, 30);  // 09:30

                        // Parse the extracted time
                        LocalTime parsedTime2 = LocalTime.parse(extractedTime);

                        // Reduce 4 hours from the extracted time
                        LocalTime reducedTime2 = parsedTime2.plusHours(28);

                        // Output the reduced time
                        System.out.println("Reduced time 2: " + reducedTime2);

                        // Check if the extracted time is between the boundaries
                        boolean isBetween2 = !reducedTime2.isBefore(startTime2) && !reducedTime2.isAfter(endTime2);
                        //boolean isBetween2 = !parsedTime2.isBefore(startTime2) && !parsedTime2.isAfter(endTime2);

                        // Output the result
                        if (isBetween2) {
                            System.out.println("The extracted time is between 17:00 and 19:30.");
                            bloodGlucoseMeal.add(0, "3");

/*
                        //Decrypt the encrypted list using keys
                        String encryptedRequest = encrypt(key, initVector, "{\n" +
                                "\t\"timeOfCheck\": 0,\n" +
                                "\t\"currentDate\": " + bgCurrentDateList.get(i - 1) + ",\n" +
                                "\t\"filterType\": [1],\n" +
                                "\t\"type\": 0,\n" +
                                "\t\"value\": [\"" + bgRandomValues.get(i - 1) + "\"],\n" +
                                "\t\"glucoseId\": " + bloodGlucoseMeal.get(0) + ",\n" +
                                "\t\"vitalId\": 7,\n" +
                                "\t\"date\": " + bgDateList.get(i - 1) + "\n" +
                                "}"
                        );
*/

                            //Decrypt the encrypted list using keys
                            String encryptedRequest = encrypt(key, initVector, "{\n" +
                                    "\t\"timeOfCheck\": 0,\n" +
                                    "\t\"currentDate\": " + bgCurrentDateList.get(i - 1) + ",\n" +
                                    "\t\"filterType\": [1],\n" +
                                    "\t\"type\": 0,\n" +
                                    "\t\"value\": [\"" + stringValue + "\"],\n" +
                                    "\t\"glucoseId\": " + bloodGlucoseMeal.get(0) + ",\n" +
                                    "\t\"vitalId\": 7,\n" +
                                    "\t\"date\": " + bgDateList.get(i - 1) + "\n" +
                                    "}"
                            );

                            // Print or use the encrypted request
                            System.out.println("BG Encrypted request: " + encryptedRequest);

                            // Store the encrypted request in the list
                            bgEncryptedRequests.add(encryptedRequest);

                        } else {
                            System.out.println("The extracted time is not between 17:00 and 19:30.");

                            // Parse the boundaries
                            LocalTime startTime3 = LocalTime.of(21, 0); // 06:00
                            LocalTime endTime3 = LocalTime.of(23, 30);  // 09:30

                            // Parse the extracted time
                            LocalTime parsedTime3 = LocalTime.parse(extractedTime);

                            // Reduce 4 hours from the extracted time
                            LocalTime reducedTime3 = parsedTime3.plusHours(28);

                            // Output the reduced time
                            System.out.println("Reduced time 3: " + reducedTime3);

                            // Check if the extracted time is between the boundaries
                            boolean isBetween3 = !reducedTime3.isBefore(startTime3) && !reducedTime3.isAfter(endTime3);
                            //boolean isBetween3 = !parsedTime3.isBefore(startTime3) && !parsedTime3.isAfter(endTime3);

                            // Output the result
                            if (isBetween3) {
                                System.out.println("The extracted time is between 21:00 and 23:00.");
                                bloodGlucoseMeal.add(0, "4");

/*
                            //Decrypt the encrypted list using keys
                            String encryptedRequest = encrypt(key, initVector, "{\n" +
                                    "\t\"timeOfCheck\": 0,\n" +
                                    "\t\"currentDate\": " + bgCurrentDateList.get(i - 1) + ",\n" +
                                    "\t\"filterType\": [1],\n" +
                                    "\t\"type\": 0,\n" +
                                    "\t\"value\": [\"" + bgRandomValues.get(i - 1) + "\"],\n" +
                                    "\t\"glucoseId\": " + bloodGlucoseMeal.get(0) + ",\n" +
                                    "\t\"vitalId\": 7,\n" +
                                    "\t\"date\": " + bgDateList.get(i - 1) + "\n" +
                                    "}"
                            );
*/

                                //Decrypt the encrypted list using keys
                                String encryptedRequest = encrypt(key, initVector, "{\n" +
                                        "\t\"timeOfCheck\": 0,\n" +
                                        "\t\"currentDate\": " + bgCurrentDateList.get(i - 1) + ",\n" +
                                        "\t\"filterType\": [1],\n" +
                                        "\t\"type\": 0,\n" +
                                        "\t\"value\": [\"" + stringValue + "\"],\n" +
                                        "\t\"glucoseId\": " + bloodGlucoseMeal.get(0) + ",\n" +
                                        "\t\"vitalId\": 7,\n" +
                                        "\t\"date\": " + bgDateList.get(i - 1) + "\n" +
                                        "}"
                                );

                                // Print or use the encrypted request
                                System.out.println("BG Encrypted request: " + encryptedRequest);

                                // Store the encrypted request in the list
                                bgEncryptedRequests.add(encryptedRequest);

                            } else {
                                System.out.println("The extracted time is not between 21:00 and 23:00.");

                                bloodGlucoseMeal.add(0, "5");

/*
                            //Decrypt the encrypted list using keys
                            String encryptedRequest = encrypt(key, initVector, "{\n" +
                                    "\t\"timeOfCheck\": 0,\n" +
                                    "\t\"currentDate\": " + bgCurrentDateList.get(i - 1) + ",\n" +
                                    "\t\"filterType\": [1],\n" +
                                    "\t\"type\": 0,\n" +
                                    "\t\"value\": [\"" + bgRandomValues.get(i - 1) + "\"],\n" +
                                    "\t\"glucoseId\": " + bloodGlucoseMeal.get(0) + ",\n" +
                                    "\t\"vitalId\": 7,\n" +
                                    "\t\"date\": " + bgDateList.get(i - 1) + "\n" +
                                    "}"
                            );
*/

                                //Decrypt the encrypted list using keys
                                String encryptedRequest = encrypt(key, initVector, "{\n" +
                                        "\t\"timeOfCheck\": 0,\n" +
                                        "\t\"currentDate\": " + bgCurrentDateList.get(i - 1) + ",\n" +
                                        "\t\"filterType\": [1],\n" +
                                        "\t\"type\": 0,\n" +
                                        "\t\"value\": [\"" + stringValue + "\"],\n" +
                                        "\t\"glucoseId\": " + bloodGlucoseMeal.get(0) + ",\n" +
                                        "\t\"vitalId\": 7,\n" +
                                        "\t\"date\": " + bgDateList.get(i - 1) + "\n" +
                                        "}"
                                );

                                // Print or use the encrypted request
                                System.out.println("BG Encrypted request: " + encryptedRequest);

                                // Store the encrypted request in the list
                                bgEncryptedRequests.add(encryptedRequest);
                            }
                        }
                    }
                }
            }
        }
    }

    //Call the add clap api
    @Test(priority = 11)
    public void bgEncRes() {
        //Iterate the encrypted list based on size
        for (int j = 1; j <= accessTokens.size(); j++) {

            //Iterate the rows based on post id size
            for (int i = 1; i <= bgDateList.size(); i++) {

                Random random4 = new Random();
                int randomValue = random4.nextInt(201) + 100; // Generates random integer between 0 and 200, then adds 100
                String stringValue = String.valueOf(randomValue); // Convert integer to string
                //bgRandomValues.add(stringValue);
                System.out.println("BG String Value: " + i + " : " + stringValue);

                // Given timestamp
                long timestamp = Long.parseLong(bgDateList.get(i - 1));
                System.out.println("BG-E Long: " + i + " : " + timestamp);

                // Convert the timestamp to a ZonedDateTime object
                ZonedDateTime zonedDateTime = Instant.ofEpochSecond(timestamp)
                        .atZone(ZoneId.systemDefault());

                // Define the date-time format
                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                // Format the ZonedDateTime object using the formatter
                String formattedDateTime = zonedDateTime.format(formatter1);

                // Print the formatted date-time
                System.out.println("BG-E Formatted date-time: " + formattedDateTime);

                // Parse the formatted date-time string to a LocalDateTime object
                LocalDateTime localDateTime = LocalDateTime.parse(formattedDateTime, formatter1);

                // Extract the time (hh:mm) from the LocalDateTime object
                LocalTime localTime = localDateTime.toLocalTime();

                // Format the extracted time using a time formatter
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                String extractedTime = localTime.format(timeFormatter);

                // Print the extracted time
                System.out.println("BG-E Extracted time: " + extractedTime);

                // Parse the extracted time
                LocalTime parsedTime = LocalTime.parse(extractedTime);

                // Reduce 4 hours from the extracted time
                LocalTime reducedTime = parsedTime.plusHours(28);

                // Output the reduced time
                System.out.println("Reduced time: " + reducedTime);

                // Parse the boundaries
                LocalTime startTime = LocalTime.of(6, 0); // 06:00
                LocalTime endTime = LocalTime.of(9, 30);  // 09:30

                // Check if the extracted time is between the boundaries
                boolean isBetween = !reducedTime.isBefore(startTime) && !reducedTime.isAfter(endTime);
                //boolean isBetween = !parsedTime.isBefore(startTime) && !parsedTime.isAfter(endTime);

                // Output the result
                if (isBetween) {
                    System.out.println("The extracted time is between 06:00 and 09:30.");
                    bloodGlucoseMeal.add(0, "1");

                    //Decrypt the encrypted list using keys
                    String encryptedRequest = encrypt(key, initVector, "{\n" +
                            "\t\"timeOfCheck\": 0,\n" +
                            "\t\"currentDate\": " + bgCurrentDateList.get(i - 1) + ",\n" +
                            "\t\"filterType\": [1],\n" +
                            "\t\"type\": 0,\n" +
                            "\t\"value\": [\"" + stringValue + "\"],\n" +
                            "\t\"glucoseId\": " + bloodGlucoseMeal.get(0) + ",\n" +
                            "\t\"vitalId\": 7,\n" +
                            "\t\"date\": " + bgDateList.get(i - 1) + "\n" +
                            "}"
                    );

                    // Print or use the encrypted request
                    System.out.println("BG Encrypted request: " + encryptedRequest);

                    // Store the encrypted request in the list
                    //bgEncryptedRequests.add(encryptedRequest);

                    //Using the restAssured framework
                    RequestSpecification request = RestAssured.given();
                    RestAssured.useRelaxedHTTPSValidation();

                    //Set the request parameters
                    request.header("accept", "*/*");
                    request.header("Content-Type", "application/json");
                    request.header("Authorization", "Bearer " + accessTokens.get(j - 1));

                    //for (int i = 1; i <= bgEncryptedRequests.size(); i++) {
                    //request.body("{\"type\":\" " + bgEncryptedRequests.get(i - 1) + " \"}");
                    request.body("{\"type\":\" " + encryptedRequest + " \"}");

                    //Call the API and store the response
                    Response response = request.post("https://mobileapi.kaicare.ai/api/Vital/saveVital");

                    //Print the response body
                    System.out.println("BG Encrypted status code: " + i + " : " + response.getStatusCode());
                    System.out.println("BG Encrypted response: " + i + " : " + response.getBody().asString());

                    // Store the encrypted request in the list
                    bgEncryptedResponses.add(response.getBody().asString());

                } else {
                    System.out.println("The extracted time is not between 06:00 and 09:30.");
                    // Parse the boundaries
                    LocalTime startTime1 = LocalTime.of(11, 0); // 06:00
                    LocalTime endTime1 = LocalTime.of(14, 0);  // 09:30

                    // Parse the extracted time
                    LocalTime parsedTime1 = LocalTime.parse(extractedTime);

                    // Reduce 4 hours from the extracted time
                    LocalTime reducedTime1 = parsedTime1.plusHours(28);

                    // Output the reduced time
                    System.out.println("Reduced time 1: " + reducedTime1);

                    // Check if the extracted time is between the boundaries
                    boolean isBetween1 = !reducedTime1.isBefore(startTime1) && !reducedTime1.isAfter(endTime1);
                    //boolean isBetween1 = !parsedTime1.isBefore(startTime1) && !parsedTime1.isAfter(endTime1);

                    // Output the result
                    if (isBetween1) {
                        System.out.println("The extracted time is between 11:00 and 14:00.");
                        bloodGlucoseMeal.add(0, "2");

                        //Decrypt the encrypted list using keys
                        String encryptedRequest = encrypt(key, initVector, "{\n" +
                                "\t\"timeOfCheck\": 0,\n" +
                                "\t\"currentDate\": " + bgCurrentDateList.get(i - 1) + ",\n" +
                                "\t\"filterType\": [1],\n" +
                                "\t\"type\": 0,\n" +
                                "\t\"value\": [\"" + stringValue + "\"],\n" +
                                "\t\"glucoseId\": " + bloodGlucoseMeal.get(0) + ",\n" +
                                "\t\"vitalId\": 7,\n" +
                                "\t\"date\": " + bgDateList.get(i - 1) + "\n" +
                                "}"
                        );

                        // Print or use the encrypted request
                        System.out.println("BG Encrypted request: " + encryptedRequest);

                        // Store the encrypted request in the list
                        //bgEncryptedRequests.add(encryptedRequest);

                        //Using the restAssured framework
                        RequestSpecification request = RestAssured.given();
                        RestAssured.useRelaxedHTTPSValidation();

                        //Set the request parameters
                        request.header("accept", "*/*");
                        request.header("Content-Type", "application/json");
                        request.header("Authorization", "Bearer " + accessTokens.get(j - 1));

                        //for (int i = 1; i <= bgEncryptedRequests.size(); i++) {
                        //request.body("{\"type\":\" " + bgEncryptedRequests.get(i - 1) + " \"}");
                        request.body("{\"type\":\" " + encryptedRequest + " \"}");

                        //Call the API and store the response
                        Response response = request.post("https://mobileapi.kaicare.ai/api/Vital/saveVital");

                        //Print the response body
                        System.out.println("BG Encrypted status code: " + i + " : " + response.getStatusCode());
                        System.out.println("BG Encrypted response: " + i + " : " + response.getBody().asString());

                        // Store the encrypted request in the list
                        bgEncryptedResponses.add(response.getBody().asString());

                    } else {
                        System.out.println("The extracted time is not between 11:00 and 14:00.");

                        // Parse the boundaries
                        LocalTime startTime2 = LocalTime.of(17, 0); // 06:00
                        LocalTime endTime2 = LocalTime.of(19, 30);  // 09:30

                        // Parse the extracted time
                        LocalTime parsedTime2 = LocalTime.parse(extractedTime);

                        // Reduce 4 hours from the extracted time
                        LocalTime reducedTime2 = parsedTime2.plusHours(28);

                        // Output the reduced time
                        System.out.println("Reduced time 2: " + reducedTime2);

                        // Check if the extracted time is between the boundaries
                        boolean isBetween2 = !reducedTime2.isBefore(startTime2) && !reducedTime2.isAfter(endTime2);
                        //boolean isBetween2 = !parsedTime2.isBefore(startTime2) && !parsedTime2.isAfter(endTime2);

                        // Output the result
                        if (isBetween2) {
                            System.out.println("The extracted time is between 17:00 and 19:30.");
                            bloodGlucoseMeal.add(0, "3");

                            //Decrypt the encrypted list using keys
                            String encryptedRequest = encrypt(key, initVector, "{\n" +
                                    "\t\"timeOfCheck\": 0,\n" +
                                    "\t\"currentDate\": " + bgCurrentDateList.get(i - 1) + ",\n" +
                                    "\t\"filterType\": [1],\n" +
                                    "\t\"type\": 0,\n" +
                                    "\t\"value\": [\"" + stringValue + "\"],\n" +
                                    "\t\"glucoseId\": " + bloodGlucoseMeal.get(0) + ",\n" +
                                    "\t\"vitalId\": 7,\n" +
                                    "\t\"date\": " + bgDateList.get(i - 1) + "\n" +
                                    "}"
                            );

                            // Print or use the encrypted request
                            System.out.println("BG Encrypted request: " + encryptedRequest);

                            // Store the encrypted request in the list
                            //bgEncryptedRequests.add(encryptedRequest);

                            //Using the restAssured framework
                            RequestSpecification request = RestAssured.given();
                            RestAssured.useRelaxedHTTPSValidation();

                            //Set the request parameters
                            request.header("accept", "*/*");
                            request.header("Content-Type", "application/json");
                            request.header("Authorization", "Bearer " + accessTokens.get(j - 1));

                            //for (int i = 1; i <= bgEncryptedRequests.size(); i++) {
                            //request.body("{\"type\":\" " + bgEncryptedRequests.get(i - 1) + " \"}");
                            request.body("{\"type\":\" " + encryptedRequest + " \"}");

                            //Call the API and store the response
                            Response response = request.post("https://mobileapi.kaicare.ai/api/Vital/saveVital");

                            //Print the response body
                            System.out.println("BG Encrypted status code: " + i + " : " + response.getStatusCode());
                            System.out.println("BG Encrypted response: " + i + " : " + response.getBody().asString());

                            // Store the encrypted request in the list
                            bgEncryptedResponses.add(response.getBody().asString());

                        } else {
                            System.out.println("The extracted time is not between 17:00 and 19:30.");

                            // Parse the boundaries
                            LocalTime startTime3 = LocalTime.of(21, 0); // 06:00
                            LocalTime endTime3 = LocalTime.of(23, 30);  // 09:30

                            // Parse the extracted time
                            LocalTime parsedTime3 = LocalTime.parse(extractedTime);

                            // Reduce 4 hours from the extracted time
                            LocalTime reducedTime3 = parsedTime3.plusHours(28);

                            // Output the reduced time
                            System.out.println("Reduced time 3: " + reducedTime3);

                            // Check if the extracted time is between the boundaries
                            boolean isBetween3 = !reducedTime3.isBefore(startTime3) && !reducedTime3.isAfter(endTime3);
                            //boolean isBetween3 = !parsedTime3.isBefore(startTime3) && !parsedTime3.isAfter(endTime3);

                            // Output the result
                            if (isBetween3) {
                                System.out.println("The extracted time is between 21:00 and 23:00.");
                                bloodGlucoseMeal.add(0, "4");

                                //Decrypt the encrypted list using keys
                                String encryptedRequest = encrypt(key, initVector, "{\n" +
                                        "\t\"timeOfCheck\": 0,\n" +
                                        "\t\"currentDate\": " + bgCurrentDateList.get(i - 1) + ",\n" +
                                        "\t\"filterType\": [1],\n" +
                                        "\t\"type\": 0,\n" +
                                        "\t\"value\": [\"" + stringValue + "\"],\n" +
                                        "\t\"glucoseId\": " + bloodGlucoseMeal.get(0) + ",\n" +
                                        "\t\"vitalId\": 7,\n" +
                                        "\t\"date\": " + bgDateList.get(i - 1) + "\n" +
                                        "}"
                                );

                                // Print or use the encrypted request
                                System.out.println("BG Encrypted request: " + encryptedRequest);

                                // Store the encrypted request in the list
                                //bgEncryptedRequests.add(encryptedRequest);

                                //Using the restAssured framework
                                RequestSpecification request = RestAssured.given();
                                RestAssured.useRelaxedHTTPSValidation();

                                //Set the request parameters
                                request.header("accept", "*/*");
                                request.header("Content-Type", "application/json");
                                request.header("Authorization", "Bearer " + accessTokens.get(j - 1));

                                //for (int i = 1; i <= bgEncryptedRequests.size(); i++) {
                                //request.body("{\"type\":\" " + bgEncryptedRequests.get(i - 1) + " \"}");
                                request.body("{\"type\":\" " + encryptedRequest + " \"}");

                                //Call the API and store the response
                                Response response = request.post("https://mobileapi.kaicare.ai/api/Vital/saveVital");

                                //Print the response body
                                System.out.println("BG Encrypted status code: " + i + " : " + response.getStatusCode());
                                System.out.println("BG Encrypted response: " + i + " : " + response.getBody().asString());

                                // Store the encrypted request in the list
                                bgEncryptedResponses.add(response.getBody().asString());

                            } else {
                                System.out.println("The extracted time is not between 21:00 and 23:00.");

                                bloodGlucoseMeal.add(0, "5");

                                //Decrypt the encrypted list using keys
                                String encryptedRequest = encrypt(key, initVector, "{\n" +
                                        "\t\"timeOfCheck\": 0,\n" +
                                        "\t\"currentDate\": " + bgCurrentDateList.get(i - 1) + ",\n" +
                                        "\t\"filterType\": [1],\n" +
                                        "\t\"type\": 0,\n" +
                                        "\t\"value\": [\"" + stringValue + "\"],\n" +
                                        "\t\"glucoseId\": " + bloodGlucoseMeal.get(0) + ",\n" +
                                        "\t\"vitalId\": 7,\n" +
                                        "\t\"date\": " + bgDateList.get(i - 1) + "\n" +
                                        "}"
                                );

                                // Print or use the encrypted request
                                System.out.println("BG Encrypted request: " + encryptedRequest);

                                // Store the encrypted request in the list
                                //bgEncryptedRequests.add(encryptedRequest);

                                //Using the restAssured framework
                                RequestSpecification request = RestAssured.given();
                                RestAssured.useRelaxedHTTPSValidation();

                                //Set the request parameters
                                request.header("accept", "*/*");
                                request.header("Content-Type", "application/json");
                                request.header("Authorization", "Bearer " + accessTokens.get(j - 1));

                                //for (int i = 1; i <= bgEncryptedRequests.size(); i++) {
                                //request.body("{\"type\":\" " + bgEncryptedRequests.get(i - 1) + " \"}");
                                request.body("{\"type\":\" " + encryptedRequest + " \"}");

                                //Call the API and store the response
                                Response response = request.post("https://mobileapi.kaicare.ai/api/Vital/saveVital");

                                //Print the response body
                                System.out.println("BG Encrypted status code: " + i + " : " + response.getStatusCode());
                                System.out.println("BG Encrypted response: " + i + " : " + response.getBody().asString());

                                // Store the encrypted request in the list
                                bgEncryptedResponses.add(response.getBody().asString());
                            }
                        }
                    }
                }
            }
        }

        System.out.println("BG Response list: " + bgEncryptedResponses);
        System.out.println("BG Response size: " + bgEncryptedResponses.size());
    }

    //Set the encrypt method for encrypted values using key's
    public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            String enc = Base64.encodeBase64String(encrypted);
            System.out.println(enc);

            return Base64.encodeBase64String(encrypted);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String strToDecrypt, String key, String initVector) {
        try {
            // Add null checks
            if (strToDecrypt == null || strToDecrypt.trim().isEmpty()) {
                System.out.println("Error: strToDecrypt is null or empty");
                return null;
            }
            if (key == null || key.trim().isEmpty()) {
                System.out.println("Error: key is null or empty");
                return null;
            }
            if (initVector == null || initVector.trim().isEmpty()) {
                System.out.println("Error: initVector is null or empty");
                return null;
            }

            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            return new String(cipher.doFinal(java.util.Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
            System.out.println("Input string: " + strToDecrypt);
            System.out.println("Key: " + key);
            System.out.println("InitVector: " + initVector);
            e.printStackTrace();
        }
        return null;
    }

}
