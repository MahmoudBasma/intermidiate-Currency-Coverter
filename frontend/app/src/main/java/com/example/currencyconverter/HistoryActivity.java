package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class HistoryActivity extends AppCompatActivity {
    Intent intent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //creating the top menu to be used
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:
                goToHelp(); //go to resource website for additional help and info
                return true;
            case R.id.home:
                backHome(); // go to home page to convert
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
    }
            private void goToHelp() {//as before
                String url = "https://www.thelebaneseguide.com/lira-rate";
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }

            private void backHome() {// as before
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
}