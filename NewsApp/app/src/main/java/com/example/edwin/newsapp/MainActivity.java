package com.example.edwin.newsapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView mTextView;
    private ProgressDialog mProgressDialog;
    private URL newsApiUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTextView = (TextView) findViewById(R.id.textView);

        newsApiUrl = NetworkUtils.buildUrl();

        new GetResponseTask().execute();
    }

    private class GetResponseTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mTextView.setText("");
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Please wait");
            mProgressDialog.setMessage("Loading data...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(URL... params) {
            String results = null;
            try {
               results = NetworkUtils.getResponseFromHttpUrl(newsApiUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(String results) {

            if(results != null && !results.equals("")){
                mTextView.setText(results);
            }
            else{
                Snackbar.make(findViewById(R.id.coordinatorLayout), "An error occurred please try again", Snackbar.LENGTH_LONG)
                        .show();
            }
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            new GetResponseTask().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
