package newscollector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// The User class represents a system user with associated attributes and methods for managing user preferences and registration information.

public class User {
    private String userId; // Unique identifier for the user
    private String userName; // Username chosen by the user
    private String userPassword; // Password for user authentication
    private Date registrationDate; // Date when the user registered
    private List<String> preferences; // List of preferred article categories

    // Constructor to initialize a new User object.
    public User(String userId, String userName, String userPassword) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.preferences = new ArrayList<>();
        this.registrationDate = new Date(); // Set registration date to the current date
    }

    // Factory method to create a new User object with a null userId.
    public static User createUser(String userName, String userPassword) {
        return new User(null, userName, userPassword);
    }

    // Getter and Setter methods

    public String getUserId() {
        return userId;
    }

    public Date getRegDate() {
        return registrationDate;
    }

    public void setRegDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return userName;
    }

    public String getPassword() {
        return userPassword;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public String getPrimarycategoryName() {
        return preferences.isEmpty() ?"General" : preferences.get(0);
    }

    // Updates the user's list of preferences.
    public void updatePreferences(List<String> preferences) {
        this.preferences.clear();
        this.preferences.addAll(preferences);
    }

}
