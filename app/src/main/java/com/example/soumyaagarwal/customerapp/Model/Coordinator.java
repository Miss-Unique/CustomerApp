package com.example.soumyaagarwal.customerapp.Model;

/**
 * Created by RajK on 15-07-2017.
 */

public class Coordinator {
    private String name,username,password,contact, address;

    public Coordinator() {
    }

    public Coordinator(String name, String username, String password, String contact, String address) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.address = address;
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
