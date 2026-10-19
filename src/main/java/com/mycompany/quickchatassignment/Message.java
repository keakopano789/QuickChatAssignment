package com.mycompany.quickchatassignment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Message class for QuickChat (Part 2).
 * Holds one message and the methods that check, hash, send and store it.
 *
 * 
 *
 * @author Student
 */
public class Message {

    // Details of one message
    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String text;

    // Single-dimension arrays shared by all messages while the program runs
    private static final int MAX_MESSAGES = 100;
    private static final Message[] sentMessages = new Message[MAX_MESSAGES];
    private static final Message[] storedMessages = new Message[MAX_MESSAGES];
    private static int sentCount = 0;     // how many messages are in sentMessages
    private static int storedCount = 0;   // how many messages are in storedMessages
    private static final String FILE_NAME = "messages.json";

    // Constructor
    public Message(String messageID, int messageNumber, String recipient, String text) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.text = text;
    }

    // Creates a random ten-digit message ID
    public static String generateMessageID() {
        Random random = new Random();
        String id = "";
        for (int i = 0; i < 10; i++) {
            id = id + random.nextInt(10);
        }
        return id;
    }

    // Message ID must not be more than ten characters
    public boolean checkMessageID() {
        return messageID.length() <= 10;
    }

    // Recipient must start with the international code (+27) and have 9 digits after it
    public static String checkRecipientCell(String cell) {
        boolean valid = cell.length() == 12 && cell.startsWith("+27");

        // Every character after the "+" must be a digit
        for (int i = 1; valid && i < cell.length(); i++) {
            if (!Character.isDigit(cell.charAt(i))) {
                valid = false;
            }
        }

        if (valid) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    // Message must not be longer than 250 characters
    public static String checkMessageLength(String text) {
        if (text.length() <= 250) {
            return "Message ready to send.";
        } else {
            int extra = text.length() - 250;
            return "Message exceeds 250 characters by " + extra + "; please reduce the size.";
        }
    }

    // Hash = first two digits of ID : message number : FIRSTWORD + LASTWORD (all caps)
    public String createMessageHash() {
        String trimmed = text.trim();

        int firstSpace = trimmed.indexOf(' ');
        String firstWord;
        if (firstSpace == -1) {
            firstWord = trimmed;
        } else {
            firstWord = trimmed.substring(0, firstSpace);
        }

        int lastSpace = trimmed.lastIndexOf(' ');
        String lastWord = trimmed.substring(lastSpace + 1);

        String firstTwo = messageID.substring(0, 2);
        String words = removeSymbols(firstWord) + removeSymbols(lastWord);

        return (firstTwo + ":" + messageNumber + ":" + words).toUpperCase();
    }

    // Keeps only letters and digits (removes ? , ! . etc.)
    private String removeSymbols(String word) {
        String clean = "";
        for (int i = 0; i < word.length(); i++) {
            if (Character.isLetterOrDigit(word.charAt(i))) {
                clean = clean + word.charAt(i);
            }
        }
        return clean;
    }

    // 1 = send, 2 = disregard, 3 = store
    public String sentMessage(int choice) {
        if (choice == 1) {
            if (sentCount >= MAX_MESSAGES) {
                return "Message limit reached.";
            }
            sentMessages[sentCount] = this;
            sentCount++;
            return "Message successfully sent.";
        } else if (choice == 2) {
            return "Press 0 to delete the message.";
        } else if (choice == 3) {
            storeMessage();
            return "Message successfully stored.";
        } else {
            return "Invalid option.";
        }
    }

    // Saves all stored messages to a JSON file
    public void storeMessage() {
        if (storedCount >= MAX_MESSAGES) {
            System.out.println("Message limit reached.");
            return;
        }
        storedMessages[storedCount] = this;
        storedCount++;

        // Copy only the filled part of the array so no empty (null) spots are saved
        Message[] toSave = new Message[storedCount];
        for (int i = 0; i < storedCount; i++) {
            toSave[i] = storedMessages[i];
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            gson.toJson(toSave, writer);
        } catch (IOException e) {
            System.out.println("Could not save to the JSON file: " + e.getMessage());
        }
    }

    public String getMessageID() {
        return messageID;
    }

    // Full details in the required order: ID, Hash, Recipient, Message
    public String getDetails() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + createMessageHash()
                + "\nRecipient: " + recipient
                + "\nMessage: " + text;
    }

    // Returns all the messages sent while the program is running
    public static String printMessages() {
        String all = "";
        for (int i = 0; i < sentCount; i++) {
            all = all + sentMessages[i].getDetails() + "\n\n";
        }
        return all;
    }

    // Returns the total number of messages sent
    public static int returnTotalMessages() {
        return sentCount;
    }

    // Empties the arrays by resetting the counters (used by the unit tests)
    public static void resetAll() {
        sentCount = 0;
        storedCount = 0;
    }
}