package com.yapm.android;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchCredentialFragment extends Fragment {

    private Vault vault;
    private CredentialAdapter adapter;

    private EditText searchField;
    private RadioGroup searchType;
    private RecyclerView recyclerView;
    private TextView noResults;

    public SearchCredentialFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.search_credential,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        vault = new Vault(requireContext());

        searchField = view.findViewById(R.id.et_search);
        searchType = view.findViewById(R.id.rg_search_type);
        recyclerView = view.findViewById(R.id.rv_search_results);
        noResults = view.findViewById(R.id.tv_no_results);

        ArrayList<Credential> credentials = new ArrayList<>();

        adapter = new CredentialAdapter(credentials, credential -> {

            String type = "";
            String service = "";

            if (credential instanceof SiteCredential) {
                type = "SITE";
                service = ((SiteCredential) credential).getSite();
            }
            else if (credential instanceof SocialMediaCredential) {
                type = "SOCIAL_MEDIA";
                service = ((SocialMediaCredential) credential).getPlatform();
            }
            else if (credential instanceof WifiCredential) {
                type = "WIFI";
                service = ((WifiCredential) credential).getSsid();
            }

            Bundle bundle = new Bundle();

            bundle.putString("type", type);
            bundle.putString("service", service);
            bundle.putString("username", credential.getUsername());

            androidx.navigation.Navigation.findNavController(requireView())
                    .navigate(R.id.action_SearchCredentialFragment_to_CredentialDetailsFragment, bundle);
        });

        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerView.setAdapter(adapter);

        searchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                performSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchType.setOnCheckedChangeListener((group, checkedId) -> {
            performSearch();
        });

        performSearch();
    }

    private void performSearch() {

        String service = searchField.getText().toString().trim();

        int selectedId = searchType.getCheckedRadioButtonId();

        String type;

        if (selectedId == R.id.rb_search_site) {
            type = "SITE";
        }

        else if (selectedId == R.id.rb_search_social_media) {
            type = "SOCIAL_MEDIA";
        }

        else if (selectedId == R.id.rb_search_wifi) {
            type = "WIFI";
        }

        else {
            type = "ALL";
        }

        ArrayList<Credential> results = vault.search(service, type);

        adapter.updateCredentials(results);

        if (results.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        }

        else {
            recyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (vault != null) {
            vault.close();
            vault = null;
        }
    }
}
