package com.google.codeu.servlets;


import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.util.Scanner;


@WebServlet("/ChartServlet")
public class ChartServlet extends HttpServlet {

  private JsonArray charityDonationArray;

  // This class could be its own file if we needed it outside this servlet
  private static class charityDonation {
    String charityName;
    double donation;

    private charityDonation (String charityName, double donation) {
      this.charityName = charityName;
      this.donation = donation;
    }
  }


  @Override
  public void init() {
	charityDonationArray = new JsonArray();
    Gson gson = new Gson();
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/WEB-INF/charity-form.csv"));
    scanner.nextLine(); //skips first line (the csv header)
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");

      String curName = cells[1];
      //System.out.println(curName);
      double curDonation = Double.parseDouble(cells[2]);
      //System.out.println(curDonation);
      charityDonationArray.add(gson.toJsonTree(new charityDonation(curName, curDonation)));
    }
    scanner.close();
  }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
     response.setContentType("application/json");
     response.getOutputStream().println(charityDonationArray.toString());
   }


}
