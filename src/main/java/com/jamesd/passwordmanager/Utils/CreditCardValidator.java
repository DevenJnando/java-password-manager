package com.jamesd.passwordmanager.Utils;

public abstract class CreditCardValidator {

    public CreditCardValidator() {
        throw new UnsupportedOperationException("Cannot instantiate an abstract utility class.");
    }

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

    public static int size(String number) {
        return number.length();
    }

    public static int firstNumber(String number) {
        return Integer.parseInt(number.substring(0, 1));
    }

    public static int firstAndSecondNumber(String number) {
        return Integer.parseInt(number.substring(0, 2));
    }

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

    public static Boolean checkCardValidity(String cardNumber, String cardType) {
        Boolean lengthIsValid = lengthValidator(cardNumber, cardType);
        Boolean luhnCheckPassed = luhnCheck(cardNumber);
        return (lengthIsValid && luhnCheckPassed);
    }
}
