package com.jamesd.passwordmanager.DAO;

import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry;
import com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry;
import com.jamesd.passwordmanager.Models.Users.User;
import com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.jamesd.passwordmanager.PasswordManagerApp;
import com.jamesd.passwordmanager.Utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class responsible for all password database queries
 */
public class StoredPassSQLQueries extends SQLQueries {

    private static CosmosClient storedPassClient;
    private static CosmosDatabase storedPassDb;
    private static CosmosContainer storedPassUserPasswordsContainer;

    protected static Logger logger = LoggerFactory.getLogger(StoredPassSQLQueries.class);

    /**
     * Connects to the password database server, creates the password database if it does not already exist, and
     * connects to the logged-in user's container using the user's username. Creates the container if it does not yet
     * exist
     * @param key Key to the password database server
     * @param username Username of the currently logged-in user
     * @throws SQLException Throws SQLException if no user with the specified username can be found in the database
     */
    public static void initialiseWithUsername(String key, String username) throws SQLException {
        storedPassClient = connectToStoredPassServer(key);
        storedPassDb = createIfNotExistsStoredPasswordsDb();
        List<User> users = MasterSQLQueries.queryUsersByUsername(username);
        if(users.size() != 0) {
            for(User user : users) {
                storedPassUserPasswordsContainer = createIfNotExistUserPasswordsContainer(user.getUsername(),
                        "/masterUsername");
            }
        } else {
            throw new SQLException("No User with username " + username + " found in database.");
        }
    }

    /**
     * Connects to the password database server, creates the password database if it does not already exist, and
     * connects to the logged-in user's container using the user's email. Creates the container if it does not yet exist
     * @param key Key to the password database server
     * @param email Email of the currently logged-in user
     * @throws SQLException Throws SQLException if no user with the specified email can be found in the database
     */
    public static void initialiseWithEmail(String key, String email) throws SQLException {
        storedPassClient = connectToStoredPassServer(key);
        storedPassDb = createIfNotExistsStoredPasswordsDb();
        List<User> users = MasterSQLQueries.queryUsersByEmail(email);
        if(users.size() != 0) {
            for(User user : users) {
                logger.info("Username: " + user.getUsername());
                storedPassUserPasswordsContainer = createIfNotExistUserPasswordsContainer(user.getUsername(),
                        "/masterUsername");
            }
        } else {
            throw new SQLException("No User with email " + email + " found in database.");
        }
    }

    /**
     * Connects to the password database server
     * @param key Key which unlocks the password database server
     * @return CosmosClient object connected to the password database server
     */
    private static CosmosClient connectToStoredPassServer(String key) {
        logger.info("Using Azure Cosmos DB endpoint: " + PropertiesUtil.getProperties().getProperty("storedPassDb"));
        CosmosClient client = connect(PropertiesUtil.getProperties().getProperty("storedPassDb"), key);
        return client;
    }

    /**
     * Connects to the password database, or creates it if it does not yet exist
     * @return CosmosDatabase object containing the password database
     */
    private static CosmosDatabase createIfNotExistsStoredPasswordsDb() {
        String dbId = PropertiesUtil.getProperties().getProperty("storedPassDbName");
        return createIfNotExistsDb(getStoredPassClient(), dbId);
    }

    /**
     * Connects to the logged-in user's container in the password database, or creates it if it does not yet exist
     * @param userContainerId ID of the logged-in user's container ID
     * @param partitionKeyPath Partition key of the container
     * @return CosmosContainer object which holds the logged-in user's container
     */
    private static CosmosContainer createIfNotExistUserPasswordsContainer(String userContainerId, String partitionKeyPath) {
        return createIfNotExistContainer(getStoredPassDb(), userContainerId, partitionKeyPath);
    }

    /**
     * Updates a PasswordEntryFolder in memory. Should be called in conjunction with a folder update in the password
     * database
     * @param folder PasswordEntryFolder object which is to be updated in memory
     */
    private static void updateInMemoryPasswordFolderData(PasswordEntryFolder folder) {
        List<PasswordEntryFolder> loadedFolders = PasswordManagerApp.getPasswordHomeController().getPasswordEntryFolders();
        for(PasswordEntryFolder currentFolder : loadedFolders) {
            if(folder.getPasswordFolder().contentEquals(currentFolder.getPasswordFolder())) {
                currentFolder.setData(folder.getData());
                break;
            }
        }
    }

