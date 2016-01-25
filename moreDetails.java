package com.yuxuanli.weatherforecastinandroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by LiYuxuan on 11/17/15.
 */
public class moreDetails extends Activity {

    private static final int TRANSPARENT_GREY = Color.argb(0, 185, 185, 185);
    private static final int FILTERED_GREY = Color.argb(155, 185, 185, 185);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moredetails);

        Intent i = getIntent();

        final Button next24hours = (Button) findViewById(R.id.next24hours_btn);
        final Button next7days = (Button) findViewById(R.id.next7days_btn);
        final Button moreHours_btn = new Button(this);
        next7days.setBackgroundColor(TRANSPARENT_GREY);
        final TableLayout next24hours_layout = (TableLayout) findViewById(R.id.next24hours_layout);
        final TableLayout next7days_layout = (TableLayout) findViewById(R.id.next7days_layout);

        String result = i.getStringExtra("result");

        String unit = i.getStringExtra("unit");


        final TextView more_details_header = (TextView) findViewById(R.id.more_details_header);
        more_details_header.setText(i.getStringExtra("header"));

        TextView temp_header = (TextView) findViewById(R.id.temp_header);
        if(unit.equals("us")){
            temp_header.setText("Temp(\u00b0F)");}
        else{
            temp_header.setText("Temp(\u00b0C)");
        }

        try{
            JSONObject jObject = new JSONObject(result);

            String timezone = jObject.getString("timezone");
            //Log.d("timezone",timezone);
            JSONObject hourly = jObject.getJSONObject("hourly");

            JSONArray data = hourly.getJSONArray("data");

            final TableRow[] hidedTableRow = new TableRow[24];

            for(int j=1;j<49;j++){
                JSONObject currentData = data.getJSONObject(j);

                String time = getTime(currentData.getInt("time"), timezone);

                String temperature = getTemp(currentData.getDouble("temperature"));

                TableRow currentRow = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                currentRow.setLayoutParams(lp);
                if(j%2 == 0){
                    //do nothing
                }
                else{
                    currentRow.setBackgroundColor(Color.GRAY);
                }
                currentRow.setPadding(0, 20, 0, 20);

                TextView currentTime = new TextView(this);
                currentTime.setText(time);
                currentTime.setLayoutParams(lp);
                currentTime.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);

                ImageView currentIcon = new ImageView(this);
                setWeatherImage(currentData.getString("icon"), currentIcon);
                TableRow.LayoutParams imageParams = new TableRow.LayoutParams(100,100);
                currentIcon.setLayoutParams(imageParams);

                TextView currentTemp = new TextView(this);
                currentTemp.setText(temperature);
                currentTemp.setLayoutParams(lp);
                currentTemp.setGravity(Gravity.CENTER);

                currentRow.addView(currentTime);
                currentRow.addView(currentIcon);
                currentRow.addView(currentTemp);

                next24hours_layout.addView(currentRow,new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                if(j>24){
                    currentRow.setVisibility(currentRow.GONE);
                    hidedTableRow[j-25] = currentRow;
                }

                if(j==25){
                    moreHours_btn.setText("+");
                    moreHours_btn.setTextColor(Color.BLACK);
                    next24hours_layout.addView(moreHours_btn);
                }

            }

            moreHours_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    moreHours_btn.setVisibility(moreHours_btn.GONE);
                            for(int h=0; h<24;h++){
                                hidedTableRow[h].setVisibility(hidedTableRow[h].VISIBLE);
                            }
                }
            });

            JSONObject daily = jObject.getJSONObject("daily");
            JSONArray daily_data = daily.getJSONArray("data");


            for(int j=1; j<8 ; j++){
                String date, minAndMax;
                JSONObject current_data = daily_data.getJSONObject(j);
                date = getDate(current_data.getInt("time"), timezone);
                minAndMax = getMinAndMax(current_data.getDouble("temperatureMin"), current_data.getDouble("temperatureMax"), unit);

                TableRow currentRow = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                currentRow.setLayoutParams(lp);

                TextView currentDate = new TextView(this);
                currentDate.setText(date);
                currentDate.setTextSize(20);
                currentDate.setTypeface(null, Typeface.BOLD);
                currentDate.setPadding(20, 30, 20, 30);

                TextView currentMinAndMax = new TextView(this);
                currentMinAndMax.setText(minAndMax);
                currentMinAndMax.setTextSize(18);
                currentMinAndMax.setPadding(20, 30, 20, 30);

                ImageView currentIcon = new ImageView(this);
                currentIcon.setPadding(0,30,0,30);
                setWeatherImage(current_data.getString("icon"), currentIcon);
                TableRow.LayoutParams imageParams = new TableRow.LayoutParams(200,200);
                currentIcon.setLayoutParams(imageParams);

                currentRow.addView(currentDate);
                currentRow.addView(currentIcon);

                TableRow secondRow = new TableRow(this);

                secondRow.setLayoutParams(lp);
                secondRow.addView(currentMinAndMax);

                currentRow.setBackgroundColor(setColor(j));
                secondRow.setBackgroundColor(setColor(j));

                TableRow blankRow = new TableRow(this);
                blankRow.setLayoutParams(lp);
                TextView blankText = new TextView(this);
                blankText.setText("  ");
                blankRow.addView(blankText);

                next7days_layout.addView(currentRow, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                next7days_layout.addView(secondRow,new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                next7days_layout.addView(blankRow,new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            }
        }
        catch(Exception e){}

        next24hours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next24hours_layout.setVisibility(TableLayout.VISIBLE);
                next7days_layout.setVisibility(TableLayout.GONE);
                next7days.setBackgroundColor(TRANSPARENT_GREY);
                next24hours.setBackgroundColor(FILTERED_GREY);
            }
        });

        next7days.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                next24hours_layout.setVisibility(TableLayout.GONE);
                next7days_layout.setVisibility(TableLayout.VISIBLE);

                next7days.setBackgroundColor(FILTERED_GREY);
                next24hours.setBackgroundColor(TRANSPARENT_GREY);

            }
        });

    }

    public String getTime(int timestamp,String timezone){
        Date result =null;

        Date time = new Date((long) timestamp*1000);
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        TimeZone tz = TimeZone.getTimeZone(timezone);
        formatter.setTimeZone(tz);

        try{result = formatter.parse(formatter.format(time));}catch(Exception e){}

        return formatter.format(result);

    }

    public String getDate(int timestamp, String timezone){
        Date result = null;
        Date time = new Date((long) timestamp*1000);

        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd");
        TimeZone tz = TimeZone.getTimeZone(timezone);
        formatter.setTimeZone(tz);

        try{
            result = formatter.parse(formatter.format(time));
        }
        catch(Exception e){}

        return formatter.format(result);
    }

    public String getMinAndMax(double min,double max, String unit){

        String symbol = "";

        if(unit.equals("us")){
            symbol = "\u00b0F";
        }
        else{
            symbol = "\u00b0F";
        }

        String mintemp = Integer.toString((int) min);
        String maxtemp = Integer.toString((int)max);

        return "Min: " + mintemp + symbol + " | Max: " + maxtemp + symbol;
    }

    public String getTemp(double temp){
        String result ="";
        result = Integer.toString((int)temp);

        return result;
    }

    public void setWeatherImage(String iconValue, ImageView icon){

        if(iconValue.equals("clear-day")){
            icon.setImageResource(R.drawable.clear);
        }
        else if(iconValue.equals("clear-night")){
            icon.setImageResource(R.drawable.clear_night);
        }
        else if(iconValue.equals("rain")){
            icon.setImageResource(R.drawable.rain);
        }
        else if(iconValue.equals("snow")){
            icon.setImageResource(R.drawable.snow);
        }
        else if(iconValue.equals("sleet")){
            icon.setImageResource(R.drawable.sleet);
            //img = "sleet.png";
        }
        else if(iconValue.equals("wind")){
            icon.setImageResource(R.drawable.wind);
        }
        else if(iconValue.equals("fog")){
            icon.setImageResource(R.drawable.fog);
        }
        else if(iconValue.equals("cloudy")){
            icon.setImageResource(R.drawable.cloudy);
        }
        else if(iconValue.equals("partly-cloudy-day")){
            icon.setImageResource(R.drawable.cloud_day);
        }
        else if(iconValue.equals("partly-cloudy-night")){
            icon.setImageResource(R.drawable.cloud_night);
        }
        else{
            //do nothing
        }
    }

    public int setColor(int j){
        int result = 0;

        if(j==1){
            result =  ContextCompat.getColor(getApplicationContext(), R.color.row1);
        }
        else if(j==2){
            result = ContextCompat.getColor(getApplicationContext(), R.color.row2);
        }
        else if(j==3){
            result = ContextCompat.getColor(getApplicationContext(), R.color.row3);
        }
        else if(j==4){
            result = ContextCompat.getColor(getApplicationContext(), R.color.row4);
        }
        else if(j==5){
            result = ContextCompat.getColor(getApplicationContext(), R.color.row5);
        }
        else if(j==6){
            result = ContextCompat.getColor(getApplicationContext(), R.color.row6);
        }
        else if(j==7){
            result = ContextCompat.getColor(getApplicationContext(), R.color.row7);
        }
        else {
            //do nothing
        }
        return result;
    }
}
