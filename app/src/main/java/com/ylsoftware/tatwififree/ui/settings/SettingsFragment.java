package com.ylsoftware.tatwififree.ui.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ylsoftware.tatwififree.HotspotLoader;
import com.ylsoftware.tatwififree.R;
import com.ylsoftware.tatwififree.databinding.FragmentSettingsBinding;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    private Button updateButton;
    private TextView updateInfo;
    private ProgressBar updateProgress;
    final OkHttpClient okHttpClient = new OkHttpClient();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        updateButton = root.findViewById(R.id.update_button);
        updateButton.setOnClickListener(view -> updateButtonClick());

        updateInfo = root.findViewById(R.id.update_info);

        updateProgress = root.findViewById(R.id.update_progress);
        updateProgress.setVisibility(View.GONE);

        this.updateInfoText();

        return root;
    }

    public void updateInfoText() {
        updateInfo.setText(getString(R.string.settings_notify_count, HotspotLoader.load(getContext()).size()));
    }

    public void updateButtonClick() {
        updateProgress.setVisibility(View.VISIBLE);
        Request request = new Request.Builder()
                .url(getString(R.string.update_hotspot_list_url))
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                updateFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                updateSuccess(response.body().string());
            }
        });
    }

    public void updateFailure(Exception e) {
        getActivity().runOnUiThread(() -> {
            updateProgress.setVisibility(View.GONE);
            Toast.makeText(getContext(), getString(R.string.settings_notity_update_failure, e.getMessage()), Toast.LENGTH_LONG).show();
        });
    }

    public void updateSuccess(String data) {
        getActivity().runOnUiThread(() -> {
            updateProgress.setVisibility(View.GONE);

            try {
                if (HotspotLoader.loadFromString(data).isEmpty()) {
                    throw new Exception("Empty set from server");
                }
                HotspotLoader.save(getContext(), data);
            } catch (Exception e) {
                Toast.makeText(getContext(), getString(R.string.settings_notity_update_failure, e.getMessage()), Toast.LENGTH_LONG).show();
                return;
            }

            updateInfoText();
            Toast.makeText(getContext(), getString(R.string.settings_notity_update_success), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}