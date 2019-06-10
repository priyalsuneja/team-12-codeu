package com.google.codeu.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class fetchData
 */
@WebServlet("/fetchData")
public class fetchData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public fetchData() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		System.out.println("check!!");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CodeU?user=root&password=Yty712719#&useSSL=false");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT CharityName, Donation FROM Charities");
			int rowcount = 0;
			if (rs.last()) {
				  rowcount = rs.getRow();
				  rs.beforeFirst(); 
			}
			String[] name = new String[rowcount];
			int[] donation = new int[rowcount];
			int count =0;
			while(rs.next())
			{	
				if(!rs.getString("CharityName").equals(request.getSession().getAttribute("CharityName")))
				{
					name[count] = rs.getString("CharityName");
					donation[count] = rs.getInt("Donation");
					//out.print("<h1>" + name[count] + "</h1>");
				}
				count++;
			}
			request.setAttribute("CharityName",name);
			request.setAttribute("Donation", donation);
			RequestDispatcher rd = request.getRequestDispatcher("chart.jsp");
			rd.forward(request, response);
			conn.close();
		}catch(SQLException se) {
			se.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
			System.err.println("Got an exception!");
		}
	}
}

