/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.quickchatassignment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Message class (Part 2).
 *
 * @author Student
 */
public class MessageTest {

    // Test data from the assignment
    private final String message1 = "Hi Mike, can you join us for dinner tonight?";
    private final String message2 = "Hi Keegan, did you receive the payment?";

    @BeforeEach
    public void setUp() {
        Message.resetAll();   // start every test with empty lists
    }

    // --- Message length ---
    @Test
    public void testMessageLengthSuccess() {
        assertEquals("Message ready to send.", Message.checkMessageLength(message1));
    }

    @Test
    public void testMessageLengthFailure() {
        String longMessage = "a".repeat(260);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength(longMessage));
    }

    // --- Recipient number ---
    @Test
    public void testRecipientCorrect() {
        assertEquals("Cell phone number successfully captured.",
                Message.checkRecipientCell("+27718693002"));
    }

    @Test
    public void testRecipientIncorrect() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                Message.checkRecipientCell("08575975889"));
    }

    // --- Message hash ---
    @Test
    public void testMessageHashMessage1() {
        Message m = new Message("0012345678", 0, "+27718693002", message1);
        assertEquals("00:0:HITONIGHT", m.createMessageHash());
    }

    @Test
    public void testMessageHashLoop() {
        String[] ids = {"0012345678", "0498765432"};
        String[] texts = {message1, message2};
        String[] expected = {"00:0:HITONIGHT", "04:1:HIPAYMENT"};

        for (int i = 0; i < texts.length; i++) {
            Message m = new Message(ids[i], i, "+27718693002", texts[i]);
            assertEquals(expected[i], m.createMessageHash());
        }
    }

    // --- Message ID ---
    @Test
    public void testMessageIDCreated() {
        String id = Message.generateMessageID();
        Message m = new Message(id, 0, "+27718693002", message1);

        assertEquals(10, id.length());
        assertTrue(m.checkMessageID());
        assertEquals("Message ID generated: " + id, "Message ID generated: " + m.getMessageID());
    }

    @Test
    public void testMessageIDTooLong() {
        Message m = new Message("12345678901", 0, "+27718693002", message1);
        assertFalse(m.checkMessageID());
    }

    // --- Send, disregard, store ---
    @Test
    public void testSendMessage() {
        Message m = new Message("0012345678", 0, "+27718693002", message1);
        assertEquals("Message successfully sent.", m.sentMessage(1));
    }

    @Test
    public void testDisregardMessage() {
        Message m = new Message("0498765432", 1, "08575975889", message2);
        assertEquals("Press 0 to delete the message.", m.sentMessage(2));
    }

    @Test
    public void testStoreMessage() {
        Message m = new Message("0012345678", 0, "+27718693002", message1);
        assertEquals("Message successfully stored.", m.sentMessage(3));
    }

    // --- Total messages ---
    @Test
    public void testTotalMessagesSent() {
        Message m1 = new Message("0012345678", 0, "+27718693002", message1);
        Message m2 = new Message("0498765432", 1, "08575975889", message2);

        m1.sentMessage(1);   // sent
        m2.sentMessage(2);   // disregarded, so not counted

        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    public void testPrintMessages() {
        Message m1 = new Message("0012345678", 0, "+27718693002", message1);
        m1.sentMessage(1);
        assertTrue(Message.printMessages().contains("00:0:HITONIGHT"));
    }
}