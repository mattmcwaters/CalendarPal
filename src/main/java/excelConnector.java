
import org.apache.poi.*;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class excelConnector {
    String directory;
    HSSFWorkbook workbook;
    calendarConnector cC;
    public excelConnector(String directory, calendarConnector cC){
        this.cC = cC;
        this.directory = directory;

            if( !cC.abort && properFile()){
                boolean exists = (new File(directory).exists());
                if (exists) {
                    new File(directory).delete();
                    writeToDoc();
                } else {
                    writeToDoc();
                }
            }
        else{
                System.out.println("\nExcel Document failed to write\nCould be file name or quota issue");

        }

    }

    public boolean properFile(){
        boolean check1 = directory.endsWith(".xls") | directory.endsWith(".xlt") | directory.endsWith(".xlm")| directory.endsWith(".xlsx");
        boolean check2 = (directory.endsWith("\\"));
        boolean check3 = directory.endsWith("/");
        return check1 && !check2 && !check3;
    }


    private void writeToDoc(){
        workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Sample sheet");


        Map<String,  HSSFRichTextString[]> data = new HashMap<String,  HSSFRichTextString[]>();
        data.put("1", new  HSSFRichTextString[] {rich("Subject"), rich("Location"), rich("Distance one way"), rich("Distance both ways"), rich("Date")});
        for (int i = 0; i < cC.numberOfMatchingItems; i++) {
            if(cC.eventIsGood(i)){
                data.put((String.valueOf(i+2)), new HSSFRichTextString[] {richNoBold(cC.subLoca[i][0]), richNoBold(cC.subLoca[i][1]), richNoBold(cC.subLoca[i][2]),
                        richNoBold(parseDistance(cC.subLoca[i][2])),richNoBold(cC.subLoca[i][3])});
            }
            if(i == cC.numberOfMatchingItems -1){
                data.put((String.valueOf(i+4)), new HSSFRichTextString[] {rich(cC.getTotalDist())});
            }
        }

        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset) {
            Row row = sheet.createRow(rownum++);
            HSSFRichTextString[] objArr = data.get(key);
            int cellnum = 0;
            for (HSSFRichTextString obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                    cell.setCellValue(obj);
            }

        }
        sheet.autoSizeColumn(0, true);
        sheet.autoSizeColumn(1, true);
        sheet.autoSizeColumn(2, true);
        sheet.autoSizeColumn(3, true);
        sheet.autoSizeColumn(4, true);
        sheet.autoSizeColumn(5, true);

        /*
        for (int i = 0; i < cC.numberOfMatchingItems; i++) {
            Row row = sheet.createRow(rownum++);
            int cellnum = 0;
            Cell cell = row.createCell(cellnum++);
            cell.setCellValue(cC.subLoca[i][0]);
        }
        */
        try {
            FileOutputStream out =
                    new FileOutputStream(new File(directory));
            workbook.write(out);
            out.close();
            System.out.println("\nExcel written successfully..");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String parseDistance(String distance){
        String parse;

        String[] split = distance.split("\\s+");

        parse = split[0].replaceAll(",", "");

        if(split[1].equals("km")){

            parse = String.valueOf((Double.valueOf(parse))*2);
        }
        else{
            parse = String.valueOf((Double.valueOf(parse)/1000)*2);
        }
        return parse + " km";
    }

    public  HSSFRichTextString rich(String text){
        HSSFRichTextString my_rich_text_string = new HSSFRichTextString( text );
        HSSFFont font1 = workbook.createFont();
        font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
        return my_rich_text_string;
    }

    public  HSSFRichTextString richNoBold(String text){
        HSSFRichTextString my_rich_text_string = new HSSFRichTextString( text );
        return my_rich_text_string;
    }

}