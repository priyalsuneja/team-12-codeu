        google.charts.load('current', {packages: ['corechart']});
        google.charts.setOnLoadCallback(drawChart);
        function drawChart(){
          fetch("/ChartServlet")
              .then((response) => {
                return response.json();
              })
              .then((charityJson) => {
                var charityData = new google.visualization.DataTable();
                //define columns for the DataTable instance
                charityData.addColumn('string', 'Charity Name');
                charityData.addColumn('number', 'Amount of Donation');

                for (i = 0; i < charityJson.length; i++) {
                  charityRow = [];
                  var charityName = charityJson[i].charityName;
                  var donation = charityJson[i].donation;
                  console.log(donation);
                  charityRow.push(charityName, donation);

                  charityData.addRow(charityRow);
               }
               var chartOptions = {
                                 width: 800,
                                 height: 400
                   };
               var charityChart = new google.visualization.BarChart(document.getElementById('charity_chart'));
               charityChart.draw(charityData, chartOptions);
           })
        }