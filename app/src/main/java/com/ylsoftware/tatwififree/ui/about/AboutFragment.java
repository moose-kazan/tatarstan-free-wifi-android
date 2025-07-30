package com.ylsoftware.tatwififree.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ylsoftware.tatwififree.R;
import com.ylsoftware.tatwififree.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AboutViewModel aboutViewModel =
                new ViewModelProvider(this).get(AboutViewModel.class);

        binding = FragmentAboutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button buttonOpenMap = root.findViewById(R.id.about_button_map);
        buttonOpenMap.setOnClickListener(view -> buttonOpenMapClick());

        Button buttonDonate = root.findViewById(R.id.about_button_donate);
        buttonDonate.setOnClickListener(view -> buttonDonateClick());

        return root;
    }

    private void buttonDonateClick() {
        Uri donateUri = Uri.parse(getString(R.string.about_donate_url));
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, donateUri);
        startActivity(urlIntent);
    }

    private void buttonOpenMapClick() {
        Uri mapUri = Uri.parse(getString(R.string.about_open_map_url));
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, mapUri);
        startActivity(urlIntent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}