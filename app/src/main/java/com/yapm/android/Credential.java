package com.yapm.android;

public abstract class Credential {
    protected String username;
    protected String password;
    protected String notes;
    protected static int count=0;

    public Credential() {
        count++;
    }

    public Credential(String username, String password, String notes) {
        this.username = username;
        this.password = password;
        this.notes = notes;
    }

    public Credential(Credential other) {
        this.username = other.username;
        this.password = other.password;
        this.notes = other.notes;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getNotes() { return notes; }
    public static void count(){ System.out.println(count);}

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setNotes(String notes) { this.notes = notes; }

    public abstract String getCategory();
}
