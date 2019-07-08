const urlParams = new URLSearchParams(window.location.search);
const parameterType = urlParams.get('type');

function fetchCharities(){
  const url = '/about?type=' + parameterType;
  fetch(url).then((response) => {
    return response.text();
  }).then((charityList) => {
    const charityContainer = document.getElementById('display-charities');
    if(aboutMe == ''){
      aboutMe = 'Sorry, there are no charities matching that keyword.';
    }

   charityContainer.innerHTML = charityList;

  });
}