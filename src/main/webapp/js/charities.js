/** Creates and displays a map on the page with everyone's favourite locations */
function createCharities() {

      fetch('/charity-data').then(function(response) {
        return response.json();
      }).then((charities) => {

        const map = new google.maps.Map(document.getElementById('map'), {
          center: {lat: 35.78613674, lng: -119.4491591},
          zoom:7
        });

        charities.forEach((charity) => {
          setTimeout(geocodeAddress(charity.location,map), 1000);
        });

        //geocodeAddress(charities[0].location,map);
      });
    /*const map = new google.maps.Map(document.getElementById('map'), {
        center: {lat:26.80,  lng: 16.59 },
        zoom: 2.0
      });


    geocodeAddress("1050 THORNDIKE ST PALMER MA", map);*/

 }


/** Adds a marker that shows an info window when clicked.
function addLandmark(map, lat, lng, title, description) {
  const marker = new google.maps.Marker ({
    position: {lat: lat, lng: lng},
    map: map,
    title: title
  });
  const infoWindow = new google.maps.InfoWindow({
    content: description
  });
  marker.addListener('click', function() {
    infoWindow.open(map, marker);
  });
}*/


function geocodeAddress(address, resultsMap) {

        var geocoder = new google.maps.Geocoder();
       // var address = document.getElementById('address').value;
        geocoder.geocode({'address': address}, function(results, status) {
          if (status === 'OK') {
            resultsMap.setCenter(results[0].geometry.location);
            var marker = new google.maps.Marker({
              map: resultsMap,
              position: results[0].geometry.location

            });
           console.log("Successful")
          } else {
            alert('Geocode was not successful for the following reason: ' + status);
          }
        });
      }
