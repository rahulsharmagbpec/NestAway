package com.example.anonymous.nestaway;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.xml.namespace.NamespaceContext;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getName();
    private GoogleMap mMap;
    private ArrayList<House> list;
    private TextView tapTV;
    private SeekBar seekBar;
    private Button doneButton;
    private LinearLayout linearLayout;
    private Marker myMarker;
    private Circle myCircle;
    private int radius;
    private TextView analyse;
    private TextView analyseShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        tapTV = (TextView) findViewById(R.id.textView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        doneButton = (Button) findViewById(R.id.doneButton);
        linearLayout = (LinearLayout) findViewById(R.id.llayout);
        analyse = (TextView) findViewById(R.id.textView2);
        analyseShare = (TextView) findViewById(R.id.textView3);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (myMarker != null) {
                    radius = i;
                    if (myCircle != null) {
                        myCircle.remove();
                    }
                    myCircle = mMap.addCircle(new CircleOptions()
                            .center(myMarker.getPosition())
                            .radius(i).strokeWidth(2));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        GetJson getJson = new GetJson();
        getJson.execute();
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
        drawMarkers();
    }

    private void drawMarkers()
    {
        int minRent = 90000;
        int maxRent = 0;
        int maxShared = 0;
        int minShared = 100;
        Marker minRentMarker = null;
        Marker maxRentMarker = null;
        //for map bound
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        int length = list.size();
        for(int i=0; i<length; i++)
        {
            String title = list.get(i).getTitle();
            String rent = list.get(i).getRent()+"";
            LatLng location = new LatLng(list.get(i).getLatitude(), list.get(i).getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(title+",Rent:"+rent));

            builder.include(location);
            if(minRent > list.get(i).getRent())
            {
                minRent = list.get(i).getRent();
                minRentMarker = marker;
            }
            if(maxRent < list.get(i).getRent())
            {
                maxRent = list.get(i).getRent();
                maxRentMarker = marker;
            }

            if(minShared > list.get(i).getSharing())
            {
                minShared = list.get(i).getSharing();
            }
            if(maxShared < list.get(i).getSharing())
            {
                maxShared = list.get(i).getSharing();
            }
        }
        int padding = 0; // offset from edges of the map in pixels
        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cameraUpdate);
        analyse.setText("Min Rent = " + minRent + ", \nMax Rent = " + maxRent);
        analyseShare.setText("Min Share = "+minShared+", \nMax Share = "+maxShared);
        if(minRentMarker != null) {
            minRentMarker.setIcon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }
        if(maxRentMarker != null)
        {
            minRentMarker.setIcon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }
    }

    public void drawGestureClicked(View v)
    {
        mMap.clear();
        tapTV.setVisibility(View.VISIBLE);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                myMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("My Area"));
                tapTV.setVisibility(View.GONE);
                seekBar.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);
                doneButton.setVisibility(View.VISIBLE);
            }
        });
    }

    public void saveButtonClicked(View v)
    {
        if(myCircle != null && myMarker != null)
        {
            doneButton.setVisibility(View.INVISIBLE);
            seekBar.setVisibility(View.INVISIBLE);

            linearLayout.setVisibility(View.VISIBLE);
            drawInclosedMarkers();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "please draw a circle first", Toast.LENGTH_LONG).show();
        }
    }

    private void drawInclosedMarkers()
    {
        int minRent = 90000;
        int maxRent = 0;
        int maxShared = 0;
        int minShared = 1000;
        Marker minRentMarker = null;
        Marker maxRentMarker = null;

        if(list != null)
        {
            int length = list.size();
            for(int i=0; i<length; i++)
            {
                if(distFrom(myMarker.getPosition().latitude, myMarker.getPosition().longitude,
                                list.get(i).getLatitude(), list.get(i).getLongitude()) < radius)
                {
                    String title = list.get(i).getTitle();
                    String rent = list.get(i).getRent()+"";
                    LatLng location = new LatLng(list.get(i).getLatitude(), list.get(i).getLongitude());
                    Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(title+",Rent:"+rent));
                    if(minRent > list.get(i).getRent())
                    {
                        minRent = list.get(i).getRent();
                        minRentMarker = marker;
                    }
                    if(maxRent < list.get(i).getRent())
                    {
                        maxRent = list.get(i).getRent();
                        maxRentMarker = marker;
                    }

                    if(minShared > list.get(i).getSharing())
                    {
                        minShared = list.get(i).getSharing();
                    }
                    if(maxShared < list.get(i).getSharing())
                    {
                        maxShared = list.get(i).getSharing();
                    }
                }
            }
            analyse.setText("Min Rent = "+minRent+", \nMax Rent = "+maxRent);
            analyseShare.setText("Min Share = "+minShared+", \nMax Share = "+maxShared);

            if(minRentMarker != null) {
                minRentMarker.setIcon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            }
            if(maxRentMarker != null)
            {
                maxRentMarker.setIcon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }
        }
    }

    public float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        Log.e(TAG, dist+"");
        return dist;
    }

    public void listAllClicked(View v)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void tryAgainClicked(View v)
    {
        GetJson getJson = new GetJson();
        getJson.execute();
    }
}
