
function displayCharities() {

	const type = document.getElementById('search-text').value;
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
