package com.example.edwin.newsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.edwin.newsapp.Adapters.NewsRecyclerAdapter;
import com.example.edwin.newsapp.Models.NewsItem;
import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NewsRecyclerAdapter.NewsAdapterOnClickHandler {
    private ProgressDialog mProgressDialog;
    private URL newsApiUrl;
    private RecyclerView mRecyclerView;
    private NewsRecyclerAdapter mNewsAdapter;
    private ArrayList articlesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_news);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mNewsAdapter = new NewsRecyclerAdapter(this);
        mRecyclerView.setAdapter(mNewsAdapter);


        new GetResponseTask().execute(NetworkUtils.buildUrl());
    }

    @Override
    public void onClick(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    private class GetResponseTask extends AsyncTask<URL, Void, ArrayList<NewsItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage("Loading data...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected ArrayList<NewsItem> doInBackground(URL... params) {
            String jsonResults;
            ArrayList<NewsItem> results = null;
            try {
                jsonResults = NetworkUtils.getResponseFromHttpUrl(params[0]);
                results = NetworkUtils.parseJSON(jsonResults);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(final ArrayList<NewsItem> data) {

            if(data != null){
                mNewsAdapter.setNewsData(data);
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
            new GetResponseTask().execute(NetworkUtils.buildUrl());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
