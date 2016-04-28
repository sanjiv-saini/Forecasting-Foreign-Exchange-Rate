/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restApi;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 *
 * @author sanju singh
 */
public class JsonDataParser {
    
      public String[] parseXRate(String jsonStr){

        String[] rate = new String[4];

        JSONObject obj = new JSONObject(jsonStr);
        obj = obj.getJSONObject("query");
        obj = obj.getJSONObject("results");
        JSONArray jsonArray = obj.getJSONArray("rate");
        int n = jsonArray.length();       

        for (int i = 0; i < n; ++i) {
          JSONObject curr = jsonArray.getJSONObject(i);
          rate[i] = curr.getString("Rate");          
        }
        
        return rate;
      }     
}
