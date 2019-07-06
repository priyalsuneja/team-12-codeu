
function displayCharities() {

	const type = document.getElementById('search-text').value;
    const url = '/search-charities?type=' + type;
    console.log(url);
    fetch(url).then(function(response) {
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
            const charityContainer = document.getElementById('display-charities');
            charityContainer.innerHTML += charity.name + ', ' + charity.city + '<br>';
          });
      }
    });

 }
