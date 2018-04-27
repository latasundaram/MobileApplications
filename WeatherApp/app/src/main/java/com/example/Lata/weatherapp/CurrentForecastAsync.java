package com.example.Lata.weatherapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Lata on 04-04-2017.
 */

public class CurrentForecastAsync extends AsyncTask<String,Void,CurrentCityDetails> {
    IData activity;

    public CurrentForecastAsync(IData activity) {
        this.activity = activity;
    }

    @Override
    protected void onPostExecute(CurrentCityDetails currentCityDetails) {
        super.onPostExecute(currentCityDetails);
        activity.setForecast(currentCityDetails);
    }

    @Override
    protected CurrentCityDetails doInBackground(String... params) {
        try {
            URL url=new URL(params[0]);
            HttpURLConnection con= (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            int statusCode=con.getResponseCode();
            if(statusCode==HttpURLConnection.HTTP_OK){
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb=new StringBuilder();
                String line=br.readLine();
                while(line!=null){
                    sb.append(line);
                    line=br.readLine();
                }
                JSONArray entryArray=new JSONArray(sb.toString());
                JSONObject appJSONObject=entryArray.getJSONObject(0);
                return CurrentCityDetails.createDetails(appJSONObject);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface IData{
        public void setForecast(CurrentCityDetails details);
    }
}
