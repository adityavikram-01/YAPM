package com.yapm.android;

public class Credential {
private String site;
private String username;
private String password;
private String notes;
private String category;

public Credential() {}
public Credential(String site, String username, String password, String notes, String category) {
    this.site = site;
    this.username = username;
    this.password = password;
    this.notes = notes;
    this.category = category;
}

public Credential(Credential other) {
    this.site = other.site;
    this.username = other.username;
    this.password = other.password;
    this.notes = other.notes;
    this.category = other.category;
}

public String getSite(){
    return site;
}

public String getUsername(){
    return username;
}

public String getPassword() {
    return password;
}

public String getNotes() {
    return notes;
}

public String getCategory() {
    return category;
}

public void setSite(String site){
    this.site = site;
}

public void setUsername(String username) {
    this.username = username;
}

public void setPassword(String password) {
    this.password = password;
}

public void setNotes(String notes) {
    this.notes = notes;
}

public void setCategory(String category) {
    this.category = category;
}
}
