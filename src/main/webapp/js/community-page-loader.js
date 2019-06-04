      /** Fetches users and adds them to the page. */
      function fetchUserList() 
      {
        const url = '/user-list';
        fetch(url).then((response) => {
          return response.json();
        }).then((users) => {
          const list = document.getElementById('list');
          list.innerHTML = '';
		  
          /*separating user link and about me section by " " */
          users.forEach((user) => {
            let endOfUsernameindex = user.indexOf(" ");
            const userListItem = buildUserListItem(user.substring(0, endOfUsernameindex), user.substring(endOfUsernameindex+1));
            list.appendChild(userListItem);
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
       */
      function buildUserListItem(user, aboutMe) 
      {
        const userListItem = document.createElement('li');
		
        const userLink = document.createElement('a');
        userLink.setAttribute('href', '/user-page.html?user=' + user);
        userLink.appendChild(document.createTextNode(user));
        userListItem.appendChild(userLink);
        userListItem.appendChild(document.createElement('br'));
		
        if(aboutMe!==null && aboutMe!=="")
        {
          const aboutMeText = document.createElement('textarea');
          aboutMeText.setAttribute('readonly', 'readonly');
          aboutMeText.innerHTML = aboutMe;
          userListItem.appendChild(aboutMeText);
        }
        return userListItem;
      }

      /** Fetches data and populates the UI of the page. */
      function buildUI() 
      {
       fetchUserList();
      }
