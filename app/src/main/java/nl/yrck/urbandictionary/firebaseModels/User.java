package nl.yrck.urbandictionary.firebaseModels;

public class User {

    public String username;
    public String email;

    public User() {
        // Empty constructor needed for Firebase
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}