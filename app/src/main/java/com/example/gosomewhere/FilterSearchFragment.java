package com.example.gosomewhere;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//Provides user a way to filter the search for locations by providing a radius to search
//and checking the boxes of the locations to search from
public class FilterSearchFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private CheckBox filterParks;
    private CheckBox filterTourist;
    private CheckBox filterNature;

    private TextView radiusInput;

    //Will be used to hold whether a checkbox has been checked and will be passed to method
    //in maps activity to filter searches
    private Boolean filterForParks = false;
    private Boolean filterForNature = false;
    private Boolean filterForTourist = false;

    //Runs when user selects "Filter Search" from the navigation drawer
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filtersearch, container, false);

        Button gosomewherebutton = getActivity().findViewById(R.id.goSomewhereButton);
        gosomewherebutton.setVisibility(View.INVISIBLE);

        filterParks = (CheckBox)view.findViewById(R.id.filterParks);
        filterTourist = (CheckBox)view.findViewById(R.id.filterTourist);
        filterNature = (CheckBox)view.findViewById(R.id.filterNature);
        radiusInput = (TextView)view.findViewById(R.id.radiusInput);
        filterParks.setOnCheckedChangeListener(this);
        filterTourist.setOnCheckedChangeListener(this);
        filterNature.setOnCheckedChangeListener(this);

        //runs when user pressed the "Apply Filters" button
        Button applyFilters = view.findViewById(R.id.applyFilterButton);
        applyFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int radius = checkRadius();
                MapsActivity mapsActivity = (MapsActivity) getActivity();
                //Sending the filter data to maps activity to perform searches
                mapsActivity.getFilterResults(radius, filterForParks, filterForTourist, filterForNature);
                mapsActivity.openMapsActivity(); //Opens map activity when user has pressed button after entering filter data
            }
        });

        return view;
    }

    //Setting variables to true if the checkbox is checked
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        filterForParks = filterParks.isChecked();

        filterForTourist = filterTourist.isChecked();

        filterForNature = filterNature.isChecked();
    }

    //Used to check that the user input for radius is valid
    //User will input number as if it were in miles
    //will calculate input as metres using metre = mile * 1609.344
    private int checkRadius() {
        int radius;
        try {
            radius = Integer.parseInt(radiusInput.getText().toString());
            //Setting limit on the values user can input as a radius (50 is max and 1 is min)
            if(radius > 0 && radius <= 50) {
                radius *= 1609.344; //Calculating miles to metres
                return radius;
            } else {
                Toast.makeText(getActivity(), R.string.radius_limits, Toast.LENGTH_SHORT).show();
                return 0;
            }

        } catch(NumberFormatException e) {
            radius = 0;
            return radius;
        }
    }
}
