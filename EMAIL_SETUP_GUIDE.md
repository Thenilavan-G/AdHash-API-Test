# 📧 Email Setup Guide for AdHash API Test Reports

This guide will help you configure automatic email notifications for your AdHash API test reports.

## 🎯 Features

- ✅ **Automatic HTML Report Emails** - Sends detailed test reports after each execution
- ✅ **Multiple Email Providers** - Supports Gmail, Outlook, and custom SMTP
- ✅ **Rich HTML Content** - Includes test summary table and full HTML report attachment
- ✅ **Multiple Recipients** - Send to multiple email addresses
- ✅ **Secure Configuration** - Uses environment variables for credentials

## 🔧 Setup Instructions

### Step 1: Choose Your Email Provider

#### Option A: Gmail Setup
1. **Enable 2-Factor Authentication** on your Gmail account
2. **Generate App Password**:
   - Go to Google Account settings
   - Security → 2-Step Verification → App passwords
   - Generate password for "Mail"
   - Copy the 16-character password

#### Option B: Outlook Setup
1. **Use your regular Outlook credentials**
2. **Ensure SMTP is enabled** (usually enabled by default)

### Step 2: Configure Environment Variables

#### For Local Testing:
Set these environment variables in your system:

```bash
# Enable email functionality
EMAIL_ENABLED=true

# Email provider (gmail or outlook)
EMAIL_PROVIDER=gmail

# Your email credentials
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password

# Recipients (comma-separated for multiple)
EMAIL_RECIPIENTS=recipient1@email.com,recipient2@email.com
```

#### For GitHub Actions:
Add these as repository secrets:

1. Go to your repository → Settings → Secrets and variables → Actions
2. Add these secrets:
   - `EMAIL_ENABLED`: `true`
   - `EMAIL_PROVIDER`: `gmail` or `outlook`
   - `EMAIL_USERNAME`: Your email address
   - `EMAIL_PASSWORD`: Your app password
   - `EMAIL_RECIPIENTS`: Comma-separated recipient emails

### Step 3: Update GitHub Actions Workflow

Add email configuration to your `.github/workflows/scheduled-tests.yml`:

```yaml
env:
  EMAIL_ENABLED: ${{ secrets.EMAIL_ENABLED }}
  EMAIL_PROVIDER: ${{ secrets.EMAIL_PROVIDER }}
  EMAIL_USERNAME: ${{ secrets.EMAIL_USERNAME }}
  EMAIL_PASSWORD: ${{ secrets.EMAIL_PASSWORD }}
  EMAIL_RECIPIENTS: ${{ secrets.EMAIL_RECIPIENTS }}
```

## 🚀 Usage

### Automatic Email Sending
Once configured, emails will be sent automatically after each test execution with:
- ✅ **Test Summary Table** with pass/fail/skip counts
- ✅ **HTML Report Attachment** with detailed results
- ✅ **Timestamp** and execution status
- ✅ **Professional Formatting** with AdHash branding

### Manual Testing
Test your email configuration locally:

```bash
# Set environment variables
export EMAIL_ENABLED=true
export EMAIL_PROVIDER=gmail
export EMAIL_USERNAME=your-email@gmail.com
export EMAIL_PASSWORD=your-app-password
export EMAIL_RECIPIENTS=test@email.com

# Run tests
mvn clean test
```

## 📊 Email Report Contents

### Email Subject
```
AdHash API Test Report - [STATUS] - [TIMESTAMP]
```

### Email Body Includes:
- 🎯 **Test Execution Summary**
- 📊 **Results Table** (Total, Passed, Failed, Skipped)
- 📈 **Success Percentage**
- 📄 **HTML Report Attachment**
- 🕒 **Execution Timestamp**

### HTML Report Attachment
- Complete TestNG HTML report
- Individual test results
- Execution times
- Error details for failed tests

## 🔒 Security Best Practices

1. **Never commit credentials** to your repository
2. **Use App Passwords** instead of regular passwords
3. **Limit recipient list** to authorized personnel only
4. **Regularly rotate** app passwords
5. **Use environment variables** for all sensitive data

## 🛠️ Troubleshooting

### Common Issues:

#### "Authentication Failed"
- ✅ Verify app password is correct
- ✅ Check 2FA is enabled (for Gmail)
- ✅ Ensure EMAIL_USERNAME and EMAIL_PASSWORD are set

#### "Email Not Sent"
- ✅ Check EMAIL_ENABLED=true
- ✅ Verify EMAIL_RECIPIENTS format (comma-separated)
- ✅ Check network connectivity

#### "HTML Report Not Found"
- ✅ Ensure tests are running successfully
- ✅ Check target/surefire-reports/ directory exists
- ✅ Verify Maven Surefire plugin configuration

### Debug Mode:
Add this to see detailed email logs:
```bash
export EMAIL_DEBUG=true
```

## 📞 Support

If you encounter issues:
1. Check the console output for error messages
2. Verify all environment variables are set correctly
3. Test with a single recipient first
4. Ensure your email provider allows SMTP access

## 🎉 Success!

Once configured, you'll receive professional HTML email reports after each test execution, keeping your team informed about API health and performance!
