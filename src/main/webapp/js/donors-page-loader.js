const urlParams = new URLSearchParams(window.location.search);
const parameterError = urlParams.get('error');

function showErrorMessage() {
  const errorMsg = document.getElementById("errMsg");
  if(parameterError===null) {
	  errorMsg.innerHTML= "";
  }
  else {
	  errorMsg.innerHTML= "Image you upoaded it not recognized as a donation sticker!";
  }
}
  
/**Fetches the Blobstore upload url and pass it to the form action*/
function fetchBlobstoreUrl() {
  const errorMsg = document.getElementById("errMsg");
  fetch('/blobstore-upload-url')
  .then((response) => {
	return response.text();
  })
  .then((imageUploadUrl) => {
	  fetch('/login-status')
      .then((response) => {
        return response.json();
      })
      .then((loginStatus) => {
        if (loginStatus.isLoggedIn) {
	      const messageForm = document.getElementById('message-form');
	      messageForm.action = imageUploadUrl;
        }
		else {
		  errorMsg.innerHTML = "login to post your sticker!";
        }			
      });
  });
}

/** Fetches messages and add them to the page. */
function fetchMessages() {
  const url = '/stickers';
  var bloodDonates = 0;
  var organDonates = 0;
  var moneyDonates = 0;
  var otherDonates = 0;
  const bloodDonation = document.getElementById("blood-donation");
  bloodDonation.innerHTML = "Blood Donors: " +0;
  const organDonation = document.getElementById("organ-donation");
  organDonation.innerHTML = "Organ Donors: " +0;
  const moneyDonation = document.getElementById("money-donation");
  moneyDonation.innerHTML = "Money Donors: " +0;
  const otherDonation = document.getElementById("other-donation");
  otherDonation.innerHTML = "Other Donors: " +0;
  
  fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((messages) => {
        const messagesContainer = document.getElementById('message-container');
        if (messages.length == 0) {
          messagesContainer.innerHTML = '<p>No stickers yet.</p>';
        } else {
          messagesContainer.innerHTML = '';
        }
        messages.forEach((message) => {
          const messageDiv = buildMessageDiv(message);
          messagesContainer.appendChild(messageDiv);
		  var messageTxt = message.text.toLowerCase()
		  if(messageTxt.includes("blood") ){
			  bloodDonates++;
		      bloodDonation.innerHTML = "Blood Donors: " + bloodDonates;
		  }
		  else if(messageTxt.includes("organ") ){
			  organDonates++;
			  organDonation.innerHTML = "Organ Donors: " + organDonates;
		  }
		  else if(messageTxt.includes("money") ){
			  moneyDonates++;
			  moneyDonation.innerHTML = "Money Donors: " + moneyDonates;
		  }
		  else {
			  otherDonates++;
			  otherDonation.innerHTML = "Other Donors: " + otherDonates;
		  }
        });
      });
}

/**
 * Builds an element that displays the message.
 * @param {Message} message
 * @return {Element}
 */
function buildMessageDiv(message) {
  const headerDiv = document.createElement('div');
  headerDiv.classList.add('message-header');
  headerDiv.appendChild(document.createTextNode(
      message.user + ' - ' + new Date(message.timestamp)));

  const bodyDiv = document.createElement('div');
  bodyDiv.classList.add('message-body');
  bodyDiv.innerHTML = message.text;

  const messageDiv = document.createElement('div');
  messageDiv.classList.add('message-div');
  messageDiv.appendChild(headerDiv);
  messageDiv.appendChild(bodyDiv);

  return messageDiv;
}

/** Fetches data and populates the UI of the page. */
function buildUI() {
  addLoginOrLogoutLinkToNavigation();
  fetchMessages();
  fetchBlobstoreUrl();
  showErrorMessage();
  
}

/**
 * Adds a login or logout link to the page, depending on whether the user is
 * already logged in.
 */
function addLoginOrLogoutLinkToNavigation() {
  const navigationElement = document.getElementById('navigation');
  if (!navigationElement) {
    console.warn('Navigation element not found!');
    return;
  }

  fetch('/login-status')
      .then((response) => {
        return response.json();
      })
      .then((loginStatus) => {
        if (loginStatus.isLoggedIn) {
          navigationElement.appendChild(createListItem(createLink(
              '/user-page.html?user=' + loginStatus.username, 'Your Page')));

          navigationElement.appendChild(
              createListItem(createLink('/logout', 'Logout')));
        } else {
          navigationElement.appendChild(
              createListItem(createLink('/login', 'Login')));
        }
      });
}

/**
 * Creates an li element.
 * @param {Element} childElement
 * @return {Element} li element
 */
function createListItem(childElement) {
  const listItemElement = document.createElement('li');
  listItemElement.appendChild(childElement);
  return listItemElement;
}

/**
 * Creates an anchor element.
 * @param {string} url
 * @param {string} text
 * @return {Element} Anchor element
 */
function createLink(url, text) {
  const linkElement = document.createElement('a');
  linkElement.appendChild(document.createTextNode(text));
  linkElement.href = url;
  return linkElement;
}