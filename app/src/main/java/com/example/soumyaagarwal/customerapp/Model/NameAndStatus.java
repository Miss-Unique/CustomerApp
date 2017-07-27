package com.example.soumyaagarwal.customerapp.Model;

/**
 * Created by RajK on 10-07-2017.
 */

public class NameAndStatus {
    private String id;
    private String name;
    private Boolean online;
    private String num;

    public NameAndStatus(String id, String name, Boolean online,String num) {
        this.id = id;
        this.name = name;
        this.online = online;
        this.num = num;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public NameAndStatus() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }
}
