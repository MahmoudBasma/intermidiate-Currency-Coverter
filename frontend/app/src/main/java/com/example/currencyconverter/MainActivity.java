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
    //We scrapped a website to return all the 4 values.
    // Our app will be converting on any of the values below
    int bank , blackMarketHigh , blackMarketLow , official , money;
    Spinner currencies;
    Spinner rates;
    TextView actualRate, bankRate, officialRate;
    Button btn;


 //This is the get request Object
 //The DownloadTask object will allow us to retrieve the scraped values from the rates API


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Finding the text view to update them with the actual rates later on in the home page
        actualRate = (TextView) findViewById(R.id.blackMarket);
        bankRate = (TextView) findViewById(R.id.bankRate);
        officialRate = (TextView) findViewById(R.id.officialRate);

        //api that fetches the data and returns the 4 rates

        String url = "http://10.21.147.46/intermidiate%20Currency%20Coverter/backend/APIs/rate_api.php";
        DownloadTask task = new DownloadTask();
        task.execute(url);

        }

    class DownloadTask extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... urls){
            String result = "";
            URL url;
            HttpURLConnection http;
            //Gets the JSON file from the api
            try{
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
            //This function takes the json object and fill the values in the pulic variables.
            //It then logs them and update the text views of the home page

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
                actualRate.setText(blackMarketLow);
                bankRate.setText(bank);
                officialRate.setText(official);

            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    //THis is a menu options creater. It have 2 sections
    //Section 1: info --> it will take you to the rates source page and article
    //Section 2: history --> it will take you to a section where you can view the previous calculations

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

    private void goToHelp() {
        // redirects to the resource website, sorry couldn't use Lirarate.org,
        // scrapping the data wasn't possible and fetching the original api was banned
        // and didn't work most of the time
        //The Lebanese guide website contains more exchange rates

        String url = "https://www.thelebaneseguide.com/lira-rate";
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void showHistory() { // redirects us to our history of converting
        intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void goToCalculator(View view){
        //Redirect you to the calculator section where you can convert money on 4 rates
        Intent obj = new Intent(getApplicationContext(), Calculator.class);
        startActivity(obj);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }



}