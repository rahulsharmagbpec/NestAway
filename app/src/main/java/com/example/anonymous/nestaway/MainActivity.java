package com.example.anonymous.nestaway;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    ListView listView;
    ArrayList<House> list;
    private final static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        listView = (ListView) findViewById(R.id.list);
        GetJson getJson = new GetJson();
        getJson.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    class GetJson extends AsyncTask<String, Void,String>
    {
        ProgressBar progressBar;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            ArrayList<NameValuePair> parameters = new ArrayList<>();

            GetJsonFromUrl getJsonFromUrl = new GetJsonFromUrl();
            String response = getJsonFromUrl.getJSONFromUrl(ApiList.getLocations, 1, parameters);
            Log.e(TAG, response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            parseJsonResponse(s);
        }
    }

    private void parseJsonResponse(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("houses");
            int length = jsonArray.length();
            list = new ArrayList<>();

            for(int i=0; i<length; i++)
            {
                House house = new House();
                house.setBhk(jsonArray.getJSONObject(i).getString("bhk_details"));
                house.setType(jsonArray.getJSONObject(i).getString("house_type"));
                house.setId(jsonArray.getJSONObject(i).getInt("id"));
                house.setStatusCode(jsonArray.getJSONObject(i).getInt("status_code"));
                house.setTitle(jsonArray.getJSONObject(i).getString("title"));
                house.setLatitude(jsonArray.getJSONObject(i).getDouble("lat_double"));
                house.setLongitude(jsonArray.getJSONObject(i).getDouble("long_double"));
                house.setBedRoom(jsonArray.getJSONObject(i).getInt("bed_available_count"));
                house.setRent(jsonArray.getJSONObject(i).getInt("min_rent"));
                house.setSlug(jsonArray.getJSONObject(i).getString("slug"));
                house.setNestAwayId(jsonArray.getJSONObject(i).getString("nestaway_id"));
                house.setSharing(jsonArray.getJSONObject(i).getInt("shared"));
                house.setPrivateRoom(jsonArray.getJSONObject(i).getInt("is_private"));
                house.setImage(jsonArray.getJSONObject(i).getString("image_url"));
                house.setGender(jsonArray.getJSONObject(i).getString("gender"));
                house.setLocality(jsonArray.getJSONObject(i).getString("locality"));

                list.add(house);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

        listView.setAdapter(new CustomAdapter(this, list));
    }
}
