package com.yuxuanli.weatherforecastinandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import com.facebook.FacebookSdk;
import com.facebook.CallbackManager;
import com.facebook.share.widget.ShareDialog;
import com.facebook.share.model.ShareLinkContent;
import android.net.Uri;
import com.facebook.share.Sharer;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookDialog;
/**
 * Created by LiYuxuan on 11/15/15.
 */
public class currently extends Activity{

    CallbackManager callbackManager;
    ShareDialog shareDialog;
    String summary_fb;
    double temperature_fb;
    String image_url_fb;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currently);

        //initialize facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        //create a ShareDialog
        shareDialog = new ShareDialog(this);

        //Register Callback for the ShareDialog
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast toast = Toast.makeText(getApplicationContext(),"Facebook post successfully",Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            public void onCancel() {
                Toast toast = Toast.makeText(getApplicationContext(),"Facebook post cancelled",Toast.LENGTH_LONG);
                toast.show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast toast = Toast.makeText(getApplicationContext(), "Facebook post in error", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        

        Intent i = getIntent();
        final String result = i.getStringExtra("json");

        final String city = i.getStringExtra("city");
        final String state = i.getStringExtra("state");
        final String unit = i.getStringExtra("unit");
        //final String summary_fb = "" ;
        //final double temperature_fb = 0;

        try{
            JSONObject jObject = new JSONObject(result);

            String timezone = jObject.getString("timezone");
            final String lon = jObject.getString("longitude");
            final String lat = jObject.getString("latitude");

            JSONObject currently = jObject.getJSONObject("currently");

            setWeatherImage(currently.getString("icon"));

            TextView summary = (TextView) findViewById(R.id.summary);
            TextView temperature = (TextView) findViewById(R.id.temperature);
            TextView min_and_max = (TextView) findViewById(R.id.min_and_max);
            TextView precipitation = (TextView) findViewById(R.id.precipitation);
            TextView chance_of_rain = (TextView) findViewById(R.id.chance_of_rain);
            TextView wind_speed = (TextView) findViewById(R.id.wind_speed);
            TextView dew_point = (TextView) findViewById(R.id.dew_point);
            TextView humidity = (TextView) findViewById(R.id.humidity);
            TextView visibility = (TextView) findViewById(R.id.visibility);
            TextView sunrise= (TextView) findViewById(R.id.sunrise);
            TextView sunset = (TextView) findViewById(R.id.sunset);

            summary_fb = currently.getString("summary");
            summary.setText(getSummary(city, state, currently.getString("summary")));

            temperature_fb = currently.getDouble("temperature");
            temperature.setText(getTemp(currently.getDouble("temperature"),unit));

            JSONObject daily = jObject.getJSONObject("daily");
            JSONArray data = daily.getJSONArray("data");
            JSONObject data0 = data.getJSONObject(0);
            min_and_max.setText(getMinAndMAx(data0.getDouble("temperatureMin"), data0.getDouble("temperatureMax")));

            precipitation.setText(getPrecipitation(currently.getDouble("precipIntensity")));
            chance_of_rain.setText(getChanceOfRain(currently.getDouble("precipProbability")));
            wind_speed.setText(getWindSpeed(currently.getDouble("windSpeed"), unit));
            dew_point.setText(getDewPoint(currently.getDouble("dewPoint"), unit));
            humidity.setText(getHumidity(currently.getDouble("humidity")));
            visibility.setText(getVisibility(currently.getDouble("visibility"), unit));

            sunrise.setText(getTime(data0.getInt("sunriseTime"), timezone));
            sunset.setText(getTime(data0.getInt("sunsetTime"), timezone));

        }catch(Exception e){}

        Button moreDetails = (Button) findViewById(R.id.more_details);
        moreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent nextScreen = new Intent(currently.this, moreDetails.class);

                nextScreen.putExtra("result", result);
                nextScreen.putExtra("unit", unit);
                nextScreen.putExtra("header","More Details for "+city+", "+state);
                startActivity(nextScreen);

            }

        });

        final ImageView fb_btn = (ImageView) findViewById(R.id.fb_btn);
        fb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //share the LinkContent through ShareDialog
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Current Weather in "+ city +" ,"+ state)
                            .setContentDescription(
                                    summary_fb + ", " + (int) temperature_fb + (unit == "us" ? "°F" : "°C"))
                            .setImageUrl(Uri.parse(image_url_fb))
                            .setContentUrl(Uri.parse("forecast.io"))
                            .build();

                    shareDialog.show(linkContent);
                }
            }



        });

        Button map_btn = (Button) findViewById(R.id.view_map);

        map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //After click map button

                Intent nextScreen = new Intent(currently.this, MapActivity.class);

                try{
                    JSONObject jObject = new JSONObject(result);

                    final Double lon = jObject.getDouble("longitude");
                    final Double lat = jObject.getDouble("latitude");

                    nextScreen.putExtra("lon",lon);
                    nextScreen.putExtra("lat",lat);


                    startActivity(nextScreen);
                    Log.v("TAG","start map activity");
                }catch (Exception e){}
            }
        });


    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void setWeatherImage(String iconValue){

        ImageView icon = (ImageView) findViewById(R.id.weather_icon);

        if(iconValue.equals("clear-day")){
            icon.setImageResource(R.drawable.clear);
            image_url_fb = "http://cs-server.usc.edu:45678/hw/hw8/images/clear.png";
        }
        else if(iconValue.equals("clear-night")){
            icon.setImageResource(R.drawable.clear_night);
            image_url_fb = "http://cs-server.usc.edu:45678/hw/hw8/images/clear_night.png";

        }
        else if(iconValue.equals("rain")){
            icon.setImageResource(R.drawable.rain);
            image_url_fb = "http://cs-server.usc.edu:45678/hw/hw8/images/rain.png";

        }
        else if(iconValue.equals("snow")){
            icon.setImageResource(R.drawable.snow);
            image_url_fb = "http://cs-server.usc.edu:45678/hw/hw8/images/snow.png";

        }
        else if(iconValue.equals("sleet")){
            icon.setImageResource(R.drawable.sleet);
            image_url_fb = "http://cs-server.usc.edu:45678/hw/hw8/images/sleet.png";

            //img = "sleet.png";
        }
        else if(iconValue.equals("wind")){
            icon.setImageResource(R.drawable.wind);
            image_url_fb = "http://cs-server.usc.edu:45678/hw/hw8/images/wind.png";

        }
        else if(iconValue.equals("fog")){
            icon.setImageResource(R.drawable.fog);
            image_url_fb = "http://cs-server.usc.edu:45678/hw/hw8/images/fog.png";

        }
        else if(iconValue.equals("cloudy")){
            icon.setImageResource(R.drawable.cloudy);
            image_url_fb = "http://cs-server.usc.edu:45678/hw/hw8/images/cloudy.png";

        }
        else if(iconValue.equals("partly-cloudy-day")){
            icon.setImageResource(R.drawable.cloud_day);
            image_url_fb = "http://cs-server.usc.edu:45678/hw/hw8/images/cloud_day.png";

        }
        else if(iconValue.equals("partly-cloudy-night")){
            icon.setImageResource(R.drawable.cloud_night);
            image_url_fb = "http://cs-server.usc.edu:45678/hw/hw8/images/cloud_night.png";

        }
        else{
            //do nothing
        }
    }

    public String getPrecipitation(double precipIntencity){
        String precipitation = "";
        if(precipIntencity>=0 && precipIntencity<0.002){
            precipitation = "None";
        }
        else if(precipIntencity>=0.002 && precipIntencity<0.017){
            precipitation= "Very Light";
        }
        else if(precipIntencity>=0.017 && precipIntencity<0.1){
            precipitation = "Light";
        }
        else if(precipIntencity>=0.1 && precipIntencity<0.4){
            precipitation = "Moderate";
        }
        else if(precipIntencity>=0.4){
            precipitation = "Heavy";
        }
        else{
            //Oops!
        }
        return precipitation;
    }

    public String getChanceOfRain(double chance_of_rain){
        String result = "";

        DecimalFormat myFormatter = new DecimalFormat("###.##");
        result = myFormatter.format(chance_of_rain*100);

        result += " %";

        return result;
    }

    public String getWindSpeed(double wind_speed,String unit){
        String result = "";

        DecimalFormat myFormatter = new DecimalFormat("###.##");
        result = myFormatter.format(wind_speed);

        if(unit.equals("us")){
            result += " mph";
        }
        else{
            result += " m/s";
        }


        return result;
    }

    public String getDewPoint(double dew_point,String unit){
        String result = "";

        DecimalFormat myFormatter = new DecimalFormat("###.##");
        result = myFormatter.format(dew_point);

        if(unit.equals("us")){
            result += " \u00b0F";
        }
        else{
            result += " \u00b0C";
        }

        return result;
    }

    public String getHumidity(double humidity){
        String result = "";
        result = Integer.toString((int)(humidity*100));
        result += " %";
        return result;
    }

    public String getVisibility(double visibility,String unit){
        String result ="";
        DecimalFormat myFormatter = new DecimalFormat("###.##");
        result = myFormatter.format(visibility);

        if(unit.equals("us")){
            result += " mi";
        }
        else{
            result += " km";
        }
        return result;
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
    public String getTemp(double temp,String unit){
        String result ="";
        result = Integer.toString((int)temp);

        if(unit.equals("us")){
            result += " \u00b0F";
        }
        else{
            result += " \u00b0C";
        }
        return result;
    }

    public String getMinAndMAx(double min, double max){
        String result = "";
        result = "L:"+ Integer.toString((int)min) + "\u00B0 | H:" + Integer.toString((int)max)+"\u00B0";
        return result;
    }

    public String getSummary(String city, String state, String summary){
        String result = "";
        result = summary + " in " + city +", "+state;
        return result;
    }
}
