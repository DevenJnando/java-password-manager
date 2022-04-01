package com.jamesd.passwordmanager.Utils;

import java.util.*;
import java.util.stream.Collectors;

import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Color;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class which generates a new password and checks the overall strength of a password
 */
public abstract class PasswordCreateUtil {

    /**
     * Constructor throws UnsupportedOperationException - class is abstract
     */
    public PasswordCreateUtil() {
        throw new UnsupportedOperationException("Cannot instantiate an abstract utility class.");
    }

    /**
     * Populates a list of Integers between the bottom and top parameters
     * @param bottom Lowest Integer to add to List
     * @param top Highest Integer to add to List
     * @return List of Integer objects
     */
    public static List<Integer> populateList(int bottom, int top) {
        List<Integer> list = new ArrayList<>();
        for(int i = bottom; i < top; i++) {
            list.add(i);
        }
        return list;
    }

    /**
     * Picks four random Integers from a List of Integers and returns them as their own List
     * @param list List of Integers to choose random numbers from
     * @return List of four random Integer objects
     */
    public static List<Integer> fourRandomCharacters(List<Integer> list) {
        List<Integer> randomList = new ArrayList<>();
        for(int i = 0; i < 4; i ++) {
            Collections.shuffle(list);
            randomList.add(list.get(0));
        }
        return randomList;
    }

