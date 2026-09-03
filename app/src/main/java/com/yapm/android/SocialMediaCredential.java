package com.yapm.android;

public class SocialMediaCredential extends Credential {
    private String platform;

    public SocialMediaCredential(String platform, String username, String password, String notes) {
        super(username, password, notes);
        this.platform = platform;
    }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    @Override
    public String getCategory() { return "Social Media"; }
}
