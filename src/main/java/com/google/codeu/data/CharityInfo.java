/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.codeu.data;

/**
 *
 * @author 018639476
 */
public class CharityInfo {
    private String email;
    private String webLink;
    private String contactLink;
    private String donateLink;
    private String otherInfo;

    public CharityInfo(String email, String webLink, String contactLink, String donateLink, String otherInfo) {
        this.email = email;
        this.webLink = webLink;
        this.contactLink = contactLink;
        this.donateLink = donateLink;
        this.otherInfo = otherInfo;
    }

    public CharityInfo(String email, String webLink, String contactLink, String donateLink) {
        this(email, webLink, contactLink, donateLink, "");
    }

    public String getEmail() {
        return email;
    }

    public String getWebLink() {
        return webLink;
    }

    public String getContactLink() {
        return contactLink;
    }

    public String getDonateLink() {
        return donateLink;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public void setContactLink(String contactLink) {
        this.contactLink = contactLink;
    }

    public void setDonateLink(String donateLink) {
        this.donateLink = donateLink;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }
    
}
