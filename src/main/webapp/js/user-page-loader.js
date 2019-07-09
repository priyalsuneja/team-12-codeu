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

/**create list of notifications*/
function listNotifications() {
  const notificationsDiv = document.getElementById('notification-container');
  const notificationsBtn = document.getElementById('notifications-btn');
  const closeBtn = document.createElement('Button');
  closeBtn.innerHTML="Close";
  closeBtn.onclick = function() {
    notificationsDiv.style.visibility = 'hidden';
  }
  notificationsDiv.appendChild(closeBtn);
	
  /*creating body div for notifications*/
  const bodyDiv = document.createElement('Div');
  notificationsDiv.appendChild(bodyDiv);
  notificationsBtn.onclick = function() {
	notificationsDiv.style.visibility = 'visible';
	
	/*requesting list of notifications*/
	const url = "/notifications?user="+parameterUsername;
	fetch(url)
	  .then((response)=> {
        return response.json();
	  })
        .then((notifications)=> {
          if (notifications.length == 0) {
            bodyDiv.innerHTML = '<p>No notifications</p>';
          } else {
            bodyDiv.innerHTML = '';
			notifications.forEach((note) => {
			  const noteDiv = document.createElement('Div');
              noteDiv.innerHTML = new Date(note.timestamp)+"<br>"+note.text+"<br>"+"<a href='/user-page.html?user="+note.link+"'>"+note.link+"</a>";
			  noteDiv.style.borderStyle = "solid";
			  noteDiv.style.borderWidths = "1px";
			  noteDiv.style.marginTop = "2px";
			  
              bodyDiv.appendChild(noteDiv);
            });
          }
		});
  }
}

/** created a map to show the location of charity*/
function createMap(){
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
		  var longitude = locations[0].longitude;
		  console.log(longitude);
		  var latitude = locations[0].latitude;
	      console.log(latitude);
          var myMarker = new google.maps.Marker({
            position: {lat: latitude, lng: longitude},
            map: map,
			zIndex: google.maps.Marker.MAX_ZINDEX + 1
		  });
		  
		  const infoWindow = new google.maps.InfoWindow({
            content: '<p> You are at: '+locations[0].latitude+', '+locations[0].longitude+'</p>'
          });
		  
		  myMarker.addListener('click', function() {
            infoWindow.open(map, myMarker);
          });
          
			
		  /*adding location of other nearby charities*/
		  for(var i = 1; i<locations.length; i++){
            longitude = locations[i].longitude;
			console.log(longitude);
		    latitude = locations[i].latitude;
			console.log(latitude);
            const otherMarker = new google.maps.Marker({
              position: {lat: latitude, lng: longitude},
              map: map,
              icon: {url: "http://maps.google.com/mapfiles/ms/icons/yellow-dot.png"}
			});
			const otherInfoWindow = new google.maps.InfoWindow({
              content: '<p>'+locations[i].user+'</p>'
            });
			
            otherMarker.addListener('click', function() {
              otherInfoWindow.open(map, otherMarker);
            });
		  }  
        }
	  });
}

/**show map-container and form if view self*/
function showMapFormIfViewSelf() {
  fetch('/login-status')
      .then((response) => {
        return response.json();
      })
      .then((loginStatus) => {
        if (loginStatus.isLoggedIn &&
            loginStatus.username == parameterUsername) {
          document.getElementById('map-form-div').classList.remove('hidden');
          }
        });
}

