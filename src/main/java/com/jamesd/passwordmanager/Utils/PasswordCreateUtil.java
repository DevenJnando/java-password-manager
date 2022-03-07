package com.jamesd.passwordmanager.Utils;

import java.util.*;
import java.util.stream.Collectors;

import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Color;
import org.apache.commons.lang3.StringUtils;

public abstract class PasswordCreateUtil {

    public PasswordCreateUtil() {
        throw new UnsupportedOperationException("Cannot instantiate an abstract utility class.");
    }

    private static List populateList(int bottom, int top) {
        List<Integer> list = new ArrayList();
        for(int i = bottom; i < top; i++) {
            list.add(i);
        }
        return list;
    }

    private static List<Integer> fourRandomCharacters(List<Integer> list) {
        List<Integer> randomList = new ArrayList<>();
        for(int i = 0; i < 4; i ++) {
            Collections.shuffle(list);
            randomList.add(list.get(0));
        }
        return randomList;
    }

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
        String finalString = buffer.toString();
        return finalString;
    }

    private static Integer characterLength(String password) {
        List<Integer> charLength = password.chars().boxed().collect(Collectors.toList());
        return charLength.size();
    }

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

    private static Integer numericals(String password) {
        List<Integer> numericals = password.chars()
                .filter(o -> StringUtils.containsAny(String.valueOf((char) o), "0123456789"))
                .boxed()
                .collect(Collectors.toList());
        return numericals.size();
    }

    private static Integer specialChars(String password) {
        List<Integer> specialChars = password.chars()
                .filter(o -> String.valueOf((char) o)
                        .matches("[^a-zA-Z0-9]"))
                .boxed()
                .collect(Collectors.toList());
        return specialChars.size();
    }

    public static Integer characterContentStrength(int content) {
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

    public static Integer passwordStrength(String password) {
        Integer upperCaseChars = upperCaseChars(password);
        Integer lowerCaseChars = lowerCaseChars(password);
        Integer numericals = numericals(password);
        Integer specialChars = specialChars(password);

        Integer upperCaseStrength = characterContentStrength(upperCaseChars);
        Integer lowerCaseStrength = characterContentStrength(lowerCaseChars);
        Integer numericalStrength = characterContentStrength(numericals);
        Integer specialCharStrength = characterContentStrength(specialChars);

        Integer totalStrength = upperCaseStrength + lowerCaseStrength + numericalStrength + specialCharStrength;
        return totalStrength;
    }

    private static boolean isBetween (int x, int bottom, int top) {
        return bottom <= x && x <= top;
    }

    public static Boolean checkPasswordStrength(String enteredPassword, Label passwordLabel, String initalText) {
        Boolean passwordIsAcceptable = false;
        Integer strength = passwordStrength(enteredPassword);
        if (isBetween(strength, 0, 4)) {
            passwordLabel.setText(initalText + " (Too weak!)");
            passwordLabel.setTextFill(Color.DARKRED);
            passwordIsAcceptable = false;
        } else if (isBetween(strength, 4, 8)) {
            passwordLabel.setText(initalText + "  (Weak...)");
            passwordLabel.setTextFill(Color.RED);
            passwordIsAcceptable = true;
        } else if (isBetween(strength, 8, 12)) {
            passwordLabel.setText(initalText + "  (Medium)");
            passwordLabel.setTextFill(Color.ORANGE);
            passwordIsAcceptable = true;
        } else if (isBetween(strength, 12, 15)) {
            passwordLabel.setText(initalText + "  (Strong)");
            passwordLabel.setTextFill(Color.GREEN);
            passwordIsAcceptable = true;
        } else if (strength == 16) {
            passwordLabel.setText(initalText + "  (Very strong)");
            passwordLabel.setTextFill(Color.DARKGREEN);
            passwordIsAcceptable = true;
        }
        return passwordIsAcceptable;
    }

    public static TextFormatter<String> createTextFormatter(Integer length) {
        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if(newText.length() > length) {
                return null;
            } else {
                return change;
            }
        });
        return textFormatter;
    }
}
