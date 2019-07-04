/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.codeu.data;

import java.util.UUID;

/**
 *
 * @author 018639476
 */
public class Location {
    private UUID id;
    private double longitude;
    private double latitude;
    private String text;
    private String user;

    public Location(UUID id, double longitude, double latitude, String text, String user) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.text = text;
        this.user = user;
    }
    
    public Location(double longitude, double latitude, String text, String user) {
        this(UUID.randomUUID(), longitude, latitude, text, user);
    }
    
    public void setId(UUID id) {
        this.id = id;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }


}
