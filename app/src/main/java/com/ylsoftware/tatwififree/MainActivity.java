package com.ylsoftware.tatwififree;

import android.Manifest;
import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.ylsoftware.tatwififree.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    public static String SETTIGS_UI_MODE = "uiMode";

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private ActivityResultLauncher<String[]> locationPermissionRequest;

    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UiModeManager uiManager = (UiModeManager) getApplicationContext().getSystemService(Context.UI_MODE_SERVICE);
        int uiMode = getPreferences(Context.MODE_PRIVATE).getInt(SETTIGS_UI_MODE, UiModeManager.MODE_NIGHT_AUTO);
        if (uiMode == UiModeManager.MODE_NIGHT_YES) {
            uiManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_YES);
        } else if (uiMode == UiModeManager.MODE_NIGHT_NO) {
            uiManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_NO);
        } else {
            uiManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_AUTO);
        }

        super.onCreate(savedInstanceState);

        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showPermissionsRequest();
            return;
        }

        initMainUI();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    protected void showPermissionsRequest() {
        setContentView(R.layout.activity_main_permissions);
        this.locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = null;
                    Boolean coarseLocationGranted = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                        coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION,false);
                    }
                    if (fineLocationGranted != null && fineLocationGranted) {
                        initMainUI();
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        // Do nothing....
                    } else {
                    }
                });
    }

    public void requestPermissions(View view) {

        this.locationPermissionRequest.launch(new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    protected void initMainUI() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_settings, R.id.nav_about)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
}