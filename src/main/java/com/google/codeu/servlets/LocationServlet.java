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
import com.google.codeu.data.Notification;
import com.google.codeu.data.User;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
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

    Location userLocation = datastore.getUserLocation(user);
    List<Location> locations = new ArrayList<>();
    /*get near charity locations if the user location exists, otherwise return empty list of locations*/
    if(userLocation!=null) {
        locations = datastore.getAllNearCharityLocations(userLocation);
        locations.add(0, userLocation);
    }
    
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
    double longitude = Double.parseDouble(request.getParameter("longitude").toString());
    double latitude = Double.parseDouble(request.getParameter("latitude").toString());
    
    /*check validity of input*/
    if(Math.abs(latitude)>90 || Math.abs(longitude)>180)  {
		response.sendRedirect("/user-page.html?user=" + user);
        return;
	}
    
    Location userLocation = datastore.getUserLocation(user);
  
    
    /*check if location object exists*/
    if(userLocation==null)
    {
      /*make a new location for user*/
      String text = "Charity Location";
      userLocation = new Location(latitude, longitude, text, user);
      datastore.storeLocation(userLocation);
    }
    else //location already exists => update it
    {
      String existingLocationId = userLocation.getId().toString();
      datastore.updateLocation(existingLocationId, latitude, longitude); 
    }
    
    /*send notification to other users nearby, if the updated location is for a charity type user*/
    User locationUser = datastore.getUser(user);
    if(locationUser!=null && locationUser.getType()!=null && locationUser.getType()== User.CHARITY_TYPE) {
      List<Location> allNearLocations = datastore.getAllNearLocations(userLocation);
      if(allNearLocations!=null && allNearLocations.size()>0) {
        /*send notification to other charities near by*/
        for(int i = 0; i<allNearLocations.size(); i++)
        {
          Notification notification = new Notification(user, allNearLocations.get(i).getUser(), "New charity location found near you!");//in this case link is also user: test@example.com
          datastore.storeNotification(notification);
        }
      }
    }
  }
    
}
