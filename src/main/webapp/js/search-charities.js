function displayCharities() {

      fetch('/search-charities').then(function(response) {
        return response.json();
      }).then((charities) => {
        charities.forEach((charity) => {
          const charityContainer = document.getElementById('display-charities');
          charityContainer.innerHTML += charityList;
        });

      });

 }
