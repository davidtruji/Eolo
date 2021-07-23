package com.dtsoftware.paraglidinggps;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dtsoftware.paraglidinggps.ui.flights.FlightsFragment;
import com.dtsoftware.paraglidinggps.ui.nav.NavFragment;
import com.dtsoftware.paraglidinggps.ui.routes.RoutesFragment;
import com.dtsoftware.paraglidinggps.ui.waypoints.WaypointsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity {

    private static final String TAG_NAVIGATION_FRAGMENT = "fragment_navigation";
    private static final String TAG_ROUTE_FRAGMENT = "fragment_route";
    private static final String TAG_FLIGHTS_FRAGMENT = "fragment_flights";
    private static final String TAG_WAYPOINTS_FRAGMENT = "fragment_waypoints";

    private Fragment fragmentNav;
    private Fragment fragmentWaypoints;
    private Fragment fragmentFlights;
    private Fragment fragmentRoutes;
    private FragmentManager fm;
    private Fragment activeFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        navView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                //No hace nada!
            }
        });

        fragmentNav = new NavFragment();
        fragmentRoutes = new RoutesFragment();
        fragmentFlights = new FlightsFragment();
        fragmentWaypoints = new WaypointsFragment();

        fm = getSupportFragmentManager();

        Log.i(getString(R.string.debug_tag), "Creado el NavFragment");
        fm.beginTransaction().add(R.id.nav_host_fragment, fragmentNav, TAG_NAVIGATION_FRAGMENT).commit();
        activeFragment = fragmentNav;

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

//                FragmentTransaction transaction = fm.beginTransaction();

                // Limpio la pila de BACK antes de entrar en un destino de nivel TOP
                for (int i = 0; i < fm.getBackStackEntryCount(); i++)
                    fm.popBackStack();

                //TODO: Arreglar navegacion por fragments IMPORTANTE
                switch (item.getItemId()) {
                    case R.id.navigation_nav:

                        //                       Utils.hideAllFragments(fm);
                        //                       Utils.removeAllFragments(fm);
//                        if (activeFragment != fragmentNav)
//                        transaction.remove(activeFragment);
//                        transaction.show(fragmentNav).commit();
//                        transaction.replace(R.id.nav_host_fragment, fragmentNav).commit();
                        setFragment(fragmentNav);
                        break;
                    case R.id.navigation_waypoints:

//                        if (fragmentWaypoints == null) {
//                            Log.i(getString(R.string.debug_tag), "Creado el WaypointsFragment");
//                            fragmentWaypoints = new WaypointsFragment();
//                            fm.beginTransaction().add(R.id.nav_host_fragment, fragmentWaypoints, TAG_WAYPOINTS_FRAGMENT).commit();
//                        }
//                        Utils.hideAllFragments(fm);
//                        transaction.show(fragmentWaypoints).commit();
//                        transaction.replace(R.id.nav_host_fragment, fragmentWaypoints).commit();
                        setFragment(fragmentWaypoints);
                        break;
                    case R.id.navigation_flights:

//                        if (fragmentFlights == null) {
//                            Log.i(getString(R.string.debug_tag), "Creado el FlightsFragment");
//                            fragmentFlights = new FlightsFragment();
//                            fm.beginTransaction().add(R.id.nav_host_fragment, fragmentFlights, TAG_FLIGHTS_FRAGMENT).commit();
//                        }
//                        Utils.hideAllFragments(fm);
//                        transaction.show(fragmentFlights).commit();

//                        transaction.replace(R.id.nav_host_fragment, fragmentFlights).commit();
                        setFragment(fragmentFlights);

                        break;
                    case R.id.navigation_route:

//                        if (fragmentRoutes == null) {
//                            Log.i(getString(R.string.debug_tag), "Creado el RoutesFragment");
//                            fragmentRoutes = new RoutesFragment();
//                            fm.beginTransaction().add(R.id.nav_host_fragment, fragmentRoutes, TAG_ROUTE_FRAGMENT).commit();
//                        }
//                        Utils.hideAllFragments(fm);
//                        transaction.show(fragmentRoutes).commit();
                        setFragment(fragmentRoutes);

                        break;

                }
                return true;
            }
        });


    }


    public void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        // getSupportActionBar().hide(); //TODO: Esconder Toolbar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                // View.SYSTEM_UI_FLAG_FULLSCREEN);
        );

    }

    public void showSystemUI() {
        //  getSupportActionBar().show();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction transaction = fm.beginTransaction();

        if (activeFragment == fragmentNav) {

            transaction.hide(activeFragment);
            transaction.add(R.id.nav_host_fragment, fragment);

        } else {
            if (fragment == fragmentNav)
                transaction.show(fragmentNav).remove(activeFragment);
            else {
                transaction.add(R.id.nav_host_fragment, fragment).remove(activeFragment);
            }

        }

        transaction.commit();
        activeFragment = fragment;


    }


}