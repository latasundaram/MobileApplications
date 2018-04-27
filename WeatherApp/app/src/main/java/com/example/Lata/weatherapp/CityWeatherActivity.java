package com.example.Lata.weatherapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CityWeatherActivity extends AppCompatActivity implements LocationKeyAsync.IData,
        fiveDayForecastAsync.IData,fiveDayAdapter.IData,CurrentForecastAsync.IData{

    String searchCityName,searchCountryName,searchCityKey;
    ProgressDialog pd;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    fiveDayAdapter adapter;
    private DatabaseReference mDatabase;
    private DatabaseReference ref;
    ArrayList<SavedCityDetails> savedCities;
    ArrayList<String> savedCityKeys= new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    static final String PREFS_NAME="CitySharedPref";
    static final String CITY_KEY="current_city_key";
    static final String CITY_NAME="current_city_name";
    static final String COUNTRY_NAME="current_country_name";
    String tempUnit;

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ref =  FirebaseDatabase.getInstance().getReference("/SavedCities");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Count " ,""+dataSnapshot.getChildrenCount());
                if(dataSnapshot.getChildrenCount()>0){
                    savedCities=new ArrayList<SavedCityDetails>();
                    savedCityKeys=new ArrayList<String>();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        savedCityKeys.add(postSnapshot.getKey());
                        SavedCityDetails savedDetails = postSnapshot.getValue(SavedCityDetails.class);
                        savedCities.add(savedDetails);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        sharedPreferences=this.getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        tempUnit=sharedPreferences.getString(MainActivity.TEMP_UNIT,"Celcius");
        searchCityName=this.getIntent().getStringExtra("cityName");
        searchCountryName=this.getIntent().getStringExtra("countryName");
        new LocationKeyAsync(this).execute("http://dataservice.accuweather.com/locations/v1/"+searchCountryName+"/" +
                "search?apikey=EnwGEN25l4v3yVWrMvXAmGJxbchHvbM3&q="+searchCityName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        pd=new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.cityweather_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.SaveCity){
            new CurrentForecastAsync(CityWeatherActivity.this).execute("http://dataservice.accuweather.com/currentconditions/v1/" +
                    searchCityKey+"?apikey=EnwGEN25l4v3yVWrMvXAmGJxbchHvbM3");
        }
        else if(item.getItemId()==R.id.SetCurrentCity){
            sharedPreferences=this.getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
            editor=sharedPreferences.edit();
            if(!sharedPreferences.getString(CITY_KEY,"").equals("")){
                editor.putString(CITY_KEY, searchCityKey);
                editor.putString(CITY_NAME, searchCityName);
                editor.putString(COUNTRY_NAME, searchCountryName);
                editor.commit();
                Toast.makeText(getApplicationContext(),"Current city updated",Toast.LENGTH_SHORT).show();
            }
            else{
                editor.putString(CITY_KEY, searchCityKey);
                editor.putString(CITY_NAME, searchCityName);
                editor.putString(COUNTRY_NAME, searchCountryName);
                editor.commit();
                Toast.makeText(getApplicationContext(),"Current city saved",Toast.LENGTH_SHORT).show();
            }
        }
        else if(item.getItemId()==R.id.Settings){
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setLocationKey(String lockey) {
        if(lockey!=null) {
           searchCityKey=lockey;
           new fiveDayForecastAsync(this).execute("http://dataservice.accuweather.com/forecasts/v1/daily/5day/" +
                   searchCityKey+"?apikey=EnwGEN25l4v3yVWrMvXAmGJxbchHvbM3");
        }
        else {
            Toast.makeText(getApplicationContext(),"City not found",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void setWeatherDetails(ArrayList<CityWeatherDetails> fiveDayDetails) {
        TextView titleText= (TextView) findViewById(R.id.title);
        titleText.setText("Daily forecasts for "+searchCityName+", "+searchCountryName);
        titleText.setTextColor(Color.parseColor("#00DDFF"));

        recyclerView= (RecyclerView) findViewById(R.id.horizontalRecyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,1,GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new fiveDayAdapter(fiveDayDetails,this,this);
        recyclerView.setAdapter(adapter);

        setDetails(fiveDayDetails.get(0));
        pd.dismiss();
    }

    public void setDetails(final CityWeatherDetails details) {
        TextView extendedForecast=(TextView)findViewById(R.id.extendedForecast);
        extendedForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(details.getExtendedForecast()));
                startActivity(intent);
            }
        });

        TextView headlineText= (TextView) findViewById(R.id.headLine);
        headlineText.setText(details.getHeadline());

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date= null;
        try {
            date = formatter.parse(details.getCurrentDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat formatter1= new SimpleDateFormat("MMM dd, yyyy");

        TextView forecastDate=(TextView) findViewById(R.id.forecastDate);
        forecastDate.setText("Forecast On " + formatter1.format(date));

        TextView temperature=(TextView)findViewById(R.id.temperature);
        double min,max,t;
        if(tempUnit.equals("Celcius")){
            t=(Double.parseDouble(details.getMinTemp())-32);
            t=t*5/9;
            min=Math.round(t*10.0)/10.0;
            t=(Double.parseDouble(details.getMaxTemp())-32);
            t=t*5/9;
            max=Math.round(t*10.0)/10.0;
            temperature.setText(max+"°/"+min+"°");
        }
        else if(tempUnit.equals("Fahrenheit")){
            temperature.setText(details.getMaxTemp()+"°/"+details.getMinTemp()+"°");
        }

        ImageView dayImage=(ImageView)findViewById(R.id.dayImage);
        Picasso.with(getApplicationContext()).load("http://developer.accuweather.com/sites/default/files/"+
                details.getDayIcon()+"-s.png").into(dayImage);

        ImageView nightImage=(ImageView)findViewById(R.id.nightImage);
        Picasso.with(getApplicationContext()).load("http://developer.accuweather.com/sites/default/files/"+
                details.getNightIcon()+"-s.png").into(nightImage);

        TextView dayText=(TextView)findViewById(R.id.dayWeather);
        dayText.setText(details.getDayWeather().trim());

        TextView nightText=(TextView)findViewById(R.id.nightWeather);
        nightText.setText(details.getNightWeather().trim());

        TextView moreDetails=(TextView)findViewById(R.id.moreDetails);
        moreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(details.getMobileLink()));
                startActivity(intent);
            }
        });
    }

    @Override
    public void setUpData(CityWeatherDetails position) {
        setDetails(position);
    }

    @Override
    public void setForecast(CurrentCityDetails details) {
        if(!savedCityKeys.contains(searchCityKey)) {
            SavedCityDetails sd = new SavedCityDetails();
            sd.setCityKey(searchCityKey);
            sd.setCityName(searchCityName);
            sd.setCountryName(searchCountryName);
            sd.setTemperature(details.getTemperature());
            sd.setObservationDate(details.getObservationDate());
            sd.setFavorite(false);

            Map<String, Object> postValues = sd.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/SavedCities/" + searchCityKey, postValues);

            mDatabase.updateChildren(childUpdates);
            Toast.makeText(getApplicationContext(),"City Saved",Toast.LENGTH_SHORT).show();
        }
        else {
            mDatabase.child("SavedCities").child(searchCityKey).child("temperature")
                    .setValue(details.getTemperature());
            mDatabase.child("SavedCities").child(searchCityKey).child("observationDate")
                    .setValue(details.getObservationDate());
            Toast.makeText(getApplicationContext(),"City Updated",Toast.LENGTH_SHORT).show();
        }
    }
}
