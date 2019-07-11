/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.CharityInfo;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.User;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * Fetches and stores charity information
 */
@WebServlet("/charity-info")
public class CharityInfoServlet extends HttpServlet {
    
  private Datastore datastore;	

  @Override
  public void init() {
    datastore = new Datastore();
  }
    
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      System.out.println("in do get for charity info");
    response.setContentType("application/json");

    String user = request.getParameter("user");
    if(user == null || user.equals("")) {
      // Request is invalid, return empty response
      response.getWriter().println("[]");
      return;
    }

    CharityInfo charityInfo = datastore.getCharityInfo(user);

    if(charityInfo == null) {
      response.getWriter().println("[]");
      return;
    }
    
    Gson gson = new Gson();
    String json = gson.toJson(charityInfo);
      System.out.println("json: "+ json);

    response.getWriter().println(json);
  }
  
    @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }
    String email = request.getParameter("user");
    String webLink = request.getParameter("webLink");
    String contactLink = request.getParameter("contactLink");
    String donateLink = request.getParameter("donateLink");
    
    CharityInfo charityInfo = new CharityInfo(email, webLink, contactLink, donateLink);
    datastore.storeCharityInfo(charityInfo);
    
    response.sendRedirect("/user-page.html?user=" + email);
  }
}
