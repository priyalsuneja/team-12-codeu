package com.google.codeu.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

/** Provides access to the data stored in eventstore. */
public class Eventstore {

  private DatastoreService eventstore;

  public Eventstore() {
    eventstore = DatastoreServiceFactory.getDatastoreService();
  }

  /**
   * Stores the Event in eventstore.
   */
  public void storeEvent(Event event) {
    Entity eventEntity = new Entity("Event", event.getId().toString());
    eventEntity.setProperty("user", event.getUser());
    eventEntity.setProperty("description", event.getDescription());
    eventEntity.setProperty("timestamp", event.getTimestamp());
    eventEntity.setProperty("tags",event.getTags());
    eventEntity.setProperty("title", event.getTitle());
    eventEntity.setProperty("volunteerList", event.getVolunteerList());
    eventstore.put(eventEntity);
  }

  /**
   * Gets messages posted by a specific user.
   *
   * @return a list of messages posted by the user, or empty list if user has never posted a
   * message. List is sorted by time descending.
   */
  public List<Event> getEvents(String user) {
    List<Event> events = new ArrayList<>();

    Query query =
            new Query("Event")
                    .setFilter(new Query.FilterPredicate("user", FilterOperator.EQUAL, user))
                    .addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = eventstore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String description = (String) entity.getProperty("description");
        long timestamp = (long) entity.getProperty("timestamp");
        @SuppressWarnings("unchecked")
		List<String> tags = (List<String>) entity.getProperty("tags");
        String title = (String) entity.getProperty("title");
        @SuppressWarnings("unchecked")	
        List<String> volunteerList;
        if((List<String>) entity.getProperty("volunteerList")== null) {
        	volunteerList = new ArrayList<String>();
        	System.out.println("null");
        }
        else volunteerList = (List<String>) entity.getProperty("volunteerList");
        Event event = new Event(tags,id, user, description, timestamp,title,volunteerList);
        events.add(event);
      } catch (Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }

    return events;
  }
public void updateEvent(String eventId, List<String> volunteerList) {
	try {
		Key messageKey = KeyFactory.createKey("Event",eventId);
        Entity messageEntity = eventstore.get(messageKey);
        messageEntity.setProperty("volunteerList", volunteerList);
        eventstore.put(messageEntity);
      } catch (Exception e) {
        System.out.println("error: " + e.toString());
      }	
 } 
  @SuppressWarnings("unchecked")
public List<Event> getAllEvents() {
	    List<Event> events = new ArrayList<>();

	    Query query = new Query("Event")
	      .addSort("timestamp", SortDirection.DESCENDING);
	    PreparedQuery results = eventstore.prepare(query);

	    for (Entity entity : results.asIterable()) {
	        try {
	            String idString = entity.getKey().getName();
	            UUID id = UUID.fromString(idString);
	            String user = (String) entity.getProperty("user");
	            String description = (String) entity.getProperty("description");
	            long timestamp = (long) entity.getProperty("timestamp");
	            @SuppressWarnings("unchecked")
	    		List<String> tags = (List<String>) entity.getProperty("tags");
	            String title = (String) entity.getProperty("title");
	            List<String> volunteerList = new ArrayList<String>();
	            if((List<String>) entity.getProperty("volunteerList")!=null)
	            	volunteerList = (List<String>) entity.getProperty("volunteerList");
	            Event event = new Event(tags,id, user, description, timestamp,title,volunteerList);
	            events.add(event);
	          } catch (Exception e) {
	            System.err.println("Error reading message.");
	            System.err.println(entity.toString());
	            e.printStackTrace();
	          }
	    }
	    return events;
	  }
}
