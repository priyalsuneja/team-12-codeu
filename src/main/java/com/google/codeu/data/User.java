package com.google.codeu.data;

public class User {

    public final static long DONOR_TYPE = 0;
    public final static long CHARITY_TYPE = 1;
    public final static long UNSET_TYPE = -1;
    
    private String email;
    private String aboutMe;
    private Long type;
    public User(String email, String aboutMe, long type) {
        this.email = email;
        this.aboutMe = aboutMe;
        this.type = type;
    }
    
    public User(String email, String aboutMe) {
        this(email, aboutMe, User.UNSET_TYPE);
    }

    public String getEmail(){
        return email;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public Long getType() {
        return type;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public void setType(Long type) {
        this.type = type;
    }
    
}