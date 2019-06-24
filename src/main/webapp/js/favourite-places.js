/** Creates and displays a map on the page with everyone's favourite locations */
function createMap() {
    const map = new google.maps.Map(document.getElementById('map'), {
        center: {lat:26.80,  lng: 16.59 },
        zoom: 2.0
      });

    addLandmark(map, 43.724911, -79.394798, 'Ginelle\'s Favourite Place',
          'Toronto is Ginelle\'s favourite place in the world.');
    addLandmark(map, 32.640176, 51.677378, 'Pardis\'s Favourite Place',
          'Esfahan is Pardis\'s favourite place in the world.');
    addLandmark(map, 30.579241, 114.344387, 'Chloe\'s Favourite Place',
          'Wuhan is Chloe\'s favourite place in the world.');
    addLandmark(map, 21.171620, 72.806259, 'Priyal\'s Favourite Place',
          'Surat is Priyal\'s favourite place in the world.');



          /*document.getElementById('submit').addEventListener('click', function() {
            geocodeAddress(geocoder, map);
          });*/
      
     //     geocodeAddress("New York", map);
    //geocodeAddress("1050 THORNDIKE ST PALMER MA", map);
    }

/** Adds a marker that shows an info window when clicked. */
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
}

/*function geocodeAddress(address, resultsMap) {

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
      }*/
