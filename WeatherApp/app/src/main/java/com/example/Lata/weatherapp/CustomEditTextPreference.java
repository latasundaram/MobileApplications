package com.example.Lata.weatherapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Lata on 07-04-2017.
 */

public class CustomEditTextPreference extends DialogPreference implements LocationKeyAsync.IData{

    EditText city;
    EditText country;
    static final String PREFS_NAME="CitySharedPref";
    static final String CITY_KEY="current_city_key";
    static final String CITY_NAME="current_city_name";
    static final String COUNTRY_NAME="current_country_name";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    String cityKey,cityName,countryName;
    boolean flag;

    public CustomEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected View onCreateDialogView() {
        sharedPreferences=getContext().getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        cityKey=sharedPreferences.getString(CITY_KEY,"");
        cityName=sharedPreferences.getString(CITY_NAME,"");
        countryName=sharedPreferences.getString(COUNTRY_NAME,"");
        View v = LayoutInflater.from(getContext()).inflate(R.layout.city_preference, null);
        city = (EditText) v.findViewById(R.id.cityPref);
        country = (EditText) v.findViewById(R.id.countryPref);
        if(!cityKey.equals("")) {
            city.setText(cityName);
            country.setText(countryName);
        }
        return v;
    }

    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        if(cityKey.equals("")) {
            builder.setTitle("Enter city details");
            builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cityName=city.getText().toString();
                    countryName=country.getText().toString();
                    flag=true;
                    if(!cityName.equals(""))
                        new LocationKeyAsync(CustomEditTextPreference.this).execute("http://dataservice.accuweather.com/locations/v1/" + countryName + "/" +
                                "search?apikey=EnwGEN25l4v3yVWrMvXAmGJxbchHvbM3&q=" + cityName);

                    else
                        Toast.makeText(getContext(), "Invalid details", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", null);
            super.onPrepareDialogBuilder(builder);
        }
        else {
            builder.setTitle("Update city details");
            builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cityName=city.getText().toString();
                    countryName=city.getText().toString();
                    flag=false;
                    if(!cityName.equals(""))
                        new LocationKeyAsync(CustomEditTextPreference.this).execute("http://dataservice.accuweather.com/locations/v1/"+countryName+"/" +
                                "search?apikey=EnwGEN25l4v3yVWrMvXAmGJxbchHvbM3&q="+cityName);
                    else
                        Toast.makeText(getContext(), "Invalid details", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", null);
            super.onPrepareDialogBuilder(builder);
        }
    }

    @Override
    public void setLocationKey(String lockey) {
        if(lockey!=null) {
            cityKey = lockey;
            editor=sharedPreferences.edit();
            editor.putString(CITY_KEY, cityKey);
            editor.putString(CITY_NAME, cityName);
            editor.putString(COUNTRY_NAME, countryName);
            editor.commit();
            if(flag)
                Toast.makeText(getContext(),"Current city saved", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(),"Current city updated", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getContext(), "City not found", Toast.LENGTH_SHORT).show();
    }
}
