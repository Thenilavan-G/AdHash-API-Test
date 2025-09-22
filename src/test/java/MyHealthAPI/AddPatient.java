package MyHealthAPI;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.codec.binary.Base64;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AddPatient {

    //Store the post id list
    List<String> number;
    //Store the thread id list
    List<String> first;
    List<String> last;
    List<String> patient;
    List<String> dob;
    List<String> gender;
    List<String> email;

    public List<String> encryptedRequests = new ArrayList<>();
    public List<String> responses = new ArrayList<>();
    public List<String> dobList = new ArrayList<>();

    //Get the post id and thread id list and store it
    @BeforeTest
    public void before() {
        PatientReqRead addPatient = new PatientReqRead();
        number = addPatient.getNumberList();
        first = addPatient.getFirstList();
        last = addPatient.getLastList();
        patient = addPatient.getPatientIdList();
        dob = addPatient.getDOBList();
        gender = addPatient.getGenderList();
        email = addPatient.getEmailList();
    }

    //@BeforeClass
    public void beforeClass() {
        // Input date and time string
        //String dateString = "04/01/2006";
        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        for (int i = 1; i <= dob.size(); i++) {
            // Define the date format pattern

            // Parse the DOB string into a LocalDate object
            LocalDate dob1 = LocalDate.parse(dob.get(i-1), formatter);

            // Convert LocalDate to ZonedDateTime with a default time zone
            ZonedDateTime zonedDateTime = dob1.atStartOfDay(ZoneId.systemDefault());

            // Extract the epoch seconds
            long timestamp = zonedDateTime.toEpochSecond();

            // Output the timestamp
            System.out.println("Timestamp for DOB " + dob.get(i-1) + ": " + timestamp);
            dobList.add(String.valueOf(timestamp));
        }
        System.out.println("DOB List : " + dobList);
        System.out.println("DOB List Size : " + dobList.size());
    }

    //Decrypt the encrypted list
    @Test(priority = 1)
    public void addPatientEncReq() {
        //Set the key value and vector value
        String key = "xfnr3PVyckouBZxW"; // 128 bit key
        String initVector = "xfnr3PVyckouBZxW"; // 16 bytes IV

        //Iterate the rows based on post id size
        for (int i = 1; i <= number.size(); i++) {
            //Decrypt the encrypted list using keys
            String encryptedRequest = encrypt(key, initVector, "{\n" +
                    "\t\"adminId\": \"34b01dee-6c5e-4e14-8042-fd03a226b7db\",\n" +
                    "\t\"profilePic\": \"\",\n" +
                    "\t\"userId\": \"1\",\n" +
                    "\t\"firstName\": \"" + first.get(i - 1) + "\",\n" +
                    "\t\"middleName\": \"\",\n" +
                    "\t\"lastName\": \"" + last.get(i - 1) + "\",\n" +
                    "\t\"mobileNumber\": \"" + number.get(i - 1) + "\",\n" +

                    // "\t\"dob\": " + dobList.get(i - 1) + ",\n" +
                    // "\t\"patientId\": \"" + patient.get(i - 1) + "\",\n" +
                    // "\t\"email\": \"" + email.get(i - 1) + "\",\n" +
                    // "\t\"gender\": \"" + gender.get(i - 1) + "\",\n" +

                    "\t\"patientId\": \"\",\n" +
                    "\t\"email\": \"\",\n" +
                    "\t\"gender\": \"\",\n" +
                    "\t\"dob\": 0,\n" +

                    "\t\"age\": \"\",\n" +
                    "\t\"unitNo\": \"\",\n" +
                    "\t\"streetAddress\": \"\",\n" +
                    "\t\"city\": \"\",\n" +
                    "\t\"state\": \"\",\n" +
                    "\t\"zipcode\": \"\",\n" +
                    "\t\"height\": \"\",\n" +
                    "\t\"weight\": \"\",\n" +
                    "\t\"bloodGroup\": \"\"\n" +
                    "}"
            );

/*
        for (int i = 1; i <= number.size(); i++) {
            //Decrypt the encrypted list using keys
            String encryptedRequest = encrypt(key, initVector, "{\n" +
                    "\t\"Id\": null,\n" +
                    "\t\"userId\": \"7001\",\n" +
                    "\t\"profilePic\": \"\",\n" +
                    "\t\"firstName\": \"Test\",\n" +
                    "\t\"middleName\": \"\",\n" +
                    "\t\"lastName\": \"Auto 2\",\n" +
                    "\t\"patientId\": \"TA0002\",\n" +
                    "\t\"age\": \"\",\n" +
                    "\t\"mobileNumber\": \"3100000002\",\n" +
                    "\t\"email\": \"testauto2@gmail.com\",\n" +
                    "\t\"unitNo\": \"123\",\n" +
                    "\t\"city\": \"Georgia\",\n" +
                    "\t\"gender\": null,\n" +
                    "\t\"dob\": 0,\n" +
                    "\t\"streetAddress\": \"101 Marietta\",\n" +
                    "\t\"zipcode\": \"30303\",\n" +
                    "\t\"state\": \"GA\",\n" +
                    "\t\"height\": null,\n" +
                    "\t\"weight\": \"\",\n" +
                    "\t\"bloodGroup\": null, \n" +
                    "\t\"GatewayId\": \"\",\n" +
                    "\t\"PulseOximeterID\": \"\",\n" +
                    "\t\"BloodGlucoseID\": \"\",\n" +
                    "\t\"BloodPresureID\": \"\",\n" +
                    "\t\"BloodPresureDeviceID\": \"\",\n" +
                    "\t\"BloodGlucoseDeviceID\": \"\",\n" +
                    "\t\"PulseOximeterDeviceID\": \"\",\n" +
                    "\t\"ThermometerDeviceID\": \"\",\n" +
                    "\t\"ScaleDeviceID\": \"\",\n" +
                    "\t\"ScaleID\": \"\",\n" +
                    "\t\"ThermometerID\": \"\",\n" +
                    "\t\"PillBoxID\": \"\",\n" +
                    "\t\"PillBoxDeviceID\": \"\",\n" +
                    "\t\"rtm\": false,\n" +
                    "\t\"rpm\": true,\n" +
                    "\t\"ccm\": true,\n" +
                    "\t\"VerbalConsents\": true,\n" +
                    "\t\"InsuranceId\": \"\",\n" +
                    "\t\"ICDCode\": \"\",\n" +
                    "\t\"Elgibility\": null \n" +


                    // "\t\"dob\": " + dobList.get(i - 1) + ",\n" +
                    // "\t\"patientId\": \"" + patient.get(i - 1) + "\",\n" +
                    // "\t\"email\": \"" + email.get(i - 1) + "\",\n" +
                    // "\t\"gender\": \"" + gender.get(i - 1) + "\",\n" +


                    "}"
            );
*/

            // Print or use the encrypted request
            System.out.println("Encrypted request: " + i + " : " + encryptedRequest);
            // Store the encrypted request in the list
            encryptedRequests.add(encryptedRequest);
        }

        System.out.println("Encrypted request: " + encryptedRequests);
        System.out.println("Encrypted request size: " + encryptedRequests.size());
    }

    //Call the add clap api
    @Test(priority = 2)
    public void addPatientEncRes() {
        System.out.println("Encrypted request list: " + encryptedRequests);
        System.out.println("Encrypted request size: " + encryptedRequests.size());

        //Iterate the encrypted list based on size
        for (int i = 1; i <= encryptedRequests.size(); i++) {
            //Using the restAssured framework
            RequestSpecification request = RestAssured.given();
            RestAssured.useRelaxedHTTPSValidation();

            //Set the request parameters
            request.header("accept", "*/*");
            request.header("Content-Type", "application/json");
            request.header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6IkpXVCJ9.eyJNYWlsSUQiOiJqYWNrQG15aGVhbHRoYWkuY29tIiwiZGlzcGxheU5hbWUiOiJKYWNrIFdpbGxpYW1zIiwiVXNlcklkIjoiMzRiMDFkZWUtNmM1ZS00ZTE0LTgwNDItZmQwM2EyMjZiN2RiIiwiaXNMYWIiOiJGYWxzZSIsImp0aSI6IjkwMDI5YTVhLWUwNzYtNDYyMy04YzBhLWEyN2MxNzliZmU0NSIsImV4cCI6MTc3MTU0MjI0NywiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo1MDAwL0xpZmVIb3BlIiwiYXVkIjoiNTMzNzE0ZGQtZmQ5Yi00ZDVjLTg3ZjQtZGY1ODI0YjUyMjFkIn0.LqDQF7XX-S6yKpbPjSIHgMkJYo_qiJi0UlBpl4RS2F4");
            request.body("{\"Key\":\" " + encryptedRequests.get(i-1) + " \"}");

            //Call the API and store the response
            Response response = request.post("https://adminapi.kaicare.ai/api/PhysicianAsst/addPatient");

            //Print the response body
            System.out.println("Encrypted status code: " + i + " : " + response.getStatusCode());
            System.out.println("Encrypted response: " + i + " : " + response.getBody().asString());

            // Store the encrypted request in the list
            responses.add(response.getBody().asString());
        }

        System.out.println("Response list: " + responses);
        System.out.println("Response size: " + responses.size());
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

}
