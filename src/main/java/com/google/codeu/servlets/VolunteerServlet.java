package com.google.codeu.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Event;
import com.google.codeu.data.Eventstore;

/**
 * Servlet implementation class VolunteerServlet
 */
@WebServlet("/VolunteerServlet")
public class VolunteerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	  private Eventstore eventstore;
	  
	  @Override
	  public void init() {
	    eventstore = new Eventstore();
	  }
	  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VolunteerServlet() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	 	UserService userService = UserServiceFactory.getUserService();
	    if (!userService.isUserLoggedIn()) {
	      response.sendRedirect("/index.html");
	      return;
	    }
	    String user = userService.getCurrentUser().getEmail();
	    String eventId = request.getParameter("eventID").trim();
	    /*storing the message in eventstore*/
	    List<Event> eventList = eventstore.getAllEvents();
	    for(int i=0;i<eventList.size();++i) {
	    	if(eventList.get(i).getId().toString().equals(eventId)) {
	    		if(eventList.get(i).getVolunteerList().isEmpty()||eventList.get(i).getVolunteerList() == null) {
	    			List<String> volunteerList = new ArrayList<String>();
	    			volunteerList.add(user);
	    			eventstore.updateEvent(eventId, volunteerList);
	    			System.out.println("success");
	    		}
	    		else {
	    			List<String> volunteerList = eventList.get(i).getVolunteerList();
	    			volunteerList.add(user);
	    			eventstore.updateEvent(eventId, volunteerList);
	    		}
	    	}
	    }
	    response.sendRedirect("displayVolunteer.html");
	}	    
}
