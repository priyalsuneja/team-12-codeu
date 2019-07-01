package com.google.codeu.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Charity;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import java.util.Scanner;

/**
 * Handles fetching and saving user data.
 */
@WebServlet("/search-charities")
public class SearchCharitiesServelet extends HttpServlet {

    private Datastore datastore;

    @Override
    public void init() {
        datastore = new Datastore();

        Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/WEB-INF/eo2.csv"));

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


}
