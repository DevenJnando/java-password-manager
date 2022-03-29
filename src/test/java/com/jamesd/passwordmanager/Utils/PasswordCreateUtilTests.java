package com.jamesd.passwordmanager.Utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordCreateUtilTests {

    private static final List<Integer> upperCaseChars = PasswordCreateUtil.populateList(65, 91);
    private static final List<Integer> lowerCaseChars = PasswordCreateUtil.populateList(97, 123);
    private static final List<Integer> numericalChars = PasswordCreateUtil.populateList(48, 58);
    private static final List<Integer> legalSpecialChars = Arrays.asList(33, 35, 36, 37, 38, 42, 63, 64, 95);

    @Test
    public void validateChars() {
        for(Integer charNo : upperCaseChars) {
            assertTrue(Character.isAlphabetic(charNo));
            assertTrue(Character.isUpperCase(charNo));
        }
        for(Integer charNo : lowerCaseChars) {
            assertTrue(Character.isAlphabetic(charNo));
            assertTrue(Character.isLowerCase(charNo));
        }
        for(Integer charNo : numericalChars) {
            assertTrue(Character.isDigit(charNo));
        }
        for(Integer charNo : legalSpecialChars) {
            assertFalse(Character.isAlphabetic(charNo));
            assertFalse(Character.isDigit(charNo));
            assertFalse(Character.isWhitespace(charNo));
        }
    }

    @Test
    public void validateFourRandomChars() {
        List<Integer> fourRandomUpperCaseChars = PasswordCreateUtil.fourRandomCharacters(upperCaseChars);
        List<Integer> fourRandomLowerCaseChars = PasswordCreateUtil.fourRandomCharacters(lowerCaseChars);
        List<Integer> fourRandomNumericalChars = PasswordCreateUtil.fourRandomCharacters(numericalChars);
        List<Integer> fourRandomSpecialChars = PasswordCreateUtil.fourRandomCharacters(legalSpecialChars);
        assertEquals(4, fourRandomUpperCaseChars.size());
        assertEquals(4, fourRandomLowerCaseChars.size());
        assertEquals(4, fourRandomNumericalChars.size());
        assertEquals(4, fourRandomSpecialChars.size());
        for(Integer charNo : fourRandomUpperCaseChars) {
            assertTrue(Character.isAlphabetic(charNo));
            assertTrue(Character.isUpperCase(charNo));
        }
        for(Integer charNo : fourRandomLowerCaseChars) {
            assertTrue(Character.isAlphabetic(charNo));
            assertTrue(Character.isLowerCase(charNo));
        }
        for(Integer charNo : fourRandomNumericalChars) {
            assertTrue(Character.isDigit(charNo));
        }
        for(Integer charNo : fourRandomSpecialChars) {
            assertFalse(Character.isAlphabetic(charNo));
            assertFalse(Character.isDigit(charNo));
            assertFalse(Character.isWhitespace(charNo));
        }
    }

    @Test
    public void testPasswordGeneration() {
        String generatedPassword = PasswordCreateUtil.generatePassword();
        System.out.println("Generated password: " + generatedPassword);
        int upperCaseCounter = 0;
        int lowerCaseCounter = 0;
        int numericalCounter = 0;
        int specialCharCounter = 0;
        for(int i = 0; i < generatedPassword.length(); i++) {
            if(Character.isUpperCase(generatedPassword.charAt(i))) {
                upperCaseCounter++;
            }
            if(Character.isLowerCase(generatedPassword.charAt(i))) {
                lowerCaseCounter++;
            }
            if(Character.isDigit(generatedPassword.charAt(i))) {
                numericalCounter++;
            }
            if(!Character.isAlphabetic(generatedPassword.charAt(i)) &&
                    !Character.isDigit(generatedPassword.charAt(i)) &&
                    !Character.isWhitespace(generatedPassword.charAt(i))) {
                specialCharCounter++;
            }
        }
        assertEquals(4, upperCaseCounter);
        assertEquals(4, lowerCaseCounter);
        assertEquals(4, numericalCounter);
        assertEquals(4, specialCharCounter);
    }

    @Test
    public void testPasswordStrengthCombinations() {
        int A = PasswordCreateUtil.passwordStrength("A");
        int a = PasswordCreateUtil.passwordStrength("a");
        int one = PasswordCreateUtil.passwordStrength("1");
        int specialOne = PasswordCreateUtil.passwordStrength("!");
        assertEquals(1, A);
        assertEquals(1, a);
        assertEquals(1, one);
        assertEquals(1, specialOne);


        int AB = PasswordCreateUtil.passwordStrength("AB");
        int ab = PasswordCreateUtil.passwordStrength("ab");
        int oneTwo = PasswordCreateUtil.passwordStrength("12");
        int specialTwo = PasswordCreateUtil.passwordStrength("!?");
        assertEquals(2, AB);
        assertEquals(2, ab);
        assertEquals(2, oneTwo);
        assertEquals(2, specialTwo);

        int ABC = PasswordCreateUtil.passwordStrength("ABC");
        int abc = PasswordCreateUtil.passwordStrength("abc");
        int oneTwoThree = PasswordCreateUtil.passwordStrength("123");
        int specialThree = PasswordCreateUtil.passwordStrength("!?#");
        assertEquals(3, ABC);
        assertEquals(3, abc);
        assertEquals(3, oneTwoThree);
        assertEquals(3, specialThree);

        int ABCD = PasswordCreateUtil.passwordStrength("ABCD");
        int abcd = PasswordCreateUtil.passwordStrength("abcd");
        int oneTwoThreeFour = PasswordCreateUtil.passwordStrength("1234");
        int specialFour = PasswordCreateUtil.passwordStrength("!?#@");
        assertEquals(4, ABCD);
        assertEquals(4, abcd);
        assertEquals(4, oneTwoThreeFour);
        assertEquals(4, specialFour);

        int oneOfEach = PasswordCreateUtil.passwordStrength("Aa1!");
        int twoOfEach = PasswordCreateUtil.passwordStrength("ABab12!?");
        int threeOfEach = PasswordCreateUtil.passwordStrength("ABCabc123!?#");
        int fourOfEach = PasswordCreateUtil.passwordStrength("ABCDabcd1234!?#@");
        assertEquals(4, oneOfEach);
        assertEquals(8, twoOfEach);
        assertEquals(12, threeOfEach);
        assertEquals(16, fourOfEach);
    }
}
