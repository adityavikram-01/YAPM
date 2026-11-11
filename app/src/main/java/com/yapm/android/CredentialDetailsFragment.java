
package com.yapm.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;
public class CredentialDetailsFragment extends Fragment {

    private Vault vault;
    private Credential credential;
    private TextView password;

    public CredentialDetailsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.credential_details,
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

        String type = requireArguments().getString("type");
        String service = requireArguments().getString("service");
        String username = requireArguments().getString("username");

        credential = vault.get(type, service, username);

        if (credential == null) {
            Navigation.findNavController(view).popBackStack();
            return;
        }

        TextView serviceName = view.findViewById(R.id.tv_detail_service);
        TextView category = view.findViewById(R.id.tv_detail_category);
        TextView usernameText = view.findViewById(R.id.tv_detail_username);
        TextView notes = view.findViewById(R.id.tv_detail_notes);

        password = view.findViewById(R.id.tv_detail_password);

        serviceName.setText(service);
        category.setText(credential.getCategory());
        usernameText.setText(credential.getUsername());
        password.setText("••••••••");

        String noteText = credential.getNotes();

        if (noteText == null || noteText.trim().isEmpty()) {
            notes.setText("No notes available");
        } else {
            notes.setText(noteText);
        }

        Button togglePassword = view.findViewById(R.id.btn_toggle_password);
        Button deleteCredential = view.findViewById(R.id.btn_delete_credential);
        Button editCredential = view.findViewById(R.id.btn_edit_credential);

        togglePassword.setOnClickListener(v -> {

            if (password.getText().toString().equals("••••••••")) {
                password.setText(credential.getPassword());
                togglePassword.setText("Hide Password");
            } else {
                password.setText("••••••••");
                togglePassword.setText("Show Password");
            }
        });

        editCredential.setOnClickListener(v -> {

            String credentialType = getCredentialType(credential);
            String credentialService = getServiceName(credential);

            Bundle bundle = new Bundle();

            bundle.putString("type", credentialType);
            bundle.putString("service", credentialService);
            bundle.putString("username", credential.getUsername());

            Navigation.findNavController(view).navigate(
                    R.id.action_CredentialDetailsFragment_to_EditCredentialFragment,
                    bundle
            );
        });

        deleteCredential.setOnClickListener(v -> {

            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Credential")
                    .setMessage("Are you sure you want to delete this credential?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        int result = vault.remove(credential);

                        if (result > 0) {
                            Toast.makeText(requireContext(),
                                    "Credential deleted successfully",
                                    Toast.LENGTH_SHORT).show();

                            Navigation.findNavController(requireView()).popBackStack();

                        } else {
                            Toast.makeText(requireContext(),
                                    "Could not delete credential",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }


    private String getCredentialType(Credential credential) {
        if (credential instanceof SiteCredential) return "SITE";
        if (credential instanceof SocialMediaCredential) return "SOCIAL_MEDIA";
        if (credential instanceof WifiCredential) return "WIFI";
        return "";
    }

    private String getServiceName(Credential credential) {

        if (credential instanceof SiteCredential) {
            return ((SiteCredential) credential).getSite();
        }

        if (credential instanceof SocialMediaCredential) {
            return ((SocialMediaCredential) credential).getPlatform();
        }

        if (credential instanceof WifiCredential) {
            return ((WifiCredential) credential).getSsid();
        }

        return "";
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }

        if (vault == null) {
            vault = new Vault(requireContext());
        }

        Bundle arguments = requireArguments();

        String type = arguments.getString("type");
        String service = arguments.getString("service");
        String username = arguments.getString("username");

        credential = vault.get(type, service, username);

        if (credential == null) {
            Navigation.findNavController(requireView()).popBackStack();
            return;
        }

        TextView serviceName = requireView().findViewById(R.id.tv_detail_service);
        TextView category = requireView().findViewById(R.id.tv_detail_category);
        TextView usernameText = requireView().findViewById(R.id.tv_detail_username);
        TextView notesText = requireView().findViewById(R.id.tv_detail_notes);
        TextView passwordText = requireView().findViewById(R.id.tv_detail_password);

        Button togglePassword = requireView().findViewById(R.id.btn_toggle_password);

        serviceName.setText(getServiceName(credential));
        category.setText(credential.getCategory());
        usernameText.setText(credential.getUsername());

        String noteText = credential.getNotes();

        notesText.setText(
                noteText == null || noteText.trim().isEmpty()
                        ? "No notes available"
                        : noteText
        );

        passwordText.setText("••••••••");
        togglePassword.setText("Show Password");
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
