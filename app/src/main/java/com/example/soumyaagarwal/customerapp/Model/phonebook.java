package com.example.soumyaagarwal.customerapp.Model;

/**
 * Created by SoumyaAgarwal on 8/6/2017.
 */

public class Phonebook {

    private String contact, name, designation;

    public Phonebook(String contact, String name, String designation) {
        this.contact = contact;
        this.name = name;
        this.designation = designation;
    }

    public Phonebook() {
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
