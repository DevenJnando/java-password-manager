package com.jamesd.passwordmanager.Models.HierarchyModels;

import com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry;
import com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry;
import com.jamesd.passwordmanager.Models.Passwords.DocumentEntry;
import com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry;
import com.jamesd.passwordmanager.PasswordManagerApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Class which models the folder which contains many PasswordEntry objects
 */
public class PasswordEntryFolder {

    private String id;
    private String masterUsername;
    private String passwordFolder;
    private String passwordType;
    private List<HashMap<Object, Object>> data;

    /**
     * Default constructor
     */
    public PasswordEntryFolder() {
        this.id = UUID.randomUUID().toString();
        this.masterUsername = PasswordManagerApp.getLoggedInUser().getUsername();
        this.passwordFolder = null;
        this.passwordType = null;
        this.data = new ArrayList<>();
    }

    /**
     * Constructor which takes the password type as a parameter
     * @param passwordType String of the required password type
     */
    public PasswordEntryFolder(String passwordType) {
        this.id = UUID.randomUUID().toString();
        this.masterUsername = PasswordManagerApp.getLoggedInUser().getUsername();
        this.passwordType = passwordType;
        this.passwordFolder = null;
        this.data = new ArrayList<>();
    }

    /**
     * Constructor which takes the password type and password folder name as parameters
     * @param passwordType String of the required password type
     * @param passwordFolder String of the password folder name
     */
    public PasswordEntryFolder(String passwordType, String passwordFolder) {
        this.id = UUID.randomUUID().toString();
        this.masterUsername = PasswordManagerApp.getLoggedInUser().getUsername();
        this.passwordType = passwordType;
        this.passwordFolder = passwordFolder;
        this.data = new ArrayList<>();
    }

    /**
     * Constructor which takes the password type and raw password entry data as parameters
     * @param passwordType String of the required password type
     * @param data Raw password data which will be added to a subclass object of PasswordEntry at a later time
     */
    public PasswordEntryFolder(String passwordType, List<HashMap<Object, Object>> data) {
        this.id = UUID.randomUUID().toString();
        this.masterUsername = PasswordManagerApp.getLoggedInUser().getUsername();
        this.passwordType = passwordType;
        this.passwordFolder = null;
        this.data = data;
    }

    /**
     * Constructor which takes the password type, password folder name and raw password entry data as parameters
     * @param passwordType String of the required password type
     * @param passwordFolder String of the password folder name
     * @param data Raw password data which will be added to a subclass object of PasswordEntry at a later time
     */
    public PasswordEntryFolder(String passwordType, String passwordFolder, List<HashMap<Object, Object>> data) {
        this.id = UUID.randomUUID().toString();
        this.masterUsername = PasswordManagerApp.getLoggedInUser().getUsername();
        this.passwordType = passwordType;
        this.passwordFolder = passwordFolder;
        this.data = data;
    }

    /**
     * Constructor which takes the password folder ID in the database, password type, password folder name and raw
     * password data as parameters
     * @param id String of the password folder ID
     * @param passwordType String of the required password type
     * @param passwordFolder String of the password folder name
     * @param data Raw password data which will be added to a subclass object of PasswordEntry at a later time
     */
    public PasswordEntryFolder(String id, String passwordType, String passwordFolder, List<HashMap<Object, Object>> data) {
        this.id = id;
        this.masterUsername = PasswordManagerApp.getLoggedInUser().getUsername();
        this.passwordType = passwordType;
        this.passwordFolder = passwordFolder;
        this.data = data;
    }

