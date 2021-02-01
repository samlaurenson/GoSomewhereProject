package com.example.gosomewhere;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Random;

//Class to provide user with a page containing a map and their current location
//Will be used to show the user the area they are searching as well as the results of their search
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private LatLng userLocation;
    private Circle mapCircle;
    private double latitude;
    private double longitude;
    private HashMap<String, LatLng> locationsHashMap = new HashMap<String, LatLng>(); //Stores place names and locations


    //Will run when activity is started
    //Gets the user location and creates the action bar and navigation drawer
    //Also provides functionality to the "Go Somewhere" button that is used to randomly select a place to go
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //If the user has allowed location permissions then get the latitude and longitude of user
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            requestLocationUpdates();

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();

                    //Will be passed to SearchLocations to find locations around current location
                    latitude = lat;
                    longitude = lon;

                    LatLng loc = new LatLng(lat, lon);
                    userLocation = loc;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16));
                }
            });
        }

        //Create the action bar and navigation drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Button to randomly select a place from the users search results
        Button goSomewhere = (Button)findViewById(R.id.goSomewhereButton);
        goSomewhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Picks a random key from within the hashmap
                //.keyset().toArray() return an array of keys
                //Then picks a random index of key
                if(locationsHashMap.size() > 0) {
                    //Creates an array of keys and then picks a random key to then zoom to
                    Object randomPlace = locationsHashMap.keySet().toArray()[new Random().nextInt(locationsHashMap.size())];
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((locationsHashMap.get(randomPlace)), 16));
                } else {
                    Toast.makeText(MapsActivity.this, R.string.no_locations, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Function to take in the types of locations user has selected to search from as well as radius
    //This function will then use these details to search for places using the SearchLocation class
    public void getFilterResults(int radius, boolean filterForParks, boolean filterForTourist, boolean filterForNature) {
        //radius > 0 AND internet connection is allowed
        if(radius > 0
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Clears the map and hashmap of locations so that previous search locations are removed
            locationsHashMap.clear();
            mMap.clear();

            //Creates a circle to show the area that the user is searching
            createCircle(userLocation, radius);

            //If the user has searched for parks then get all parks around user
            if(filterForParks) {
                new SearchLocations(radius, "park", latitude, longitude, BitmapDescriptorFactory.HUE_RED, MapsActivity.this).execute();
            }

            //If the user has searched for tourist spots then get all tourist spots around user
            if(filterForTourist) {
                new SearchLocations(radius, "tourist_attraction", latitude, longitude, BitmapDescriptorFactory.HUE_AZURE, MapsActivity.this).execute();
            }

            //If the user has searched for natural features then get all natural features around user
            if(filterForNature) {
                new SearchLocations(radius, "natural_feature", latitude, longitude, BitmapDescriptorFactory.HUE_GREEN, MapsActivity.this).execute();
            }
        }
    }

    //Option for opening up the navigation drawer on Action Bar (hamburger icon)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Method called when activity is created
    //Used to show current user location as the "blue dot"
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
        }
    }

    //Method to get the latitude and longitude of user location
    public void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //Method used to open the maps activity
    public void openMapsActivity() {
        for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }
        Button goSomewhereButtonVis = findViewById(R.id.goSomewhereButton);
        goSomewhereButtonVis.setVisibility(View.VISIBLE);
    }

    //Function that is used when the user selects an item from the navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        //If the item is the "Filter Search" item then open the filter search page
        if(id == R.id.filtersearch) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new FilterSearchFragment()).addToBackStack(null).commit();
        }
        //If item is "About" item then open the about page
        else if(id == R.id.about) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new AboutFragment()).addToBackStack(null).commit();
        }
        //If item is "Maps" item then open the maps page
        else if (id == R.id.map && getSupportFragmentManager().getBackStackEntryCount() > 0) {
            openMapsActivity();
        }

        //close the drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //Method to add location of a place to the hash map
    public void addLocation(String key, LatLng location) {
        Log.i("Location adding", key + "," + location);
        locationsHashMap.put(key, location);
    }

    //Method to create a marker for a place using the name and location of a place
    //Also provide the colour that the marker will be
    public void placeMarkers(float markerColour, String name, LatLng location) {
        mMap.addMarker(new MarkerOptions().title(name).position(location).icon(BitmapDescriptorFactory.defaultMarker(markerColour)));
    }

    //Method to draw a circle once user has entered filter results to provide a visual representation
    //of the area that is being searched
    public void createCircle(LatLng position, int radius) {
        //If there is already a circle then remove it and place another one
        if(mapCircle != null) {
            mapCircle.remove();
            mapCircle = null;
            mapCircle = mMap.addCircle(new CircleOptions().center(position).radius(radius).strokeColor(Color.BLACK));
        } else {
            mapCircle = mMap.addCircle(new CircleOptions().center(position).radius(radius).strokeColor(Color.BLACK));
        }
    }

    //Method to show spinner while program is searching for locations
    public void setMapSpinnerVisible(final boolean vis) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar progressBar = findViewById(R.id.mapsProgressBar);
                if(vis) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    //Method is used when user presses back button
    //Takes user back to the maps activity
    @Override
    public void onBackPressed() {
        openMapsActivity();
    }
}
