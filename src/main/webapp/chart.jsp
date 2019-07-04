<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
  	<form id="donation_chart" method = "GET" action = "fetchData"></form>
  	<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
  	    <% 
			String[] name = (String [])request.getAttribute("CharityName");
			int[] donation = (int [])request.getAttribute("donation");
		%>
		<script type="text/javascript">
		    google.charts.load('current', {packages: ['corechart']});
		    google.charts.setOnLoadCallback(drawChart);
		    function drawChart(){
		      //to be filled in later
		    	var donation_data = new google.visualization.DataTable();
		    	//define columns for the DataTable instance
		    	donation_data.addColumn('string', 'Charity');
		    	donation_data.addColumn('number', 'Donation');
		    	for (var i = 0; i < name.length; i++) {
		    		donation_data.addRow([name[i], donation[i]]);
		    	}
		    	
		    	var chart_options = {
	                    width: 800,
	                    height: 400
	      		};
		    	
		    	var chart = new google.visualization.BarChart(document.getElementById('donation_chart'));
		    	chart.draw(donation_data,chart_options);
		    }
		</script>
    <title>A New Chart</title>
  </head>
  <body>
    <h1>Chart Project</h1>
  </body>
</html>