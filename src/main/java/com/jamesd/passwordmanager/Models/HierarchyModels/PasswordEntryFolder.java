package com.jamesd.passwordmanager.Models.HierarchyModels;

import com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry;
import com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PasswordEntryFolder {

    private String id;
    private String masterUsername;
    private String passwordFolder;
    private String passwordType;
    private List<HashMap<Object, Object>> data;

    public PasswordEntryFolder() {
        this.id = UUID.randomUUID().toString();
        this.masterUsername = PasswordManagerApp.getLoggedInUser().getUsername();
        this.passwordFolder = null;
        this.passwordType = null;
        this.data = new ArrayList<>();
    }

    public PasswordEntryFolder(String passwordType) {
        this.id = UUID.randomUUID().toString();
        this.masterUsername = PasswordManagerApp.getLoggedInUser().getUsername();
        this.passwordType = passwordType;
        this.passwordFolder = null;
        this.data = new ArrayList<>();
    }

    public PasswordEntryFolder(String passwordType, String passwordFolder) {
        this.id = UUID.randomUUID().toString();
        this.masterUsername = PasswordManagerApp.getLoggedInUser().getUsername();
        this.passwordType = passwordType;
        this.passwordFolder = passwordFolder;
        this.data = new ArrayList<>();
    }

    public PasswordEntryFolder(String passwordType, List<HashMap<Object, Object>> data) {
        this.id = UUID.randomUUID().toString();
        this.masterUsername = PasswordManagerApp.getLoggedInUser().getUsername();
        this.passwordType = passwordType;
        this.passwordFolder = null;
        this.data = data;
    }

    public PasswordEntryFolder(String passwordType, String passwordFolder, List<HashMap<Object, Object>> data) {
        this.id = UUID.randomUUID().toString();
        this.masterUsername = PasswordManagerApp.getLoggedInUser().getUsername();
        this.passwordType = passwordType;
        this.passwordFolder = passwordFolder;
        this.data = data;
    }

    public PasswordEntryFolder(String id, String passwordName, String passwordFolder, List<HashMap<Object, Object>> data) {
        this.id = id;
        this.masterUsername = PasswordManagerApp.getLoggedInUser().getUsername();
        this.passwordType = passwordName;
        this.passwordFolder = passwordFolder;
        this.data = data;
    }

    public String getReadableTypeString() {
        List<String> types = List.of("WebPassword",
                                    "DatabasePassword",
                                    "CreditCard",
                                    "Passport",
                                    "Document");
        for(String type : types) {
            if(getPasswordType().contentEquals(type)) {
                switch(type) {
                    case "WebPassword" :
                        return "Website Passwords";
                    case "DatabasePassword" :
                        return "Database Passwords";
                    case "CreditCard" :
                        return "Credit/Debit Cards";
                    case "Passport" :
                        return "Passports";
                    case "Document" :
                        return "Documents";
                }
            }
        }
        return null;
    }

    public String getId() {
        return this.id;
    }

    public String getPasswordType() { return this.passwordType; }

    public String getPasswordFolder() {
        return this.passwordFolder;
    }

    public List<HashMap<Object, Object>> getData() {
        return data;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPasswordType(String passwordType) {
        this.passwordType = passwordType;
    }

    public void setPasswordFolder(String passwordFolder) {
        this.passwordFolder = passwordFolder;
    }

    public void setData(List<HashMap<Object, Object>> data) {
        this.data = data;
    }

    public String getMasterUsername() {
        return masterUsername;
    }

    public void setMasterUsername(String masterUsername) {
        this.masterUsername = masterUsername;
    }

    @Override
    public boolean equals(Object o) {
        PasswordEntryFolder object = (PasswordEntryFolder) o;
        return(getId() == null || getId().equals(object.getId())
        && getMasterUsername() == null || getMasterUsername().equals(object.getMasterUsername())
        && getPasswordFolder() == null || getPasswordFolder().equals(object.getPasswordFolder())
        && getPasswordType() == null || getPasswordType().equals(object.getPasswordType())
        && getData() == null || getData().equals(object.getData()));
    }

    public static class EntryFactory {

        public static Class<?> determineEntryType(PasswordEntryFolder folder) {
            switch(folder.getPasswordType()) {
                case "WebPassword":
                    return WebsitePasswordEntry.class;
                case "DatabasePassword":
                    return DatabasePasswordEntry.class;
                case "CreditCard":
                    //TODO: Implement Credit card class
                    return null;
                case "Passport":
                    //TODO: Implement Passport class
                    return null;
                case "Document":
                    //TODO: Implement Document class
                    return null;
                default:
                    return null;

            }
        }

        private static List<WebsitePasswordEntry> getListOfWebPasswords(PasswordEntryFolder folder) {
            List<WebsitePasswordEntry> listOfEntries = new ArrayList<>();
            folder.getData().forEach((data) -> {
                WebsitePasswordEntry entry = PasswordEntryBuilder.WebsitePasswordEntryBuilder.newInstance()
                        .withId((String) data.get("id"))
                        .withPasswordName((String) data.get("passwordName"))
                        .withEncryptedPassword((String) data.get("encryptedPassword"))
                        .withSiteUrl((String) data.get("siteUrl"))
                        .withMasterUsername((String) data.get("masterUsername"))
                        .withPasswordUsername((String) data.get("passwordUsername"))
                        .withDateSet((String) data.get("dateSet"))
                        .build();
                listOfEntries.add(entry);
            });
            return listOfEntries;
        }

        private static List<DatabasePasswordEntry> getListOfDatabasePasswords(PasswordEntryFolder folder) {
            List<DatabasePasswordEntry> listOfEntries = new ArrayList<>();
            folder.getData().forEach((data) -> {
                DatabasePasswordEntry entry = PasswordEntryBuilder.DatabasePasswordEntryBuilder.newInstance()
                        .withId((String) data.get("id"))
                        .withPasswordName((String) data.get("passwordName"))
                        .withEncryptedPassword((String) data.get("encryptedPassword"))
                        .withHostName((String) data.get("hostname"))
                        .withDatabaseName((String) data.get("databaseName"))
                        .withMasterUsername((String) data.get("masterUsername"))
                        .withDatabaseUsername((String) data.get("databaseUsername"))
                        .withDateSet((String) data.get("dateSet"))
                        .build();
                listOfEntries.add(entry);
            });
            return listOfEntries;
        }

        public static List<?> generateEntries(PasswordEntryFolder folder) throws ClassNotFoundException {
            Class<?> passwordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
            Class<?> databaseEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry");
            Class<?> classOfEntry = determineEntryType(folder);
            if(passwordEntryClass.equals(classOfEntry)) {
                return getListOfWebPasswords(folder);
            } if(databaseEntryClass.equals(classOfEntry)) {
                return getListOfDatabasePasswords(folder);
            }
            else {
                return new ArrayList<>();
            }
        }
    }

}
