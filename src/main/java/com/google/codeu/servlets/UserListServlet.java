package com.google.codeu.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.codeu.data.Datastore;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Set;

/**
 * Handles fetching all users for the community page.
 */
@WebServlet("/user-list")
public class UserListServlet extends HttpServlet{

    private Datastore datastore;	

    @Override
    public void init() {
        datastore = new Datastore();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
        HashMap<String, String> usersMap = datastore.getUsers();
        response.setContentType("application/json");
        Gson gson = new Gson();
        String json = gson.toJson(usersMap.values());
        response.getOutputStream().println(json);

    }
}
