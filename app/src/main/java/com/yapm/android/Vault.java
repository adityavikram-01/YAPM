package com.yapm.android;
import java.util.ArrayList;
public class Vault {
private ArrayList<Credential> credentials;

public Vault(){
    credentials = new ArrayList<>();
}

public void addCredential(Credential credential) {
    credentials.add(credential);
}

public void removeCredential(Credential credential) {
    credentials.remove(credential);
}

public ArrayList<Credential> getCredentials() {
    return credentials;
}
}
