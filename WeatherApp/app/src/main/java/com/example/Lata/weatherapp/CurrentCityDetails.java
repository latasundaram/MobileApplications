package com.example.Lata.weatherapp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lata on 04-04-2017.
 */

public class CurrentCityDetails {
    String weatherText,weatherIcon,temperature;
    String observationDate;

    static public CurrentCityDetails createDetails(JSONObject js) throws JSONException {
        CurrentCityDetails details=new CurrentCityDetails();
        details.setObservationDate(js.getString("LocalObservationDateTime"));
        details.setWeatherText(js.getString("WeatherText"));
        if(Integer.parseInt(js.getString("WeatherIcon"))<10)
            details.setWeatherIcon("0"+js.getString("WeatherIcon"));
        else
            details.setWeatherIcon(js.getString("WeatherIcon"));
        details.setTemperature(js.getJSONObject("Temperature").getJSONObject("Metric").getString("Value"));
        return details;
    }

    public String getWeatherText() {
        return weatherText;
    }

    public void setWeatherText(String weatherText) {
        this.weatherText = weatherText;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getObservationDate() {
        return observationDate;
    }

    public void setObservationDate(String observationDate) {
        this.observationDate = observationDate;
    }

    @Override
    public String toString() {
        return "CurrentCityDetails{" +
                "weatherText='" + weatherText + '\'' +
                ", weatherIcon='" + weatherIcon + '\'' +
                ", temperature='" + temperature + '\'' +
                ", observationDate=" + observationDate +
                '}';
    }
}
