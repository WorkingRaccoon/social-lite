package com.workingraccoon.backend.DTO;

import lombok.Data;

@Data
public class User {
    private String Username;
    private String Phone;
    private String Password;
    private String Email;
    private String Intro;

    /*
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getMail() { return email; }
    public void setEmail(String email) { this.email = email; };
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }
    */

}
