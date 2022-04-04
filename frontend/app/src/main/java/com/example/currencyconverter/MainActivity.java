package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {

    //creating global variables to use
    Intent intent;
    int bank , blackMarketHigh , blackMarketLow , official , money;
    Spinner currencies;
    Spinner rates;
    TextView actualRate;
    TextView amount;
    Button btn;
    TextView result;


 //mahmoud thingies:--->
    class DownloadTask extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... urls){
            String result = "";
            URL url;
            HttpURLConnection http;

            try{
                Log.i("Attempting", "Attempt2");
                url = new URL(urls[0]);
                http = (HttpURLConnection) url.openConnection();

                InputStream in = http.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while( data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }

            return result;
        }
        protected void onPostExecute(String s){
            super.onPostExecute(s);

            try{
                JSONObject json = new JSONObject(s);
                blackMarketHigh = json.getInt("Black Market rate-high");
                blackMarketLow = json.getInt("Black Market rate-low");
                bank = json.getInt("Bank rate");
                official = json.getInt("Official rate");
                Log.i("blackMarketHigh", ""+blackMarketHigh);
                Log.i("blackMarketLow", ""+blackMarketLow);
                Log.i("bank ", ""+bank );
                Log.i("official", ""+official);

            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    public class UploadTask extends AsyncTask<String, Void, Boolean> {

        URL url;
        HttpURLConnection http;
        HttpURLConnection con;

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        public UploadTask(){
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                String jsonInputString = "{\"amount\": 700, \"rate\": \"bank\", \"currency\": \"USD\"}";

                byte[] out = jsonInputString.getBytes(StandardCharsets.UTF_8);
                int length = out.length;

                urlConnection.setFixedLengthStreamingMode(length);
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.connect();
                try(OutputStream os = urlConnection.getOutputStream()) {
                    os.write(out);
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }
//        protected String doInBackground(String... params) {
//            String urlString = params[0]; // URL to call
//            String data = params[1]; //data to post
//            Log.i("Data",data);
//            OutputStream out = null;
//
//
//            try {
//
//                URL url = new URL(urlString);
//                HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                con.setRequestMethod("POST");
//                out = new BufferedOutputStream(con.getOutputStream());
//
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//                writer.write(data);
//                writer.flush();
//                writer.close();
//                out.close();
//                con.connect();
//                Log.i("status", "real success");
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
//            return null;
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //creating the top menu to be used
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:
                goToHelp(); //go to resource website for additional help and info
                return true;
            case R.id.history:
                showHistory(); // go to history which shows your converting history
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //api that fetches the data

        String url = "http://10.21.147.46/intermidiate%20Currency%20Coverter/backend/APIs/rate_api.php";
        DownloadTask task = new DownloadTask();
        task.execute(url);

        //api that sends the data to the DB
        String postUrl = "http://10.21.147.46/intermidiate%20Currency%20Coverter/backend/APIs/db_api.php";
        UploadTask task1 = new UploadTask();

        boolean requestResult = true;
        try {
         requestResult = task1.execute(postUrl).get();
            if(!requestResult){
                Log.i("Attempt:", "Failed");
                System.out.println("Something went wrong!");
            }
            Log.i("Attempt:", "Post Request sent");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("status", "success");

        rates = findViewById(R.id.rate); // spinner to show the available converting rates
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.rates, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rates.setAdapter(adapter);
        rates.setOnItemSelectedListener(this);

        currencies = findViewById(R.id.currency); // spinner to show which currency to convert to
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.currency, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencies.setAdapter(adapter2);
        currencies.setOnItemSelectedListener(this);

        actualRate = findViewById(R.id.actualRate); // shows the value of the converting rate
        result = (TextView) findViewById(R.id.result);
        btn = findViewById(R.id.convert);

        btn.setOnClickListener(view -> { // lambda expression for converting and checking if something is missing
            if (currencies.getSelectedItem().toString().equals("") || rates.getSelectedItem().toString().equals("")) {
                Toast.makeText(getApplicationContext(), getString(R.string.koosa), Toast.LENGTH_SHORT).show();
                result.setText(null);

            } else if (currencies.getSelectedItem().toString().equals("LBP")) {
                usdToLBP(view);
            } else {
                lbpToUSD(view);
            }
        });


                    //koosa don't look here: اختراعات قواص الطائر
       /* switch (currencies.getSelectedItem().toString()) {
            case "USD":
                switch (rates.getSelectedItem().toString()){
                    case "Black Market High Rate":
                        lbpToUSD(rates, blackMarketHigh);

                    case "Black Market Low Rate":
                        lbpToUSD(rates, blackMarketLow);

                    case "Bank Rate":
                        lbpToUSD(rates, bank);

                    case "Official Rate":
                        lbpToUSD(rates, official);
                }
                    :) was trying to implement the word by using switch case cause it is similar to When statements in Kotlin...
            case "LBP":
                switch (rates.getSelectedItem().toString()){
                    case "Black Market High Rate":
                        usdToLBP(rates, blackMarketHigh);

                    case "Black Market Low Rate":
                        usdToLBP(rates, blackMarketLow);

                    case "Bank Rate":
                        usdToLBP(rates, bank);

                    case "Official Rate":
                        usdToLBP(rates, official);
                }*/


    }


    private void goToHelp() { // redirects to the resource website, sorry couldn't use Lirarate.org, scrapping the data wasn't possible and fetching the original api was banned and didn't work most of the time
        String url = "https://www.thelebaneseguide.com/lira-rate";
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void showHistory() { // redirects us to our history of converting
        intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { //selects which rate we going to chose or convert with
        //if(rates.getSelectedItem().toString().equals(""))
        //Toast.makeText(getApplicationContext(), getString(R.string.koosa), Toast.LENGTH_SHORT).show();
        if (rates.getSelectedItem().toString().equals("Black Market High Rate"))
            actualRate.setText(blackMarketHigh + "");

        if (rates.getSelectedItem().toString().equals("Black Market Low Rate"))
            actualRate.setText(blackMarketLow + "");

        if (rates.getSelectedItem().toString().equals("Bank Rate"))
            actualRate.setText(bank + "");
        if (rates.getSelectedItem().toString().equals("Official Rate"))
            actualRate.setText(official + "");

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { //koosa mitil illita
    }


    private void lbpToUSD(View view) {


        amount = (TextView) findViewById(R.id.amount);
        if (amount.getText().toString().equals("")) { //checking whether the amount inserted by the user is actually null or not to check whether to get a result or no
            Toast.makeText(getApplicationContext(), getString(R.string.koosa), Toast.LENGTH_SHORT).show();
        } else {
            money = Integer.parseInt(amount.getText().toString()) / Integer.parseInt(actualRate.getText().toString());
            TextView result = (TextView) findViewById(R.id.result);
            result.setText(money + " USD");
        }

        money = Integer.parseInt(amount.getText().toString()) / Integer.parseInt(actualRate.getText().toString());
        TextView result = (TextView) findViewById(R.id.result);
        result.setText(money + " USD");
    }

    private void usdToLBP(View view) {

        amount = (TextView) findViewById(R.id.amount);
        if (amount.getText().toString().equals("")) {// same as before^^^
            Toast.makeText(getApplicationContext(), getString(R.string.koosa), Toast.LENGTH_SHORT).show();
        } else {
            money = Integer.parseInt(amount.getText().toString()) * Integer.parseInt(actualRate.getText().toString());
            TextView result = (TextView) findViewById(R.id.result);
            result.setText(money + " LBP");
        }
    }


}