package com.ylsoftware.tatwififree.ui.home;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.ylsoftware.tatwififree.Hotspot;
import com.ylsoftware.tatwififree.HotspotAdapter;
import com.ylsoftware.tatwififree.HotspotLoader;
import com.ylsoftware.tatwififree.R;
import com.ylsoftware.tatwififree.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    ArrayList<Hotspot> hotspots = new ArrayList<>();
    HotspotAdapter hotspotAdapter = null;

    private SwipeRefreshLayout swipeRefreshLayout;

    private FragmentHomeBinding binding;

    private FusedLocationProviderClient fusedLocationClient;

    private Location lastLocation = null;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.lastLocation = new Location("");
            this.lastLocation.setLatitude(savedInstanceState.getDouble("last_lat", 0));
            this.lastLocation.setLongitude(savedInstanceState.getDouble("last_lon", 0));
            //Log.i("FOUNDLOCATION", this.lastLocation.toString());
        }

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

        //Log.i("Clicked", "lat=" + item.lon + " lon=" + item.lon);
        this.hotspotAdapter = new HotspotAdapter(hotspots, this::openNavigator, this.lastLocation);

        hotspotListView.setAdapter(this.hotspotAdapter);

        swipeRefreshLayout = root.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this::getGeoLocation);

        swipeRefreshLayout.setRefreshing(true);
        getGeoLocation();

        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("last_lon", lastLocation.getLongitude());
        outState.putDouble("last_lat", lastLocation.getLatitude());
    }

    private void openNavigator(Hotspot hotspot) {
        Uri navUri = Uri.parse("geo:" + hotspot.lat + "," + hotspot.lon + "?q=" + Uri.encode(hotspot.address));
        //Uri navUri = Uri.parse("geo:" + hotspot.lat + "," + hotspot.lon);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, navUri);
        startActivity(mapIntent);
    }


    @SuppressLint("MissingPermission")
    private void getGeoLocation() {
        //Log.i("LOCATION:", "START");
        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this.getActivity(), location -> {
                    if (location != null) {
                        this.lastLocation = location;
                        hotspotAdapter.setCurrentLocation(location);
                        //Log.i("LOCATION:", "LON" + location.getLongitude() + " LAT: "+ location.getLatitude());
                    }
                })
                .addOnFailureListener(this.getActivity(), e -> {
                    Toast toast = Toast.makeText(getActivity(), getString(R.string.notify_cant_get_geo), Toast.LENGTH_LONG);
                    toast.show();
                })
                .addOnCompleteListener(this.getActivity(), task -> swipeRefreshLayout.setRefreshing(false));
    }

    private void loadHotspotList() {
        this.hotspots = HotspotLoader.load(this.getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}