package com.google.codeu.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Scanner;

/**
 * Returns Charity data as a JSON array, e.g. [{"lat": 38.4404675, "lng": -122.7144313}]
 */
@WebServlet("/charity-data")
public class CharitiesDataServelet extends HttpServlet {

  private JsonArray charityLocationArray;

  @Override
  public void init() {
    charityLocationArray = new JsonArray();
    Gson gson = new Gson();
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/WEB-INF/us-charities.csv"));
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");

      String city = cells[4];

      // use api to get lang, lat from city name 

      // create Charity obj 

      charityLocationArray.add(gson.toJsonTree(new UfoSighting(lat, lng)));
    }
    scanner.close();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    response.getOutputStream().println(charityLocationArray.toString());
  }

  // This class could be its own file if we needed it outside this servlet
  private static class Charity{
    String name;
    String url; 
    double lat;
    double lng;

    private Charity(String name, String url, double lat, double lng) {
      this.name = name;
      this.url = url;  
      this.lat = lat;
      this.lng = lng;
    }
  }
}