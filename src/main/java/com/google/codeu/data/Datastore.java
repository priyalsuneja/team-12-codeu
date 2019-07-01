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

}

