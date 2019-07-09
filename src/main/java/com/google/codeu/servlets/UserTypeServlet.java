/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.codeu.servlets;

import com.google.codeu.data.Datastore;
import com.google.codeu.data.User;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * Fetches or stores user type: charity, donor, unset
 * should define all attributes of user, and avoid having null for any user attribute
 */

@WebServlet("/user-type")
public class UserTypeServlet extends HttpServlet{
    
    private Datastore datastore;	

    @Override
    public void init() {
        datastore = new Datastore();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        String user = request.getParameter("user");

        if(user == null || user.equals("")) {
        // Request is invalid, return empty response
        return;
    }

    User userData = datastore.getUser(user);

    if(userData == null || userData.getType()== null) {
      return;
    }

    response.getOutputStream().println(userData.getType());
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {      
    
    }
        
}