    /**
     * Adds a new PasswordEntryFolder object to the password database
     * @param folder PasswordEntryFolder to be added to the database
     */
    public static void addNewPasswordFolderToDb(PasswordEntryFolder folder) {
        logger.info("Adding new password folder to the database...");
        getUserPasswordsContainer().createItem(folder);
        logger.info("Password folder created successfully.");
    }

    /**
     * Adds a new WebsitePasswordEntry object to its parent folder, and then updates that folder in the database
     * @param folder PasswordEntryFolder which the WebsitePasswordEntry object belongs to
     * @param passwordName Name of the password
     * @param siteUrl URL of the website the password is for
     * @param masterUsername Username of the user in this application
     * @param passwordUsername Username of the user in the website password entry
     * @param currentDate Current date of the update
     * @param encryptedPassword Encrypted password of the website password entry
     * @throws ClassNotFoundException Throws ClassNotFoundException if the WebsitePasswordEntry class cannot be found
     */
    public static void addNewWebsitePasswordToDb(PasswordEntryFolder folder, String passwordName, String siteUrl,
                                                 String masterUsername, String passwordUsername, String currentDate,
                                                 String encryptedPassword) throws ClassNotFoundException {
        logger.info("Adding new password with website URL to database...");
        Class<?> passwordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
        Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
        if(passwordEntryClass.equals(classOfEntry)) {
            WebsitePasswordEntry entry = new WebsitePasswordEntry(passwordName, siteUrl, masterUsername, passwordUsername,
                    currentDate, encryptedPassword);
            HashMap<Object, Object> passwordMap = new HashMap<>();
            passwordMap.put("id", entry.getId());
            passwordMap.put("passwordName", entry.getPasswordName());
            passwordMap.put("encryptedPassword", entry.getEncryptedPassword());
            passwordMap.put("siteUrl", entry.getSiteUrl());
            passwordMap.put("masterUsername", entry.getMasterUsername());
            passwordMap.put("passwordUsername", entry.getPasswordUsername());
            passwordMap.put("dateSet", entry.getDateSet());
            folder.getData().add(passwordMap);
            updateInMemoryPasswordFolderData(folder);
            getUserPasswordsContainer().upsertItem(folder);
            logger.info("Website password entry created successfully.");
        } else {
            logger.error("Class cast exception occurred! " + classOfEntry + " is not of type " + passwordEntryClass);
        }
    }

    /**
     * Adds a new DatabasePasswordEntry object to its parent folder, and then updates that folder in the database
     * @param folder PasswordEntryFolder which the WebsitePasswordEntry object belongs to
     * @param passwordName Name of the password
     * @param hostname hostname of the database server
     * @param databaseName name of the database
     * @param masterUsername Username of the user in this application
     * @param databaseUsername Username of the user in the database password entry
     * @param currentDate Current date of the update
     * @param encryptedPassword Encrypted password of the database password entry
     * @throws ClassNotFoundException Throws ClassNotFoundException if the DatabasePasswordEntry class cannot be found
     */
    public static void addNewDatabasePasswordToDb(PasswordEntryFolder folder, String passwordName, String hostname,
                                                  String masterUsername, String databaseName, String databaseUsername,
                                                  String currentDate, String encryptedPassword) throws ClassNotFoundException {
        logger.info("Adding new database password to database...");
        Class<?> passwordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry");
        Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
        if(passwordEntryClass.equals(classOfEntry)) {
            DatabasePasswordEntry entry = new DatabasePasswordEntry(passwordName, hostname, databaseName, masterUsername,
                    databaseUsername, currentDate, encryptedPassword);
            HashMap<Object, Object> passwordMap = new HashMap<>();
            passwordMap.put("id", entry.getId());
            passwordMap.put("passwordName", entry.getPasswordName());
            passwordMap.put("encryptedPassword", entry.getEncryptedPassword());
            passwordMap.put("hostname", entry.getHostName());
            passwordMap.put("databaseName", entry.getDatabaseName());
            passwordMap.put("masterUsername", entry.getMasterUsername());
            passwordMap.put("databaseUsername", entry.getDatabaseUsername());
            passwordMap.put("dateSet", entry.getDateSet());
            folder.getData().add(passwordMap);
            updateInMemoryPasswordFolderData(folder);
            getUserPasswordsContainer().upsertItem(folder);
            logger.info("Database password entry created successfully.");
        } else {
            logger.error("Class cast exception occurred! " + classOfEntry + " is not of type " + passwordEntryClass);
        }
    }

