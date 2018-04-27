package com.example.Lata.weatherapp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lata on 05-04-2017.
 */

public class CityWeatherDetails {
    private String headline,extendedForecast,currentDate,minTemp,maxTemp,dayIcon,dayWeather,nightIcon,nightWeather,mobileLink;

    static public CityWeatherDetails createWeatherDetails(JSONObject js) throws JSONException {
        CityWeatherDetails details=new CityWeatherDetails();
        details.setCurrentDate(js.getString("Date"));
        details.setMinTemp(js.getJSONObject("Temperature").getJSONObject("Minimum").getString("Value"));
        details.setMaxTemp(js.getJSONObject("Temperature").getJSONObject("Maximum").getString("Value"));
        if(Integer.parseInt(js.getJSONObject("Day").getString("Icon"))<10)
            details.setDayIcon("0"+js.getJSONObject("Day").getString("Icon"));
        else
            details.setDayIcon(js.getJSONObject("Day").getString("Icon"));
        details.setDayWeather(js.getJSONObject("Day").getString("IconPhrase"));
        if(Integer.parseInt(js.getJSONObject("Night").getString("Icon"))<10)
            details.setDayIcon("0"+js.getJSONObject("Night").getString("Icon"));
        else
            details.setNightIcon(js.getJSONObject("Night").getString("Icon"));
        details.setNightWeather(js.getJSONObject("Night").getString("IconPhrase"));
        details.setMobileLink(js.getString("MobileLink"));
        return details;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getDayIcon() {
        return dayIcon;
    }

    public void setDayIcon(String dayIcon) {
        this.dayIcon = dayIcon;
    }

    public String getDayWeather() {
        return dayWeather;
    }

    public void setDayWeather(String dayWeather) {
        this.dayWeather = dayWeather;
    }

    public String getNightIcon() {
        return nightIcon;
    }

    public void setNightIcon(String nightIcon) {
        this.nightIcon = nightIcon;
    }

    public String getNightWeather() {
        return nightWeather;
    }

    public void setNightWeather(String nightWeather) {
        this.nightWeather = nightWeather;
    }

    public String getMobileLink() {
        return mobileLink;
    }

    public void setMobileLink(String mobileLink) {
        this.mobileLink = mobileLink;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getExtendedForecast() {
        return extendedForecast;
    }

    public void setExtendedForecast(String extendedForecast) {
        this.extendedForecast = extendedForecast;
    }

    @Override
    public String toString() {
        return "CityWeatherDetails{" +
                "currentDate='" + currentDate + '\'' +
                ", minTemp='" + minTemp + '\'' +
                ", maxTemp='" + maxTemp + '\'' +
                ", dayIcon='" + dayIcon + '\'' +
                ", dayWeather='" + dayWeather + '\'' +
                ", nightIcon='" + nightIcon + '\'' +
                ", nightWeather='" + nightWeather + '\'' +
                ", mobileLink='" + mobileLink + '\'' +
                '}';
    }
}
