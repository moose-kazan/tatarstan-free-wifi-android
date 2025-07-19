package com.ylsoftware.tatwififree.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private FragmentHomeBinding binding;

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

        final HotspotAdapter adapter = new HotspotAdapter(hotspots);

        hotspotListView.setAdapter(adapter);

        return root;
    }

    private void loadHotspotList() {
        loadHotspotListFromResource(R.raw.points);
        /*
        this.hotspots.clear();
        this.hotspots.add(new Hotspot("Технопарк им.Попова", 55.751587, 48.752445));
        this.hotspots.add(new Hotspot("Технопарк им.Лобачевского", 55.752194, 48.749498));
        this.hotspots.add(new Hotspot("Университет Иннополис", 55.753898, 48.74321));
        this.hotspots.add(new Hotspot("с. Пестрецы, Советская, 9", 55.748143, 49.654036));
        this.hotspots.add(new Hotspot("г. Казань, с. Осиново, Садовая,9 (ТЦ)", 55.872905, 48.882338));
        this.hotspots.add(new Hotspot("Набережная Озера Кабан", 55.781476, 49.123304));
        this.hotspots.sort(new HotspotComparator());

         */
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

    public class HotspotComparator implements Comparator<Hotspot> {

        @Override
        public int compare(Hotspot t1, Hotspot t2) {
            if (t1.distance == t2.distance) {
                return 0;
            } else if (t1.distance > t2.distance) {
                return 1;
            }
            return -1;
        }
    }
}