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
import java.util.ArrayList;

/**
 * Created by Lata on 04-04-2017.
 */

public class fiveDayForecastAsync extends AsyncTask<String,Void,ArrayList<CityWeatherDetails>> {
    IData activity;

    public fiveDayForecastAsync(IData activity) {
        this.activity = activity;
    }

    @Override
    protected void onPostExecute(ArrayList<CityWeatherDetails> cityWeatherDetailses) {
        super.onPostExecute(cityWeatherDetailses);
        activity.setWeatherDetails(cityWeatherDetailses);
    }

    @Override
    protected ArrayList<CityWeatherDetails> doInBackground(String... params) {
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
                JSONObject root=new JSONObject(sb.toString());
                JSONArray entryArray=root.getJSONArray("DailyForecasts");
                CityWeatherDetails details=new CityWeatherDetails();
                ArrayList<CityWeatherDetails> fiveDayDetails=new ArrayList<CityWeatherDetails>();
                for (int i=0;i<entryArray.length();i++){
                    JSONObject appJSONObject=entryArray.getJSONObject(i);
                    details=CityWeatherDetails.createWeatherDetails(appJSONObject);
                    details.setHeadline(root.getJSONObject("Headline").getString("Text"));
                    details.setExtendedForecast(root.getJSONObject("Headline").getString("MobileLink"));
                    fiveDayDetails.add(details);
                }
                return fiveDayDetails;
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
        public void setWeatherDetails(ArrayList<CityWeatherDetails> fiveDayDetails);
    }
}
