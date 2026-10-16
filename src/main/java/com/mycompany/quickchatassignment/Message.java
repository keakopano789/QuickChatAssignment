package com.mycompany.quickchatassignment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Message class for QuickChat (Part 2).
 * Holds one message and the methods that check, hash, send and store it.
 *
 * References:
 * Google (n.d.) Gson User Guide. Available at:
 * https://github.com/google/gson/blob/main/UserGuide.md
 * (Gson is used to write the stored messages to a JSON file.)
 *
 * @author Student
 */
public class Message {

    // Details of one message
    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String text;

    // Shared by all messages while the program runs
    private static final ArrayList<Message> sentMessages = new ArrayList<>();
    private static final ArrayList<Message> storedMessages = new ArrayList<>();
    private static int totalSent = 0;
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
            sentMessages.add(this);
            totalSent++;
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
        storedMessages.add(this);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            gson.toJson(storedMessages, writer);
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
        for (int i = 0; i < sentMessages.size(); i++) {
            all = all + sentMessages.get(i).getDetails() + "\n\n";
        }
        return all;
    }

    // Returns the total number of messages sent
    public static int returnTotalMessages() {
        return totalSent;
    }

    // Clears the lists and the counter (used by the unit tests)
    public static void resetAll() {
        sentMessages.clear();
        storedMessages.clear();
        totalSent = 0;
    }
}