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

/* Searches and displays the names of the charities matching a certain type*/
function displayCharities() {

	const type = document.getElementById('search-type').value;
    const url = '/search-charities?type=' + type;
    fetch(url, {method:'GET'}).then(function(response) {
      return response.json();
    }).then((charities) => {

        console.log(Object.entries(charities).length);
      if(Object.entries(charities).length === 0) {
        const charityContainer = document.getElementById('display-charities');
        charityContainer.innerHTML = "Sorry, there are no charities matching this keyword!";
      }

      else {
          const charityContainer = document.getElementById('display-charities');
          charityContainer.innerHTML ='';

          charities.forEach((charity) => {
            charityContainer.innerHTML += charity.name + ', ' + charity.city + '<br>';
          });
      }
    });

 }

/* Displays all the available types of charities in the database.*/
 function displayTypes() {

    const url = '/search-charities';
    fetch(url, {method:'PUT'}).then(function(response) {
      return response.json();
    }).then((types) => {

      if(Object.entries(types).length === 0) {
        const typesContainer = document.getElementById('display-types');
        typesContainer.innerHTML = "Sorry, there are no charities in the database";
      }

      else {
          const typesContainer = document.getElementById('display-types');
          typesContainer.innerHTML ='Currently Available Charity Types <br>';

          types.forEach((type) => {
            typesContainer.innerHTML += type+ '<br>';
          });
      }
    });
 }
