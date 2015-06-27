import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Matt on 2015-06-25.
 */
public class googleDirections {
    public googleDirections(calendarConnector cC, String origin){

        try{
            String dest;
            String destFixed;
            String originFixed;
            URL url;
            for (int i = 0; i < cC.subLoca.length; i++) {
                dest = cC.subLoca[i][1];
                if(cC.eventIsGood(i)){
                    destFixed = dest.replaceAll("\\W", "+");
                    originFixed = origin.replaceAll("\\W", "+");
                    url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin="+originFixed+"&destination="+destFixed);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    String line, outputString = "";
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    while ((line = reader.readLine()) != null) {
                        outputString += line;
                    }
                    JSONObject distanceJS = new JSONObject(outputString);

                    String result = distanceJS.getJSONArray("routes").getJSONObject(0).
                            getJSONArray("legs").getJSONObject(0).getJSONObject("distance").get("text").toString();

                    cC.subLoca[i][2] = result;



                }


            }




        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


}