    /**
     * Adds a new CreditDebitCardEntry object to its parent folder, and then updates that folder in the database
     * @param folder PasswordEntryFolder which the WebsitePasswordEntry object belongs to
     * @param passwordName Name of the password
     * @param cardNumber Card number on the credit/debit card
     * @param expiryDate Expiry date of the credit/debit card
     * @param securityCode CCV on the back of the card
     * @param accountNumber Account number for the account the card is associated with
     * @param sortCode Sort code for the account the card is associated with
     * @param currentDate Current date of the update
     * @throws ClassNotFoundException Throws ClassNotFoundException if the DatabasePasswordEntry class cannot be found
     */
    public static void addNewCreditDebitCardToDb(PasswordEntryFolder folder, String passwordName, String cardNumber,
                                                  String cardType, String expiryDate, String securityCode, String accountNumber,
                                                  String sortCode, String masterUsername, String currentDate)
            throws ClassNotFoundException {
        logger.info("Adding new database password to database...");
        Class<?> passwordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry");
        Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
        if(passwordEntryClass.equals(classOfEntry)) {
            CreditDebitCardEntry entry = new CreditDebitCardEntry(passwordName, cardNumber, cardType, masterUsername,
                    currentDate, expiryDate, securityCode, accountNumber, sortCode);
            HashMap<Object, Object> passwordMap = new HashMap<>();
            passwordMap.put("id", entry.getId());
            passwordMap.put("passwordName", entry.getPasswordName());
            passwordMap.put("cardType", entry.getCardType());
            passwordMap.put("cardNumber", entry.getCardNumber());
            passwordMap.put("expiryDate", entry.getExpiryDate());
            passwordMap.put("securityCode", entry.getSecurityCode());
            passwordMap.put("accountNumber", entry.getAccountNumber());
            passwordMap.put("sortCode", entry.getSortCode());
            passwordMap.put("masterUsername", entry.getMasterUsername());
            passwordMap.put("dateSet", entry.getDateSet());
            folder.getData().add(passwordMap);
            updateInMemoryPasswordFolderData(folder);
            getUserPasswordsContainer().upsertItem(folder);
            logger.info("Database password entry created successfully.");
        } else {
            logger.error("Class cast exception occurred! " + classOfEntry + " is not of type " + passwordEntryClass);
        }
    }

    /**
     * Updates an already existing WebsitePasswordEntry object in the database in its parent folder
     * @param entry WebsitePasswordEntry object to be updated
     * @param folder PasswordEntryFolder the website password entry belongs to
     * @throws ClassNotFoundException Throws ClassNotFoundException if the WebsitePasswordEntry class cannot be found
     */
    public static void updateWebsitePasswordInDb(WebsitePasswordEntry entry, PasswordEntryFolder folder) throws ClassNotFoundException {
        logger.info("Updating password in database...");
        Class<?> passwordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
        Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
        if(passwordEntryClass.equals(classOfEntry)) {
            folder.getData().forEach(data -> {
                if (data.get("id").equals(entry.getId())) {
                    data.clear();
                    data.put("id", entry.getId());
                    data.put("passwordName", entry.getPasswordName());
                    data.put("encryptedPassword", entry.getEncryptedPassword());
                    data.put("siteUrl", entry.getSiteUrl());
                    data.put("masterUsername", entry.getMasterUsername());
                    data.put("passwordUsername", entry.getPasswordUsername());
                    data.put("dateSet", entry.getDateSet());
                }
            });
            updateInMemoryPasswordFolderData(folder);
            getUserPasswordsContainer().upsertItem(folder);
            logger.info("Password updated successfully.");
        } else {
            logger.error("Class cast exception occurred! " + classOfEntry + " is not of type " + passwordEntryClass);
        }
    }

