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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity {

    Fragment fragmentNav;
    Fragment fragmentWaypoints;
    Fragment fragmentFlights;
    Fragment fragmentRoutes;
    FragmentManager fm;

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

        fragmentNav = new NavFragment();
        fm = getSupportFragmentManager();

        Log.i(getString(R.string.debug_tag), "Creado el NavFragment");
        fm.beginTransaction().add(R.id.nav_host_fragment, fragmentNav).commit();

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentTransaction transaction = fm.beginTransaction();

                //Limpio la pila de BACK antes de entrar en un destino de nivel TOP
                for (int i = 0; i < fm.getBackStackEntryCount(); i++)
                    fm.popBackStack();

                switch (item.getItemId()) {
                    case R.id.navigation_nav:

                        Utils.hideAllFragments(fm);
                        transaction.show(fragmentNav).commit();

                        break;
                    case R.id.navigation_waypoints:

                        if (fragmentWaypoints == null) {
                            Log.i(getString(R.string.debug_tag), "Creado el WaypointsFragment");
                            fragmentWaypoints = new WaypointsFragment();
                            fm.beginTransaction().add(R.id.nav_host_fragment, fragmentWaypoints).commit();
                        }
                        Utils.hideAllFragments(fm);
                        transaction.show(fragmentWaypoints).commit();

                        break;
                    case R.id.navigation_flights:

                        if (fragmentFlights == null) {
                            Log.i(getString(R.string.debug_tag), "Creado el FlightsFragment");
                            fragmentFlights = new FlightsFragment();
                            fm.beginTransaction().add(R.id.nav_host_fragment, fragmentFlights).commit();
                        }
                        Utils.hideAllFragments(fm);
                        transaction.show(fragmentFlights).commit();

                        break;
                    case R.id.navigation_route:

                        if (fragmentRoutes == null) {
                            Log.i(getString(R.string.debug_tag), "Creado el RoutesFragment");
                            fragmentRoutes = new RoutesFragment();
                            fm.beginTransaction().add(R.id.nav_host_fragment, fragmentRoutes).commit();
                        }
                        //   fm.beginTransaction().hide(activeFragment).show(fragmentRoutes).commit();

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


}