    /**
     * Generates a new password which conforms to the maximum strength criteria. Should be 16 characters long with four
     * uppercase letters, four lowercase letters, four numerals and four special characters
     * @return Randomly generated password String
     */
    public static String generatePassword() {

        //Length of password
        int targetStringLength = 16;

        //Initial lists of legal chars
        List<Integer> upperCharList = populateList(65, 91);
        List<Integer> lowerCharList = populateList(97, 123);
        List<Integer> numericalList = populateList(48, 58);
        List<Integer> specialCharsList = Arrays.asList(33, 35, 36, 37, 38, 42, 63, 64, 95);

        //Four random characters from each character set are pulled into four separate lists
        List<Integer> randomUpperCharList = fourRandomCharacters(upperCharList);
        List<Integer> randomLowerCharList = fourRandomCharacters(lowerCharList);
        List<Integer> randomNumericalList = fourRandomCharacters(numericalList);
        List<Integer> randomSpecialCharsList = fourRandomCharacters(specialCharsList);

        //Each list of random characters is added to a single list of lists
        List<List<Integer>> allRandomCharsList = new ArrayList<>();
        allRandomCharsList.add(randomUpperCharList);
        allRandomCharsList.add(randomLowerCharList);
        allRandomCharsList.add(randomNumericalList);
        allRandomCharsList.add(randomSpecialCharsList);

        //The list is flat mapped into a single list of all characters for the password and shuffled
        List<Integer> allRandomChars = allRandomCharsList.stream().flatMap(list -> list.stream()).collect(Collectors.toList());
        Collections.shuffle(allRandomChars);

        //The characters are converted from an integer to a character type and built into a string which is then returned
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int charAsInt : allRandomChars) {
            buffer.append((char) charAsInt);
        }
        return buffer.toString();
    }

    /**
     * Checks all uppercase characters in a String and returns them as a List of Integers
     * @param password String to check for uppercase characters
     * @return List of Integers which correspond to uppercase characters
     */
    private static Integer upperCaseChars(String password) {
        List<Integer> results = new ArrayList<>();
        List<Integer> capitalLetters = password.chars()
                .filter(o -> String.valueOf((char) o)
                        .equals(String.valueOf((char) o)
                                .toUpperCase(Locale.ENGLISH)))
                .boxed()
                .collect(Collectors.toList());
        capitalLetters.forEach(o -> {
            int characterInt = o;
            char character = (char) characterInt;
            if(StringUtils.isAlpha(String.valueOf(character))) {
                results.add(o);
            }
        });
        return results.size();
    }

    /**
     * Checks all lowercase characters in a String and returns them as a List of Integers
     * @param password String to check for lowercase characters
     * @return List of Integers which correspond to lowercase characters
     */
    private static Integer lowerCaseChars(String password) {
        List<Integer> results = new ArrayList<>();
        List<Integer> lowerCaseLetters = password.chars()
                .filter(o -> String.valueOf((char) o)
                        .equals(String.valueOf((char) o)
                                .toLowerCase(Locale.ENGLISH)))
                .boxed()
                .collect(Collectors.toList());
        lowerCaseLetters.forEach(o -> {
            int characterInt = o;
            char character = (char) characterInt;
            if(StringUtils.isAlpha(String.valueOf(character))) {
                results.add(o);
            }
        });
        return results.size();
    }

    /**
     * Checks all numeral characters in a String and returns them as a List of Integers
     * @param password String to check for numeral characters
     * @return List of Integers which correspond to numeral characters
     */
    private static Integer numerals(String password) {
        List<Integer> numerals = password.chars()
                .filter(o -> StringUtils.containsAny(String.valueOf((char) o), "0123456789"))
                .boxed()
                .collect(Collectors.toList());
        return numerals.size();
    }

    /**
     * Checks all special characters in a String and returns them as a List of Integers
     * @param password String to check for special characters
     * @return List of Integers which correspond to special characters
     */
    private static Integer specialChars(String password) {
        List<Integer> specialChars = password.chars()
                .filter(o -> String.valueOf((char) o)
                        .matches("[^a-zA-Z0-9]"))
                .boxed()
                .collect(Collectors.toList());
        return specialChars.size();
    }

    /**
     * Takes a number and returns a strength marker based on how large the number is with four being the maximum
     * @param content Integer to check
     * @return Strength marker Integer
     */
    private static Integer characterContentStrength(int content) {
        Integer maxStrength = 4;
        switch (content) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            default:
                return maxStrength;
        }
    }

    /**
     * Returns the password strength from 0 to 16
     * @param password Password String to check strength of
     * @return Total strength of password String
     */
    public static Integer passwordStrength(String password) {
        Integer upperCaseChars = upperCaseChars(password);
        Integer lowerCaseChars = lowerCaseChars(password);
        Integer numerals = numerals(password);
        Integer specialChars = specialChars(password);

        Integer upperCaseStrength = characterContentStrength(upperCaseChars);
        Integer lowerCaseStrength = characterContentStrength(lowerCaseChars);
        Integer numericalStrength = characterContentStrength(numerals);
        Integer specialCharStrength = characterContentStrength(specialChars);

        return upperCaseStrength + lowerCaseStrength + numericalStrength + specialCharStrength;
    }

    /**
     * Checks if an integer is between two numbers
     * @param x Integer to check
     * @param bottom Lowest number to be between
     * @param top Highest number to be between
     * @return Boolean true if between the two numbers, else false
     */
    public static boolean isBetween (int x, int bottom, int top) {
        return bottom <= x && x <= top;
    }

    /**
     * Checks password strength and returns a boolean ascertaining whether the password is strong enough. Also provides
     * feedback to the user on how strong their password is.
     * @param enteredPassword Password String to have its strength checked
     * @param passwordLabel Label which feeds back strength level to user
     * @param initialText Initial message for the Label to read
     * @return Boolean true if strength is acceptable, else false
     */
    public static Boolean checkPasswordStrength(String enteredPassword, Label passwordLabel, String initialText) {
        boolean passwordIsAcceptable = false;
        int strength = passwordStrength(enteredPassword);
        if (isBetween(strength, 0, 4)) {
            passwordLabel.setText(initialText + " (Too weak!)");
            passwordLabel.setTextFill(Color.DARKRED);
            passwordIsAcceptable = false;
        } else if (isBetween(strength, 4, 8)) {
            passwordLabel.setText(initialText + "  (Weak...)");
            passwordLabel.setTextFill(Color.RED);
            passwordIsAcceptable = true;
        } else if (isBetween(strength, 8, 12)) {
            passwordLabel.setText(initialText + "  (Medium)");
            passwordLabel.setTextFill(Color.ORANGE);
            passwordIsAcceptable = true;
        } else if (isBetween(strength, 12, 15)) {
            passwordLabel.setText(initialText + "  (Strong)");
            passwordLabel.setTextFill(Color.GREEN);
            passwordIsAcceptable = true;
        } else if (strength == 16) {
            passwordLabel.setText(initialText + "  (Very strong)");
            passwordLabel.setTextFill(Color.DARKGREEN);
            passwordIsAcceptable = true;
        }
        return passwordIsAcceptable;
    }

    /**
     * Returns a text formatter preventing additional text from being entered after the length parameter has been met
     * @param length Maximum length of allowed text
     * @return TextFormatter object
     */
    public static TextFormatter<String> createTextFormatter(Integer length) {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if(newText.length() > length) {
                return null;
            } else {
                return change;
            }
        });
    }
}