/* map setLocation button function*/
function addSetLocationFunction() {
    const setLocationBtn = document.getElementById('set-location-btn');
	setLocationBtn.onclick = function() {
	  const adressInput = document.getElementById('address-input');
	  var address = adressInput.value.split(' ').join('+');
	  const urlgeo = 'https://maps.googleapis.com/maps/api/geocode/json?address='+address+'&key=AIzaSyBJ-A9BQNUd6cR6IgbOnZwr26_62tB6LRI';
	  fetch(urlgeo)
	    .then((response)=> {
		  return response.json();
		})
		  .then((locationGeo) => {
		    if (locationGeo.status == google.maps.GeocoderStatus.OK) {
			  var latlng = locationGeo.results[0].geometry.location;
			  const url = '/locations?user='+parameterUsername+'&latitude='+latlng.lat+'&longitude='+latlng.lng;
	          fetch(url, {method:'POST'})
			    .then(()=> {
				  createMap();
				});
			}
		  });
	}
}

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
		  document.getElementById('message-form').classList.remove('hidden');
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

  /*adding comments button to message-div*/
  addCommentsButton(messageDiv, message);
  
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
        editButton.innerHTML = "Edit Message";
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
    const url = '/messages?user=' + parameterUsername+"&messageId="+messageId+"&messageText="+messageText;
    fetch(url, {method:'PUT'})
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
        deleteButton.innerHTML = "Delete Message";
        const messageId = message.id;
        deleteButton.onclick = function(){
          const url = '/messages?user=' + parameterUsername+"&messageId="+messageId;
          fetch(url, {method:'DELETE'})
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

/*creating comments button*/
function addCommentsButton(messageDiv, message) {
  /*creat button*/
  const commentsButton = document.createElement('Button');
  commentsButton.innerHTML = 'Comments';
  
  /*creat comment container div*/
  const commentContainerDiv = document.createElement('div');
  
  /*create comment div*/
  const commentDiv = document.createElement('div');
  commentDiv.classList.add('comment-div');
  commentDiv.style.width = "90%";
  commentDiv.innerHTML = '';
  
  /*comments button fetches comments for the message, and make a form for posting new comments*/
  commentsButton.onclick = function() {

	/*hide button when clicked to prevent re-clicking*/
	commentsButton.style.visibility="hidden";
	
	/*fetching posted comments for the message*/
	const messageId = message.id;
    const url = '/comments?messageId='+messageId;
	fetch(url)
	  .then((response) => {
        return response.json();
      })
      .then((comments) => {
        if (comments.length == 0) {
          commentDiv.innerHTML = '<p>There are no comments on this message yet.</p>';
        } else {
          comments.forEach((comment) => {
            const bodyDiv = document.createElement('div');
            bodyDiv.classList.add('comment-body');
            bodyDiv.innerHTML = comment.user+': '+comment.text;
            commentDiv.appendChild(bodyDiv);
          });
		}
      });
	commentContainerDiv.appendChild(commentDiv);
	
    /*creat form div*/
	const formDiv = document.createElement('div');
    formDiv.classList.add('form-div');
    formDiv.style.width = "90%";
	
	/*creating comment form*/  
    var commentForm = document.createElement("form");
    commentForm.setAttribute('method',"post");
	
	/*adding commenter to the action url parameter*/
	var user;
	fetch('/login-status')
      .then((response) => {
        return response.json();
      })
      .then((loginStatus) => {
        const commenter = loginStatus.username;
		console.log(commenter);
		commentForm.setAttribute('action',"/comments?user="+parameterUsername+"&commenter="+commenter+"&messageId="+message.id);
	  });
    
	
	/*creat input area*/
    var inputArea = document.createElement("input");
    inputArea.type = "text";
    inputArea.name = "comment";
    inputArea.id = "comment-input";
	inputArea.style.width = "100%";
	inputArea.placeholder = "type you comment here ...";
	commentForm.appendChild(inputArea);
	
	/*create a submit button*/
    var submitButton = document.createElement("input");
    submitButton.type = "submit";
    submitButton.value = "Add Comment";
	commentForm.appendChild(submitButton);
	
	/*adding form to form div*/
	formDiv.appendChild(commentForm);
	commentContainerDiv.appendChild(formDiv);
  }
  /*adding comments button to message-div*/
  commentContainerDiv.appendChild(commentsButton);
  messageDiv.appendChild(commentContainerDiv);
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
  });
}
	  
/** Fetches data and populates the UI of the page. */
function buildUI() {
  setPageTitle();
  fetchUserType();
  createMap();
  addSetLocationFunction();
  fetchMessages();
  fetchAboutMe();
  fetchBlobstoreUrlAndShowForm();
  listNotifications();
  showMessageFormIfViewingSelf();
  showMapFormIfViewSelf();
  
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

function fetchUserType() {
  const url = "/user-type?user="+parameterUsername;
  const statusDiv = document.getElementById('user-status-div');
  fetch(url)
    .then((response)=> {
		return response.text();
	})
	  .then((type)=>{
		  if(type=='1')//user is charity
		  {
			statusDiv.innerHTML= "Charity Organization";
		  }
		  else if(type=='0')//user is donor
		  {
			statusDiv.innerHTML= "Donor User";
		  }
		  else //user record does not exits, or user type is null
		  {
            const statusFormDiv = document.createElement('Div');
			 
            const statusQuestionDiv = document.createElement('Div');
			statusQuestionDiv.innerHTML = "<p>Are you a charity organization?</p>";
			 
			const yesButton = document.createElement('Button');
			yesButton.innerHTML = "Yes";
			 
			const noButton = document.createElement('Button');
			noButton.innerHTML = "No";
			 
			statusFormDiv.appendChild(statusQuestionDiv);
			statusFormDiv.appendChild(yesButton);
			statusFormDiv.appendChild(noButton);
			statusDiv.appendChild(statusFormDiv);
		  }
	  });
}
