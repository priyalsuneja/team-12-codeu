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
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/notifications")
public class NotificationServlet extends HttpServlet {
  private Datastore datastore;
  
  @Override
  public void init() {
    datastore = new Datastore();
  }
 
  /** Responds with a list of notifications*/
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    String user = request.getParameter("user");

    if (user == null || user.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }
    
    List<Notification> notifications = datastore.getNotifications(user);
    Gson gson = new Gson();
    String json = gson.toJson(notifications);
    response.getWriter().println(json);      
  }
  
//  @Override
//  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//    UserService userService = UserServiceFactory.getUserService();
//    if (!userService.isUserLoggedIn()) {
//      response.sendRedirect("/index.html");
//      return;
//    }
//    String user = userService.getCurrentUser().getEmail();
//    /*location of self index 0, others near by index>=1*/
//    List<Location> locations = datastore.getLocations(user);
//    if(locations!=null && locations.size()>0) {
//      /*send notification to other charities near by*/
//      for(int i = 1; i<locations.size(); i++)
//      {
//        Notification notification = new Notification(user, user, "New charity location found near you!");//in this case link is also user: test@example.com
//        datastore.storeNotification(notification);
//      }
//    }
//  }
}
