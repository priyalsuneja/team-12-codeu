/**
 * http://usejsdoc.org/
 */
	    google.charts.load('current', {packages: ['corechart']});
	    google.charts.setOnLoadCallback(drawChart);
	    function drawChart(){
	      //to be filled in later
	    	var donation_data = new google.visualization.DataTable();
	    	//define columns for the DataTable instance
	    	donation_data.addColumn('string', 'Book Title');
	    	donation_data.addColumn('number', 'Votes');
	    	            
	    	//add data to book_data
	    	donation_data.addRows([
	    	    ["Charity1", 1234793],
	    	    ["Charity2", 102637],
	    	    ["Charity3", 73127],
	    	    ["Charity4", 4336279],
	    	    ["Charity5", 834782]
	    	]);
	    	
	    	var chart_options = {
                    width: 800,
                    height: 400
      		};
	    	
	    	var chart = new google.visualization.BarChart(document.getElementById('donation_chart'));
	    	chart.draw(donation_data,chart_options);
	    }