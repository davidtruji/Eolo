package com.dtsoftware.paraglidinggps;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.dtsoftware.paraglidinggps.ui.flights.FlightsFragment;
import com.dtsoftware.paraglidinggps.ui.nav.NavFragment;
import com.dtsoftware.paraglidinggps.ui.routes.RoutesFragment;
import com.dtsoftware.paraglidinggps.ui.waypoints.WaypointsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class MainActivity extends AppCompatActivity {

    Fragment fragmentNav= new NavFragment();
    Fragment fragmentWaypoints;
    Fragment fragmentFlights;
    Fragment fragmentRoutes;
    FragmentManager fm = getSupportFragmentManager();
    Fragment activeFragment = fragmentNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        navView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                // No hace nada al reelegir un item
            }
        });


        Log.i(getString(R.string.debug_tag),"Creado el NavFragment");
        fm.beginTransaction().add(R.id.nav_host_fragment,fragmentNav).commit();


        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_nav:
                        fm.beginTransaction().hide(activeFragment).show(fragmentNav).commit();
                        activeFragment = fragmentNav;
                        break;
                    case R.id.navigation_waypoints:
                        if(fragmentWaypoints == null){
                            Log.i(getString(R.string.debug_tag),"Creado el WaypointsFragment");
                            fragmentWaypoints = new WaypointsFragment();
                            fm.beginTransaction().add(R.id.nav_host_fragment,fragmentWaypoints).commit();
                        }
                        fm.beginTransaction().hide(activeFragment).show(fragmentWaypoints).commit();
                        activeFragment = fragmentWaypoints;
                        break;
                    case R.id.navigation_flights:
                        if(fragmentFlights == null){
                            Log.i(getString(R.string.debug_tag),"Creado el FlightsFragment");
                            fragmentFlights = new FlightsFragment();
                            fm.beginTransaction().add(R.id.nav_host_fragment,fragmentFlights).commit();
                        }
                        fm.beginTransaction().hide(activeFragment).show(fragmentFlights).commit();
                        activeFragment = fragmentFlights;
                        break;
                    case R.id.navigation_route:
                        if(fragmentRoutes == null){
                            Log.i(getString(R.string.debug_tag),"Creado el RoutesFragment");
                            fragmentRoutes = new RoutesFragment();
                            fm.beginTransaction().add(R.id.nav_host_fragment,fragmentRoutes).commit();
                        }
                        fm.beginTransaction().hide(activeFragment).show(fragmentRoutes).commit();
                        activeFragment = fragmentRoutes;
                        break;

                }
                return true;
            }
        });

    }


}