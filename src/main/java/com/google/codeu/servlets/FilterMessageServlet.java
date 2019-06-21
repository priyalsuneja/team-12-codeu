/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.codeu.servlets;

import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Respons to the request for list of messages that contain specific keyword for a specific user.
 */
@WebServlet("/filter-messages")
public class FilterMessageServlet extends HttpServlet {
  private Datastore datastore;
  
  @Override
  public void init() {
    datastore = new Datastore();
  }
  
  
  /**
   * Responds with a JSON representation of {@link Message} data for a specific user that includes keyword. 
   * Responds with an empty array if the user is not provided, or there are no messages to return.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("application/json");

    String user = request.getParameter("user");
    String keyword = request.getParameter("search");
    
    if (user == null || user.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }

    List<Message> messages = datastore.getMessages(user);
    List<Message> filteredMessages = new ArrayList<>();
    Gson gson = new Gson();
    if(keyword==null || keyword.equals(""))
    {
      String json = gson.toJson(messages);
      response.getWriter().println(json);
      return;
    }
    else if(messages!=null)
    {
      for(int i = 0; i<messages.size(); i++)
      {
        if(messages.get(i).getText().toLowerCase().contains(keyword.toLowerCase()))
        {
          filteredMessages.add(messages.get(i));
        }
      }
      String json = gson.toJson(filteredMessages);
      response.getWriter().println(json);
      return;
    }
    response.getWriter().println("[]");
  }
}
