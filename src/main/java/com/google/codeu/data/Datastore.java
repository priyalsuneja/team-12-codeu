/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codeu.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;


import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/** Provides access to the data stored in Datastore. */
public class Datastore {

  private DatastoreService datastore;

  public Datastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /**
   * Stores the Message in Datastore.
   */
  public void storeMessage(Message message) {
    Entity messageEntity = new Entity("Message", message.getId().toString());
    messageEntity.setProperty("user", message.getUser());
    messageEntity.setProperty("text", message.getText());
    messageEntity.setProperty("timestamp", message.getTimestamp());

    datastore.put(messageEntity);
  }

  /**
   * Gets messages posted by a specific user.
   *
   * @return a list of messages posted by the user, or empty list if user has never posted a
   * message. List is sorted by time descending.
   */
  public List<Message> getMessages(String user) {
    List<Message> messages = new ArrayList<>();

    Query query =
            new Query("Message")
                    .setFilter(new Query.FilterPredicate("user", FilterOperator.EQUAL, user))
                    .addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");

        Message message = new Message(id, user, text, timestamp);
        messages.add(message);
      } catch (Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }

    return messages;
  }

  /**
   *  Gets all messages posted by every user.
   *
   *  @return a list of messages posted by every user. List is sorted by time descending.
   */
  public List<Message> getAllMessages() {
    List<Message> messages = new ArrayList<>();

    Query query = new Query("Message")
      .addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String user = (String) entity.getProperty("user");
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");

        Message message = new Message(id, user, text, timestamp);
        messages.add(message);
      } catch (Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }

    return messages;
  }
  /**
   * Stores the User in Datastore.
   */
  public void storeUser(User user) {
    Entity userEntity = new Entity("User", user.getEmail());
    userEntity.setProperty("email", user.getEmail());
    userEntity.setProperty("aboutMe", user.getAboutMe());
    datastore.put(userEntity);
  }

  /**
   * Returns the User owned by the email address, or
   * null if no matching User was found.
   */
  public User getUser(String email) {

    Query query = new Query("User")
            .setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, email));
    PreparedQuery results = datastore.prepare(query);
    Entity userEntity = results.asSingleEntity();
    if (userEntity == null) {
      return null;
    }

    String aboutMe = (String) userEntity.getProperty("aboutMe");
    User user = new User(email, aboutMe);

    return user;
  }
  
  /**
  * Returns a set of all users who have posted a message or about me
  */
  public HashMap<String, String> getUsers()
  {
      HashMap<String, String> usersMap = new HashMap<>();

      //adding users who have posted a message
      Query messageQuery = new Query("Message");
      PreparedQuery messageQueryResults = datastore.prepare(messageQuery);
      for(Entity entity: messageQueryResults.asIterable())
      {
        usersMap.put(entity.getProperty("user").toString(),entity.getProperty("user")+"|" );
      }
      
      //adding users who have posted aboutMe
      Query userQuery = new Query("User");
      PreparedQuery userQueryResults = datastore.prepare(userQuery);
      for(Entity entity: userQueryResults.asIterable())
      {
        usersMap.put(entity.getProperty("email").toString(),entity.getProperty("email") +"|"+ entity.getProperty("aboutMe") );
      }
	
    return usersMap;
  }
  
  public int getTotalMessageCount() {
	  Query query = new Query("Message");
	  PreparedQuery results = datastore.prepare(query);
	  return results.countEntities(FetchOptions.Builder.withLimit(1000));
  }

  /**
   * Stores Charity in datastore
   */

  public void storeCharity(Charity charity) {
    Entity charEntity = new Entity("Charity", charity.getName());
    charEntity.setProperty("name", charity.getName());
    charEntity.setProperty("city", charity.getCity());
    charEntity.setProperty("type", charity.getType());
    datastore.put(charEntity);
  }

  /**
   * Returns the Charity owned by the name, or
   * null if no matching Charity was found.
   */
  public Charity getCharity(String name) {

    Query query = new Query("Charity")
            .setFilter(new Query.FilterPredicate("name", FilterOperator.EQUAL, name));
    PreparedQuery results = datastore.prepare(query);
    Entity charEntity = results.asSingleEntity();
    if (charEntity == null) {
      return null;
    }

    String city = (String) charEntity.getProperty("city");
    String type = (String) charEntity.getProperty("type");
    Charity charity = new Charity(name,city,type);

    return charity;
  }

  /**
   * Gets charities with the given type.
   *
   * @return a list of charities that have the given type. List is sorted by time descending.
   */
  public List<Charity> getCharities(String type) {
    List<Charity> charities = new ArrayList<>();

    Query query =
            new Query("Charity")
                    .setFilter(new Query.FilterPredicate("type", FilterOperator.EQUAL, type))
                    .addSort("name", SortDirection.ASCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {

        String name = (String) entity.getProperty("name");
        String city = (String) entity.getProperty("city");

        Charity charity = new Charity(name, city, type);
        charities.add(charity);
      } catch (Exception e) {
        System.err.println("Error finding charities.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }

    return charities;
  }

  /**
   * Gets list of all the distinct types of charities.
   *
   * @return a list of charities that have the given type. List is sorted by time descending.
   */
  public List<String> getCharityTypes() {
    List<String> types = new ArrayList<>();

    Query query =
            new Query("Charity")
                    .addSort("type", SortDirection.ASCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {

        String type = (String) entity.getProperty("type");

        if( !types.contains(type) && !type.equals("Organization type")) {
          types.add(type);
        }
      } catch (Exception e) {
        System.err.println("Error finding types.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }

    return types;
  }

  public void deleteMessage(String messageId) {
    try {
      Key messageKey = KeyFactory.createKey("Message", messageId);
      datastore.delete(messageKey);
    } catch (Exception e) {
      System.out.println("error: " + e.toString());
    }
  }

  public void editMessage(String messageId, String messageText)
  {
    try {
      Key messageKey = KeyFactory.createKey("Message", messageId);
      Entity messageEntity = datastore.get(messageKey);
      messageEntity.setProperty("text", messageText);
      datastore.put(messageEntity);
    } catch (Exception e) {
      System.out.println("error: " + e.toString());
    }
  }

 /*
  * Store a comment in Datastore
  */
  public void storeComment(Comment comment)
  {
    Entity commentEntity = new Entity("Comment", comment.getId().toString());
    commentEntity.setProperty("user", comment.getUser());
    commentEntity.setProperty("messageId", comment.getMessagId());
    commentEntity.setProperty("text", comment.getText());
    commentEntity.setProperty("timestamp", comment.getTimestamp());
    datastore.put(commentEntity);
  }
  
 /*
  * Returns set of comments for a specific message
  */  
  public List<Comment> getComments(String messageId)
  {
    List<Comment> comments = new ArrayList<>();
    /*query comment for a specific message by messageId*/
    Query query =
            new Query("Comment")
                    .setFilter(new Query.FilterPredicate("messageId", FilterOperator.EQUAL, messageId))
                    .addSort("timestamp", SortDirection.ASCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String user = (String) entity.getProperty("user");
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");
        Comment comment = new Comment(id, user, messageId, text, timestamp);
        comments.add(comment);
      } catch (Exception e) {
        System.err.println("Error reading comments.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }
    return comments;
  }

  /*stores a location entity in datastore*/
  public void storeLocation(Location location)
  {
    Entity userEntity = new Entity("Location", location.getId().toString());
    userEntity.setProperty("longitude", String.valueOf(location.getLongitude()));
    userEntity.setProperty("latitude", String.valueOf(location.getLatitude()));
    userEntity.setProperty("text", location.getText());
    userEntity.setProperty("user", location.getUser());
    datastore.put(userEntity);
  }
  
  /*
  * Return the list of locations including:
  * location of charity at index zero and
  * location of nearby charities from index 1 and above
  */
  public List<Location> getLocations(String user)
  {
    List<Location> locations = new ArrayList<>();

    Query query =
            new Query("Location")
                    .setFilter(new Query.FilterPredicate("user", FilterOperator.EQUAL, user));

    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String text = (String) entity.getProperty("text");
        double longitude = Double.parseDouble(entity.getProperty("longitude").toString());
        double latitude = Double.parseDouble(entity.getProperty("latitude").toString());
        Location location = new Location(id, latitude, longitude, text, user);
        locations.add(location);
      } catch (Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }
    
    if(locations.size()==1)//if one location found
    {
        Query allLocations =
           new Query("Location")
                   .setFilter(new Query.FilterPredicate("user", FilterOperator.NOT_EQUAL, user));
        PreparedQuery allLocationresults = datastore.prepare(allLocations);
        for (Entity entity : allLocationresults.asIterable()) {
          try {
            double otherLongitude = Double.parseDouble(entity.getProperty("longitude").toString());
            double otherLatitude = Double.parseDouble(entity.getProperty("latitude").toString());
            if(Math.abs(locations.get(0).getLatitude()-otherLatitude)<3 && Math.abs(locations.get(0).getLongitude()-otherLongitude)<3)
            {
              String idString = entity.getKey().getName();
              UUID id = UUID.fromString(idString);
              String text = (String) entity.getProperty("text");
              String otherUser = (String) entity.getProperty("user");
              Location location = new Location(id, otherLatitude, otherLongitude, text, otherUser);
              locations.add(location);
            }

          } catch (Exception e) {
            System.err.println("Error reading message.");
            System.err.println(entity.toString());
            e.printStackTrace();
          }
        }
    }
    else
    {
      if(locations.size()>1)
        System.out.println("Invalid: multiple locations for a charity!!!!!!!!!!!!!");
      locations = new ArrayList<>();//if locations.size()==0, 0r >1 =>invalid: set to empty array
    }
    return locations;
  }
  
  /*update a location entity stored in datastore*/
  public void updateLocation(String locationId, double latitude, double longitude) {
    try {
      Key locationKey = KeyFactory.createKey("Location", locationId);
      Entity locationEntity = datastore.get(locationKey);
      locationEntity.setProperty("longitude", longitude);
      locationEntity.setProperty("latitude", latitude);
      datastore.put(locationEntity);
    } catch (Exception e) {
      System.out.println("error: " + e.toString());
    }
  }
  
  /*stores a notification entity for a user*/
  public void storeNotification(Notification notification)
  {
      /*creating notification entity*/
      Entity notificationEntity = new Entity("Notification", notification.getId().toString());
      notificationEntity.setProperty("user", notification.getUser());
      notificationEntity.setProperty("text", notification.getText());
      notificationEntity.setProperty("timestamp", notification.getTimestamp());
      notificationEntity.setProperty("link", notification.getLink());
      datastore.put(notificationEntity);
  }
  
  /*returns a list of notifications for a user*/
  public List<Notification> getNotifications(String user)
  {
      List<Notification> notifications = new ArrayList<Notification>();
      Query query =
            new Query("Notification")
                    .setFilter(new Query.FilterPredicate("user", FilterOperator.EQUAL, user))
                    .addSort("timestamp", SortDirection.DESCENDING);
      PreparedQuery results = datastore.prepare(query); 
      for(Entity entity: results.asIterable())
      {
        try {
          String idString = entity.getKey().getName();
          UUID id = UUID.fromString(idString);
          String text = (String) entity.getProperty("text");
          long timestamp = (long) entity.getProperty("timestamp");
          String link = (String) entity.getProperty("link");

          Notification notification = new Notification(link, id, user, text, timestamp);
          notifications.add(notification);
        } catch (Exception e) {
          System.err.println("Error reading notification.");
          System.err.println(entity.toString());
          e.printStackTrace();
        }
      }
      return notifications;
  }
}

