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

import java.util.*;

/** Provides access to the data stored in Datastore. */
public class Datastore {

  public static final int LAT_LONG_DEGREE_DIFF = 3;
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
   * Stores/Updates the User in Datastore.
   * 
   */
  public void storeUser(User user) {
    Entity userEntity = new Entity("User", user.getEmail());
    userEntity.setProperty("email", user.getEmail());
    userEntity.setProperty("aboutMe", user.getAboutMe());
    userEntity.setProperty("type", user.getType());
    datastore.put(userEntity);
      System.out.println("user stored or updated!!!!!!!!!!!!!!!!!!!!");
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

    String newAboutMe = getUserAboutMe(userEntity);
    Long newType = getUserType(userEntity); 
    User user = new User(email, newAboutMe, newType);
//    storeUser(user);
    
    return user;
  }
  
  /*this function is to take care of exceptions in case previous user records in datastore don't have aboutMe attribute*/
  private String getUserAboutMe(Entity userEntity) {
    try {
        String aboutMe = (String) userEntity.getProperty("aboutMe");
        if(aboutMe ==null)
        {
            aboutMe = "";
        }
        return aboutMe;
    }
    catch(Exception e) {
        System.out.println("could not get user aboutMe: "+e.getMessage());
        return new String("");
    }
  }
  
  /*this function is to take care of exceptions in case previous user records in datastore don't have type attribute*/
  private Long getUserType(Entity userEntity) {
    try { //userEntity.getProperty("type")!=null
        Long type = (Long) userEntity.getProperty("type");
        if(type ==null)
        {
            type = User.UNSET_TYPE;
        }
        return type;
    }
    catch(Exception e)//userEntity.getProperty("type")==null
    {
        System.out.println("could not get user type: "+e.getMessage());
        return User.UNSET_TYPE;
    }
  }
  
