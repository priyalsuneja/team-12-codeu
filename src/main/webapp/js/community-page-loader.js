	  /*list of username+aboutMe for all charities*/
	  var userNameAndAboutList = [];  

	 /** Fetches users and adds them to the page. */
      function fetchUserList() 
      {
		userNameAndAboutList = []; 
        const url = '/user-list';
        fetch(url).then((response) => {
          return response.json();
        }).then((users) => {
          const list = document.getElementById('list');
          list.innerHTML = '';
		  
          /*separating user link and about me section by "|" */
          users.forEach((user) => {
            let endOfUsernameindex = user.indexOf("|");
            const userListItem = buildUserListItem(user.substring(0, endOfUsernameindex), user.substring(endOfUsernameindex+1));
            list.appendChild(userListItem);
			userNameAndAboutList.push(user);
          });
        });
      }

      /**
       * Builds a list element that contains:
       * a link to a user page, e.g. and 
       * displays user "about me" text if posted by user
       * example:
       * <li><a href="/user-page.html?user=test@example.com">test@example.com</a>
       * <textarea readonly = 'readonly'> about me <textarea></li>
	   * param user = test@example.com (String)
	   * param aboutMe = about me text (String)
       */
      function buildUserListItem(user, aboutMe) 
      {
        const userListItem = document.createElement('li');
		
        const userLink = document.createElement('a');
        userLink.setAttribute('href', '/user-page.html?user=' + user);
        userLink.appendChild(document.createTextNode(user));
        userListItem.appendChild(userLink);
        userListItem.appendChild(document.createElement('br'));
		
        if(aboutMe)
        {
          const aboutMeDiv = document.createElement('Div');
          aboutMeDiv.innerHTML = aboutMe;
		  aboutMeDiv.id = "about-me-div";
          userListItem.appendChild(aboutMeDiv);
        }
        return userListItem;
      }
	  
	  /*filers users by key word in their name or aboutMe description*/
	  function filterUsers() {
		const keyword = document.getElementById('search-text').value;
		const listul = document.getElementById('list');
        listul.innerHTML = '';
		var filteredList = [];
		
		if(userNameAndAboutList.length!=0)
		{
           if(keyword=="" || keyword===undefined)
		   {
             fetchUserList();
		   }
		   else
		   {
             /*make a list of filtered users*/
             for(var i = 0; i<userNameAndAboutList.length; i++) {
               if(userNameAndAboutList[i].toLowerCase().includes(keyword.toLowerCase()))
			   {
                 let endOfUsernameindex = userNameAndAboutList[i].indexOf("|");
                 const userListItem = buildUserListItem(userNameAndAboutList[i].substring(0, endOfUsernameindex), userNameAndAboutList[i].substring(endOfUsernameindex+1));
                 listul.appendChild(userListItem);
			   }
			 }			 
		   }   
		}
	  }

      /** Fetches data and populates the UI of the page. */
      function buildUI() 
      {
       fetchUserList();
      }
	  

	  
