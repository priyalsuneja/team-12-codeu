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
public class Notification extends Message{
  private String link;

    public Notification(String link, String user, String text) {
        super(user, text);
        this.link = link;
    }

    public Notification(String link, UUID id, String user, String text, long timestamp) {
        super(id, user, text, timestamp);
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
  
}
