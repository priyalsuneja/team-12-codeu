function displayCharities() {

      fetch('/search-charities').then(function(response) {
        return response.json();
      }).then((charities) => {

        if(JSON.stringify(charities) === JSON.stringify({})) {
          const charityContainer = document.getElementById('display-charities');
          charityContainer.innerHTML += "Sorry, there are no charities matching this keyword!";
          Console.log("here");
        }
        charities.forEach((charity) => {
          const charityContainer = document.getElementById('display-charities');
          console.log(charity)
          charityContainer.innerHTML += charity;
        });

      });

 }
