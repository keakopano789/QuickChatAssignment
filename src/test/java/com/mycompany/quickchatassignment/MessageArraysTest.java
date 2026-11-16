package com.mycompany.quickchatassignment;

import java.io.File;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Part 3 (arrays, search, delete, report and reading the JSON file).
 * The test data comes from the assignment brief.
 *
 * @author Student
 */
public class MessageArraysTest {

    private static final String TEST_FILE = "test-messages.json";

    // Test data texts from the assignment
    private final String text1 = "Did you get the cake?";
    private final String text2 = "Where are you? You are late! I have asked you to be on time.";
    private final String text3 = "Yohoooo, I am at your gate.";
    private final String text4 = "It is dinner time!";
    private final String text5 = "Ok, I am leaving without you.";

    @BeforeEach
    public void setUp() {
        Message.setFileName(TEST_FILE);   // do not touch the real messages.json
        new File(TEST_FILE).delete();     // start every test with no file
        Message.resetAll();               // start every test with empty arrays
    }

    @AfterAll
    public static void cleanUp() {
        new File(TEST_FILE).delete();
    }

    // Fills the arrays with the five test messages (nothing is hard-coded in the app itself)
    private void loadTestData() {
        new Message("1000000001", 0, "+27834557896", text1).sentMessage(1);   // Sent
        new Message("2000000002", 1, "+27838884567", text2).sentMessage(3);   // Stored
        new Message("3000000003", 2, "+27834484567", text3).sentMessage(2);   // Disregard
        new Message("0838884567", 3, "0838884567", text4).sentMessage(1);     // Sent
        new Message("5000000005", 4, "+27838884567", text5).sentMessage(3);   // Stored
    }

    // --- Arrays ---
    @Test
    public void testSentMessagesArrayPopulated() {
        loadTestData();
        assertArrayEquals(new String[]{"Did you get the cake?", "It is dinner time!"},
                Message.getSentMessageTexts());
    }

    @Test
    public void testDisregardedMessagesArrayPopulated() {
        loadTestData();
        assertArrayEquals(new String[]{"Yohoooo, I am at your gate."},
                Message.getDisregardedMessageTexts());
    }

    @Test
    public void testStoredMessagesArrayPopulated() {
        loadTestData();
        assertArrayEquals(new String[]{text2, text5}, Message.getStoredMessageTexts());
    }

    @Test
    public void testMessageHashArrayPopulated() {
        loadTestData();
        String[] expected = {"10:0:DIDCAKE", "20:1:WHERETIME", "30:2:YOHOOOOGATE",
            "08:3:ITTIME", "50:4:OKYOU"};
        assertArrayEquals(expected, Message.getMessageHashes());
    }

    @Test
    public void testMessageIDArrayPopulated() {
        loadTestData();
        String[] expected = {"1000000001", "2000000002", "3000000003", "0838884567", "5000000005"};
        assertArrayEquals(expected, Message.getMessageIDs());
    }

    // --- Longest message ---
    @Test
    public void testLongestMessage() {
        loadTestData();
        assertEquals(text2, Message.longestStoredMessage());
    }

    @Test
    public void testLongestMessageNoStoredMessages() {
        assertEquals("No stored messages.", Message.longestStoredMessage());
    }

    // --- Search for a message ID ---
    @Test
    public void testSearchMessageIDFound() {
        loadTestData();
        assertEquals("It is dinner time!", Message.searchByMessageID("0838884567"));
    }

    @Test
    public void testSearchMessageIDNotFound() {
        loadTestData();
        assertEquals("Message ID not found.", Message.searchByMessageID("9999999999"));
    }

    @Test
    public void testDisplayByMessageIDShowsRecipientAndMessage() {
        loadTestData();
        assertEquals("Recipient: 0838884567\nMessage: It is dinner time!",
                Message.displayByMessageID("0838884567"));
    }

    // --- Search for a recipient ---
    @Test
    public void testSearchByRecipientFound() {
        loadTestData();
        assertArrayEquals(new String[]{text2, text5}, Message.searchByRecipient("+27838884567"));
    }

    @Test
    public void testSearchByRecipientNotFound() {
        loadTestData();
        assertEquals(0, Message.searchByRecipient("+27000000000").length);
    }

    // --- Delete using the message hash ---
    @Test
    public void testDeleteMessageByHash() {
        loadTestData();
        assertEquals("Message: \"" + text2 + "\" successfully deleted.",
                Message.deleteByMessageHash("20:1:WHERETIME"));

        // The stored array, hash array and ID array must all be updated
        assertArrayEquals(new String[]{text5}, Message.getStoredMessageTexts());
        assertEquals(4, Message.getMessageHashes().length);
        assertEquals(4, Message.getMessageIDs().length);
        assertEquals("Message ID not found.", Message.searchByMessageID("2000000002"));
    }

    @Test
    public void testDeleteMessageHashNotFound() {
        loadTestData();
        assertEquals("Message hash not found.", Message.deleteByMessageHash("00:0:NOTHING"));
        assertEquals(2, Message.getStoredMessageTexts().length);
    }

    // --- Reports ---
    @Test
    public void testSentReportShowsHashRecipientAndMessage() {
        loadTestData();
        String report = Message.sentReport();
        assertTrue(report.contains("10:0:DIDCAKE"));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains(text1));
        assertTrue(report.contains("08:3:ITTIME"));
        assertTrue(report.contains(text4));
        assertFalse(report.contains(text2));   // stored messages are not in the sent report
    }

    @Test
    public void testStoredReportShowsFullDetails() {
        loadTestData();
        String report = Message.storedReport();
        assertTrue(report.contains("Message ID: 2000000002"));
        assertTrue(report.contains("Message Hash: 20:1:WHERETIME"));
        assertTrue(report.contains("Recipient: +27838884567"));
        assertTrue(report.contains(text2));
        assertTrue(report.contains(text5));
    }

    @Test
    public void testStoredSenderAndRecipient() {
        loadTestData();
        String result = Message.storedSendersAndRecipients("Kyle Smith");
        assertTrue(result.contains("Sender: Kyle Smith | Recipient: +27838884567"));
    }

    // --- Reading the JSON file into the stored array ---
    @Test
    public void testReadJsonFileIntoArray() {
        loadTestData();            // storing the messages also writes the JSON file
        Message.resetAll();        // empty the arrays, as if the program was just started

        int loaded = Message.loadStoredMessages();

        assertEquals(2, loaded);
        assertArrayEquals(new String[]{text2, text5}, Message.getStoredMessageTexts());
    }

    @Test
    public void testReadJsonFileMissing() {
        assertEquals(0, Message.loadStoredMessages());
    }
}