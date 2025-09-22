package MyHealthAPI;

import java.util.ArrayList;
import java.util.List;

public class PatientReqRead {

    //Store the number
    ArrayList<String> numberList = new ArrayList<>();
    //Store the first name
    ArrayList<String> firstList = new ArrayList<>();
    //Store the last name
    ArrayList<String> lastList = new ArrayList<>();
    //Store the patient id
    ArrayList<String> patientIdList = new ArrayList<>();
    ArrayList<String> dobList = new ArrayList<>();
    ArrayList<String> genderList = new ArrayList<>();
    //Store the email id
    ArrayList<String> emailIdList = new ArrayList<>();
    ArrayList<String> hrDateList = new ArrayList<>();
    ArrayList<String> bpDateList = new ArrayList<>();
    ArrayList<String> oxyDateList = new ArrayList<>();
    ArrayList<String> bgDateList = new ArrayList<>();
    ArrayList<String> bgCurrentDateList = new ArrayList<>();

    //Set the Excel sheet path and sheet name
    public void getData() {
        String excelPath = "./data/Test_data.xlsx";
        String sheetName = "AddPatient";
        //String sheetName = "TestData";
        testData(excelPath, sheetName);
    }

    //Call the Excel sheet and row count
    public void testData(String excelPath, String sheetName) {
        var excel = new GetExcelReq(excelPath, sheetName);

        //Get row count
        int rowCount = excel.getRowCount();

        //Iterate the row and store the first name's
        for (int i = 1; i < rowCount; i++) {
            int j = 0;
            String cellData = excel.getCellDataString(i, j);
            firstList.add(cellData);
            //System.out.println("firstList: " + firstList);
        }

        //Iterate the row and store the last name's
        for (int i = 1; i < rowCount; i++) {
            int j = 1;
            String cellData = excel.getCellDataString(i, j);
            lastList.add(cellData);
        }

        //Iterate the row and store the user id's
        for (int i = 1; i < rowCount; i++) {
            int j = 2;
            String cellData = excel.getCellDataString(i, j);
            patientIdList.add(cellData);
        }

        //Iterate the row and store the number's
        for (int i = 1; i < rowCount; i++) {
            int j = 3;
            String cellData = excel.getCellDataString(i, j);
            dobList.add(cellData);
        }

        //Iterate the row and store the number's
        for (int i = 1; i < rowCount; i++) {
            int j = 4;
            String cellData = excel.getCellDataString(i, j);
            genderList.add(cellData);
        }

        //Iterate the row and store the number's
        for (int i = 1; i < rowCount; i++) {
            int j = 5;
            String cellData = excel.getCellDataString(i, j);
            numberList.add(cellData);
        }

        //Iterate the row and store the access token's
        for (int i = 1; i < rowCount; i++) {
            int j = 6;
            String cellData = excel.getCellDataString(i, j);
            emailIdList.add(cellData);
        }

        for (int i = 1; i < rowCount; i++) {
            int j = 7;
            String cellData = excel.getCellDataString(i, j);
            hrDateList.add(cellData);
        }

        for (int i = 1; i < rowCount; i++) {
            int j = 8;
            String cellData = excel.getCellDataString(i, j);
            bpDateList.add(cellData);
        }

        for (int i = 1; i < rowCount; i++) {
            int j = 9;
            String cellData = excel.getCellDataString(i, j);
            oxyDateList.add(cellData);
        }

        for (int i = 1; i < rowCount; i++) {
            int j = 10;
            String cellData = excel.getCellDataString(i, j);
            bgDateList.add(cellData);
        }

        for (int i = 1; i < rowCount; i++) {
            int j = 11;
            String cellData = excel.getCellDataString(i, j);
            bgCurrentDateList.add(cellData);
        }
    }

    //Return the number list
    public List<String> getNumberList() {
        return numberList;
    }
    //Return the user id list
    public List<String> getPatientIdList() {
        return patientIdList;
    }
    //Return the access token list
    public List<String> getEmailList() {
        return emailIdList;
    }
    //Return the first name list
    public List<String> getFirstList() {
        return firstList;
    }
    //Return the last name list
    public List<String> getLastList() {
        return lastList;
    }
    public List<String> getDOBList() {
        return dobList;
    }
    public List<String> getGenderList() {
        return genderList;
    }
    public List<String> getHRDateList() {
        return hrDateList;
    }
    public List<String> getBPDateList() {
        return bpDateList;
    }
    public List<String> getOxyDateList() {
        return oxyDateList;
    }
    public List<String> getBGDateList() {
        return bgDateList;
    }
    public List<String> getBGCurrentDateList() {
        return bgCurrentDateList;
    }

    //Set the Excel sheet calling method
    public PatientReqRead() {
        getData();
    }

}
