package com.google.codeu.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Event;
import com.google.codeu.data.Eventstore;
import com.google.codeu.data.Message;

/**
 * Servlet implementation class EventServlet
 */
@WebServlet("/EventServlet")
public class EventServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
	  private Eventstore eventstore;
	  
	  @Override
	  public void init() {
	    eventstore = new Eventstore();
	  }
	  
    public EventServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	 public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		 	UserService userService = UserServiceFactory.getUserService();
		    if (!userService.isUserLoggedIn()) {
		      response.sendRedirect("/index.html");
		      return;
		    }
		    String user = userService.getCurrentUser().getEmail();
		    String temp = request.getParameter("tags");
		    String []tagSet = temp.split(",");
		    String title = request.getParameter("title");
		    List <String> tags = Arrays.asList(tagSet);
		    String description = request.getParameter("description");
		    /*storing the message in eventstore*/
		    Event event = new Event(tags,user,description,title);
		    eventstore.storeEvent(event);
		    response.sendRedirect("displaycharities.html");
	 }  
}
