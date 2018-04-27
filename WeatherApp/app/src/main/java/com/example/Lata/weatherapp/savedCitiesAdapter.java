package com.example.Lata.weatherapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Lata on 06-04-2017.
 */

public class savedCitiesAdapter extends RecyclerView.Adapter<savedCitiesAdapter.ViewHolder> {

    ArrayList<SavedCityDetails> mData;
    Context mContext;
    IData activity;

    public savedCitiesAdapter(ArrayList<SavedCityDetails> mData, Context mContext, IData activity) {
        this.mData = mData;
        this.mContext = mContext;
        this.activity=activity;
    }

    @Override
    public savedCitiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.savedcities_layout,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(savedCitiesAdapter.ViewHolder holder, int position) {
        holder.savedCity.setText(mData.get(position).getCityName()+","+mData.get(position).getCountryName());
        holder.savedTemp.setText("Temperature : "+mData.get(position).getTemperature()+"°C");
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date= null;
        try {
            date = formatter.parse(mData.get(position).getObservationDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        PrettyTime p=new PrettyTime();
        holder.savedDate.setText("Last updated : "+p.format(date));
        if(mData.get(position).isFavorite()){
            holder.favorite.setImageResource(R.drawable.star_gold);
        }
        else {
            //holder.favorite.setBackgroundResource(R.drawable.img);
            holder.favorite.setImageResource(R.drawable.star_g);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView savedCity;
        TextView savedTemp;
        TextView savedDate;
        ImageView favorite;

        public ViewHolder(View itemView) {
            super(itemView);
            savedCity= (TextView) itemView.findViewById(R.id.savedCity);
            savedTemp=(TextView)itemView.findViewById(R.id.savedTemp);
            savedDate=(TextView)itemView.findViewById(R.id.savedTime);
            favorite=(ImageView) itemView.findViewById(R.id.star);
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.setFavorite(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    activity.deleteSavedCity(getAdapterPosition());
                    return true;
                }
            });
        }
    }

    public interface IData{
        public void setFavorite(int position);
        public void deleteSavedCity(int position);
    }
}
