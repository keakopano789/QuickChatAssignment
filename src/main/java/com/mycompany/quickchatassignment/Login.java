/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quickchatassignment;

/**
 *
 * @author Student
 */
public class Login {
    private String username;
    private String password;
    private String cellNumber;
    //  Check Username: must contain '_' and be <= 5 characters
    public boolean checkUserName(String username) {
        boolean hasUnderscore = false;
        for (int i = 0; i < username.length(); i++) {
            if (username.charAt(i) == '_') {
                hasUnderscore = true;
                break;
            }
        }
        return hasUnderscore && username.length() <= 5;
    }

    //  Check Password Complexity
    public boolean checkPasswordComplexity(String password) {
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        // Loop through each character
        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i); 
            if (Character.isUpperCase(ch)) {
                hasUpper = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(ch)) {
                hasSpecial = true;
            }
        }

        // Final return statement
        return password.length() >= 8 && hasUpper && hasDigit && hasSpecial;
    }

    // Check Cell Phone Number (+27 followed by 9 digits)
    public boolean checkCellPhoneNumber(String cellNumber) {
        // Must start with +27 and be exactly 12 characters long (+27 + 9 digits)
        if (!cellNumber.startsWith("+27")) {
            return false;
        }
        return false;
}
//  Register User
    public String registerUser(String username, String password, String cellNumber) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber(cellNumber)) {
            return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        }

        this.username = username;
        this.password = password;

        return "User registered successfully!";
    }

    //  Login User
    public boolean loginUser(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    // Return Login Status
    public String returnLoginStatus(boolean loginSuccess, String firstName, String lastName) {
        if (loginSuccess) {
            return "Welcome " + firstName + " " + lastName + ", it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}