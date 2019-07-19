package com.google.codeu.data;

public class Charity {

    private String name;
   // private String displayName;
    private String city;
   // private String displayCity;
    private String type;
   // private String displayType;

    public Charity() {
        this.name = "";
        this.city = "";
        this.type = "";
     /*   this.displayName = "";
        this.displayCity = "";
        this.displayType = "";*/
    }

    public Charity(String name, String city, String type) {
     //   this.displayName = name;
     //   this.displayCity = city;
     //   this.displayType = type;
        this.type = type;
        this.name = name;
        this.city = city;
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

   /* public String getDisplayName() {
        return displayName;
    }

    public String getDisplayCity() {
        return displayCity;
    }

    public String getDisplayType() {
        return displayType;
    }*/
}