    /**
     * Updates an already existing DatabasePasswordEntry object in the database in its parent folder
     * @param entry DatabasePasswordEntry object to be updated
     * @param folder PasswordEntryFolder the database password entry belongs to
     * @throws ClassNotFoundException Throws ClassNotFoundException if the DatabasePasswordEntry class cannot be found
     */
    public static void updateDatabasePasswordInDb(DatabasePasswordEntry entry, PasswordEntryFolder folder) throws ClassNotFoundException {
        logger.info("Updating password in database...");
        Class<?> passwordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry");
        Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
        if(passwordEntryClass.equals(classOfEntry)) {
            folder.getData().forEach(data -> {
                if (data.get("id").equals(entry.getId())) {
                    data.clear();
                    data.put("id", entry.getId());
                    data.put("passwordName", entry.getPasswordName());
                    data.put("encryptedPassword", entry.getEncryptedPassword());
                    data.put("hostname", entry.getHostName());
                    data.put("masterUsername", entry.getMasterUsername());
                    data.put("databaseName", entry.getDatabaseName());
                    data.put("databaseUsername", entry.getDatabaseUsername());
                    data.put("dateSet", entry.getDateSet());
                }
            });
            updateInMemoryPasswordFolderData(folder);
            getUserPasswordsContainer().upsertItem(folder);
            logger.info("Password updated successfully.");
        } else {
            logger.error("Class cast exception occurred! " + classOfEntry + " is not of type " + passwordEntryClass);
        }
    }

    /**
     * Updates an already existing CreditDebitCardEntry object in the database in its parent folder
     * @param entry CreditDebitCardEntry object to be updated
     * @param folder PasswordEntryFolder the database password entry belongs to
     * @throws ClassNotFoundException Throws ClassNotFoundException if the CreditDebitCardEntry class cannot be found
     */
    public static void updateCreditDebitCardInDb(CreditDebitCardEntry entry, PasswordEntryFolder folder) throws ClassNotFoundException {
        logger.info("Updating password in database...");
        Class<?> passwordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry");
        Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
        if(passwordEntryClass.equals(classOfEntry)) {
            folder.getData().forEach(data -> {
                if (data.get("id").equals(entry.getId())) {
                    data.clear();
                    data.put("id", entry.getId());
                    data.put("passwordName", entry.getPasswordName());
                    data.put("cardNumber", entry.getCardNumber());
                    data.put("cardType", entry.getCardType());
                    data.put("masterUsername", entry.getMasterUsername());
                    data.put("expiryDate", entry.getExpiryDate());
                    data.put("securityCode", entry.getSecurityCode());
                    data.put("accountNumber", entry.getAccountNumber());
                    data.put("sortCode", entry.getSortCode());
                    data.put("dateSet", entry.getDateSet());
                }
            });
            updateInMemoryPasswordFolderData(folder);
            getUserPasswordsContainer().upsertItem(folder);
            logger.info("Password updated successfully.");
        } else {
            logger.error("Class cast exception occurred! " + classOfEntry + " is not of type " + passwordEntryClass);
        }
    }

    /**
     * Deletes an already existing PasswordEntry object in the database in its parent folder
     * @param entry PasswordEntry object to be deleted
     * @param folder PasswordEntryFolder the password entry belongs to
     * @throws ClassNotFoundException Throws ClassNotFoundException if a subclass of PasswordEntry cannot be found
     */
    public static void deletePasswordInDb(Object entry, PasswordEntryFolder folder) throws ClassNotFoundException {
        logger.info("Deleting password in database...");
        Class<?> websiteEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
        Class<?> databaseEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.DatabasePasswordEntry");
        Class<?> creditDebitCardEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.CreditDebitCardEntry");
        Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
        int indexToRemove = -1;
        boolean classFound = false;
        if(websiteEntryClass.equals(classOfEntry)) {
            WebsitePasswordEntry websitePasswordEntry = (WebsitePasswordEntry) entry;
            for(HashMap<Object, Object> data : folder.getData()) {
                if(data.get("id").equals(websitePasswordEntry.getId())) {
                    indexToRemove = folder.getData().indexOf(data);
                }
            }
            classFound = true;
        }
        else if(databaseEntryClass.equals(classOfEntry)) {
            DatabasePasswordEntry databasePasswordEntry = (DatabasePasswordEntry) entry;
            for(HashMap<Object, Object> data : folder.getData()) {
                if(data.get("id").equals(databasePasswordEntry.getId())) {
                    indexToRemove = folder.getData().indexOf(data);
                }
            }
            classFound = true;
        }
        else if(creditDebitCardEntryClass.equals(classOfEntry)) {
            CreditDebitCardEntry creditDebitCardEntry = (CreditDebitCardEntry) entry;
            for(HashMap<Object, Object> data : folder.getData()) {
                if(data.get("id").equals(creditDebitCardEntry.getId())) {
                    indexToRemove = folder.getData().indexOf(data);
                }
            }
            classFound = true;
        }
        else {
            logger.error("Class cast exception occurred! " + classOfEntry + " is not of types {" +
                    "\n" + websiteEntryClass +
                    "\n" + databaseEntryClass +
                    "\n}");
        }

        if(classFound) {
            folder.getData().remove(indexToRemove);
            updateInMemoryPasswordFolderData(folder);
            getUserPasswordsContainer().upsertItem(folder);
            logger.info("Entry deleted successfully.");
        }
    }

