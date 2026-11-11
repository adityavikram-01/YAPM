package com.yapm.android;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.Objects;

public class EditCredentialFragment extends Fragment {

    private Vault vault;
    private Credential oldCredential;

    private RadioGroup serviceType;
    private EditText serviceName;
    private EditText username;
    private EditText password;
    private EditText notes;

    private CompoundButton ownPassword;
    private View generatorLayout;

    private EditText passwordLength;
    private CompoundButton lowercase;
    private CompoundButton uppercase;
    private CompoundButton numbers;
    private CompoundButton symbols;
    private EditText generatedPassword;

    private TextView serviceNameLabel;
    private Button saveButton;

    public EditCredentialFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.add_credential,
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

        serviceType = view.findViewById(R.id.rg_service_type);
        serviceName = view.findViewById(R.id.et_service_name);
        username = view.findViewById(R.id.et_username);
        password = view.findViewById(R.id.et_password);
        notes = view.findViewById(R.id.et_notes);

        ownPassword = view.findViewById(R.id.sw_own_password);
        generatorLayout = view.findViewById(R.id.layout_generator);

        serviceNameLabel = view.findViewById(R.id.tv_service_name);
        saveButton = view.findViewById(R.id.btn_add_credential);

        passwordLength = view.findViewById(R.id.et_password_length);
        lowercase = view.findViewById(R.id.sw_lowercase);
        uppercase = view.findViewById(R.id.sw_uppercase);
        numbers = view.findViewById(R.id.sw_numbers);
        symbols = view.findViewById(R.id.sw_symbols);
        generatedPassword = view.findViewById(R.id.et_generated_password);

        Bundle arguments = requireArguments();

        String type = arguments.getString("type");
        String service = arguments.getString("service");
        String oldUsername = arguments.getString("username");

        oldCredential = vault.get(type, service, oldUsername);

        if (oldCredential == null) {
            Toast.makeText(requireContext(),
                    "Credential not found",
                    Toast.LENGTH_SHORT).show();

            Navigation.findNavController(view).popBackStack();
            return;
        }

        loadCredential();
        setupForm();
    }

    private void loadCredential() {

        if (oldCredential instanceof SiteCredential) {
            serviceType.check(R.id.rb_site);
            serviceName.setText(
                    ((SiteCredential) oldCredential).getSite()
            );
        }
        else if (oldCredential instanceof SocialMediaCredential) {
            serviceType.check(R.id.rb_social_media);
            serviceName.setText(
                    ((SocialMediaCredential) oldCredential).getPlatform()
            );
        }
        else if (oldCredential instanceof WifiCredential) {
            serviceType.check(R.id.rb_wifi);
            serviceName.setText(
                    ((WifiCredential) oldCredential).getSsid()
            );
        }

        username.setText(oldCredential.getUsername());
        password.setText(oldCredential.getPassword());

        String noteText = oldCredential.getNotes();

        if (noteText != null) {
            notes.setText(noteText);
        }

        saveButton.setText("Save Changes");
    }

    private void setupForm() {

        updateServiceLabel(serviceType.getCheckedRadioButtonId());

        serviceType.setOnCheckedChangeListener(
                (group, checkedId) -> updateServiceLabel(checkedId)
        );

        ownPassword.setOnCheckedChangeListener(
                (button, checked) -> {

                    if (checked) {
                        password.setVisibility(View.VISIBLE);
                        generatorLayout.setVisibility(View.GONE);
                    } else {
                        password.setVisibility(View.GONE);
                        generatorLayout.setVisibility(View.VISIBLE);
                    }
                }
        );

        saveButton.setOnClickListener(v -> saveChanges());

        Button generateButton =
                requireView().findViewById(R.id.btn_generate_password);

        if (generateButton != null) {
            generateButton.setOnClickListener(v -> generatePassword());
        }
    }

    private void updateServiceLabel(int checkedId) {

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
    }

    private void generatePassword() {

        try {
            int length = Integer.parseInt(
                    passwordLength.getText().toString()
            );

            String generated = PasswordGenerator.PasswordGenerator(
                    length,
                    lowercase.isChecked(),
                    uppercase.isChecked(),
                    numbers.isChecked(),
                    symbols.isChecked()
            );

            if (generated.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Select password options and a valid length",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            generatedPassword.setText(generated);
            generatedPassword.setVisibility(View.VISIBLE);

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(),
                    "Enter a valid password length",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void saveChanges() {

        String service = serviceName.getText().toString().trim();
        String user = username.getText().toString().trim();
        String pass;

        if (ownPassword.isChecked()) {
            pass = password.getText().toString();
        } else {
            pass = generatedPassword.getText().toString();
        }

        String noteText = notes.getText().toString().trim();

        if (service.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Please fill in all required fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedType = serviceType.getCheckedRadioButtonId();

        Credential updatedCredential;

        if (selectedType == R.id.rb_site) {
            updatedCredential = new SiteCredential(
                    service, user, pass, noteText
            );
        }
        else if (selectedType == R.id.rb_social_media) {
            updatedCredential = new SocialMediaCredential(
                    service, user, pass, noteText
            );
        }
        else if (selectedType == R.id.rb_wifi) {
            updatedCredential = new WifiCredential(
                    service, user, pass, noteText
            );
        }
        else {
            Toast.makeText(requireContext(),
                    "Please select a credential type",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int result = vault.update(oldCredential, updatedCredential);

            if (result > 0) {
                Toast.makeText(requireContext(),
                        "Credential updated",
                        Toast.LENGTH_SHORT).show();

                Navigation.findNavController(requireView()).popBackStack();
            } else {
                Toast.makeText(requireContext(),
                        "Unable to update credential",
                        Toast.LENGTH_SHORT).show();
            }

        } catch (SQLiteConstraintException e) {
            Toast.makeText(requireContext(),
                    "A credential with these details already exists",
                    Toast.LENGTH_LONG).show();
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
