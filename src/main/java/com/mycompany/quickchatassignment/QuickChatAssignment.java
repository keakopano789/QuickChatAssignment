/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.quickchatassignment;

import java.util.Scanner;

/**
 * QuickChat - Part 1 (register and login) and Part 2 (send messages).
 *
 * @author Student
 */
public class QuickChatAssignment {

    // Reads a whole number from the user and keeps asking until it is valid
    private static int readNumber(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Login app = new Login();

        // === Ask for name and surname ===
        System.out.print("Enter your first name: ");
        String firstName = sc.nextLine();

        System.out.print("Enter your surname: ");
        String surname = sc.nextLine();
        System.out.println("Welcome " + firstName + " " + surname + "!");

        // === Registration Section ===
        String username;
        do {
            System.out.print("Enter username: ");
            username = sc.nextLine();
            if (!app.checkUserName(username)) {
                System.out.println("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
            }
        } while (!app.checkUserName(username));
        System.out.println("Username successfully captured.");

        String password;
        do {
            System.out.print("Enter password: ");
            password = sc.nextLine();
            if (!app.checkPasswordComplexity(password)) {
                System.out.println("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
            }
        } while (!app.checkPasswordComplexity(password));
        System.out.println("Password successfully captured.");

        String cellNumber;
        do {
            System.out.print("Enter cell number: ");
            cellNumber = sc.nextLine();
            if (!app.checkCellPhoneNumber(cellNumber)) {
                System.out.println("Cell phone number incorrectly formatted or does not contain international code.");
            }
        } while (!app.checkCellPhoneNumber(cellNumber));
        System.out.println("Cell phone number successfully added.");

        System.out.println(app.registerUser(username, password, cellNumber));

        // === Login Section ===
        System.out.println("\n=== User Login ===");
        System.out.print("Enter username: ");
        String loginUsername = sc.nextLine();

        System.out.print("Enter password: ");
        String loginPassword = sc.nextLine();

        boolean success = app.loginUser(loginUsername, loginPassword);
        System.out.println(app.returnLoginStatus(success, firstName, surname));

        // Users can only use QuickChat if they logged in successfully
        if (!success) {
            return;
        }

        // === Part 2: QuickChat ===
        System.out.println("\nWelcome to QuickChat.");

        int numMessages = readNumber(sc, "How many messages do you want to enter? ");
        int messagesEntered = 0;

        int option = 0;
        // The application runs until the user chooses 3 (Quit)
        while (option != 3) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            option = readNumber(sc, "Choose an option: ");

            if (option == 1) {
                if (messagesEntered >= numMessages) {
                    System.out.println("You have already entered all " + numMessages + " messages.");
                }

                // The for loop only allows the set number of messages
                for (int i = messagesEntered; i < numMessages; i++) {
                    System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");

                    // Recipient
                    String recipient;
                    String recipientResult;
                    do {
                        System.out.print("Enter recipient cell number (e.g. +27718693002): ");
                        recipient = sc.nextLine();
                        recipientResult = Message.checkRecipientCell(recipient);
                        System.out.println(recipientResult);
                    } while (!recipientResult.equals("Cell phone number successfully captured."));

                    // Message text
                    String text;
                    do {
                        System.out.print("Enter your message: ");
                        text = sc.nextLine();
                        if (text.length() > 250) {
                            System.out.println("Please enter a message of less than 250 characters.");
                            System.out.println(Message.checkMessageLength(text));
                        }
                    } while (text.length() > 250);
                    System.out.println(Message.checkMessageLength(text));

                    // Create the message (the loop counter is the message number)
                    String id = Message.generateMessageID();
                    Message message = new Message(id, i, recipient, text);
                    System.out.println("Message ID generated: " + id);

                    // Send, disregard or store
                    int choice;
                    do {
                        System.out.println("1) Send Message");
                        System.out.println("2) Disregard Message");
                        System.out.println("3) Store Message to send later");
                        choice = readNumber(sc, "Choose an option: ");
                    } while (choice < 1 || choice > 3);

                    System.out.println(message.sentMessage(choice));

                    if (choice == 1) {
                        System.out.println("\n" + message.getDetails());
                    } else if (choice == 2) {
                        String answer = sc.nextLine();
                        if (answer.trim().equals("0")) {
                            System.out.println("Message deleted.");
                        } else {
                            System.out.println("Message was not sent.");
                        }
                    }
                    messagesEntered++;
                }

                System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());

            } else if (option == 2) {
                System.out.println("Coming Soon.");
            } else if (option == 3) {
                System.out.println("Goodbye!");
            } else {
                System.out.println("Please choose 1, 2 or 3.");
            }
        }
    }
}