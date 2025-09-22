package MyHealthAPI;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TestingPgm {

    public static void main(String[] args) {

        // DOB value in the format MM/dd/yyyy
        String dobValue = "04/01/2006";

        // Define the date format pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Parse the DOB string into a LocalDate object
        LocalDate dob = LocalDate.parse(dobValue, formatter);

        // Convert LocalDate to ZonedDateTime with a default time zone
        ZonedDateTime zonedDateTime = dob.atStartOfDay(ZoneId.systemDefault());

        // Extract the epoch seconds
        long timestamp = zonedDateTime.toEpochSecond();

        // Output the timestamp
        System.out.println("Timestamp for DOB " + dobValue + ": " + timestamp);


    }

}
