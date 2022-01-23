package com.example.weatheapp;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter  extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder>{
    private Context context;
    public ArrayList<WeatherRVModal> weatherRVModalArrayList;

    public WeatherRVAdapter(ArrayList<WeatherRVModal> weatherRVModalArrayList, Context context) {
        this.weatherRVModalArrayList = weatherRVModalArrayList;
        this.context= context;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);
        return new ViewHolder(view);
    }
    public void setIcon(String icon,ImageView img){
        Runnable imageThread = new Runnable() {
            @Override
            public void run() {
                try {
                    String imgUrl = "https:"+icon;
                    Bitmap bitmap=downloadImg(imgUrl);
                    img.post(new Runnable() {
                        @Override
                        public void run() {
                            if(bitmap!=null) img.setImageBitmap(bitmap);
                            else{
                                Log.i("BITMAP", "returned bitmap is null");}
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(imageThread).start();
    }
    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {
         WeatherRVModal modal = weatherRVModalArrayList.get(position);
         holder.temperatureTV.setText(modal.getTemperature()+ "°F");
         //holder.conditionTV.setImageBitmap();
//         if(holder.conditionTV != null){
//             Picasso.get().load("https:".concat(modal.getIcon())).into(holder.conditionTV);
//         }
       // Log.i("iconstr", modal.getIcon());
        holder.windTV.setText(modal.getWindSpeed()+ "km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");

        try{
            Date t = input.parse(modal.getTime());
            holder.timeTV.setText(output.format(t));
           // holder.conditionTV.setImageBitmap();
           // setIcon(modal.getIcon(), holder.conditionTV);
           // Picasso.get().load("https:".concat(modal.getIcon())).into(holder.conditionTV);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
    }
    private Bitmap downloadImg(String myUrl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            // Log.i(DEBUG_TAG, "The response is: " + response);

            is = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        }catch(Exception e) {
            Log.i( "downloading image", e.toString());
        }finally {
            if (is != null) {
                is.close();
            }
        }

        return null;
    }
    @Override
    public int getItemCount() {
        return weatherRVModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        private TextView windTV, temperatureTV, timeTV;
        private ImageView conditionTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTV = itemView.findViewById(R.id.idTVWindSpeed);
            temperatureTV = itemView.findViewById(R.id.idTVTemperature);
            timeTV = itemView.findViewById(R.id.idTVTime);
            conditionTV = itemView.findViewById(R.id.idTVCondition);
        }
    }
}
