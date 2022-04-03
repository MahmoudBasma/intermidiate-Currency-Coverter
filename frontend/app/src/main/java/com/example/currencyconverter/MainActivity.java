package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {

    //creating global variables to use
    int bank , blackMarketHigh, blackMarketLow, official, money;
    Spinner currencies;
    Spinner rates;
    TextView actualRate;
    TextView amount;
    Button btn;


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

    public class UploadTask extends AsyncTask<String, String, String> {

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
        protected String doInBackground(String... params) {
            String urlString = params[0]; // URL to call
            String data = params[1]; //data to post
            Log.i("Data",data);
            OutputStream out = null;


            try {

                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                out = new BufferedOutputStream(con.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                out.close();
                con.connect();
                Log.i("status", "real success");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:
                goToHelp();
                return true;
            case R.id.history:
                showHistory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Mahmoud re-add your ip address to work it out
        String url = "http://185.97.92.122/intermidiate%20Currency%20Coverter/backend/APIs/rate_api.php";
        DownloadTask task = new DownloadTask();
        task.execute(url);

        //same here king
        String postUrl = "http://192.168.0.119/intermidiate%20Currency%20Coverter/backend/APIs/db_api.php";
        UploadTask task1 = new UploadTask();
        String jsonInputString = "{\"amount\": 700, \"rate\": \"bank\", \"currency\": \"USD\"}";
        task1.execute(postUrl, jsonInputString);
        Log.i("status", "success");

        rates = findViewById(R.id.rate);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.rates,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rates.setAdapter(adapter);
        rates.setOnItemSelectedListener(this);

        currencies = findViewById(R.id.currency);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.currency,android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencies.setAdapter(adapter2);
        currencies.setOnItemSelectedListener(this);

        actualRate = findViewById(R.id.actualRate);

        btn  = findViewById(R.id.convert);

        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(currencies.getSelectedItem().toString().equals("")){
                    Toast.makeText(getApplicationContext(), getString(R.string.koosa), Toast.LENGTH_LONG).show();
                }
                else if(currencies.getSelectedItem().toString().equals("LBP")){
                    usdToLBP(view);
                } else{
                    lbpToUSD(view);
                }
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


    private void goToHelp() {
        Intent intent = new Intent(this,HelpActivity.class);
        startActivity(intent);
    }

    private void showHistory() {
        Intent intent = new Intent(this,HistoryActivity.class);
        startActivity(intent);
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
      //  if(rates.getSelectedItem().toString().equals(""))
            //Toast.makeText(getApplicationContext(), getString(R.string.koosa), Toast.LENGTH_LONG).show();
        if(rates.getSelectedItem().toString().equals("Black Market High Rate"))
            actualRate.setText(blackMarketHigh+"");

        if(rates.getSelectedItem().toString().equals("Black Market Low Rate"))
            actualRate.setText(blackMarketLow +"");

        if(rates.getSelectedItem().toString().equals("Bank Rate"))
            actualRate.setText(bank+"");
        if(rates.getSelectedItem().toString().equals("Official Rate"))
            actualRate.setText(official+"");

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }


    private void lbpToUSD(View view){


        amount = (TextView) findViewById(R.id.amount);

        money =  Integer.parseInt(amount.getText().toString()) / Integer.parseInt(actualRate.getText().toString());
        TextView result  = (TextView) findViewById(R.id.result);
        result.setText( money + " USD");
    }
    private void usdToLBP(View view) {

        amount = (TextView) findViewById(R.id.amount);
        money =  Integer.parseInt(amount.getText().toString()) * Integer.parseInt(actualRate.getText().toString());
        TextView result  = (TextView) findViewById(R.id.result);
        result.setText(money + " LBP");
    }


}