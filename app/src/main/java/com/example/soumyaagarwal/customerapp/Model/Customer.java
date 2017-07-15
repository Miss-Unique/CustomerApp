package com.example.soumyaagarwal.customerapp.Model;

public class Customer {

    public Customer(){

    }

    private String name;
    private String phone_num;
    private String address;
    private String id,password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private int Color=-1;

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    public Customer(String name, String phone_num, String address, String id, int color, String password) {
        this.name = name;
        this.phone_num = phone_num;
        this.address = address;
        this.id = id;
        Color = color;
        this.password = password;
    }

    public String getPhone_num() {
        return phone_num;
    }
}


