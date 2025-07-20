package com.ylsoftware.tatwififree.ui.home;

import static com.google.android.gms.location.LocationServices.*;
import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import com.ylsoftware.tatwififree.Hotspot;
import com.ylsoftware.tatwififree.HotspotAdapter;
import com.ylsoftware.tatwififree.R;
import com.ylsoftware.tatwififree.databinding.FragmentHomeBinding;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;

public class HomeFragment extends Fragment {
    ArrayList<Hotspot> hotspots = new ArrayList<Hotspot>();
    HotspotAdapter hostspotAdapter = null;

    private FragmentHomeBinding binding;

    private FusedLocationProviderClient fusedLocationClient;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final RecyclerView hotspotListView = root.findViewById(R.id.hotspot_list);

        RecyclerView.LayoutManager layoutMgr = new LinearLayoutManager(getActivity());

        hotspotListView.setLayoutManager(layoutMgr);

        loadHotspotList();

        this.hostspotAdapter = new HotspotAdapter(hotspots);

        hotspotListView.setAdapter(this.hostspotAdapter);

        getGeoLocation();

        return root;
    }

    @SuppressLint("MissingPermission")
    private void getGeoLocation() {
        //Log.i("LOCATION:", "START");
        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this.getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    hostspotAdapter.setCurrentLocation(location);
                    //Log.i("LOCATION:", "LON" + location.getLongitude() + " LAT: "+ location.getLatitude());
                }
            }
        });
    }

    private void loadHotspotList() {
        loadHotspotListFromResource(R.raw.points);
    }

    private void loadHotspotListFromResource(int resId) {
        DataInputStream dis = null;
        try {
            InputStream is = getResources().openRawResource(resId);
            dis = new DataInputStream(is);
            byte[] buff = new byte[dis.available()];
            dis.read(buff, 0, dis.available());
            loadHotspotListFromString(new String(buff));
        }
        catch (Exception e) {
            Log.e("Can't load file", e.getMessage());
        }
        finally {
            try {
                dis.close();
            } catch (IOException e) {
                Log.e("Can't close file", e.getMessage());
            }
        }
    }
    @SuppressLint("MissingPermission")
    private void loadHotspotListFromString(String data) {
        Gson gson = new Gson();
        Hotspot[] hotspotList = gson.fromJson(data, Hotspot[].class);

        this.hotspots.clear();
        for (int i = 0; i < hotspotList.length; i++) {
            this.hotspots.add(hotspotList[i]);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}