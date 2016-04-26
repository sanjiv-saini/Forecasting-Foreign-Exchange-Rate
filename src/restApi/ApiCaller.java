/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.SwingWorker;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.apache.http.HttpHeaders.USER_AGENT;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;


/**
 *
 * @author sanju singh
 */
public class ApiCaller extends SwingWorker<Object, Void>{
    
    String builtUrl;
    String jsonStr;
    
    @Override
    protected Void doInBackground() throws IOException{

            String baseUrl = "https://query.yahooapis.com/v1/public/yql?";
            String q = "select * from yahoo.finance.xchange where pair in (\"EURINR\")";
            String diagnostics = "true";
            String env = "store://datatables.org/alltableswithkeys";
            String format = "json";
            
            List<NameValuePair> params = new LinkedList<NameValuePair>();
            
            params.add(new BasicNameValuePair("q", q));
            params.add(new BasicNameValuePair("format", format));
            params.add(new BasicNameValuePair("diagnostics", diagnostics));
            params.add(new BasicNameValuePair("env", env));
            
            String paramString = URLEncodedUtils.format(params, "utf-8");
            
            builtUrl = baseUrl + paramString;
            
            HttpClient client = HttpClients.createDefault();

            HttpGet request = new HttpGet(builtUrl);
            
            // add request header
            request.addHeader("User-Agent", USER_AGENT);
            
            
            HttpResponse response = client.execute(request);
            
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            
            jsonStr = result.toString();
  
          return null;

    }

     @Override
    public void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(ApiCaller.class.getName()).log(Level.SEVERE, null, ex);
        }
               System.out.println("\n" + builtUrl + "\n\n" + jsonStr);      

    }
    
  }
  
