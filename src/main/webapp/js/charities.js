/** Creates and displays a map on the page with everyone's favourite locations */
function createCharities() {

      fetch('/charity-data').then(function(response) {
        return response.json();
      }).then((charities) => {

        const map = new google.maps.Map(document.getElementById('map'), {
          center: {lat: 39.141964, lng: -94.571583},
          zoom:5
        });

        charities.forEach((charity) => {
         // geocodeAddress(charity.location,map);
         addLandmark(map, parseFloat(charity.lat), parseFloat(charity.lng), charity.name)
        });

      });

 }


//Adds a marker that shows an info window when clicked.
function addLandmark(map, lat, lng, title) {
  const marker = new google.maps.Marker ({
    position: {lat: lat, lng: lng},
    map: map,
    title: title
  });
  marker.addListener('click', function() {
    infoWindow.open(map, marker);
  });
}


function geocodeAddress(address, resultsMap) {

        var geocoder = new google.maps.Geocoder();
        geocoder.geocode({'address': address}, function(results, status) {
          if (status === 'OK') {
            resultsMap.setCenter(results[0].geometry.location);
            var marker = new google.maps.Marker({
              map: resultsMap,
              position: results[0].geometry.location
            });
            console.log(results[0].geometry.location);
          } else {
            alert('Geocode was not successful for the following reason: ' + status);
          }
        });
      }
