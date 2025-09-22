package MyHealthAPI;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GetExcelReq {

    static XSSFWorkbook wb;
    static XSSFSheet sheet;

    //Call the Excel sheet path and sheet name
    public GetExcelReq(String excelPath, String sheetName) //Constructor
    {
        try {
            wb = new XSSFWorkbook(excelPath);
            sheet = wb.getSheet(sheetName);
        } catch (Exception exp) {
            //System.out.println(exp.getMessage());
        }
    }

    //Get and store the call data on each cell
    public String getCellDataString(int rowNum, int colNum)
    {
        String cellData = null;
        try {
            cellData = sheet.getRow(rowNum).getCell(colNum).getStringCellValue();
        } catch (Exception exp) {
//			System.out.println(exp.getMessage());
        }

        return cellData;
    }

    //Get the row count
    public int getRowCount()
    {
        int rowCount = 0;
        try {
            String excelPath = "./data/Test_data.xlsx";

            wb = new XSSFWorkbook(excelPath);
            sheet = wb.getSheet("AddPatient");
            //sheet = wb.getSheet("TestData");

            rowCount = sheet.getPhysicalNumberOfRows();

            //System.out.println("No of Rows : "+rowCount);

        } catch (Exception exp) {
            //System.out.println(exp.getMessage());
        }
        return rowCount;
    }

}
