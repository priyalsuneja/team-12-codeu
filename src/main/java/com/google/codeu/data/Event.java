package com.google.codeu.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** A single message posted by a user. */
public class Event {

  private UUID id;
  private String user;
  private String description;
  private long timestamp;
  private List <String> tags;
  private String title;
  private List<String> volunteerList = new ArrayList<String>();
  /**
   * Constructs a new {@link Message} posted by {@code user} with {@code text} content. Generates a
   * random ID and uses the current system time for the creation time.
   */
  public Event(List<String> tags,String user, String description,String title) {
    this(tags,UUID.randomUUID(), user, description, System.currentTimeMillis(),title);
  }

  public Event(List<String> tags,UUID id,String user, String description, long timestamp, String title) {
	this.tags = tags;
    this.id = id;
    this.user = user;
    this.description = description;
    this.timestamp = timestamp;
    this.title = title;
  }

/**
 * @return the id
 */
public UUID getId() {
	return id;
}

/**
 * @param id the id to set
 */
public void setId(UUID id) {
	this.id = id;
}

/**
 * @return the user
 */
public String getUser() {
	return user;
}

/**
 * @param user the user to set
 */
public void setUser(String user) {
	this.user = user;
}

/**
 * @return the description
 */
public String getDescription() {
	return description;
}

/**
 * @param description the description to set
 */
public void setDescription(String description) {
	this.description = description;
}

/**
 * @return the timestamp
 */
public long getTimestamp() {
	return timestamp;
}

/**
 * @param timestamp the timestamp to set
 */
public void setTimestamp(long timestamp) {
	this.timestamp = timestamp;
}

/**
 * @return the tags
 */
public List<String> getTags() {
	return tags;
}

/**
 * @param tags the tags to set
 */
public void setTags(List<String> tags) {
	this.tags = tags;
}


/**
 * @return the title
 */
public String getTitle() {
	return title;
}

/**
 * @param title the title to set
 */
public void setTitle(String title) {
	this.title = title;
}

public void addVolunteer(String user) {
	volunteerList.add(user);
}

/**
 * @param volunteerList the volunteerList to set
 */
public void setVolunteerList(List<String> volunteerList) {
	this.volunteerList = volunteerList;
}

public List<String> getVolunteerList(){
	return volunteerList;
}
}
