package com.example.Lata.weatherapp;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lata on 05-04-2017.
 */

public class SavedCityDetails {
    String cityName,cityKey,countryName,temperature,observationDate;
    boolean favorite;

    public SavedCityDetails(){

    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityKey() {
        return cityKey;
    }

    public void setCityKey(String cityKey) {
        this.cityKey = cityKey;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
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

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "SavedCityDetails{" +
                "cityName='" + cityName + '\'' +
                ", cityKey='" + cityKey + '\'' +
                ", countryName='" + countryName + '\'' +
                ", temperature='" + temperature + '\'' +
                ", observationDate='" + observationDate + '\'' +
                ", favorite=" + favorite +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("cityKey",cityKey);
        result.put("cityName", cityName);
        result.put("countryName", countryName);
        result.put("temperature", temperature);
        result.put("observationDate", observationDate);
        result.put("favorite", favorite);
        return result;
    }
}
