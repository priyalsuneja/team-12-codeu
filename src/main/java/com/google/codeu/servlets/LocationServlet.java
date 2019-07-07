/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Location;
import com.google.codeu.data.Message;
import com.google.codeu.data.Notification;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author 018639476
 */
@WebServlet("/locations")
public class LocationServlet extends HttpServlet{
    
  private Datastore datastore;
  
  @Override
  public void init() {
    datastore = new Datastore();
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    String user = request.getParameter("user");

    if (user == null || user.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }

    List<Location> locations = datastore.getLocations(user);
    Gson gson = new Gson();
    String json = gson.toJson(locations);

    response.getWriter().println(json);
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }
    
    String user = userService.getCurrentUser().getEmail();
    double longitude = Double.parseDouble(request.getParameter("longitude"));
    double latitude = Double.parseDouble(request.getParameter("latitude"));
    
    /*check validity of input*/
    if(Math.abs(latitude)>90 || Math.abs(longitude)>180) 
        return;
    
    List<Location> locations = datastore.getLocations(user);
  
    
    /*check if location object exists*/
    if(locations==null || locations.isEmpty())
    {
      /*make location object*/
      String text = "Charity Location";
      Location location = new Location(longitude, latitude, text, user);
      datastore.storeLocation(location);
    }
    else //location already exists => update it
    {
      String existingLocationId = locations.get(0).getId().toString();
      datastore.updateLocation(existingLocationId, longitude, latitude); 
    }
    
    locations = datastore.getLocations(user);
     if(locations!=null && locations.size()>0) {
      /*send notification to other charities near by*/
      for(int i = 1; i<locations.size(); i++)
      {
        Notification notification = new Notification(user, locations.get(i).getUser(), "New charity location found near you!");//in this case link is also user: test@example.com
        datastore.storeNotification(notification);
      }
    }
    
//    /* Store notifications for near by charities about this changse */
//    NotificationServlet notificationServlet = new NotificationServlet();
//    notificationServlet.doPost(request, response);
    
    /*redirect to user page*/
    response.sendRedirect("/user-page.html?user=" + user);
    
  }
    
}
