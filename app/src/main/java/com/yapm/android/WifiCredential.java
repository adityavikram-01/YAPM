package com.yapm.android;

public class WifiCredential extends Credential {
    private String ssid;

    public WifiCredential(String ssid, String username, String password, String notes) {
        super(username, password, notes);
        this.ssid = ssid;
    }

    public String getSsid() { return ssid; }
    public void setSsid(String ssid) { this.ssid = ssid; }

    @Override
    public String getCategory() { return "WiFi"; }
}
