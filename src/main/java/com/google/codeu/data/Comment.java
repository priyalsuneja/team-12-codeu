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
public class Comment {
  private UUID id;
  private String messagId;
  private String text;
  private String user;
  private long timestamp;

    public Comment(UUID id, String user, String messagId, String text, long timestamp) {
        this.id = id;
        this.messagId = messagId;
        this.text = text;
        this.user = user;
        this.timestamp = timestamp;
    }
    
    public Comment(String user, String messageId, String text)
    {
        this(UUID.randomUUID(), user, messageId, text, System.currentTimeMillis());
    }

    public UUID getId() {
        return id;
    }

    public String getMessagId() {
        return messagId;
    }

    public String getText() {
        return text;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setMessagId(String messagId) {
        this.messagId = messagId;
    }

    public void setText(String text) {
        this.text = text;
    }
	
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
}
