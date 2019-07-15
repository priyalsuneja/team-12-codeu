/**
 * http://usejsdoc.org/
 */
  // Fetch events and add them to the page.
  function fetchEvents() {
    const url = '/eventDisplay';
    fetch(url).then((response) => {
      return response.json();
    }).then((events) => {
      const eventContainer = document.getElementById('message-container');
      if(events.length == 0){
       eventContainer.innerHTML = '<p>There are no events yet.</p>';
      }
      else{
       eventContainer.innerHTML = '';  
      }
      eventss.forEach((event) => {  
       const eventDiv = buildEventDiv(event);
       eventContainer.appendChild(eventDiv);
      });
    });
  }
  
  function buildEventDiv(event) {
    const usernameDiv = document.createElement('div');
    usernameDiv.classList.add("left-align");
    usernameDiv.appendChild(document.createTextNode(event.user));
   
    const timeDiv = document.createElement('div');
    timeDiv.classList.add('right-align');
    timeDiv.appendChild(document.createTextNode(new Date(event.timestamp)));
   
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
   
    return messageDiv;
  }
  
  // Fetch data and populate the UI of the page.
  function buildUI() {
    fetchEvents();
  }