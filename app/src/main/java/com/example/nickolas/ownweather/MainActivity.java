package com.example.nickolas.ownweather;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //    static TextView test;
    EditText editText;
    RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;
    private WeatherModel model;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        editText = (EditText) findViewById(R.id.townET);
//        test = (TextView) findViewById(R.id.testText);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                MenuItem item1 = menu.findItem(R.id.action_geo);
                item1.setVisible(true);
                loadWeather();
                break;
            case R.id.action_geo:
                String str = "geo:" + Double.toString(model.city.coordinates.lat)+ "," + Double.toString(model.city.coordinates.lon);
                Uri.Builder builder = new Uri.Builder();
                Uri uri = Uri.parse(str);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
        }
        return false;
    }

    void loadWeather() {
        new WeatherDownloadTask().execute(editText.getText().toString());
    }

    void setAdapter(WeatherModel wm) {
        mRecyclerView.setAdapter(new MyAdapter(wm, this));
    }

    class WeatherDownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Network.buildURL(name))
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
            if (s != null && !s.equals("")) {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                model = WeatherJson.toWeatherModel(obj);
                setAdapter(model);
            }
        }
    }


}
