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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Calculator extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    int bank , blackMarketHigh , blackMarketLow , official , money;
    Intent intent;
    Spinner currencies;
    Spinner rates;
    TextView actualRate;
    TextView amount;
    Button btn;
    TextView result;

    private void goToHelp() { // redirects to the resource website, sorry couldn't use Lirarate.org, scrapping the data wasn't possible and fetching the original api was banned and didn't work most of the time
        String url = "https://www.thelebaneseguide.com/lira-rate";
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void goToHome(View view){
        Intent obj = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(obj);
    }

    private void showHistory() { // redirects us to our history of converting
        intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //selects which rate we going to chose or convert with
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

    class DownloadTask extends AsyncTask<String, Void, String> {
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

    public class UploadTask extends AsyncTask<String, Void, String> {

        URL url;
        HttpURLConnection http;

        public UploadTask(){
            //set context variables if required
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);
            try{
                JSONObject json = new JSONObject(s);
                String status = json.getString("status");
                Log.i("Connection status:", ""+status);

            }catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder status = new StringBuilder();

            try {
                url = new URL(strings[0]);
                http = (HttpURLConnection) url.openConnection();
                InputStream in = http.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while( data != -1){
                    char current = (char) data;
                    status.append(current);
                    data = reader.read();
                }

                /* ORIGINALLY, we wanted to do a post request from the APP. We tried sending
                the post attributes one by one and as a JSON file however the api never worked
                It worked on postman, but not here. So, I decided to use a get request with attributes.
                It is not the best way, yet it is guaranteed to work

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
                 */

            }
            catch (Exception e){
                e.printStackTrace();
            }

            return status.toString();
        }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

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

    }
}