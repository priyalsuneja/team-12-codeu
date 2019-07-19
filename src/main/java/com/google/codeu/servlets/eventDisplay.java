package com.google.codeu.servlets;

import com.google.codeu.data.Message;
import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Event;
import com.google.codeu.data.Eventstore;
import com.google.gson.Gson;

/**
 * Handles fetching all messages for the public feed.
 */
@WebServlet("/eventDisplay")
public class eventDisplay extends HttpServlet {
  private final static Logger LOGGER = Logger.getLogger(eventDisplay.class.getName());
  private Eventstore eventstore;
  
  @Override
  public void init() {
    LOGGER.setLevel(Level.INFO);
    eventstore = new Eventstore();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    LOGGER.info("/eventDisplay: doGet");
    response.setContentType("application/json");
  
    List<Event> events = eventstore.getAllEvents();
    Gson gson = new Gson();
    String json = gson.toJson(events);
  
    response.getOutputStream().println(json);
  }
}
