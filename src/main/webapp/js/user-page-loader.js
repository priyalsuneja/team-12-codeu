/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Get ?user=XYZ parameter value
const urlParams = new URLSearchParams(window.location.search);
const parameterUsername = urlParams.get('user');

// URL must include ?user=XYZ parameter. If not, redirect to homepage.
if (!parameterUsername) {
  window.location.replace('/');
}

/** Sets the page title based on the URL parameter username. */
function setPageTitle() {
  document.getElementById('page-title').innerText = parameterUsername;
  document.title = parameterUsername + ' - User Page';
}

/** created a map to show the location of charity*/
function createMap(){
	var longitude;
	var latitude;
  const url = "/locations?user="+parameterUsername;
  fetch(url)
    .then((response)=> {
      return response.json();
	})
	  .then((locations)=> {
        const map = new google.maps.Map(document.getElementById('map'), {
          center: {lat: 35, lng: -100},
          zoom:3
        });
        if (locations.length > 0){
		  
		  /*adding location of the self charity*/
		  longitude = locations[0].longitude;
		  console.log(longitude);
		  latitude = locations[0].latitude;
	      console.log(latitude);
          new google.maps.Marker({
            position: {lat: latitude, lng: longitude},
            map: map,
			zIndex: google.maps.Marker.MAX_ZINDEX + 1
		  });
			
		  /*adding location of other nearby charities*/
		  for(var i = 1; i<locations.length; i++){
            longitude = locations[i].longitude;
			console.log(longitude);
		    latitude = locations[i].latitude;
			console.log(latitude);
            new google.maps.Marker({
                position: {lat: latitude, lng: longitude},
                map: map,
				icon: {url: "http://maps.google.com/mapfiles/ms/icons/yellow-dot.png"}
			});
		  }  
        }
	  });
}

/**show location-form if view self*/
//to be done later ...

/**
 * Shows the message form if the user is logged in and viewing their own page.
 */
function showMessageFormIfViewingSelf() {
  fetch('/login-status')
      .then((response) => {
        return response.json();
      })
      .then((loginStatus) => {
        if (loginStatus.isLoggedIn &&
            loginStatus.username == parameterUsername) {
          document.getElementById('about-me-form').classList.remove('hidden');
          }
        });
  }


