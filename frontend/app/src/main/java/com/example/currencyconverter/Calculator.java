package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

        Intent current = getIntent();
        blackMarketHigh = Integer.parseInt(current.getStringExtra("blackMarketHigh"));
        blackMarketLow = Integer.parseInt(current.getStringExtra("blackMarketLow"));
        bank = Integer.parseInt(current.getStringExtra("bank"));
        official = Integer.parseInt(current.getStringExtra("official"));


        
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