  /**
  * Returns a set of all users who have been saved to datastore as a Charity type
  */
  public HashMap<String, String> getCharityUsers()
  {
    HashMap<String, String> usersMap = new HashMap<>();
    Query userQuery = new Query("User");
    PreparedQuery userQueryResults = datastore.prepare(userQuery);
    for(Entity entity: userQueryResults.asIterable())
    {
      try {
          if(Long.parseLong(entity.getProperty("type").toString())==User.CHARITY_TYPE)
          {
            usersMap.put(entity.getProperty("email").toString(),entity.getProperty("email") +"|"+ entity.getProperty("aboutMe") );
          }
      }catch(Exception e) {
          System.out.println("could not read user: "+e.getMessage());
      }
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
    charEntity.setProperty("displayName", charity.getDisplayName());
    charEntity.setProperty("displayCity", charity.getDisplayCity());
    charEntity.setProperty("displayType", charity.getDisplayType());
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
  public List<Charity> getCharities(String name, String city, String type) {

    List<Charity> charities = new ArrayList<>();

    Query query;

    if( isNull(name) ) {

        if( isNull(city) ) {

            if( isNull(type) ) {  // nothing provided
                return charities;
            }
            else {    // only type provided
                query =
                        new Query("Charity")
                                .setFilter(new Query.FilterPredicate("type", FilterOperator.EQUAL, type))
                                .addSort("name", SortDirection.ASCENDING);
            }

        }

        else {

            if( isNull(type) ) { // only city provided
                query =
                        new Query("Charity")
                                .setFilter(new Query.FilterPredicate("city", FilterOperator.EQUAL, city))
                                .addSort("name", SortDirection.ASCENDING);
            }

            else {    // type and city provided
                Collection<Query.Filter> filters = new ArrayList<>();

                filters.add(new Query.FilterPredicate("city", FilterOperator.EQUAL, city));
                filters.add(new Query.FilterPredicate("type", FilterOperator.EQUAL, type));

                query =
                        new Query("Charity")
                                .setFilter(new Query.CompositeFilter(Query.CompositeFilterOperator.AND, filters))
                                .addSort("name", SortDirection.ASCENDING);
            }
        }
    }
    else {

        if( isNull(city) ) {

            if( isNull(type) ) {  // name provided
                query =
                        new Query("Charity")
                                .setFilter(new Query.FilterPredicate("name", FilterOperator.EQUAL, name))
                                .addSort("name", SortDirection.ASCENDING);
            }
            else {    // name and type provided

                Collection<Query.Filter> filters = new ArrayList<>();

                filters.add(new Query.FilterPredicate("name", FilterOperator.EQUAL, name));
                filters.add(new Query.FilterPredicate("type", FilterOperator.EQUAL, type));

                query =
                        new Query("Charity")
                                .setFilter(new Query.CompositeFilter(Query.CompositeFilterOperator.AND, filters))
                                .addSort("name", SortDirection.ASCENDING);
            }

        }

        else {

            if( isNull(type) ) { // name and city provided

                Collection<Query.Filter> filters = new ArrayList<>();

                filters.add(new Query.FilterPredicate("name", FilterOperator.EQUAL, name));
                filters.add(new Query.FilterPredicate("city", FilterOperator.EQUAL, city));

                query =
                        new Query("Charity")
                                .setFilter(new Query.CompositeFilter(Query.CompositeFilterOperator.AND, filters))
                                .addSort("name", SortDirection.ASCENDING);
            }

            else {    // all three provided
                Collection<Query.Filter> filters = new ArrayList<>();

                filters.add(new Query.FilterPredicate("city", FilterOperator.EQUAL, city));
                filters.add(new Query.FilterPredicate("type", FilterOperator.EQUAL, type));
                filters.add(new Query.FilterPredicate("name", FilterOperator.EQUAL, name));

                query =
                        new Query("Charity")
                                .setFilter(new Query.CompositeFilter(Query.CompositeFilterOperator.AND, filters))
                                .addSort("name", SortDirection.ASCENDING);
            }
        }
    }

    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {

        String resultName = (String) entity.getProperty("displayName");
        String resultCity = (String) entity.getProperty("displayCity");
        String resultType = (String) entity.getProperty("displayType");

        Charity charity = new Charity(resultName, resultCity, resultType);
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

        String type = (String) entity.getProperty("displayType");

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

  public void deleteCharities() {
    try {
        Query query = new Query("Charity");
        PreparedQuery results = datastore.prepare(query);

        for (Entity entity : results.asIterable()) {
            datastore.delete(entity.getKey());
        }
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
  * Return the location of the user
  */
  public Location getUserLocation(String user)
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
    if(locations.size()==0 || locations.size()>1)//location does not exist or multiple invalid locations
        return null;
    else
        return locations.get(0);
  }
  
  /*helper function for  getAllNearCharityLocations() and getAllNearLocations()*/
  private Location locationEntityToLocationObj(Entity locationEntity) {
    try {
      double otherLongitude = Double.parseDouble(locationEntity.getProperty("longitude").toString());
      double otherLatitude = Double.parseDouble(locationEntity.getProperty("latitude").toString());
      String idString = locationEntity.getKey().getName();
      UUID id = UUID.fromString(idString);
      String text = (String) locationEntity.getProperty("text");
      String user = (String) locationEntity.getProperty("user");
      Location location = new Location(id, otherLatitude, otherLongitude, text, user);
      return location;
    } catch (Exception e) {
      System.err.println("Error reading location.");
      System.err.println(locationEntity.toString());
      e.printStackTrace();
      return null;
    }
  }
  
  /*returns a list of all-type user locations (charity, donor, unset) that are close to an specific location*/
  public List<Location> getAllNearLocations(Location location) {
    List<Location> locations = new ArrayList<>();
    Query allLocations =
       new Query("Location")
               .setFilter(new Query.FilterPredicate("user", FilterOperator.NOT_EQUAL, location.getUser()));
    PreparedQuery allLocationresults = datastore.prepare(allLocations);
    for (Entity entity : allLocationresults.asIterable()) {
      Location otherLocation = locationEntityToLocationObj(entity);
      if(otherLocation!=null) {
        /*check distance between location to define if the are considered near*/
        if(Math.abs(location.getLatitude()-otherLocation.getLatitude())<Datastore.LAT_LONG_DEGREE_DIFF 
                && Math.abs(location.getLongitude()-otherLocation.getLongitude())<Datastore.LAT_LONG_DEGREE_DIFF)
        {
          /*add the near loaction to list*/
          locations.add(otherLocation);
        }
      }
    }
    return locations;
  }
  
  /*returns a list of charity-user locations near a specific location*/
  public List<Location> getAllNearCharityLocations(Location location) {   
    List<Location> nearCharitylocations = new ArrayList<>();
    List<Location> allNearLocations = getAllNearLocations(location);
    
    for(Location nearLocation:allNearLocations) {
      /*add the near location to list if it is for a charity-type user, otherwise ignore it*/
      User nearLocationUser = getUser(nearLocation.getUser());
      if(Util.isValidCharityUser(nearLocationUser)) {
        nearCharitylocations.add(nearLocation);
      }
    }
    return nearCharitylocations;
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
  
  public void  storeCharityInfo(CharityInfo charityInfo) {
      Entity charityInoEntity = new Entity("CharityInfo", charityInfo.getEmail());
      charityInoEntity.setProperty("email", charityInfo.getEmail());
      charityInoEntity.setProperty("webLink", charityInfo.getWebLink());
      charityInoEntity.setProperty("contactLink", charityInfo.getContactLink());
      charityInoEntity.setProperty("donateLink", charityInfo.getDonateLink());
      charityInoEntity.setProperty("otherInfo", charityInfo.getOtherInfo());
      datastore.put(charityInoEntity);
      System.out.println("charityInfo: "+charityInfo.getEmail()+", "+charityInfo.getWebLink()+ ", "+charityInfo.getContactLink()
    +", "+charityInfo.getDonateLink());
  }
  
  public CharityInfo getCharityInfo(String email) {
    Query query = new Query("CharityInfo")
            .setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, email));
    PreparedQuery results = datastore.prepare(query);
    Entity infoEntity = results.asSingleEntity();
    if (infoEntity == null) {
      return null;
    }

    String webLink = (String)infoEntity.getProperty("webLink");
    String contactLink = (String)infoEntity.getProperty("contactLink");
    String donateLink = (String)infoEntity.getProperty("donateLink");
    String otherInfo = (String)infoEntity.getProperty("otherInfo");
    CharityInfo charityInfo = new CharityInfo(email, webLink, contactLink, donateLink, otherInfo);
    return charityInfo;
  }

    /**
     * Util method to see if a String is null/empty
     * @param str string to check
     * @return true if string is null/empty, false otherwise
     */
  private boolean isNull(String str) {
      return ( (str.length() == 0) || str == null || str.equals("null") || str.equals("none"));
  }
}

