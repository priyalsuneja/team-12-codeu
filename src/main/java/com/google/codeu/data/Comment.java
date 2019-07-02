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

    public Comment(UUID id, String messagId, String text) {
        this.id = id;
        this.messagId = messagId;
        this.text = text;
    }
    
    public Comment(String messageId, String text)
    {
        this(UUID.randomUUID(), messageId, text);
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
    
}
