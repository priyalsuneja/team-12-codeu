package com.google.codeu.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles fetching all messages for the public feed.
 */
@WebServlet("/feed")
public class MessageFeedServlet extends HttpServlet {
  private final static Logger LOGGER = Logger.getLogger(MessageServlet.class.getName());

  @Override
  public void init() {
    LOGGER.setLevel(Level.INFO);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    LOGGER.info("/feed: doGet");
    response.getOutputStream().println("this will be my message feed");
  }
}
