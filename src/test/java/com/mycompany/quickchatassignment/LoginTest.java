/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.quickchatassignment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Student
 */
public class LoginTest {
    
   @Test
    public void testCheckUserNameCorrect() {
        Login app = new Login();
        assertTrue(app.checkUserName("kyl_1"));
    }

    @Test
    public void testCheckUserNameIncorrect() {
        Login app = new Login();
        assertFalse(app.checkUserName("kyle!!!!!!"));
    }

    @Test
    public void testCheckPasswordComplexityCorrect() {
        Login app = new Login();
        assertTrue(app.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void testCheckPasswordComplexityIncorrect() {
        Login app = new Login();
        assertFalse(app.checkPasswordComplexity("password"));
    } 

 @Test
    public void testCheckCellPhoneCorrect() {
        Login app = new Login();
        assertTrue(app.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    public void testCheckCellPhoneIncorrect() {
        Login app = new Login();
        assertFalse(app.checkCellPhoneNumber("08966553"));
    }

    @Test
    public void testRegisterUserSuccess() {
        Login app = new Login();
        String result = app.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertEquals("User registered successfully!", result);
    }

    @Test
    public void testLoginSuccess() {
        Login app = new Login();
        app.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(app.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginFail() {
        Login app = new Login();
        app.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(app.loginUser("wrong", "wrong"));
    }
}