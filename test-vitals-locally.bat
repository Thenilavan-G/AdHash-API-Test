@echo off
echo ========================================
echo Testing Vitals Automation Locally
echo ========================================

echo.
echo Current time: %date% %time%
echo.

echo Creating test data directory...
if not exist "data" mkdir data

echo.
echo Creating minimal Excel file for testing...
python -c "
import pandas as pd
import os

# Create test data
data = {
    'FirstName': ['TestUser', 'TestUser'],
    'LastName': ['One', 'Two'], 
    'PatientID': ['PAT001', 'PAT002'],
    'DOB': ['01/01/1990', '01/01/1985'],
    'Gender': ['M', 'F'],
    'MobileNumber': ['9876543210', '9876543211'],
    'Email': ['test1@example.com', 'test2@example.com'],
    'HRDate': ['', ''],
    'BPDate': ['', ''],
    'OxyDate': ['', ''],
    'BGDate': ['', ''],
    'BGCurrentDate': ['', '']
}

df = pd.DataFrame(data)
df.to_excel('data/Test_data.xlsx', sheet_name='AddPatient', index=False)
print('Excel file created successfully')
"

echo.
echo Running vitals tests...
mvn test -DsuiteXmlFile=vitals-testng.xml -DfailIfNoTests=false

echo.
echo ========================================
echo Test execution completed!
echo Check the console output above for results.
echo ========================================
pause
