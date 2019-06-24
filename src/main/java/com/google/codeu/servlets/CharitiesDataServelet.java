package com.google.codeu.servlets;

import com.google.api.client.json.Json;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Returns Charity data as a JSON array, e.g. [{"lat": 38.4404675, "lng": -122.7144313}]
 */
@WebServlet("/charity-data")
public class CharitiesDataServelet extends HttpServlet {

  private JsonArray charityArray;

  @Override
  public void init() {
    charityArray = new JsonArray();
    Gson gson = new Gson();
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/WEB-INF/eo1.csv"));
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");

      if(cells.length == 0) {
        continue;
      }


      String address = cells[3] + " " + cells[4] + " " + cells[5];
      Charity newCharity = new Charity(cells[1], address);

     /* ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("JavaScript");

      // read script file
      engine.eval(Files.newBufferedReader(Paths.get("/home/priyalsuneja/team-12-codeu/src/main/webapp/js/charities.js"), StandardCharsets.UTF_8));

      Invocable inv = (Invocable) engine;

      // call function from script file
      inv.invokeFunction("yourFunction", "param");*/

      // use api to get lang, lat from city name 

      // create Charity obj 

      charityArray.add(gson.toJsonTree(newCharity));
    }
    scanner.close();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    response.getOutputStream().println(charityArray.toString());
  }

  // This class could be its own file if we needed it outside this servlet
  private static class Charity{
    String name;
    //String url;
    String location;
   // double lat;
   // double lng;

    private Charity(String name, String location) {
      this.name = name;
      //this.url = url;
      this.location = location;
    }
  }
}