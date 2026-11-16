package com.mycompany.quickchatassignment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Message class for QuickChat (Parts 2 and 3).
 * Holds one message and the methods that check, hash, send, store, search
 * and report on messages. Plain arrays are used (no ArrayList).
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

    // Arrays shared by all messages while the program runs
    private static final int MAX_MESSAGES = 100;
    private static final int MAX_ALL = MAX_MESSAGES * 3;   // sent + disregarded + stored
    private static final Message[] sentMessages = new Message[MAX_MESSAGES];
    private static final Message[] disregardedMessages = new Message[MAX_MESSAGES];
    private static final Message[] storedMessages = new Message[MAX_MESSAGES];
    // These two arrays are parallel: the same index belongs to the same message
    private static final String[] messageHashes = new String[MAX_ALL];
    private static final String[] messageIDs = new String[MAX_ALL];

    // Counters: how many items are filled in each array
    private static int sentCount = 0;
    private static int disregardedCount = 0;
    private static int storedCount = 0;
    private static int hashCount = 0;   // used for both the hash array and the ID array

    // Name of the JSON file (the unit tests change it so they do not touch the real file)
    private static String fileName = "messages.json";

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
            addHashAndID();
            return "Message successfully sent.";
        } else if (choice == 2) {
            // Part 3: keep the disregarded message in its own array
            if (disregardedCount < MAX_MESSAGES) {
                disregardedMessages[disregardedCount] = this;
                disregardedCount++;
                addHashAndID();
            }
            return "Press 0 to delete the message.";
        } else if (choice == 3) {
            storeMessage();
            return "Message successfully stored.";
        } else {
            return "Invalid option.";
        }
    }

    // Adds this message's hash and ID to the two parallel arrays
    private void addHashAndID() {
        if (hashCount < MAX_ALL) {
            messageHashes[hashCount] = createMessageHash();
            messageIDs[hashCount] = messageID;
            hashCount++;
        }
    }

    // Adds this message to the stored array and saves the stored messages to the JSON file
    public void storeMessage() {
        if (storedCount >= MAX_MESSAGES) {
            System.out.println("Message limit reached.");
            return;
        }
        storedMessages[storedCount] = this;
        storedCount++;
        addHashAndID();
        saveStoredToFile();
    }

    // Writes every stored message to the JSON file
    private static void saveStoredToFile() {
        // Copy only the filled part of the array so no empty (null) spots are saved
        Message[] toSave = new Message[storedCount];
        for (int i = 0; i < storedCount; i++) {
            toSave[i] = storedMessages[i];
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(toSave, writer);
        } catch (IOException e) {
            System.out.println("Could not save to the JSON file: " + e.getMessage());
        }
    }

    // Part 3: reads the JSON file into the stored array. Call it once when the program starts.
    // Returns how many messages were loaded.
    public static int loadStoredMessages() {
        File file = new File(fileName);
        if (!file.exists()) {
            return 0;
        }

        Gson gson = new Gson();
        try (FileReader reader = new FileReader(file)) {
            Message[] loaded = gson.fromJson(reader, Message[].class);
            if (loaded == null) {
                return 0;
            }

            int added = 0;
            for (int i = 0; i < loaded.length && storedCount < MAX_MESSAGES; i++) {
                if (loaded[i] != null) {
                    storedMessages[storedCount] = loaded[i];
                    storedCount++;
                    loaded[i].addHashAndID();
                    added++;
                }
            }
            return added;
        } catch (IOException | JsonParseException e) {
            System.out.println("Could not read the JSON file: " + e.getMessage());
            return 0;
        }
    }

    // ---------- Getters ----------
    public String getMessageID() {
        return messageID;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getText() {
        return text;
    }

    // Full details in the required order: ID, Hash, Recipient, Message
    public String getDetails() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + createMessageHash()
                + "\nRecipient: " + recipient
                + "\nMessage: " + text;
    }

    // ---------- Array getters (copies of the filled part only) ----------
    private static String[] copyTexts(Message[] list, int count) {
        String[] texts = new String[count];
        for (int i = 0; i < count; i++) {
            texts[i] = list[i].text;
        }
        return texts;
    }

    public static String[] getSentMessageTexts() {
        return copyTexts(sentMessages, sentCount);
    }

    public static String[] getDisregardedMessageTexts() {
        return copyTexts(disregardedMessages, disregardedCount);
    }

    public static String[] getStoredMessageTexts() {
        return copyTexts(storedMessages, storedCount);
    }

    public static String[] getMessageHashes() {
        String[] copy = new String[hashCount];
        for (int i = 0; i < hashCount; i++) {
            copy[i] = messageHashes[i];
        }
        return copy;
    }

    public static String[] getMessageIDs() {
        String[] copy = new String[hashCount];
        for (int i = 0; i < hashCount; i++) {
            copy[i] = messageIDs[i];
        }
        return copy;
    }

    // ---------- Part 3 features ----------

    // (a) Sender and recipient of every stored message
    // (the sender is the user who logged in, because messages only store the recipient)
    public static String storedSendersAndRecipients(String sender) {
        if (storedCount == 0) {
            return "No stored messages.";
        }
        String all = "";
        for (int i = 0; i < storedCount; i++) {
            all = all + "Sender: " + sender + " | Recipient: " + storedMessages[i].recipient + "\n";
        }
        return all;
    }

    // (b) The longest stored message
    public static String longestStoredMessage() {
        if (storedCount == 0) {
            return "No stored messages.";
        }
        int longestIndex = 0;
        for (int i = 1; i < storedCount; i++) {
            if (storedMessages[i].text.length() > storedMessages[longestIndex].text.length()) {
                longestIndex = i;
            }
        }
        return storedMessages[longestIndex].text;
    }

    // Looks for a message ID in the sent, stored and disregarded arrays
    private static Message findByMessageID(String id) {
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i].messageID.equals(id)) {
                return sentMessages[i];
            }
        }
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i].messageID.equals(id)) {
                return storedMessages[i];
            }
        }
        for (int i = 0; i < disregardedCount; i++) {
            if (disregardedMessages[i].messageID.equals(id)) {
                return disregardedMessages[i];
            }
        }
        return null;
    }

    // (c) Search by message ID - returns the message text
    public static String searchByMessageID(String id) {
        Message found = findByMessageID(id);
        if (found == null) {
            return "Message ID not found.";
        }
        return found.text;
    }

    // (c) Search by message ID - returns the recipient and the message (shown on screen)
    public static String displayByMessageID(String id) {
        Message found = findByMessageID(id);
        if (found == null) {
            return "Message ID not found.";
        }
        return "Recipient: " + found.recipient + "\nMessage: " + found.text;
    }

    // (d) All sent or stored messages for one recipient
    public static String[] searchByRecipient(String recipient) {
        String[] results = new String[MAX_MESSAGES * 2];
        int found = 0;

        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i].recipient.equals(recipient)) {
                results[found] = sentMessages[i].text;
                found++;
            }
        }
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i].recipient.equals(recipient)) {
                results[found] = storedMessages[i].text;
                found++;
            }
        }

        // Copy only the filled part so the caller does not get empty (null) spots
        String[] trimmed = new String[found];
        for (int i = 0; i < found; i++) {
            trimmed[i] = results[i];
        }
        return trimmed;
    }

    // (e) Delete a stored message using its hash
    public static String deleteByMessageHash(String hash) {
        int index = -1;
        for (int i = 0; i < storedCount && index == -1; i++) {
            if (storedMessages[i].createMessageHash().equalsIgnoreCase(hash)) {
                index = i;
            }
        }
        if (index == -1) {
            return "Message hash not found.";
        }

        String deletedText = storedMessages[index].text;
        String deletedHash = storedMessages[index].createMessageHash();

        // Move every message after it one step to the left, then shrink the array
        for (int i = index; i < storedCount - 1; i++) {
            storedMessages[i] = storedMessages[i + 1];
        }
        storedCount--;
        storedMessages[storedCount] = null;

        removeHashAndID(deletedHash);
        saveStoredToFile();   // keep the JSON file the same as the array

        return "Message: \"" + deletedText + "\" successfully deleted.";
    }

    // Removes one hash and its matching ID from the two parallel arrays
    private static void removeHashAndID(String hash) {
        int index = -1;
        for (int i = 0; i < hashCount && index == -1; i++) {
            if (messageHashes[i].equals(hash)) {
                index = i;
            }
        }
        if (index == -1) {
            return;
        }
        for (int i = index; i < hashCount - 1; i++) {
            messageHashes[i] = messageHashes[i + 1];
            messageIDs[i] = messageIDs[i + 1];
        }
        hashCount--;
        messageHashes[hashCount] = null;
        messageIDs[hashCount] = null;
    }

    // (f) Report of all sent messages: hash, recipient and message
    public static String sentReport() {
        String report = "=== SENT MESSAGES REPORT ===\n";
        if (sentCount == 0) {
            return report + "No sent messages.\n";
        }
        for (int i = 0; i < sentCount; i++) {
            report = report + "Message Hash: " + sentMessages[i].createMessageHash()
                    + "\nRecipient: " + sentMessages[i].recipient
                    + "\nMessage: " + sentMessages[i].text + "\n\n";
        }
        return report;
    }

    // (f) Report with the full details of all stored messages
    public static String storedReport() {
        String report = "=== STORED MESSAGES REPORT ===\n";
        if (storedCount == 0) {
            return report + "No stored messages.\n";
        }
        for (int i = 0; i < storedCount; i++) {
            report = report + storedMessages[i].getDetails() + "\n\n";
        }
        return report;
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

    // Used by the unit tests: use a different JSON file so the real one is not changed
    public static void setFileName(String newFileName) {
        fileName = newFileName;
    }

    // Empties the arrays by resetting the counters (used by the unit tests)
    public static void resetAll() {
        sentCount = 0;
        disregardedCount = 0;
        storedCount = 0;
        hashCount = 0;
    }
}