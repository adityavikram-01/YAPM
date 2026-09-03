package com.yapm.android;

public class SiteCredential extends Credential {
    private String site;

    public SiteCredential() { super(); }

    public SiteCredential(String site, String username, String password, String notes) {
        super(username, password, notes);
        this.site = site;
    }

    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }

    @Override
    public String getCategory() { return "Site"; }
}
