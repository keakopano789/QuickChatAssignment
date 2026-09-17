/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.quickchatassignment;

import java.util.Scanner;

/**
 *
 * @author Student
 */
public class QuickChatAssignment {

    public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
        Login app = new Login();  // Create an object of Login class

      // === Ask for name and surname ===
    System.out.print("Enter your first name: ");
    String firstName = sc.nextLine();

    System.out.print("Enter your surname: ");
    String surname = sc.nextLine();

    System.out.println("Welcome " + firstName + " " + surname + "!");
    
        // === Registration Section ===
         // This section validates the username input
String username;
do {
    System.out.print("Enter username: ");
    username = sc.nextLine();
    if(!app.checkUserName(username)) {
        System.out.println("Username is not correctly formatted; please ensure that your username is contains an underscore and is no more than five characters long");
        //This loop ensures the username is valid before continuing
    }
} while(!app.checkUserName(username));
System.out.println("Username successfully captured");

  

// Password input loop
//This loop ensures the password meets complexity rules
String password;
do {
    System.out.print("Enter password: ");
    password = sc.nextLine();
    if(!app.checkPasswordComplexity(password)){
        System.out.println("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
    }
} while(!app.checkPasswordComplexity(password));
System.out.println("Password successfully captured");

// Variable renamed for clarity
String cellNumber;
do {
    System.out.print("Enter cell number ");
    cellNumber = sc.nextLine();
    if(!app.checkCellPhoneNumber(cellNumber)){
        System.out.println("Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.");
    }
} while(!app.checkCellPhoneNumber(cellNumber));
System.out.println("Cell number successfully captured");

        // Call registerUser() and display result
        System.out.println(app.registerUser(username, password, cellNumber));
        

        // === Login Section ===
        System.out.println("\n=== User Login ===");
        System.out.print("Enter username: ");
        String lu = sc.nextLine();

        System.out.print("Enter password: ");
        String lp = sc.nextLine();

        // Verify login
        boolean success = app.loginUser(lu, lp);

        // Show login status message
        System.out.println(app.returnLoginStatus(success, "Kopano" , "Student"));   
    }
}
