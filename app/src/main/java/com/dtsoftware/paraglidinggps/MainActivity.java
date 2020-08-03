package com.dtsoftware.paraglidinggps;

import android.os.Bundle;
import android.view.MenuItem;

import com.dtsoftware.paraglidinggps.ui.nav.NavFragment;
import com.dtsoftware.paraglidinggps.ui.waypoints.Waypoints;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class MainActivity extends AppCompatActivity {

    final Fragment fragmentNav= new NavFragment();
    final Fragment fragmentWaypoints = new Waypoints();//TODO: Declarar el resto de fragments
    final FragmentManager fm = getSupportFragmentManager();
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


        fm.beginTransaction().add(R.id.nav_host_fragment,fragmentNav).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment,fragmentWaypoints).hide(fragmentWaypoints).commit();


        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_nav:
                        fm.beginTransaction().hide(activeFragment).show(fragmentNav).commit();
                        activeFragment = fragmentNav;
                        break;
                    case R.id.navigation_waypoints:
                        fm.beginTransaction().hide(activeFragment).show(fragmentWaypoints).commit();
                        activeFragment = fragmentWaypoints;
                        break;
                }
                return true;
            }
        });

    }


}