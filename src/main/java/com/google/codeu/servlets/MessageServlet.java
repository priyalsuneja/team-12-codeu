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

package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.*;


/** Handles fetching and saving {@link Message} instances. */
@WebServlet("/messages")
public class MessageServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  /**
   * Responds with a JSON representation of {@link Message} data for a specific user. Responds with
   * an empty array if the user is not provided.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("application/json");

    String user = request.getParameter("user");

    if (user == null || user.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }

    List<Message> messages = datastore.getMessages(user);
    Gson gson = new Gson();
    String json = gson.toJson(messages);

    response.getWriter().println(json);
  }

  /** Stores a new {@link Message}. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    String user = userService.getCurrentUser().getEmail();
    String text = Jsoup.clean(request.getParameter("text"), Whitelist.none());
	
    String textWithImagesReplaced = tagImageURLs(text);
	
    Message message = new Message(user, textWithImagesReplaced);
    datastore.storeMessage(message);

    response.sendRedirect("/user-page.html?user=" + user);
  }
  
  /** Replaces valid image url in the message with image tag
   *  Does not change the strig message if there are no valid image urls
   */ 
  public String tagImageURLs(String message)
  {
    String regex = "(https?://\\S*\\.(?:png|PNG|jpg|JPG|jpeg|JPEG|gif|GIF))";
    Pattern pattern = Pattern.compile(regex); 
    Matcher matcher = pattern.matcher(message);  
    
    //creating list of all urls that match the pattern
    List<String> urls = new ArrayList<String>();
    int firstMarchedUrlIndex = -1;
    //adding first matched url and getting its index
    if(matcher.find())
    {
      String matchedUrl = matcher.group(1);
      firstMarchedUrlIndex = matcher.start();
      urls.add(matchedUrl);
    }
    //adding other matched urls
    while(matcher.find())
    {
      String matchedUrl = matcher.group(1);
      urls.add(matchedUrl);
    }
    //checking if url is valid Using Java library
    String[] nonUrlTexts = message.split(regex);
    
    for(int i  = 0; i<urls.size(); i++)
    {
      boolean isUrlValid = false;
      try {
        
        URI uri = new URL(urls.get(i)).toURI();
        uri.parseServerAuthority();
        isUrlValid = true;
      } catch (Exception e) {}
      
      if(isUrlValid)
      {
        String replacement = "<img src=\"$1\" />";
        urls.set(i, urls.get(i).replaceAll(regex, replacement));
      }
    }

    //putting urls and nonUrlTexts back into one single message
    message = "";
    if(firstMarchedUrlIndex==0)
    {
      for(int i = 0; i<urls.size(); i++)
      {
        message+=urls.get(i);
        if(i+1<nonUrlTexts.length)
          message+=nonUrlTexts[i+1];
      }
    }
    else
    {
      for(int i = 0; i<nonUrlTexts.length; i++)
      {
        message+=nonUrlTexts[i];
        if(i<urls.size())
          message+=urls.get(i);
      }
    }
    
    return message;
  }
}
