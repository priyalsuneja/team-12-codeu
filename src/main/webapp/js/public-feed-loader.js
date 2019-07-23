  // Fetch messages and add them to the page.
  function fetchMessages() {
    const url = '/feed';
    fetch(url).then((response) => {
      return response.json();
    }).then((messages) => {
      const messageContainer = document.getElementById('message-container');
      if(messages.length == 0){
       messageContainer.innerHTML = '<p>There are no posts yet.</p>';
      }
      else{
       messageContainer.innerHTML = '';  
      }
      messages.forEach((message) => {  
       const messageDiv = buildMessageDiv(message);
       messageContainer.appendChild(messageDiv);
      });
    });
  }
  
  function buildMessageDiv(message) {
    const usernameDiv = document.createElement('div');
    usernameDiv.classList.add("left-align");
    usernameDiv.appendChild(document.createTextNode(message.user));
   
    const timeDiv = document.createElement('div');
    timeDiv.classList.add('right-align');
    timeDiv.appendChild(document.createTextNode(new Date(message.timestamp)));
   
    const headerDiv = document.createElement('div');
    headerDiv.classList.add('message-header');
    headerDiv.appendChild(usernameDiv);
    headerDiv.appendChild(timeDiv);
   
    const bodyDiv = document.createElement('div');
    bodyDiv.classList.add('message-body');
    bodyDiv.innerHTML = message.text;
   
    const messageDiv = document.createElement('div');
    messageDiv.classList.add("message-div");
    messageDiv.appendChild(headerDiv);
    messageDiv.appendChild(bodyDiv);
	
	/*adding comment feature*/
	addCommentsButton(messageDiv, message);
   
    return messageDiv;
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
		const user = "";
		commentForm.setAttribute('action',"/comments?user="+user+"&commenter="+commenter+"&messageId="+message.id);
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
  
  // Fetch data and populate the UI of the page.
  function buildUI() {
	addLoginOrLogoutLinkToNavigation();
    fetchMessages();
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