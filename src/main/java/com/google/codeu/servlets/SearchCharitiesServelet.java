package com.google.codeu.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Charity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import java.util.Scanner;

/**
 * Handles fetching and saving user data.
 */
@WebServlet("/search-charities")
public class SearchCharitiesServelet extends HttpServlet {

    private Datastore datastore;

    /**
     * Inititalized the datastore object and reads data from the file and and
     * adds any data that wasn't already in there.
     */
    @Override
    public void init() {
        datastore = new Datastore();

        Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/WEB-INF/search-charities.csv"));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] cells = line.split(",");

            if (cells.length == 0) {
                continue;
            }

            // adding charities to datastore
            if( datastore.getCharity(cells[1]) == null) {
                Charity charity = new Charity(cells[1], cells[3], cells[4]);
                datastore.storeCharity(charity);
            }
        }
    }

    /**
     * Responds with a JSON Object of all the charities that match the type specified in the
     * request.
     */
    @Override
    public void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {

        String type = Jsoup.clean(request.getParameter("type"), Whitelist.none());
        System.out.println("IN GET");

        if(type == null ){
            System.out.println("NULL TYPE");
        }
        List<Charity> charities = datastore.getCharities(type);

        JsonArray charitiesArray = new JsonArray();
        Gson gson = new Gson();

        for(Charity charity : charities) {
            charitiesArray.add(gson.toJsonTree(charity));
        }

        response.setContentType("application/json");
        response.getOutputStream().println(charitiesArray.toString());

        //response.sendRedirect("/displaycharities.html" );
    }
    @Override
    public void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("Type: " + request.getParameter("type"));
        String type = Jsoup.clean(request.getParameter("type"), Whitelist.none());

        if(type == null ){
            System.out.println("NULL TYPE");
        }
        List<Charity> charities = datastore.getCharities(type);

        JsonArray charitiesArray = new JsonArray();
        Gson gson = new Gson();

        for(Charity charity : charities) {
            charitiesArray.add(gson.toJsonTree(charity));
            System.out.println(charity);
        }

        response.setContentType("application/json");
        response.getOutputStream().println(charitiesArray.toString());

        //response.sendRedirect("/displaycharities.html" );
    }


}
