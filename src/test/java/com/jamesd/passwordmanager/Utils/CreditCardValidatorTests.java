package com.jamesd.passwordmanager.Utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreditCardValidatorTests {

    @Test
    public void getFirstNumberTest() {
        String testNumber = "1234567890";
        int firstNumber = CreditCardValidator.firstNumber(testNumber);
        assertEquals(firstNumber, 1);
    }

    @Test
    public void getFirstAndSecondNumberTest() {
        String testNumber = "1234567890";
        int firstAndSecondNumber = CreditCardValidator.firstAndSecondNumber(testNumber);
        assertEquals(firstAndSecondNumber, 12);
    }

    @Test
    public void lengthValidatorTest() {
        String testNumberVisa = "40000000000000";
        String testNumberMastercard = "5500000000000000";
        String testNumberAmericanExpress = "370000000000000";
        Boolean isValidVisa = CreditCardValidator.lengthValidator(testNumberVisa, "Visa");
        Boolean isValidMasterCard = CreditCardValidator.lengthValidator(testNumberMastercard, "Mastercard");
        Boolean isValidAmericanExpress = CreditCardValidator.lengthValidator(testNumberAmericanExpress, "American Express");
        assertTrue(isValidVisa);
        assertTrue(isValidMasterCard);
        assertTrue(isValidAmericanExpress);
    }

    @Test
    public void lengthValidatorNegativeTest() {
        String testNumberVisa = "10000000000000";
        String testNumberMastercard = "500000000";
        String testNumberAmericanExpress = "380000000000000";
        Boolean isValidVisa = CreditCardValidator.lengthValidator(testNumberVisa, "Visa");
        Boolean isValidMasterCard = CreditCardValidator.lengthValidator(testNumberMastercard, "Mastercard");
        Boolean isValidAmericanExpress = CreditCardValidator.lengthValidator(testNumberAmericanExpress, "American Express");
        assertFalse(isValidVisa);
        assertFalse(isValidMasterCard);
        assertFalse(isValidAmericanExpress);
    }

    @Test
    public void luhnCheckTest() {
        String testNumber = "5355220539778237";
        Boolean isValid = CreditCardValidator.luhnCheck(testNumber);
        assertTrue(isValid);
    }

    @Test
    public void luhnCheckNegativeTest() {
        String testNumber = "4263784908293188";
        Boolean isValid = CreditCardValidator.luhnCheck(testNumber);
        assertFalse(isValid);
    }

    @Test
    public void fullCardValidityTest() {
        String testNumber = "5355220539778237";
        Boolean isValid = CreditCardValidator.checkCardValidity(testNumber, "Mastercard");
        assertTrue(isValid);
    }

    @Test
    public void fullCardValidityNegativeTest() {
        String testNumber = "5355220539778237";
        Boolean isValid = CreditCardValidator.checkCardValidity(testNumber, "Visa");
        assertFalse(isValid);
    }
}
