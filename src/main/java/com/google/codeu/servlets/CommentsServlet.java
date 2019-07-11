/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.codeu.servlets;

import com.google.codeu.data.Comment;
import com.google.codeu.data.Datastore;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 *
 * @author 018639476
 */
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {
    
  private Datastore datastore;
    
  @Override
  public void init() {
    datastore = new Datastore();
  }
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    String messageId = request.getParameter("messageId");

    if (messageId == null || messageId.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }
    /*getting list of comments for the messageId from datastore*/
    List<Comment> comments = datastore.getComments(messageId);
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    /*writting comments to response*/
    response.getWriter().println(json);
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {  
    String user = request.getParameter("user");
    if(user==null) {
      response.sendRedirect("/index.html");
      return;
    }
    
    try{
      String messageId = request.getParameter("messageId");
	  String commenter = request.getParameter("commenter");
      String comment = Jsoup.clean(request.getParameter("comment"), Whitelist.none());
      
      if(messageId!=null && comment!=null)
      {
        /*creat comment object and store it to datastore*/
        Comment commentObj = new Comment(commenter, messageId, comment);
        datastore.storeComment(commentObj);
      }
    }
    catch(Exception e)
    {
        System.out.println("err posting comment: "+e.getMessage());
    }
    if(user.equals(""))//the comment is comming from public feed
    {
        System.out.println("in public feed redirect!!!!!!!!!!!!!!!!!!!!!!!");
      response.sendRedirect("/public-feed.html");
      return;
    }
    
    //comment is comming from user-page
    response.sendRedirect("/user-page.html?user=" + user);
  }
}
