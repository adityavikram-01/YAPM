
package com.yapm.android;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.IOException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class AuthenticationFragment extends Fragment {

    private String generatedPhrase;

    public AuthenticationFragment() {
        super(R.layout.fragment_authentication);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        TextView tvInstruction =
                view.findViewById(R.id.tv_auth_instruction);

        TextView tvMasterPhrase =
                view.findViewById(R.id.tv_master_phrase);

        EditText etMasterPhrase =
                view.findViewById(R.id.et_master_phrase);

        Button btnAuthenticate =
                view.findViewById(R.id.btn_authenticate);

        Button btnCopyPhrase =
                view.findViewById(R.id.btn_copy_phrase);


        boolean setup = !AuthenticationManager.isSetup(requireContext());


        // FIRST-TIME SETUP
        if (setup) {

            tvInstruction.setText(
                    "Your new 12-word master phrase. Save it safely!"
            );

            tvMasterPhrase.setVisibility(View.VISIBLE);

            try {

                generatedPhrase =
                        AuthenticationManager.generateMasterPhrase(
                                requireContext()
                        );

                tvMasterPhrase.setText(generatedPhrase);

                btnCopyPhrase.setVisibility(View.VISIBLE);

                btnCopyPhrase.setOnClickListener(v -> {

                    ClipboardManager clipboard =
                            (ClipboardManager) requireContext()
                                    .getSystemService(Context.CLIPBOARD_SERVICE);

                    ClipData clip = ClipData.newPlainText(
                            "Master Phrase",
                            generatedPhrase
                    );

                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(requireContext(),
                            "Master phrase copied!",
                            Toast.LENGTH_SHORT).show();
                });

            } catch (IOException e) {

                Toast.makeText(requireContext(),
                        "Could not generate master phrase.",
                        Toast.LENGTH_LONG).show();

                btnAuthenticate.setEnabled(false);
                return;
            }

            btnAuthenticate.setText("Confirm Master Phrase");

        }

        // NORMAL LOGIN
        else {

            tvInstruction.setText(
                    "Enter your 12-word master phrase to access YAPM."
            );

            tvMasterPhrase.setVisibility(View.GONE);

            btnAuthenticate.setText("Login");
        }


        btnAuthenticate.setOnClickListener(v -> {

            String enteredPhrase =
                    etMasterPhrase.getText().toString()
                            .trim()
                            .replaceAll("\\s+", " ");


            if (enteredPhrase.isEmpty()) {

                etMasterPhrase.setError("Please enter your master phrase");
                return;
            }


            // FIRST-TIME SETUP
            if (setup) {

                if (!generatedPhrase.equalsIgnoreCase(enteredPhrase)) {

                    etMasterPhrase.setError(
                            "The phrase does not match. Try again."
                    );

                    return;
                }

                boolean saved =
                        AuthenticationManager.saveMasterPhrase(
                                requireContext(),
                                generatedPhrase
                        );

                if (!saved) {

                    Toast.makeText(requireContext(),
                            "Could not save master phrase.",
                            Toast.LENGTH_LONG).show();

                    return;
                }

                Toast.makeText(requireContext(),
                        "Master phrase saved successfully!",
                        Toast.LENGTH_SHORT).show();
            }

            // NORMAL LOGIN
            else {

                boolean correct =
                        AuthenticationManager.verifyMasterPhrase(
                                requireContext(),
                                enteredPhrase
                        );

                if (!correct) {

                    etMasterPhrase.setError(
                            "Incorrect master phrase."
                    );

                    return;
                }
            }


            // OPEN HOME SCREEN
            NavController navController =
                    Navigation.findNavController(view);

            navController.navigate(
                    R.id.action_AuthenticationFragment_to_FirstFragment
            );

        });
    }
}
