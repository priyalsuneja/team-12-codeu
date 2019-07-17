/**
 * http://usejsdoc.org/
 */
  // Fetch events and add them to the page.
  function fetchVolunteers() {
    const url = '/eventDisplay';
    fetch(url).then((response) => {
      return response.json();
    }).then((events) => {
      const eventContainer = document.getElementById('display-charities');
      if(events.length == 0){
       eventContainer.innerHTML = '<p>There are no events yet.</p>';
      }
      else{
       eventContainer.innerHTML = '';  
      }
      events.forEach((event) => {  
       console.log(event.volunteerList.length);
       const eventDiv = buildEventDiv(event);
       eventContainer.appendChild(eventDiv);
      });
    });
  }
  
  function buildEventDiv(event) {
    const usernameDiv = document.createElement('div');
    usernameDiv.classList.add("left-align");
    usernameDiv.appendChild(document.createTextNode(event.user));
   
    const idDiv = document.createElement('div');
    idDiv.classList.add('right-align');
    idDiv.appendChild(document.createTextNode(event.id));
    
    const headerDiv = document.createElement('div');
    headerDiv.classList.add('message-header');
    headerDiv.appendChild(usernameDiv);
    headerDiv.appendChild(idDiv);
    
    const titleDiv = document.createElement('div');
    titleDiv.classList.add('message-title');
    titleDiv.innerHTML = "Event Title: " + event.title;
    
    const volunteerDiv = document.createElement('div');
    volunteerDiv.classList.add('message-volunteer');
    volunteerDiv.innerHTML = "Volunteers email: ";
    
    const messageDiv = document.createElement('div');
    messageDiv.classList.add("message-div");
    messageDiv.appendChild(headerDiv);
    messageDiv.appendChild(titleDiv);
    messageDiv.appendChild(volunteerDiv);
    var list = event.volunteerList;
    //console.log(list.length)
    if(list.length == 0){
    	const volunteerDiv = document.createElement('div');
    	volunteerDiv.classList.add('message-volunteer');
    	volunteerDiv.innerHTML = "(No Volunteers Yet)";
    	messageDiv.appendChild(volunteerDiv);
    }
    for(var i=0;i<list.length;++i){
    	const volunteerDiv = document.createElement('div');
    	volunteerDiv.classList.add('message-volunteer');
    	volunteerDiv.innerHTML = (i+1) + ") " + list[i];
    	messageDiv.appendChild(volunteerDiv);
    }
    return messageDiv;
  }
  
  // Fetch data and populate the UI of the page.
  function buildUI() {
    fetchVolunteers();
  }