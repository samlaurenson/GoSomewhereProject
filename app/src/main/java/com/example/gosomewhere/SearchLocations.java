package com.example.gosomewhere;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
Class builds and reads the contents of the search URL
and calls methods in MapsActivity to store the location name and coordinates for a place
and to place markers for each place
 */
public class SearchLocations extends AsyncTask<Void, Void, String> {


    private int radius;
    private String type;
    private double longitude;
    private double latitude;
    private float typeColour;
    private WeakReference<MapsActivity> mapsActivity;

    //Constructor to assign variable details for when following methods are executed
    //assigns everything that is needed to build URL to search for places and to place markers
    public SearchLocations(int radius, String type, double latitude, double longitude, float typeColour, MapsActivity activity) {
        this.radius = radius;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.typeColour = typeColour;
        mapsActivity = new WeakReference<MapsActivity>(activity);
    }

    //Method that will be used to read the contents of the URL that is provided with the
    //getUrl method
    //Returns a string which contains the contents of the URL
    @Override
    protected String doInBackground(Void... voids) {
        String returnVal = "";
        HttpURLConnection urlConnection;

        try {
            //Creates a url using the getUrl method
            URL url = new URL(getUrl(type, radius));

            mapsActivity.get().setMapSpinnerVisible(true);
            Thread.sleep(2000);
            urlConnection = (HttpURLConnection) url.openConnection();
            Thread.sleep(6000);    //Adding timers in case Place API restrictions doesn't load data properly
            mapsActivity.get().setMapSpinnerVisible(false);

            //Reading contents of the URL and adding it to a string variable which will be returned
            InputStreamReader streamReader = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            returnVal = stringBuilder.toString();

            //checking output of the built string
            //Log.e("Json", stringBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return returnVal;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            //Creates a JSON object of the string returned from "doInBackground"
            JSONObject jsonObject = new JSONObject(s);

            //Gets all the data stored in the "results" object
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for(int i = 0; i < jsonArray.length(); i++) {
                //Holds an element of data for an object in results
                JSONObject resultObj = jsonArray.getJSONObject(i);

                //Checking name of place is output correctly
                String name = resultObj.getString("name");

                //Location stored under Geometry
                //so have to create geometry object to get to location
                JSONObject geometry = resultObj.getJSONObject("geometry");

                //Latitude and longitude stored under location
                JSONObject location = geometry.getJSONObject("location");

                //Checking latitude and longitude of location are output correctly
                String lat = location.getString("lat"); //Could check if location.getDouble("lat"); works
                String lon = location.getString("lng");

                double latitude = Double.parseDouble(lat);
                double longitude = Double.parseDouble(lon);

                LatLng placeLocation = new LatLng(latitude, longitude);

                //Adds place name and coordinates to hashmap with all other places
                mapsActivity.get().addLocation(name, placeLocation);

                //Uses method to place markers by passing in colour of marker, name of marker and the location
                mapsActivity.get().placeMarkers(typeColour, name, placeLocation);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Method to piece together a URL to produce a list of places containing their details
    private String getUrl(String placeType, int radius) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location="+latitude+","+longitude);
        googlePlacesUrl.append("&radius="+radius);
        googlePlacesUrl.append("&type="+placeType);
        googlePlacesUrl.append("&key="+"YOUR_API_KEY_HERE");
        Log.d("URL", googlePlacesUrl.toString());
        return googlePlacesUrl.toString();
    }
}
