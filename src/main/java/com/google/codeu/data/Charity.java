package com.google.codeu.data;

public class Charity {

    private String name;
    private String city;
    private String type;

    public Charity() {
        this.name = "";
        this.city = "";
        this.type = "";
    }

    public Charity(String name, String city, String type) {
        this.name = name;
        this.city = city;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getType() {
        return type;
    }
}