    /**
     * Converts the password types stored in the database to more readable Strings for user convenience
     * @return List of all password types in a more readable form
     */
    public String getReadableTypeString() {
        List<String> types = List.of("WebPassword",
                                    "DatabasePassword",
                                    "CreditCard",
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
                    case "Document" :
                        return "Documents";
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the password folder ID
     * @return Folder ID String
     */
    public String getId() {
        return this.id;
    }

    /**
     * Retrieves the password folder type
     * @return Folder type String
     */
    public String getPasswordType() { return this.passwordType; }

    /**
     * Retrieves the name of the folder
     * @return Folder name String
     */
    public String getPasswordFolder() {
        return this.passwordFolder;
    }

    /**
     * Retrieves the raw password data of this folder
     * @return List of HashMaps containing raw password data
     */
    public List<HashMap<Object, Object>> getData() {
        return data;
    }

    /**
     * Sets the folder ID
     * @param id Folder ID String
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the folder type
     * @param passwordType Folder type String
     */
    public void setPasswordType(String passwordType) {
        this.passwordType = passwordType;
    }

    /**
     * Sets the folder name
     * @param passwordFolder Folder name String
     */
    public void setPasswordFolder(String passwordFolder) {
        this.passwordFolder = passwordFolder;
    }

    /**
     * Sets the folder's raw password data
     * @param data List of HashMaps containing raw password data
     */
    public void setData(List<HashMap<Object, Object>> data) {
        this.data = data;
    }

    /**
     * Retrieves the username of the logged-in user in this application
     * @return Master username String
     */
    public String getMasterUsername() {
        return masterUsername;
    }

    /**
     * Sets the username of the logged-in user in this application
     * @param masterUsername Master username String
     */
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

    /**
     * Static class which builds all PasswordEntry subclass objects within a folder
     */
    public static class EntryFactory {

        /**
         * Reflective method which determines the subclass of PasswordEntry which the folder parameter will contain
         * @param folder PasswordEntryFolder to be reflected
         * @return The subclass of PasswordEntry objects this folder holds
         */
        public static Class<?> determineEntryType(PasswordEntryFolder folder) {
            switch(folder.getPasswordType()) {
                case "WebPassword":
                    return WebsitePasswordEntry.class;
                case "DatabasePassword":
                    return DatabasePasswordEntry.class;
                case "CreditCard":
                    return CreditDebitCardEntry.class;
                case "Document":
                    return DocumentEntry.class;
                default:
                    return null;

            }
        }

        /**
         * Builds each WebsitePasswordEntry object within the PasswordEntryFolder parameter
         * @param folder PasswordEntryFolder to build WebsitePasswordEntry objects from
         * @return List of WebsitePasswordEntry objects
         */
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

        /**
         * Builds each DatabasePasswordEntry object within the PasswordEntryFolder parameter
         * @param folder PasswordEntryFolder to build DatabasePasswordEntry objects from
         * @return List of DatabasePasswordEntry objects
         */
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

        /**
         * Builds each CreditDebitCardEntry object within the PasswordEntryFolder parameter
         * @param folder PasswordEntryFolder to build CreditDebitCardEntry objects from
         * @return List of CreditDebitCardEntry objects
         */
        private static List<CreditDebitCardEntry> getListOfCreditDebitCards(PasswordEntryFolder folder) {
            List<CreditDebitCardEntry> listOfEntries = new ArrayList<>();
            folder.getData().forEach((data) -> {
                CreditDebitCardEntry entry = PasswordEntryBuilder.CreditDebitCardEntryBuilder.newInstance()
                        .withId((String) data.get("id"))
                        .withPasswordName((String) data.get("passwordName"))
                        .withMasterUsername((String) data.get("masterUsername"))
                        .withCreditDebitCardNumber((String) data.get("cardNumber"))
                        .withCreditDebitCardType((String) data.get("cardType"))
                        .withExpiryDate((String) data.get("expiryDate"))
                        .withSecurityCode((String) data.get("securityCode"))
                        .withAccountNumber((String) data.get("accountNumber"))
                        .withSortCode((String) data.get("sortCode"))
                        .withDateSet((String) data.get(("dateSet")))
                        .build();
                listOfEntries.add(entry);
            });
            return listOfEntries;
        }

        /**
         * Builds each DocumentEntry object within the PasswordEntryFolder parameter
         * @param folder PasswordEntryFolder to build DocumentEntry objects from
         * @return List of DocumentEntry objects
         */
        private static List<DocumentEntry> getListOfDocuments(PasswordEntryFolder folder) {
            List<DocumentEntry> listOfEntries = new ArrayList<>();
            folder.getData().forEach((data) -> {
                DocumentEntry entry = PasswordEntryBuilder.DocumentEntryBuilder.newInstance()
                        .withId((String) data.get("id"))
                        .withName((String) data.get("passwordName"))
                        .withDescription((String) data.get("documentDescription"))
                        .withMasterUsername((String) data.get("masterUsername"))
                        .withDateSet((String) data.get(("dateSet")))
                        .withStorageReference((String) data.get("documentStorageReference"))
                        .build();
                listOfEntries.add(entry);
            });
            return listOfEntries;
        }

        /**
         * Method which calls the appropriate PasswordEntry factory method depending on the subclass of PasswordEntry
         * objects which the folder parameter contains
         * @param folder PasswordEntryFolder object containing some subclass of PasswordEntry objects
         * @return List of PasswordEntry subclass objects
         * @throws ClassNotFoundException Throws ClassNotFoundException if a subclass of PasswordEntry cannot be found
         */
        public static List<?> generateEntries(PasswordEntryFolder folder) throws ClassNotFoundException {
            Class<?> passwordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
            Class<?> databaseEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry");
            Class<?> creditCardEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry");
            Class<?> documentEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DocumentEntry");
            Class<?> classOfEntry = determineEntryType(folder);
            if(passwordEntryClass.equals(classOfEntry)) {
                return getListOfWebPasswords(folder);
            } if(databaseEntryClass.equals(classOfEntry)) {
                return getListOfDatabasePasswords(folder);
            } if(creditCardEntryClass.equals(classOfEntry)) {
                return getListOfCreditDebitCards(folder);
            } if(documentEntryClass.equals(classOfEntry)) {
                return getListOfDocuments(folder);
            }
            else {
                return new ArrayList<>();
            }
        }
    }

}
