
package com.yapm.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class AddCredentialFragment extends Fragment {

    private Vault vault;

    private RadioGroup serviceType;
    private TextView serviceNameLabel;
    private EditText serviceName;
    private EditText username;
    private EditText password;
    private EditText generatedPassword;
    private EditText passwordLength;

    private EditText notes;

    private SwitchCompat ownPassword;
    private SwitchCompat lowercase;
    private SwitchCompat uppercase;
    private SwitchCompat numbers;
    private SwitchCompat symbols;

    private View generatorLayout;

    public AddCredentialFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.add_credential, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        vault = new Vault(requireContext());

        serviceType = view.findViewById(R.id.rg_service_type);
        serviceNameLabel = view.findViewById(R.id.tv_service_name);
        serviceName = view.findViewById(R.id.et_service_name);
        username = view.findViewById(R.id.et_username);
        password = view.findViewById(R.id.et_password);
        generatedPassword = view.findViewById(R.id.et_generated_password);
        passwordLength = view.findViewById(R.id.et_password_length);

        ownPassword = view.findViewById(R.id.sw_own_password);
        lowercase = view.findViewById(R.id.sw_lowercase);
        uppercase = view.findViewById(R.id.sw_uppercase);
        numbers = view.findViewById(R.id.sw_numbers);
        symbols = view.findViewById(R.id.sw_symbols);

        generatorLayout = view.findViewById(R.id.layout_generator);

        Button generateButton = view.findViewById(R.id.btn_generate_password);
        notes = view.findViewById(R.id.et_notes);
        Button addButton = view.findViewById(R.id.btn_add_credential);

        serviceType.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.rb_site) {
                serviceNameLabel.setText("Site Name");
                serviceName.setHint("Enter site name");
            }

            else if (checkedId == R.id.rb_social_media) {
                serviceNameLabel.setText("Platform Name");
                serviceName.setHint("Enter platform name");
            }

            else if (checkedId == R.id.rb_wifi) {
                serviceNameLabel.setText("WiFi Name");
                serviceName.setHint("Enter WiFi name");
            }
        });

        ownPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                password.setVisibility(View.VISIBLE);
                generatorLayout.setVisibility(View.GONE);
            }

            else {
                password.setVisibility(View.GONE);
                generatorLayout.setVisibility(View.VISIBLE);
            }
        });

        generateButton.setOnClickListener(v -> generatePassword());

        addButton.setOnClickListener(v -> addCredential(v));
    }

    private void generatePassword() {

        String lengthText = passwordLength.getText().toString().trim();

        if (lengthText.isEmpty()) {
            passwordLength.setError("Enter password length");
            return;
        }

        int length;

        try {
            length = Integer.parseInt(lengthText);
        }

        catch (NumberFormatException e) {
            passwordLength.setError("Invalid password length");
            return;
        }

        if (length < PasswordGenerator.MIN_LENGTH ||
                length > PasswordGenerator.MAX_LENGTH) {

            passwordLength.setError(
                    "Length must be between " +
                            PasswordGenerator.MIN_LENGTH + " and " +
                            PasswordGenerator.MAX_LENGTH
            );
            return;
        }

        if (!lowercase.isChecked() &&
                !uppercase.isChecked() &&
                !numbers.isChecked() &&
                !symbols.isChecked()) {

            Toast.makeText(
                    requireContext(),
                    "Select at least one character type",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String result = PasswordGenerator.PasswordGenerator(
                length,
                lowercase.isChecked(),
                uppercase.isChecked(),
                numbers.isChecked(),
                symbols.isChecked()
        );

        if (result.isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    "Could not generate password",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        generatedPassword.setText(result);
        generatedPassword.setVisibility(View.VISIBLE);
    }

    private void addCredential(View view) {

        int selectedType = serviceType.getCheckedRadioButtonId();

        if (selectedType == -1) {
            Toast.makeText(
                    requireContext(),
                    "Select a service type",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String service = serviceName.getText().toString().trim();
        String user = username.getText().toString().trim();
        String noteText = notes.getText().toString().trim();

        if (service.isEmpty()) {
            serviceName.setError("Enter service name");
            return;
        }

        if (user.isEmpty()) {
            username.setError("Enter username");
            return;
        }

        String pass;

        if (ownPassword.isChecked()) {
            pass = password.getText().toString();
        }

        else {
            pass = generatedPassword.getText().toString();
        }

        if (pass.trim().isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    ownPassword.isChecked()
                            ? "Enter your password"
                            : "Generate a password first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Credential credential;

        if (selectedType == R.id.rb_site) {
            credential = new SiteCredential(
                    service, user, pass, noteText
            );
        }

        else if (selectedType == R.id.rb_social_media) {
            credential = new SocialMediaCredential(
                    service, user, pass, noteText
            );
        }

        else {
            credential = new WifiCredential(
                    service, user, pass, noteText
            );
        }

        try {
            long result = vault.add(credential);

            if (result == -1) {
                Toast.makeText(
                        requireContext(),
                        "Credential already exists or could not be saved",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            Toast.makeText(
                    requireContext(),
                    "Credential added successfully",
                    Toast.LENGTH_SHORT
            ).show();

            Navigation.findNavController(view).navigateUp();
        }

        catch (Exception e) {
            Toast.makeText(
                    requireContext(),
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (vault != null) {
            vault.close();
        }
    }
}
