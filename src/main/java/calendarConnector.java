
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
    int missingLocations;
    String[][] subLoca;
    int mode;

    public calendarConnector(int mode){
        this.mode = mode;
    }

    public void getAppts(String subjectFilter, String start, String end){
        ActiveXComponent outlokAx = new ActiveXComponent("Outlook.Application");


        Dispatch namespace = outlokAx.getProperty("Session").toDispatch();
        Dispatch calendarFolder = Dispatch.call(namespace, "GetDefaultFolder", 9).toDispatch();

        Dispatch calItems = Dispatch.get(calendarFolder, "items").toDispatch();
        String customFilter = "";
        if(mode == 0){
            customFilter = "@SQL=\"urn:schemas:calendar:dtstart\" > '" +  start + "' and  \"urn:schemas:calendar:dtend\" < '" +  end + "'";

        }
        else if(mode == 1){
           customFilter = "@SQL=\"urn:schemas:calendar:dtstart\" > '" +  start + "' and  \"urn:schemas:calendar:dtend\" < '" +  end  + "' and \"urn:schemas:httpmail:subject\" ci_phrasematch '"+ subjectFilter + "'" ;

        }

        Dispatch.call(calItems, "Sort", "[Start]");

        Dispatch restrictedItems = Dispatch.call(calItems, "Restrict", new Variant(customFilter)).toDispatch(); //Works only with dates

        Dispatch.put(restrictedItems, "IncludeRecurrences", "False");
        numberOfMatchingItems= Dispatch.call(restrictedItems, "Count").toInt();

        Dispatch lastitemFound = null;
        int i = 0;
        if (restrictedItems != null && restrictedItems.m_pDispatch > 0) {
            Dispatch findItem = Dispatch.call(restrictedItems, "GetFirst").toDispatch();
            Dispatch firstItem = findItem;
            /*
            boolean Continue = true;
            while (Continue &&  findItem.m_pDispatch > 0) {

                findItem = Dispatch.call(restrictedItems, "GetNext").toDispatch();

                numberOfMatchingItems++;
                try{
                    if(Dispatch.get(findItem, "Start").toString() == null){
                        Continue = false;
                        numberOfMatchingItems--;
                    }
                }
                catch(Exception e){
                    Continue = false;
                }


              System.out.print(numberOfMatchingItems + " ");
            }
             */
            if(numberOfMatchingItems != 0){
                this.initSubLoca();
                findItem = firstItem;

                while ((findItem != null && findItem.m_pDispatch > 0)) {

                    lastitemFound = findItem;
                    try{

                        String subject = Dispatch.get(findItem, "Subject").toString();
                        String location = Dispatch.get(findItem, "Location").toString();
                        String date = Dispatch.get(findItem, "Start").toString();
                        findItem = Dispatch.call(restrictedItems, "GetNext").toDispatch();


                        this.subLoca[i][0] = subject;
                        this.subLoca[i][1] = location;
                        this.subLoca[i][3] = date;
                        i++;

                    }
                    catch(Exception e){
                        //e.printStackTrace();
                        i++;
                    }

                }
            }
            else{
                System.out.println("Found no appointements containing the appropriate keyword");
            }






        }

    }

    public void newLocation(int index, String location){
        subLoca[index][1] = location;
    }

    public void removeEvent(int index){
        subLoca[index][4] = ("Do not calculate");
    }

    public void  getDistance(String origin){
        try{
            if(numberOfMatchingItems != 0){
                String originFixed = "";
                String destFixed = "";
                String dest;
                for (int i = 0; i < this.subLoca.length; i++) {
                    dest = this.subLoca[i][1];
                    if(!eventIsGood(i)){
                        missingLocations += 1;
                    }
                    else{
                        destFixed += dest.replaceAll("\\W", "+");
                        originFixed += origin.replaceAll("\\W", "+");
                        if(!(i==subLoca.length-1)){
                            destFixed += "|";
                            originFixed += "|";
                        }
                    }
                }
                URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+originFixed+"&destinations="+destFixed);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                String line, outputString = "";
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    outputString += line;
                }
                if(numberOfMatchingItems != missingLocations){
                    JSONObject distanceJS = new JSONObject(outputString);
                    JSONArray arr = distanceJS.getJSONArray("rows");
                    JSONObject distance = arr.getJSONObject(0);
                    JSONArray arr2 = distance.getJSONArray("elements");
                    int j=0;
                        for (int i = 0; i < this.subLoca.length; i++) {

                            if(eventIsGood(i)){
                                JSONObject finalObj = arr2.getJSONObject(j).getJSONObject("distance");
                                subLoca[i][2] = finalObj.getString("text");
                                j++;
                            }
                        }
                    }
                 }
            }
        catch(Exception e){
            System.out.println("Error in calculating distances\n  Please Try Again");
            e.printStackTrace();
        }


    }

    public void checkLocations(){
        for (int i = 0; i < numberOfMatchingItems; i++) {
            if(subLoca[i][1].equals("")){
                subLoca[i][4] = "No address";
            }
        }
    }

    public void emptyErrors(){
        for (int i = 0; i < numberOfMatchingItems; i++) {
            subLoca[i][4] = "";
        }
    }

    public void initSubLoca() {
        this.subLoca = new String[numberOfMatchingItems][5];
        this.emptyErrors();
    }

    public boolean eventIsGood(int index) {
        return subLoca[index][4].equals("");
    }

    public String printOne(int i){
        String result = "";
        int count = i+1;
        result+= "\nEvent " + count + "\n   Subject: " + subLoca[i][0] +
                "\n    Date: " +subLoca[i][3] + "\n      Location "
                        + subLoca[i][1] ;



        return result;
    }

    public String getTotalDist(){
        double total = 0;
        if(missingLocations != numberOfMatchingItems && numberOfMatchingItems != 0){
            String parse;
            for (int i = 0; i < numberOfMatchingItems; i++) {
                if(eventIsGood(i)){
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
            }
        }
        String result =(String.valueOf(total));
        return "\nTotal Distance Traveled is " + result + "km";
    }

    public String printOut(){
        String result = "";
        if(numberOfMatchingItems != 0){
            result += getTotalDist() + "\n";
            for (int i = 0; i < this.subLoca.length; i++) {
                if(eventIsGood(i)){
                    int count = i+1;
                    result+= "\nEvent " + count + "\n   Subject: " + subLoca[i][0] +
                            "\n    Date: " +subLoca[i][3] + "\n      Location "
                            + subLoca[i][1] + "\n       Distance one way: " + subLoca[i][2];
                }

            }
        }

        return result;
    }

}
