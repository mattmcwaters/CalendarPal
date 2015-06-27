import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Matt on 2015-06-25.
 */
public class bingMaps {

    public bingMaps(){
        try{
            URL url = new URL("http://dev.virtualearth.net/REST/V1/Routes/Driving?wp.0=119&rockport&crescent%toronto&&wp.1=515&younge&street$toronto&avoid=minimizeTolls&key=AhWH1KoM3tat5P2ArdFICR1kFGymlgfB2q9nr-Z5eJ0AsxnyyN0H58ssJQXz0A9n");


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String line, outputString = "";
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                outputString += line;
            }
            JSONObject distanceJS = new JSONObject(outputString);
            System.out.println(distanceJS.toString());

        }
        catch(Exception e){
            e.printStackTrace();
        }



    }

    public static void main(String[] args) {
        bingMaps testing = new bingMaps();

    }
}
