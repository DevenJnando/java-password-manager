package com.jamesd.passwordmanager.Utils;

/**
 * Utility class which validates that a credit/debit card is an actual credit/debit card number and not faked or incorrect
 */
public abstract class CreditCardValidator {

    /**
     * Constructor throws error if called - class is abstract
     */
    public CreditCardValidator() {
        throw new UnsupportedOperationException("Cannot instantiate an abstract utility class.");
    }

    /**
     * Validates the length of the credit/debit card depending on the card type
     * @param cardNumber Card number String
     * @param cardType Card type String (Can be Visa, Mastercard or American Express)
     * @return Boolean true if valid, else false
     */
    public static Boolean lengthValidator(String cardNumber, String cardType) {
        Boolean valid = false;
        switch(cardType) {
            case "Visa" :
                valid = (size(cardNumber) >= 13 && size(cardNumber) <= 16) && firstNumber(cardNumber) == 4;
                break;
            case "Mastercard" :
                valid = ((size(cardNumber) == 16) &&
                        (firstAndSecondNumber(cardNumber) >= 51 && firstAndSecondNumber(cardNumber) <= 55));
                break;
            case "American Express" :
                valid = ((size(cardNumber) == 15) &&
                        (firstAndSecondNumber(cardNumber) == 34 || firstAndSecondNumber(cardNumber) == 37));
                break;
        }
        return valid;
    }

    /**
     * Retrieves the length of the card number
     * @param number Card number String
     * @return Length of the card number
     */
    public static int size(String number) {
        return number.length();
    }

    /**
     * Gets the first number of a card number
     * @param number Card number String
     * @return First number of the card number as an Integer
     */
    public static int firstNumber(String number) {
        return Integer.parseInt(number.substring(0, 1));
    }

    /**
     * Gets the first and second number of a card number
     * @param number Card number String
     * @return First and second number of the card number as an Integer
     */
    public static int firstAndSecondNumber(String number) {
        return Integer.parseInt(number.substring(0, 2));
    }

    /**
     * Implementation of the Luhn algorithm which is used as a checksum to ensure that the entered card number is a
     * valid card number; not forged, faked or simply incorrect
     * @param cardNumber Card number String
     * @return Boolean true if the remainder is zero, else false
     */
    public static Boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean skipNextNumber = true;
        for(int i = cardNumber.length() - 1; i >= 0; i--) {
            int currentNumber = Integer.parseInt(cardNumber.substring(i, i+1));
            if(!skipNextNumber) {
                currentNumber *= 2;
                if(currentNumber > 9) {
                    currentNumber = (currentNumber % 10) + 1;
                }
            }
            sum += currentNumber;
            skipNextNumber = !skipNextNumber;
        }
        return (sum % 10 == 0);
    }

    /**
     * Validates both the length and checksum of a credit/debit card
     * @param cardNumber Card number String
     * @param cardType Type of card String (Visa, Mastercard or American Express)
     * @return Boolean true if length and checksum are valid, else false
     */
    public static Boolean checkCardValidity(String cardNumber, String cardType) {
        Boolean lengthIsValid = lengthValidator(cardNumber, cardType);
        Boolean luhnCheckPassed = luhnCheck(cardNumber);
        return (lengthIsValid && luhnCheckPassed);
    }
}
