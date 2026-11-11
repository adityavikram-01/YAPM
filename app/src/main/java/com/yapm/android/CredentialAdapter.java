package com.yapm.android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CredentialAdapter extends RecyclerView.Adapter<CredentialAdapter.CredentialViewHolder> {

    public interface OnCredentialClickListener {
        void onCredentialClick(Credential credential);
    }

    private OnCredentialClickListener listener;
    private ArrayList<Credential> credentials;

    public CredentialAdapter(
            ArrayList<Credential> credentials,
            OnCredentialClickListener listener) {

        this.credentials = credentials;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CredentialViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_credential, parent, false);

        return new CredentialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull CredentialViewHolder holder, int position) {

        Credential credential = credentials.get(position);

        String service = "";

        if (credential instanceof SiteCredential) {
            service = ((SiteCredential) credential).getSite();
        }

        else if (credential instanceof SocialMediaCredential) {
            service = ((SocialMediaCredential) credential).getPlatform();
        }

        else if (credential instanceof WifiCredential) {
            service = ((WifiCredential) credential).getSsid();
        }

        holder.service.setText(service);
        holder.category.setText(credential.getCategory());
        holder.username.setText(credential.getUsername());

        holder.itemView.setOnClickListener(view -> {
            listener.onCredentialClick(credential);
        });
    }

    @Override
    public int getItemCount() {
        return credentials.size();
    }

    public void updateCredentials(ArrayList<Credential> newCredentials) {
        credentials.clear();
        credentials.addAll(newCredentials);
        notifyDataSetChanged();
    }

    public static class CredentialViewHolder extends RecyclerView.ViewHolder {

        TextView service;
        TextView category;
        TextView username;

        public CredentialViewHolder(@NonNull View itemView) {
            super(itemView);

            service = itemView.findViewById(R.id.tv_credential_service);
            category = itemView.findViewById(R.id.tv_credential_category);
            username = itemView.findViewById(R.id.tv_credential_username);
        }
    }
}
