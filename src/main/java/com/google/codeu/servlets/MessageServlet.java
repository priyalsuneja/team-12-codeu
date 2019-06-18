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

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
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
import java.util.Map;
import java.util.regex.*;

import java.util.concurrent.TimeUnit;


/** Handles fetching and saving {@link Message} instances. */
@WebServlet("/messages")
public class MessageServlet extends HttpServlet {

  private final static String imageReplacement = "<img src=\"$1\" />";
  private final static String videoReplacement = "<video controls><source src=\"$1\" type=\"video/webm\"><source src = \"$1\" type = \"video/mp4\"></video>";
  private final static String audioReplacement = "<audio controls><source src=\"$1\" type=\"audio/mp3\"><source src = \"$1\" type = \"audio/wav\"></audio>";
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
    /*replace file urls with corresponding html tags(<img>, <video>, <audio>)*/
    String textReplaced = tagURLs(text);
    
    /*adding image tags for the image file uploaded to Blobstore*/
    String messageText = textReplaced;
    //get image url ulpoaded to Blobstore
    List<String> imageBlobUrls = getUploadedFileUrl(request, "image");
    //add image tage for uploaded image url at the end of message text
    if(imageBlobUrls!=null ) {
      for(String url:imageBlobUrls)
      {
        messageText += "<img src=\"" + url + "\" />";   
      }
    }
    
    /*storing the message in Datastore*/
    Message message = new Message(user, messageText);
    datastore.storeMessage(message);

    response.sendRedirect("/user-page.html?user=" + user);
  }
  
   /**
    * Returns a URL that points to the uploaded file, or null if the user didn't upload a file.
    */
  private List<String> getUploadedFileUrl(HttpServletRequest request, String formInputElementName)
  {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // User submitted form without selecting a file, so we can't get a URL. (devserver)
    if(blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobKey blobKey = blobKeys.get(0);
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }
	
    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    List<String> imageBlobUrls = new ArrayList<String>();
	
    for(BlobKey blobK: blobKeys)
    {
      // Checking the validity of the file to make sure it's an image
      String fileType = new BlobInfoFactory().loadBlobInfo(blobK).getContentType().toString().toLowerCase();
      if(!(fileType.equals("image/jpg") ||fileType.equals("image/jpeg") || fileType.equals("image/gif") || fileType.equals("image/png")))
      {
        blobstoreService.delete(blobK);
      }
	  else //blob is an image
      {
        try
        {
          //getting the image URL to the uploaded file
          ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobK);  
          String imageUrl = imagesService.getServingUrl(options);//getServingUrl locks the blob, so it cannot be deleted with blobstoreService.delete(blobK); 
          imageBlobUrls.add(imageUrl);
        }
        catch(IllegalArgumentException e) //This is an additional step to check validity of file.
        {
          //imagesService.getServingUrl() raises an exception if blob is not an image. 	
        }
      }
    }
    return imageBlobUrls;
  }
  
  /**
   * Replaces valid image url in the message with image tag
   *  Does not change the strig message if there are no valid image urls
   */ 
  public String tagURLs(String message)
  {
    String regex = "(https?://\\S*?\\.(?:png|PNG|jpg|JPG|jpeg|JPEG|gif|GIF|mp4|MP4|webm|mp3|MP3|wav|WAV))";
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
	
    //creating list of non-url sections of text
    String[] nonUrlTexts = message.split(regex);
	
    //checking if url is valid Using Java library
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
        if(urls.get(i).toLowerCase().endsWith("mp4") ||urls.get(i).toLowerCase().endsWith("webm"))
          urls.set(i, urls.get(i).replaceAll(regex, videoReplacement));
        else if(urls.get(i).toLowerCase().endsWith("mp3") || urls.get(i).toLowerCase().endsWith("wav"))
          urls.set(i, urls.get(i).replaceAll(regex, audioReplacement));
        else
          urls.set(i, urls.get(i).replaceAll(regex, imageReplacement));
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
