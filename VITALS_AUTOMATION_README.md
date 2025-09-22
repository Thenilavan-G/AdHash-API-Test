# Daily Vitals Test Automation

This project has been modified to automatically generate vitals data using IST (Indian Standard Time) timezone instead of reading dates from Excel files.

## Changes Made

### 1. Modified AddVitals.java
- **Removed Excel dependency for dates**: The system no longer reads date values from Excel columns 7-11
- **Added IST timezone support**: All date operations now use `Asia/Kolkata` timezone
- **Automatic date generation**: The system generates date ranges automatically:
  - Heart Rate: Last 30 days
  - Blood Pressure: Last 25 days  
  - Oxygen: Last 20 days
  - Blood Glucose: Last 15 days
  - Blood Glucose Current: Last 15 days (extends to tomorrow)

### 2. Key Features
- **IST Timezone**: All timestamps are generated using Indian Standard Time
- **Dynamic Date Ranges**: Different vitals have different historical date ranges
- **Random Time Generation**: Each day gets random hours (2-14) and minutes (0-59)
- **Mobile Numbers**: Still reads mobile numbers from Excel for login functionality

### 3. GitHub Actions Automation
- **Daily Schedule**: Runs every day at 10:00 AM IST (4:30 AM UTC)
- **Manual Trigger**: Can be triggered manually via GitHub Actions UI
- **Automatic Excel Creation**: Creates minimal Excel file with test mobile numbers
- **Test Execution**: Runs the vitals tests automatically

## How It Works

### Date Generation Process
1. **Current IST Time**: Gets current date/time in IST timezone
2. **Historical Dates**: Generates start dates going back N days for each vital type
3. **Daily Timestamps**: Creates timestamps for each day from start date to current date
4. **Random Times**: Each timestamp gets random hour/minute for realistic data

### Example Date Generation
```java
// Heart Rate - starts 30 days ago at 8:00 AM IST
LocalDateTime hrStartDate = currentIST.minusDays(30).withHour(8).withMinute(0);

// Blood Pressure - starts 25 days ago at 9:00 AM IST  
LocalDateTime bpStartDate = currentIST.minusDays(25).withHour(9).withMinute(0);
```

## GitHub Actions Workflow

### Schedule
- **Cron**: `30 4 * * *` (4:30 AM UTC = 10:00 AM IST)
- **Frequency**: Daily
- **Manual**: Available via workflow_dispatch

### Workflow Steps
1. Checkout repository
2. Set up Java 11
3. Cache Maven dependencies
4. Display current IST time
5. Create test data directory
6. Generate minimal Excel file for mobile numbers
7. Run vitals tests
8. Upload test results
9. Send notification on failure

## Running Locally

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- Internet connection (for API calls)

### Commands
```bash
# Run all tests
mvn test

# Run only vitals tests
mvn test -DsuiteXmlFile=vitals-testng.xml

# Run specific test class
mvn test -Dtest=MyHealthAPI.AddVitals
```

## Configuration

### Mobile Numbers
The system still requires mobile numbers from Excel for login functionality. The GitHub Actions workflow creates a minimal Excel file with test mobile numbers:

```csv
FirstName,LastName,PatientID,DOB,Gender,MobileNumber,Email
TestUser,One,PAT001,01/01/1990,M,9876543210,test1@example.com
TestUser,Two,PAT002,01/01/1985,F,9876543211,test2@example.com
```

### Date Ranges (Customizable)
You can modify the date ranges in the `generateDatesUsingIST()` method:

```java
// Modify these values to change date ranges
LocalDateTime hrStartDate = currentIST.minusDays(30);    // Heart Rate: 30 days
LocalDateTime bpStartDate = currentIST.minusDays(25);    // Blood Pressure: 25 days
LocalDateTime oxyStartDate = currentIST.minusDays(20);   // Oxygen: 20 days
LocalDateTime bgStartDate = currentIST.minusDays(15);    // Blood Glucose: 15 days
```

## Monitoring

### GitHub Actions
- Check the Actions tab in your GitHub repository
- View logs for each daily run
- Download test results artifacts
- Monitor for failures and notifications

### Test Results
- Surefire reports are uploaded as artifacts
- Console logs show IST timestamps and generated data
- API responses are logged for debugging

## Troubleshooting

### Common Issues
1. **Excel File Missing**: GitHub Actions creates it automatically
2. **Timezone Issues**: All operations use IST (`Asia/Kolkata`)
3. **API Failures**: Check network connectivity and API endpoints
4. **Date Format Issues**: Uses `dd/MM/yyyy HH:mm` format consistently

### Debug Information
The system logs extensive debug information:
- Generated start dates for each vital type
- Timestamp generation process
- API request/response details
- Current IST time at execution

## Benefits

1. **No Manual Excel Updates**: Dates are generated automatically
2. **IST Timezone Consistency**: All operations in Indian time
3. **Daily Automation**: Runs without manual intervention
4. **Flexible Date Ranges**: Different ranges for different vitals
5. **GitHub Actions Integration**: Built-in CI/CD pipeline
6. **Comprehensive Logging**: Full visibility into execution
