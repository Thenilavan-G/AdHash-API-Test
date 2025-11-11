# 🧪 Test Email Functionality

## Quick Test Instructions

### 1. Set Environment Variables (Windows)
```cmd
set EMAIL_ENABLED=true
set EMAIL_PROVIDER=gmail
set EMAIL_USERNAME=your-email@gmail.com
set EMAIL_PASSWORD=your-app-password
set EMAIL_RECIPIENTS=test-recipient@email.com
```

### 2. Run a Single Test
```cmd
mvn clean test -Dtest="generated.GeneratedApiTest#pm_AdHash_Website"
```

### 3. Expected Output
You should see:
```
✅ PASSED: pm_AdHash_Website
=============================================================
🎯 TEST EXECUTION COMPLETED - AdHash API Suite
=============================================================
📊 RESULTS SUMMARY:
   Total Tests: 1
   ✅ Passed: 1
   ❌ Failed: 0
   ⏭️ Skipped: 0
   📈 Success Rate: 100.0%
=============================================================
📄 Found HTML report: [path-to-report]
📧 Sending email report to: [test-recipient@email.com]
✅ Email sent successfully to: test-recipient@email.com
```

### 4. Check Your Email
You should receive an email with:
- ✅ Subject: "AdHash API Test Report - ALL PASSED - [timestamp]"
- ✅ HTML body with test summary table
- ✅ HTML report attachment

## Troubleshooting

### If Email Doesn't Send:
1. **Check environment variables are set**
2. **Verify Gmail App Password** (not regular password)
3. **Check recipient email format**
4. **Look for error messages in console**

### Common Error Messages:
- `"Email configuration missing"` → Set all required environment variables
- `"Authentication Failed"` → Check username/password
- `"HTML report not found"` → Ensure test ran successfully

## Full Test Suite
To test with all 60 APIs:
```cmd
mvn clean test
```

This will send a comprehensive email report with all API test results!