/** Fetches messages and add them to the page. */
function fetchMessages() {
  const url = '/messages?user=' + parameterUsername;
  fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((messages) => {
        const messagesContainer = document.getElementById('message-container');
        if (messages.length == 0) {
          messagesContainer.innerHTML = '<p>This user has no posts yet.</p>';
        } else {
          messagesContainer.innerHTML = '';
        }
        messages.forEach((message) => {
          const messageDiv = buildMessageDiv(message);
          messagesContainer.appendChild(messageDiv);
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

  /*adding delete button to message-div*/
  addDeleteButtonIfViewSelf(message, messageDiv);

  /*adding edit button to message div*/
  addEditButtonIfViewSelf(messageDiv, bodyDiv, message);

  return messageDiv;
}

function addEditButtonIfViewSelf(messageDiv, bodyDiv, message) {

  fetch('/login-status')
    .then((response) => {
      return response.json();
    })
    .then((loginStatus) => {
      if (loginStatus.isLoggedIn &&
          loginStatus.username == parameterUsername) {
        const editButton = document.createElement('Button');
        editButton.innerHTML = "Edit";
        editButton.onclick = function() {
          editButton.style.visibility="hidden";
          const bodyText = document.createElement('textarea');
          bodyText.innerHTML = message.text;
          bodyText.style.width = "100%";
          bodyDiv.innerHTML = '';
          bodyDiv.appendChild(bodyText);
          	  
          /*creating cancle and save buttons*/
          const cancelButton = document.createElement('Button');
          const saveButton = document.createElement('Button');
		  
          /*add cancle button*/
          addCancelButtonFunction(editButton, cancelButton, saveButton, messageDiv, bodyDiv, bodyText, message);
		  
          /*add save button*/
          addSaveButtonFunction(editButton, cancelButton, saveButton, messageDiv, bodyDiv, bodyText, message);
        }
        messageDiv.appendChild(editButton);
      }
    });
}

/*adding functionality to cancle button and append it to message div*/
function addCancelButtonFunction(editButton, cancelButton, saveButton, messageDiv, bodyDiv, bodyText, message) {
  cancelButton.innerHTML = "Cancel";
  cancelButton.onclick = function() {
    editButton.style.visibility="visible";
    try{
      bodyDiv.removeChild(bodyText);
      bodyDiv.innerHTML = message.text;
      messageDiv.removeChild(cancelButton);
      messageDiv.removeChild(saveButton);
    }
    catch(err) {
      console.log(err.message);
    }
  }
  messageDiv.appendChild(cancelButton);
}

/*adding functionality to save button and append it to message div*/
function addSaveButtonFunction(editButton, cancelButton, saveButton, messageDiv, bodyDiv, bodyText, message) {
  saveButton.innerHTML = "Save";
  saveButton.onclick = function() {
    const messageId = message.id;
    var messageText = bodyText.value;
    messageText = messageText.split('\n').join(" ");
    const url = '/edit-message?user=' + parameterUsername+"&messageId="+messageId+"&messageText="+messageText;
    fetch(url)
    .then((response) => {
      return response.json();
    })
    .then((messages) => {
      const messagesContainer = document.getElementById('message-container');
      if (messages.length == 0) {
        messagesContainer.innerHTML = '<p>This user has no posts yet.</p>';
      } else {
        messagesContainer.innerHTML = '';
		messages.forEach((message) => {
          const messageDiv = buildMessageDiv(message);
          messagesContainer.appendChild(messageDiv);
        });
      }
    });

    try{
	  editButton.style.visibility="visible";
      bodyDiv.removeChild(bodyText);
      bodyDiv.innerHTML = message.text;
      messageDiv.removeChild(cancelButton);
      messageDiv.removeChild(saveButton);
    }
    catch(err) {
      console.log(err.message);
    }
  }
  messageDiv.appendChild(saveButton);
}

/*creating delete button if user has login to their page*/ 
function addDeleteButtonIfViewSelf(message, div) {
  fetch('/login-status')
    .then((response) => {
      return response.json();
    })
    .then((loginStatus) => {
      if (loginStatus.isLoggedIn &&
        loginStatus.username == parameterUsername) {
        /*creat delete button*/
        const deleteButton = document.createElement("Button");
        deleteButton.innerHTML = "Delete";
        const messageId = message.id;
        deleteButton.onclick = function(){
          const url = '/deleteMessage?user=' + parameterUsername+"&messageId="+messageId;
          fetch(url)
          .then((response) => {
            return response.json();
          })
          .then((messages) => {
            const messagesContainer = document.getElementById('message-container');
            if (messages.length == 0) {
              messagesContainer.innerHTML = '<p>This user has no posts yet.</p>';
            } else {
              messagesContainer.innerHTML = '';
			  messages.forEach((message) => {
                const messageDiv = buildMessageDiv(message);
                messagesContainer.appendChild(messageDiv);
              });
            }
          });
        };
        /*adding delete button to message div*/
        div.appendChild(deleteButton);
      }
    });
}

/**Fetches the Blobstore upload url and pass it to the form action*/
function fetchBlobstoreUrlAndShowForm() {
fetch('/blobstore-upload-url')
  .then((response) => {
	return response.text();
  })
  .then((imageUploadUrl) => {
	const messageForm = document.getElementById('message-form');
	messageForm.action = imageUploadUrl;
	messageForm.classList.remove('hidden');
  });
}
	  
/** Fetches data and populates the UI of the page. */
function buildUI() {
  setPageTitle();
  createMap();
  showMessageFormIfViewingSelf();
  fetchMessages();
  fetchAboutMe();
  fetchBlobstoreUrlAndShowForm()
}

function fetchAboutMe(){
  const url = '/about?user=' + parameterUsername;
  fetch(url).then((response) => {
    return response.text();
  }).then((aboutMe) => {
    const aboutMeContainer = document.getElementById('about-me-container');
    if(aboutMe == ''){
      aboutMe = 'This user has not entered any information yet.';
    }

    aboutMeContainer.innerHTML = aboutMe;

  });
}

function fetchFilteredMessages() {
	const keyword = document.getElementById('search-text').value;
	const url = '/filter-messages?user=' + parameterUsername+'&search='+keyword;
	  fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((messages) => {
        const messagesContainer = document.getElementById('message-container');
        if (messages.length == 0) {
          messagesContainer.innerHTML = '<p>No results found.</p>';
        } else {
          messagesContainer.innerHTML = '';
        }
        messages.forEach((message) => {
          const messageDiv = buildMessageDiv(message);
          messagesContainer.appendChild(messageDiv);
        });
      });
}
