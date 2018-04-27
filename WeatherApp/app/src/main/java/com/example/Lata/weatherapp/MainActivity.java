package com.example.Lata.weatherapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LocationKeyAsync.IData,
        CurrentForecastAsync.IData,savedCitiesAdapter.IData {

    static final String PREFS_NAME="CitySharedPref";
    static final String CITY_KEY="current_city_key";
    static final String CITY_NAME="current_city_name";
    static final String COUNTRY_NAME="current_country_name";
    static final String TEMP_UNIT="temperature_unit";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    LinearLayout ll;
    String cityKey,cityName,countryName,temperatureUnit;
    String searchCityKey,searchCityName,searchCountryName;
    AlertDialog.Builder alert;
    ArrayList<SavedCityDetails> savedCities;
    DatabaseReference mDatabase;
    DatabaseReference ref;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    savedCitiesAdapter adapter;
    SharedPreferences sharedPref;

    @Override
    protected void onRestart() {
        super.onRestart();
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        ref =  FirebaseDatabase.getInstance().getReference("/SavedCities");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Count " ,""+dataSnapshot.getChildrenCount());
                    savedCities=new ArrayList<SavedCityDetails>();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        SavedCityDetails savedDetails = postSnapshot.getValue(SavedCityDetails.class);
                        savedCities.add(savedDetails);
                    }
                    setAdapterData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        sharedPreferences=this.getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        cityKey=sharedPreferences.getString(CITY_KEY,"");
        cityName=sharedPreferences.getString(CITY_NAME,"");
        countryName=sharedPreferences.getString(COUNTRY_NAME,"");
        temperatureUnit=sharedPreferences.getString(TEMP_UNIT,"");

        /*sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.OnSharedPreferenceChangeListener listener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                        sharedPreferences=MainActivity.this.getSharedPreferences(MainActivity.PREFS_NAME,MODE_PRIVATE);
                        editor=sharedPreferences.edit();
                        editor.putString(MainActivity.TEMP_UNIT, prefs.getString("TempPreference",""));
                        Log.d("demo",sharedPreferences.getString(MainActivity.TEMP_UNIT,""));
                    }
                };
        sharedPref.registerOnSharedPreferenceChangeListener(listener);*/

        ll= (LinearLayout) findViewById(R.id.topLayout);
        if(cityKey.equals("")){
            TextView textView=new TextView(this);
            textView.setText(R.string.city_label);
            ll.addView(textView);

            Button setCItyButton=new Button(this);
            setCItyButton.setText(R.string.set_city_buttonlabel);
            setCItyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout layout = new LinearLayout(MainActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText cityText = new EditText(MainActivity.this);
                    cityText.setHint("Enter Your City");
                    layout.addView(cityText);

                    final EditText countryText = new EditText(MainActivity.this);
                    countryText.setHint("Enter Your Country");
                    layout.addView(countryText);

                    TextView title=new TextView(MainActivity.this);
                    title.setText("Enter City Details");
                    title.setTextColor(Color.parseColor("#00DDFF"));

                    alert=new AlertDialog.Builder(v.getContext());
                    alert.setView(layout);
                    alert.setCustomTitle(title)
                            .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cityName=cityText.getText().toString();
                                    countryName=countryText.getText().toString();
                                    new LocationKeyAsync(MainActivity.this).execute("http://dataservice.accuweather.com/locations/v1/US/" +
                                            "search?apikey=EnwGEN25l4v3yVWrMvXAmGJxbchHvbM3&q="+cityName);
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.show();

                }
            });
            ll.addView(setCItyButton);
        }
        else {
            new CurrentForecastAsync(MainActivity.this).execute("http://dataservice.accuweather.com/currentconditions/v1/" +
                    cityKey+"?apikey=EnwGEN25l4v3yVWrMvXAmGJxbchHvbM3");
        }

        final TextView searchCity= (TextView) findViewById(R.id.cityName);

        final TextView searchCountry= (TextView) findViewById(R.id.countryName);


        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCityName=searchCity.getText().toString();
                searchCountryName=searchCountry.getText().toString();
                Intent i=new Intent(getApplicationContext(),CityWeatherActivity.class);
                i.putExtra("cityName",searchCityName);
                i.putExtra("countryName",searchCountryName);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        ref =  FirebaseDatabase.getInstance().getReference("/SavedCities");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Count " ,""+dataSnapshot.getChildrenCount());
                    savedCities=new ArrayList<SavedCityDetails>();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        SavedCityDetails savedDetails = postSnapshot.getValue(SavedCityDetails.class);
                        savedCities.add(savedDetails);
                    }
                    setAdapterData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        sharedPreferences=this.getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        cityKey=sharedPreferences.getString(CITY_KEY,"");
        cityName=sharedPreferences.getString(CITY_NAME,"");
        countryName=sharedPreferences.getString(COUNTRY_NAME,"");
        temperatureUnit=sharedPreferences.getString(TEMP_UNIT,"Celcius");

        /*sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.OnSharedPreferenceChangeListener listener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                        sharedPreferences=MainActivity.this.getSharedPreferences(MainActivity.PREFS_NAME,MODE_PRIVATE);
                        editor=sharedPreferences.edit();
                        editor.putString(MainActivity.TEMP_UNIT, prefs.getString("TempPreference",""));
                        Log.d("demo",sharedPreferences.getString(MainActivity.TEMP_UNIT,""));
                    }
                };
        sharedPref.registerOnSharedPreferenceChangeListener(listener);*/

        ll= (LinearLayout) findViewById(R.id.topLayout);
        if(cityKey.equals("")){
            TextView textView=new TextView(this);
            textView.setText(R.string.city_label);
            ll.addView(textView);

            Button setCItyButton=new Button(this);
            setCItyButton.setText(R.string.set_city_buttonlabel);
            setCItyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout layout = new LinearLayout(MainActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText cityText = new EditText(MainActivity.this);
                    cityText.setHint("Enter Your City");
                    layout.addView(cityText);

                    final EditText countryText = new EditText(MainActivity.this);
                    countryText.setHint("Enter Your Country");
                    layout.addView(countryText);

                    TextView title=new TextView(MainActivity.this);
                    title.setText("Enter City Details");
                    title.setTextColor(Color.parseColor("#00DDFF"));

                    alert=new AlertDialog.Builder(v.getContext());
                    alert.setView(layout);
                    alert.setCustomTitle(title)
                            .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cityName=cityText.getText().toString();
                                    countryName=countryText.getText().toString();
                                    new LocationKeyAsync(MainActivity.this).execute("http://dataservice.accuweather.com/locations/v1/"+
                                           countryName +"/" +"search?apikey=EnwGEN25l4v3yVWrMvXAmGJxbchHvbM3&q="+cityName);
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                    });
                    alert.show();

                }
            });
            ll.addView(setCItyButton);
        }
        else {
            new CurrentForecastAsync(MainActivity.this).execute("http://dataservice.accuweather.com/currentconditions/v1/" +
                    cityKey+"?apikey=EnwGEN25l4v3yVWrMvXAmGJxbchHvbM3");
        }

        final TextView searchCity= (TextView) findViewById(R.id.cityName);

        final TextView searchCountry= (TextView) findViewById(R.id.countryName);


        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCityName=searchCity.getText().toString();
                searchCountryName=searchCountry.getText().toString();
                if(searchCityName.equals("")||searchCountryName.equals("")){
                    Toast.makeText(MainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(getApplicationContext(), CityWeatherActivity.class);
                    i.putExtra("cityName", searchCityName);
                    i.putExtra("countryName", searchCountryName);
                    startActivity(i);
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.Settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setLocationKey(String lockey) {
       if(lockey!=null) {
           cityKey = lockey;
           editor.putString(CITY_KEY, cityKey);
           editor.putString(CITY_NAME, cityName);
           editor.putString(COUNTRY_NAME, countryName);
           editor.commit();
           ll.removeAllViews();
           new CurrentForecastAsync(MainActivity.this).execute("http://dataservice.accuweather.com/currentconditions/v1/" +
                   cityKey+"?apikey=EnwGEN25l4v3yVWrMvXAmGJxbchHvbM3");
           Toast.makeText(getApplicationContext(),"Current City details saved",Toast.LENGTH_SHORT).show();
       }
       else {
           Toast.makeText(getApplicationContext(),"City not found",Toast.LENGTH_SHORT).show();
       }
    }

    @Override
    public void setForecast(CurrentCityDetails details) {
        ll=(LinearLayout)findViewById(R.id.topLayout);
        TextView city=new TextView(MainActivity.this);
        city.setText(cityName+", "+countryName);
        city.setTypeface(Typeface.DEFAULT_BOLD);
        city.setGravity(Gravity.CENTER);
        ll.addView(city);

        TextView weatherText=new TextView(MainActivity.this);
        weatherText.setText(details.getWeatherText());
        weatherText.setGravity(Gravity.CENTER);
        ll.addView(weatherText);

        ImageView img=new ImageView(MainActivity.this);
        Picasso.with(getApplicationContext()).load("http://developer.accuweather.com/sites/default/files/"+
                details.getWeatherIcon()+"-s.png").into(img);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
        layoutParams.gravity=Gravity.CENTER;
        img.setLayoutParams(layoutParams);
        ll.addView(img);

        TextView temperature=new TextView(MainActivity.this);
        if(temperatureUnit.equals("Celcius")) {
            temperature.setText("Temperature : " + details.getTemperature() + "°C");
        }
        else if(temperatureUnit.equals("Fahrenheit")){
            double temp=Math.round((Double.parseDouble(details.getTemperature())*(9/5)+32)*10.0)/10.0;
            temperature.setText("Temperature : " + temp + "°F");
        }
        temperature.setGravity(Gravity.CENTER);
        ll.addView(temperature);

        TextView updateTime=new TextView(MainActivity.this);
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date= null;
        try {
            date = formatter.parse(details.getObservationDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        PrettyTime p=new PrettyTime();
        updateTime.setText("Updated "+p.format(date));
        updateTime.setGravity(Gravity.CENTER);
        ll.addView(updateTime);
    }

    @Override
    public void setFavorite(int position) {
        if(savedCities.get(position).isFavorite())
            mDatabase.child("SavedCities").child(savedCities.get(position).getCityKey()).child("favorite")
                .setValue(false);
        else
            mDatabase.child("SavedCities").child(savedCities.get(position).getCityKey()).child("favorite")
                    .setValue(true);
    }

    @Override
    public void deleteSavedCity(int position) {
        mDatabase.child("SavedCities").child(savedCities.get(position).getCityKey()).removeValue();
    }

    public void setAdapterData(){
        if(savedCities.size()>0) {
            TextView savedCitiesText = (TextView) findViewById(R.id.savedCitiesText);
            savedCitiesText.setText("Saved Cities");
            savedCitiesText.setTypeface(Typeface.DEFAULT_BOLD);
        }
        else {
            TextView savedCitiesText=(TextView) findViewById(R.id.savedCitiesText);
            savedCitiesText.setTypeface(Typeface.DEFAULT);
            savedCitiesText.setText("There are no cities to Display.\nSearch the city from the search box and save.");
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new savedCitiesAdapter(savedCities, this, this);
        recyclerView.setAdapter(adapter);
    }
}
