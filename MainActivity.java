package com.yuxuanli.weatherforecastinandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText street = (EditText) findViewById(R.id.street_text);
        final EditText city = (EditText) findViewById(R.id.city_text);
        final Spinner state = (Spinner) findViewById(R.id.state_spinner);
        final RadioButton degree_us = (RadioButton) findViewById(R.id.degree_us);
        final RadioButton degree_si = (RadioButton) findViewById(R.id.degree_si);
        final TextView errmsg = (TextView) findViewById(R.id.error_msg);
        Button search_btn = (Button) findViewById(R.id.search_btn);
        Button clear_btn = (Button) findViewById(R.id.clear_btn);
        Button about_btn = (Button) findViewById(R.id.about_btn);
        ImageView forecast_logo = (ImageView) findViewById(R.id.forecast_logo);

        //after search buttom clicked
        search_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v) {

                String url = "http://weatherforecastfromyuxuanli-env.elasticbeanstalk.com/?";
                String query;
                String streetText, cityText, stateText, degreeText;
                String error = "";


                streetText = street.getText().toString();
                cityText = city.getText().toString();
                String c = cityText;//used for sysntask
                stateText = state.getSelectedItem().toString();

                if(!validate(streetText)){
                    error += "Please enter street address\n";
                }
                if(!validate(cityText)){
                    error += "Please enter city name\n";
                }
                if(stateText.equals("Select State")){
                    error += "Please select state name";
                }

                if(error.length()==0){
                    errmsg.setText("");
                    if (degree_us.isChecked()) {
                        degreeText = "us";
                    } else if (degree_si.isChecked()) {
                        degreeText = "si";
                    } else {
                        degreeText = "Degree Error";
                    }

                    try{

                        streetText = URLEncoder.encode(streetText, "utf-8");
                        cityText = URLEncoder.encode(cityText,"utf-8");}catch(Exception e){}
                    query = "address="+ streetText + "&" +"city="+cityText + "&" +"state="+ stateText+ "&" + "degree="+degreeText;
                    url += query;

                    new getJSON().execute(url.toString(),c,stateText,degreeText);
                    Log.d("warning", url.toString());
                }
                else{
                    errmsg.setText(error);
                }

            }


        });

        //after clear buttom clicked
        clear_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                street.setText(null);
                city.setText(null);
                degree_us.setChecked(true);
                degree_si.setChecked(false);
                state.setSelection(0);

            }
        });

        about_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){

                Intent aboutScreen = new Intent(MainActivity.this,about.class);
                startActivity(aboutScreen);
            }
        });

        forecast_logo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                Intent forecastQuery = new Intent(Intent.ACTION_VIEW);
                forecastQuery.setData(Uri.parse("http://forecast.io"));
                startActivity(forecastQuery);
            }

        });





    }

    public boolean validate(String str){
        if(str.trim().length() == 0){
            return false;
        }
        else {
            return true;
        }

    }

    public class getJSON extends AsyncTask<String,String,String> {
        HttpURLConnection urlConnection = null;
        String city,state,unit;

        @Override
        protected String doInBackground(String... urls){

            StringBuilder result = new StringBuilder();
            try{
                city = urls[1];
                state = urls[2];
                unit = urls[3];
                URL url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-length", "0");
                urlConnection.setUseCaches(false);
                urlConnection.setAllowUserInteraction(false);
                urlConnection.connect();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while((line = reader.readLine())!=null){
                    result.append(line);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }
            return result.toString();
        }//do in background

        @Override
        protected  void onPostExecute(String result){
            final TextView textView = (TextView) findViewById(R.id.error_msg);

            //Create a JSONObject
            try{
                JSONObject jObject = new JSONObject(result);

                Intent nextScreen = new Intent(MainActivity.this,currently.class);


                nextScreen.putExtra("json",result);
                nextScreen.putExtra("city",city);
                nextScreen.putExtra("state",state);
                nextScreen.putExtra("unit",unit);



                startActivity(nextScreen);

            }catch (JSONException e){
                //Oops
            }

        }


    }
}
