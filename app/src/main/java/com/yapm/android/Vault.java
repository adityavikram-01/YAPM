
package com.yapm.android;

import android.content.Context;
import java.util.ArrayList;
public class Vault {

    private final CredentialRepository repository;

    public Vault(Context context) {
        repository = new CredentialRepository(context);
    }

    public long add(Credential credential) {
        return repository.add(credential);
    }

    public Credential get(String type, String service, String username) {
        return repository.get(type, service, username);
    }

    public ArrayList<Credential> search(String service, String type) {
        return repository.search(service, type);
    }
    public int update(Credential oldCredential, Credential newCredential) {
        return repository.update(oldCredential, newCredential);
    }

    public int remove(Credential credential) {
        return repository.remove(credential);
    }

    public void close() {
        repository.close();
    }
}
