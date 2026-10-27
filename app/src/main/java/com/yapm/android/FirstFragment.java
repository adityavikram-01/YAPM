package com.yapm.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.yapm.android.databinding.FragmentFirstBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private boolean isFabExpanded = false;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Link the 4 specific YAPM buttons
        FloatingActionButton mainFab = view.findViewById(R.id.floatingActionButton);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);
        FloatingActionButton fabUpdate = view.findViewById(R.id.fabUpdate);
        FloatingActionButton fabView = view.findViewById(R.id.fabView);
        FloatingActionButton fabImport = view.findViewById(R.id.fabImport);
        TextView create = view.findViewById(R.id.Create);
        TextView update = view.findViewById(R.id.Update);
        TextView search = view.findViewById(R.id.Search);
        TextView imp_exp = view.findViewById(R.id.Imp_exp);

// Initial setup
        fabAdd.setAlpha(0f); fabAdd.setTranslationY(50f);
        create.setAlpha(0f); create.setTranslationY(50f);
        fabUpdate.setAlpha(0f); fabUpdate.setTranslationY(50f);
        update.setAlpha(0f); update.setTranslationY(50f);
        fabView.setAlpha(0f); fabView.setTranslationY(50f);
        search.setAlpha(0f); search.setTranslationY(50f);
        fabImport.setAlpha(0f); fabImport.setTranslationY(50f);
        imp_exp.setAlpha(0f); imp_exp.setTranslationY(50f);


        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFabExpanded) {
                    // Close Menu
                    fabAdd.animate().alpha(0f).translationY(50f).setDuration(200).withEndAction(() -> fabAdd.setVisibility(View.GONE)).start();
                    create.animate().alpha(0f).translationY(50f).setDuration(200).withEndAction(() -> create.setVisibility(View.GONE)).start();
                    fabUpdate.animate().alpha(0f).translationY(50f).setDuration(200).withEndAction(() -> fabUpdate.setVisibility(View.GONE)).start();
                    update.animate().alpha(0f).translationY(50f).setDuration(200).withEndAction(() -> update.setVisibility(View.GONE)).start();
                    fabView.animate().alpha(0f).translationY(50f).setDuration(200).withEndAction(() -> fabView.setVisibility(View.GONE)).start();
                    search.animate().alpha(0f).translationY(50f).setDuration(200).withEndAction(() -> search.setVisibility(View.GONE)).start();
                    fabImport.animate().alpha(0f).translationY(50f).setDuration(200).withEndAction(() -> fabImport.setVisibility(View.GONE)).start();
                    imp_exp.animate().alpha(0f).translationY(50f).setDuration(200).withEndAction(() -> imp_exp.setVisibility(View.GONE)).start();
                    isFabExpanded = false;
                } else {
                    // Open Menu
                    fabAdd.setVisibility(View.VISIBLE);
                    create.setVisibility(View.VISIBLE);
                    fabUpdate.setVisibility(View.VISIBLE);
                    update.setVisibility(View.VISIBLE);
                    fabView.setVisibility(View.VISIBLE);
                    search.setVisibility(View.VISIBLE);
                    fabImport.setVisibility(View.VISIBLE);
                    imp_exp.setVisibility(View.VISIBLE);

                    // Pop up one by one
                    fabAdd.animate().alpha(1f).translationY(0f).setDuration(200).setStartDelay(0).start();
                    create.animate().alpha(1f).translationY(0f).setDuration(200).setStartDelay(50).start();
                    fabUpdate.animate().alpha(1f).translationY(0f).setDuration(200).setStartDelay(50).start();
                    update.animate().alpha(1f).translationY(0f).setDuration(200).setStartDelay(100).start();
                    fabView.animate().alpha(1f).translationY(0f).setDuration(200).setStartDelay(100).start();
                    search.animate().alpha(1f).translationY(0f).setDuration(200).setStartDelay(150).start();
                    fabImport.animate().alpha(1f).translationY(0f).setDuration(200).setStartDelay(150).start();
                    imp_exp.animate().alpha(1f).translationY(0f).setDuration(200).setStartDelay(200).start();
                    isFabExpanded = true;
                }
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}