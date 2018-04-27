package com.example.Lata.weatherapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Lata on 05-04-2017.
 */

public class fiveDayAdapter extends RecyclerView.Adapter<fiveDayAdapter.ViewHolder> {
    ArrayList<CityWeatherDetails> mData;
    Context mContext;
    IData activity;

    public fiveDayAdapter(ArrayList<CityWeatherDetails> mData, Context mContext, IData activity) {
        this.mData = mData;
        this.mContext = mContext;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date= null;
        try {
            //int day=Integer.parseInt(f.format(mData.get(position).getCurrentDate()));
            date = formatter.parse(mData.get(position).getCurrentDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat formatter1= new SimpleDateFormat("dd MMM''yy");
        holder.dateText.setText(formatter1.format(date));
        Picasso.with(mContext).load("http://developer.accuweather.com/sites/default/files/"+
                mData.get(position).getDayIcon()+"-s.png").into(holder.img);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            dateText= (TextView) itemView.findViewById(R.id.textView);
            img=(ImageView)itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.setUpData(mData.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface IData{
        public void setUpData(CityWeatherDetails position);
    }
}
