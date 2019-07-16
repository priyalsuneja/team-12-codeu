package com.google.codeu.servlets;

import com.google.api.client.json.Json;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Location;
import com.google.codeu.data.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Returns Charity data as a JSON array, e.g. [{"lat": 38.4404675, "lng": -122.7144313}]
 */
@WebServlet("/charity-data")
public class CharitiesDataServelet extends HttpServlet {
    
  private Datastore datastore;
  private JsonArray charityArray;

  @Override
  public void init() {
    datastore = new Datastore();
    charityArray = new JsonArray();
    Gson gson = new Gson();
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/WEB-INF/charities/eo1.csv"));
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");

      if(cells.length == 0) {
        continue;
      }


      String address = cells[4] + " " + cells[5] + " " + cells[6];
      Charity newCharity = new Charity(cells[1], address, cells[2], cells[3]);

      charityArray.add(gson.toJsonTree(newCharity));
    }
    scanner.close();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /*reading locations from datastore for charities user added to the web-app that don't have records in the WEB_-INF files*/
    List<Location> appCharityLocations = datastore.getAllCharityLocations();
    Gson gson = new Gson();
    for(Location loc:appCharityLocations)
    {
        /*currently the stored locations don't have the actual address, but only longitude, and latitude, so address=""*/
        Charity appCharity = new Charity(loc.getUser(), "", Double.toString(loc.getLatitude()), Double.toString(loc.getLongitude()));
        charityArray.add(gson.toJsonTree(appCharity));
    }
    
    response.setContentType("application/json");
    response.getOutputStream().println(charityArray.toString());
  }

  // This class could be its own file if we needed it outside this servlet
  private static class Charity{
    String name;
    String location;
    String lat;
    String lng;

    private Charity(String name, String location, String lat, String lng) {
      this.name = name;
      this.location = location;
      this.lat = lat;
      this.lng = lng;
    }
  }
}