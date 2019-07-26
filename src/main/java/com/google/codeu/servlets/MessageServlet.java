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
import java.net.URL;
import java.util.Map;
import java.util.regex.*;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;


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
  
  /** Deletes a message*/
  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("application/json");

    String user = request.getParameter("user");

    if (user == null || user.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }
	
    /*deleting message using message id from datastore*/
    String messageId = Jsoup.clean(request.getParameter("messageId"), Whitelist.none());
    datastore.deleteMessage(messageId);
    
    /*fetch remaining messages after delete*/
    List<Message> messages = datastore.getMessages(user);
    Gson gson = new Gson();
    String json = gson.toJson(messages);

    response.getWriter().println(json);
  }
  
   /**
   * Edits a message entity in Datastore and
   * Responds with a JSON representation of {@link Message} data for a specific user. Responds with
   * an empty array if the user is not provided.
   */
  @Override
  public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    String user = request.getParameter("user");
    if (user == null || user.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    } 
    
    /*editing message using message id from datastore*/
    String messageId = Jsoup.clean(request.getParameter("messageId"), Whitelist.none());
    String messageText = request.getParameter("messageText");
    datastore.editMessage(messageId, messageText);

    /*fetch messages after update*/
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
    if(request.getParameter("text")!=null) //then it's a message, otherwise it's a sticker
    {
        String text = Jsoup.clean(request.getParameter("text"), Whitelist.none());

      /*replace file urls with corresponding html tags(<img>, <video>, <audio>)*/
        String textReplaced = tagURLs(text);

      /*adding image tags and corresponding labels for the image file uploaded to Blobstore to the end of message*/
        String messageText = textReplaced;
        List<BlobKey> imageBlobKeys = getBlobKeys(request, "image");
        /*get image url ulpoaded to Blobstore*/
        List<String> imageBlobUrls = getUploadedFileUrl(imageBlobKeys);
        /*add image tag for uploaded image url at the end of message text*/
        if(imageBlobUrls!=null && !imageBlobUrls.isEmpty()) 
        {
          for(int i = 0; i<imageBlobUrls.size(); i++)
          {
            //add image tag for the image user uploaded to Blobstore
            messageText += "<img src=\"" + imageBlobUrls.get(i) + "\" />"; 
        
            //Get labels of the image and add it after image tag
            byte[] blobBytes = getBlobBytes(imageBlobKeys.get(i));
            List<EntityAnnotation> imageLabels = getImageLabels(blobBytes);
            //messageText = messageText+"<"; //hiding tags using <>
            messageText = messageText+"Tags: ";
            if(imageLabels != null)
            {
              for(EntityAnnotation label : imageLabels)
              {
                //messageText+= label.getDescription() + ": " + label.getScore()+", ";
                messageText+= label.getDescription() + ", ";
              }
            }
            //messageText = messageText+">";
          }
        }

        /*storing the message in Datastore*/
        Message message = new Message(user, messageText);
        datastore.storeMessage(message);
        response.sendRedirect("/user-page.html?user=" + user);
    }
    else //image is a sticker
    {
        /*adding image tags and corresponding labels for the image file uploaded to Blobstore to the end of message*/
        String messageText = "";
        List<BlobKey> imageBlobKeys = getBlobKeys(request, "image");
        if(imageBlobKeys==null || imageBlobKeys.isEmpty()) {
            response.sendRedirect("/donors.html");
            return;
        }
        /*get image url ulpoaded to Blobstore*/
        List<String> imageBlobUrls = getUploadedFileUrl(imageBlobKeys);
        
        
        if(imageBlobUrls!=null && !imageBlobUrls.isEmpty()) 
        {
            byte[] blobBytes = getBlobBytes(imageBlobKeys.get(0));
            //add image tag for the image user uploaded to Blobstore
            String messageUrl = "<img src=\"" + imageBlobUrls.get(0) + "\" />" + " <hr/>"; 
            List<EntityAnnotation> imageTexts = detectText(blobBytes);
            if(imageTexts != null)
            {
              for(EntityAnnotation imageText : imageTexts)
              {
                messageText+= imageText.getDescription() + ", ";
              }
            }
            
           if(messageText.toLowerCase().contains("donated") || messageText.toLowerCase().contains("gave, blood") || messageText.toLowerCase().contains("donor") || messageText.toLowerCase().contains("donation")) 
           {
                if(messageText.toLowerCase().contains("blood"))
                    messageText+= messageUrl + "Thanks for donating blood! you can save a life!";
                else if(messageText.toLowerCase().contains("money") || messageText.toLowerCase().contains("dollar") || messageText.toLowerCase().contains("$"))
                    messageText+= messageUrl + "Thanks for donating money! you make life better for someone!";
                else if(messageText.toLowerCase().contains("organ"))
                    messageText+= messageUrl + "Thanks for donating an organ! you gave another chance for life to another person!";
                else
                    messageText+= messageUrl + "Thanks for donating! Any donation is valuable!";
            }
           else
           {
               response.sendRedirect("/donors.html?error=NotDonation");
               return;
           }
            
            Message sticker = new Message(user, messageText);
            datastore.storeSticker(sticker);
            response.sendRedirect("/donors.html");
        }
    }
  }
  
  /**
  * Returns the BlobKey that points to the image files uploaded by the user,
  * or null if the user didn't upload any image file.
  */
  private List<BlobKey> getBlobKeys(HttpServletRequest request, String formInputElementName)
  {
    List<BlobKey> imageBlobKeys = new ArrayList<>();
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
    
    //selecting blobKeys for image blobs only
    for(BlobKey blobK: blobKeys)
    {
      // Checking the validity of the file to make sure it's an image
      String fileType = new BlobInfoFactory().loadBlobInfo(blobK).getContentType().toLowerCase();
      if(!(fileType.equals("image/jpg") ||fileType.equals("image/jpeg") || fileType.equals("image/gif") || fileType.equals("image/png")))
      {
        blobstoreService.delete(blobK);
      }
      else //blob is an image file
      {
          imageBlobKeys.add(blobK);
      }
    }
    return imageBlobKeys;
  }
  
  /**
  * Returns a list URL that points to the uploaded image files in Blobstore 
  */
  private List<String> getUploadedFileUrl(List<BlobKey> imageBlobKeys)
  {
    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    List<String> imageBlobUrls = new ArrayList<>();
	
    if(imageBlobKeys!=null)
    {
      for(BlobKey blobK: imageBlobKeys)
      {
        try
        {
          //getting the image URL to the uploaded file
          ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobK);  
          String imageUrl = imagesService.getServingUrl(options);//getServingUrl locks the blob, so it cannot be deleted with blobstoreService.delete(blobK); 
          imageBlobUrls.add(imageUrl);
        }
        catch(IllegalArgumentException e) 
        {
          System.out.println(e.getMessage());		  
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
  
  /**
  * Blobstore stores files as binary data. This function retrieves the
  * binary data stored at the BlobKey parameter.
  * return blob as byte array
  */
  private byte[] getBlobBytes(BlobKey blobKey) throws IOException {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();

    int fetchSize = BlobstoreService.MAX_BLOB_FETCH_SIZE;
    long currentByteIndex = 0;
    boolean continueReading = true;
    while (continueReading) {
      // end index is inclusive, so we have to subtract 1 to get fetchSize bytes
      byte[] b = blobstoreService.fetchData(blobKey, currentByteIndex, currentByteIndex + fetchSize - 1);
      outputBytes.write(b);

      // if we read fewer bytes than we requested, then we reached the end
      if (b.length < fetchSize) {
        continueReading = false;
      }

      currentByteIndex += fetchSize;
    }

    return outputBytes.toByteArray();
  }
  
  /**
  * Uses the Google Cloud Vision API to generate a list of labels that apply to the image
  * represented by the binary data stored in imgBytes.
  */
  private List<EntityAnnotation> getImageLabels(byte[] imgBytes) throws IOException {
    ByteString byteString = ByteString.copyFrom(imgBytes);
    Image image = Image.newBuilder().setContent(byteString).build();

    Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
    AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
    List<AnnotateImageRequest> requests = new ArrayList<>();
    requests.add(request);

    ImageAnnotatorClient client = ImageAnnotatorClient.create();
    BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
    client.close();
    List<AnnotateImageResponse> imageResponses = batchResponse.getResponsesList();
    AnnotateImageResponse imageResponse = imageResponses.get(0);

    if (imageResponse.hasError()) {
      System.err.println("Error getting image labels: " + imageResponse.getError().getMessage());
      return null;
    }

    return imageResponse.getLabelAnnotationsList();
  }


    /*text detection*/
    private List<EntityAnnotation> detectText(byte[] imgBytes) {
      List<AnnotateImageRequest> requests = new ArrayList<>();
      List<EntityAnnotation> annotations = new ArrayList<>();
      ByteString byteString = ByteString.copyFrom(imgBytes);
      Image image = Image.newBuilder().setContent(byteString).build();

      Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
      AnnotateImageRequest request =
          AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(image).build();
      requests.add(request);

      try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
        BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
        List<AnnotateImageResponse> responses = response.getResponsesList();

        for (AnnotateImageResponse res : responses) {
          if (res.hasError()) {
            System.out.println("Error: %s\n" + res.getError().getMessage());
            return null;
          }

          // For full list of available annotations, see http://g.co/cloud/vision/docs
          for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                      annotations.add(annotation);
            //out.printf("Text: %s\n", annotation.getDescription());
            //out.printf("Position : %s\n", annotation.getBoundingPoly());
          }
        }
            return annotations;
      }
      catch(Exception e)
      {
          System.out.println("error: "+e.getMessage());
          return null;
      }
    }
}