    /**
     * Deletes an entire PasswordEntryFolder, including all passwords contained within, from the password database
     * @param folder PasswordEntryFolder to delete
     */
    public static void deletePasswordFolderInDb(PasswordEntryFolder folder) {
        logger.info("Deleting password folder in database...");
        getUserPasswordsContainer().deleteItem(folder, new CosmosItemRequestOptions());
        logger.info("Password folder deleted successfully.");
    }

    /**
     * Sends a Kusto query to the logged-in user's container in the password database and returns a List of
     * PasswordEntryFolder objects as a response
     * @param kql Kusto query to send to the database
     * @return List of PasswordEntryFolder objects retrieved from the password database
     */
    private static List<PasswordEntryFolder> queryPasswordFolderContainer(String kql) {
        List<PasswordEntryFolder> passwordList = new ArrayList<>();
        CosmosPagedIterable<PasswordEntryFolder> passwords =
                getUserPasswordsContainer().queryItems(kql, new CosmosQueryRequestOptions(), PasswordEntryFolder.class);
        passwords.forEach(passwordList::add);
        logger.info("Query complete.");
        return passwordList;
    }

    /**
     * Queries the logged-in user's container in the password database by checking for PasswordEntryFolder objects which
     * match the user's username
     * @param username Username of the currently logged-in user
     * @return List of PasswordEntryFolder objects which belong to the logged-in user
     */
    public static List<PasswordEntryFolder> queryPasswordFolderContainerByUsername(String username) {
        logger.info("Querying " + getUserPasswordsContainer().getId() + " container in " + getStoredPassDb().getId() + " database...");
        String sql =  "SELECT * FROM c WHERE c.masterUsername = '" + username + "'";
        return queryPasswordFolderContainer(sql);
    }

    /**
     * Queries the logged-in user's container in the password database by checking for PasswordEntryFolder objects which
     * match specified folder name and folder type
     * @param folderName Name of the folder
     * @param type Type of folder the PasswordEntryFolder object is
     * @return List of PasswordEntryFolder objects which contain the folderName and type parameters
     */
    public static List<PasswordEntryFolder> queryPasswordFolderContainerByNameAndType(String folderName, String type) {
        logger.info("Querying " + getUserPasswordsContainer().getId() + " container in " + getStoredPassDb().getId() + " database...");
        logger.info("Obtaining password folders named " + folderName + " with type " + type + "...");
        String sql = "SELECT * FROM c WHERE c.passwordFolder = '" + folderName + "' AND c.passwordType = '" + type + "'";
        return queryPasswordFolderContainer(sql);
    }

    //TODO: Ensure this functionality only occurs as part of a user account deletion.
    /**
     * Completely removes the currently logged-in user's container from the database. Should only ever be called as part
     * of a full account deletion
     */
    protected static void deleteUserPasswordContainer() {
        logger.info("Deleting user's passwords container...");
        getUserPasswordsContainer().delete();
        logger.info("Container deleted successfully.");
    }

    /**
     * Retrieves the CosmosClient object which is connected to the password database server
     * @return CosmosClient object connected to password database server
     */
    public static CosmosClient getStoredPassClient() {
        return storedPassClient;
    }

    /**
     * Retrieves the CosmosDatabase object which contains the password database
     * @return CosmosDatabase object containing the password database
     */
    public static CosmosDatabase getStoredPassDb() {
        return storedPassDb;
    }

    /**
     * Retrieves the CosmosContainer object which holds the currently logged-in user's container from the database
     * @return CosmosContainer object contaiting a user container
     */
    public static CosmosContainer getUserPasswordsContainer() {
        return storedPassUserPasswordsContainer;
    }

    /**
     * Closes the connection to the password database server
     */
    public static void close() {
        storedPassClient.close();
    }
}
