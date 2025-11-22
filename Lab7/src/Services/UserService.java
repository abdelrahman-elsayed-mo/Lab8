/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Services;

import BackEnd.Instructor;
import BackEnd.Student;
import BackEnd.User;
import Utils.InputValidator;
import Utils.PasswordHasher;
import databse.JsonDatabaseManager;


public class UserService {

    private JsonDatabaseManager dbManager;
    private User currentUser;

    public UserService(JsonDatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.currentUser = null;
    }

public boolean signup(String username, String email, String password, String role) {
    if (!InputValidator.isRequiredFieldValid(username) ||
        !InputValidator.isRequiredFieldValid(password) ||
        !InputValidator.isValidEmail(email)) {
        return false;
    }

  
    if (dbManager.getUserByEmail(email) != null) {
        
    }
    if (dbManager.getUserByUsername(username) != null) {
        return false; 
    }

    String passwordHash = PasswordHasher.hash(password);
    String userId = dbManager.generateNewUserId();
    User user;

    if ("Student".equalsIgnoreCase(role)) {
        user = new Student(userId, username, email, passwordHash);
    } else if ("Instructor".equalsIgnoreCase(role)) {
        user = new Instructor(userId, username, email, passwordHash);
    } else {
        return false;
    }

    return dbManager.saveUser(user);
}

    public User login(String email, String password) {
        if (!InputValidator.isValidEmail(email) || !InputValidator.isRequiredFieldValid(password)) {
            return null;
        }

        User user = (User) dbManager.getUserByEmail(email);
        if (user == null) {
            return null;
        }

        String passwordHash = PasswordHasher.hash(password);
        if (user.getPasswordHash().equals(passwordHash)) {
            this.currentUser = user;
            return user;
        }

        return null;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }
}
