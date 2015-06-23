
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.DateUtilities;
import com.jacob.com.Dispatch;
import com.jacob.com.LibraryLoader;
import com.jacob.com.Variant;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import org.json.*;

/**
 * Created by Matt on 2015-06-21.
 */
public class calendarConnector {

    int numberOfMatchingItems = 0;
    String[][] subLoca;

    public calendarConnector(){
    }

    public void getAppts(String subjectFilter, String start, String end){
        ActiveXComponent outlokAx = new ActiveXComponent("Outlook.Application");


        Dispatch namespace = outlokAx.getProperty("Session").toDispatch();
        Dispatch calendarFolder = Dispatch.call(namespace, "GetDefaultFolder", 9).toDispatch();

        Dispatch calItems = Dispatch.get(calendarFolder, "items").toDispatch();
        String customFilter = "@SQL=\"urn:schemas:calendar:dtstart\" > '" +  start + "' and  \"urn:schemas:calendar:dtend\" < '" +  end  + "' and \"urn:schemas:httpmail:subject\" ci_phrasematch '"+ subjectFilter + "'" ;

        Dispatch restrictedItems = Dispatch.call(calItems, "Restrict", new Variant(customFilter)).toDispatch(); //Works only with dates

        Dispatch.call(calItems, "Sort", "[Start]");
        Dispatch.put(restrictedItems, "IncludeRecurrences", "False");


        Dispatch lastitemFound = null;
        int i = 0;
        if (restrictedItems != null && restrictedItems.m_pDispatch > 0) {
            Dispatch findItem = Dispatch.call(restrictedItems, "GetFirst").toDispatch();

            while (findItem.getProgramId() != null && findItem.m_pDispatch > 0) {
                numberOfMatchingItems++;
                findItem = Dispatch.call(restrictedItems, "GetNext").toDispatch();
                System.out.print(numberOfMatchingItems + " ");
            }
            /* */
            if(numberOfMatchingItems != 0){
                this.initSubLoca();
                findItem = Dispatch.call(restrictedItems, "GetFirst").toDispatch();
                while (findItem.getProgramId() != null && findItem.m_pDispatch > 0) {

                    lastitemFound = findItem;
                    String subject = Dispatch.get(findItem, "Subject").toString();
                    String location = Dispatch.get(findItem, "Location").toString();
                    String date = Dispatch.get(findItem, "Start").toString();
                    findItem = Dispatch.call(restrictedItems, "GetNext").toDispatch();

                    this.subLoca[i][0] = subject;
                    this.subLoca[i][1] = location;
                    this.subLoca[i][3] = date;
                    i++;
                }
            }
            else{
                System.out.println("Found no appointements containing the appropriate keyword");
            }






        }

    }

    public void  getDistance(String origin){
        try{
            String originFixed = "";
            String destFixed = "";
            String dest;
            for (int i = 0; i < this.subLoca.length; i++) {
                dest = this.subLoca[i][1];
                destFixed += dest.replaceAll("\\W", "+");
                originFixed += origin.replaceAll("\\W", "+");
                if(!(i==subLoca.length-1)){

                    destFixed += "|";
                    originFixed += "|";
                }

            }
            URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+originFixed+"&destinations="+destFixed);
            //System.out.println(url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String line, outputString = "";
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                outputString += line;
            }

            JSONObject distanceJS = new JSONObject(outputString);
            JSONArray arr = distanceJS.getJSONArray("rows");
            JSONObject distance = arr.getJSONObject(0);
            JSONArray arr2 = distance.getJSONArray("elements");

            for (int i = 0; i < this.subLoca.length; i++) {
                JSONObject finalObj = arr2.getJSONObject(i).getJSONObject("distance");
                subLoca[i][2] = finalObj.getString("text");
            }



        }
        catch(Exception e){
            System.out.println("Error in calculating distances\n  Please Try Again");

        }


    }

    public String[][] getSubLoca() {
        return subLoca;
    }

    public void initSubLoca() {
        this.subLoca = new String[numberOfMatchingItems][4];
    }

    public String toString(){
        String result = "";
        if(numberOfMatchingItems != 0){
            result += getTotalDist() + "\n";
            for (int i = 0; i < this.subLoca.length; i++) {
                int count = i+1;
                result+= "\nEvent " + count + "\n   Subject: " + subLoca[i][0] +
                        "\n    Date: " +subLoca[i][3] + "\n      Location "
                        + subLoca[i][1] + "\n       Distance one way: " + subLoca[i][2];
            }
        }

        return result;
    }

    public String getTotalDist(){
        double total = 0;

        String parse;
        for (int i = 0; i < subLoca.length; i++) {
            String[] split = subLoca[i][2].split("\\s+");

                parse = split[0].replaceAll(",", "");

            //parse = "22.2";
            if(split[1].equals("km")){

                total += (Double.valueOf(parse))*2;
            }
            else{
                total += (Double.valueOf(parse)/1000)*2;
            }

        }
        return "\nTotal Distance Traveled is " + total + "km";
    }
}
