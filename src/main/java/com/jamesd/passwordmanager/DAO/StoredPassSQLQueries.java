package com.jamesd.passwordmanager.DAO;

import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.jamesd.passwordmanager.Models.HierarchyModels.PasswordEntryFolder;
import com.jamesd.passwordmanager.Models.Users.User;
import com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class StoredPassSQLQueries extends SQLQueries {

    private static CosmosClient storedPassClient;
    private static CosmosDatabase storedPassDb;
    private static CosmosContainer storedPassUserPasswordsContainer;

    protected static Logger logger = LoggerFactory.getLogger(StoredPassSQLQueries.class);

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

    public static CosmosClient getStoredPassClient() {
        return storedPassClient;
    }

    public static CosmosDatabase getStoredPassDb() {
        return storedPassDb;
    }

    public static CosmosContainer getUserPasswordsContainer() {
        return storedPassUserPasswordsContainer;
    }

    private static CosmosClient connectToStoredPassServer(String key) {
        logger.info("Using Azure Cosmos DB endpoint: " + PropertiesUtil.getProperties().getProperty("storedPassDb"));
        CosmosClient client = connect(PropertiesUtil.getProperties().getProperty("storedPassDb"), key);
        return client;
    }

    private static CosmosDatabase createIfNotExistsStoredPasswordsDb() {
        String dbId = PropertiesUtil.getProperties().getProperty("storedPassDbName");
        return createIfNotExistsDb(getStoredPassClient(), dbId);
    }

    private static CosmosContainer createIfNotExistUserPasswordsContainer(String userContainerId, String partitionKeyPath) {
        return createIfNotExistContainer(getStoredPassDb(), userContainerId, partitionKeyPath);
    }

    public static void addNewPasswordFolderToDb(PasswordEntryFolder folder) {
        logger.info("Adding new password folder to the database...");
        getUserPasswordsContainer().createItem(folder);
        logger.info("Password folder created successfully.");
    }

    public static void addNewPasswordToDb(PasswordEntryFolder folder, String passwordName, String siteUrl,
                                          String masterUsername, String passwordUsername, String currentDate,
                                          String encryptedPassword) throws ClassNotFoundException {
        logger.info("Adding new password with website URL to database...");
        Class<?> passwordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
        Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(folder);
        if(passwordEntryClass.equals(classOfEntry)) {
            WebsitePasswordEntry entry = new WebsitePasswordEntry(passwordName, siteUrl, masterUsername, passwordUsername, currentDate, encryptedPassword);
            HashMap<Object, Object> passwordMap = new HashMap<>();
            passwordMap.put("id", entry.getId());
            passwordMap.put("passwordName", entry.getPasswordName());
            passwordMap.put("encryptedPassword", entry.getEncryptedPassword());
            passwordMap.put("siteUrl", entry.getSiteUrl());
            passwordMap.put("masterUsername", entry.getMasterUsername());
            passwordMap.put("passwordUsername", entry.getPasswordUsername());
            passwordMap.put("dateSet", entry.getDateSet());
            folder.getData().add(passwordMap);
            getUserPasswordsContainer().upsertItem(folder);
            logger.info("Password entry created successfully.");
        } else {
            logger.error("Class cast exception occurred! " + classOfEntry + " is not of type " + passwordEntryClass);
        }
    }

    public static void updatePasswordInDb(WebsitePasswordEntry entry, PasswordEntryFolder factory) throws ClassNotFoundException {
        logger.info("Updating password in database...");
        Class<?> passwordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
        Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(factory);
        if(passwordEntryClass.isInstance(classOfEntry)) {
            factory.getData().forEach(data -> {
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
            getUserPasswordsContainer().upsertItem(entry);
            logger.info("Password updated successfully.");
        } else {
            logger.error("Class cast exception occurred! " + classOfEntry + " is not of type " + passwordEntryClass);
        }
    }

    public static void updatePasswordInDb(WebsitePasswordEntry entry) {
        logger.info("Updating password in database...");
        getUserPasswordsContainer().upsertItem(entry);
        logger.info("Password updated successfully.");
    }

    public static void deletePasswordInDb(WebsitePasswordEntry entry, PasswordEntryFolder factory) throws ClassNotFoundException {
        logger.info("Deleting password in database...");
        Class<?> passwordEntryClass = Class.forName("com.jamesd.passwordmanager.Models.Passwords.WebsitePasswordEntry");
        Class<?> classOfEntry = PasswordEntryFolder.EntryFactory.determineEntryType(factory);
        if(passwordEntryClass.isInstance(classOfEntry)) {
            AtomicReference<Integer> indexToRemove = new AtomicReference<>(0);
            factory.getData().forEach(data -> {
                if(data.get("id").equals(entry.getId())) {
                    indexToRemove.set(factory.getData().indexOf(data));
                }
            });
            factory.getData().remove(indexToRemove.get());
            getUserPasswordsContainer().upsertItem(entry);
        } else {
            logger.error("Class cast exception occurred! " + classOfEntry + " is not of type " + passwordEntryClass);
        }
    }

    public static void deletePasswordInDb(WebsitePasswordEntry entry) {
        logger.info("Deleting password in database...");
        getUserPasswordsContainer().deleteItem(entry, new CosmosItemRequestOptions());
        logger.info("Password deleted successfully.");
    }

    public static void deletePasswordFolderInDb(PasswordEntryFolder factory) {
        logger.info("Deleting password folder in database...");
        getUserPasswordsContainer().deleteItem(factory, new CosmosItemRequestOptions());
        logger.info("Password folder deleted successfully.");
    }

    private static List<PasswordEntryFolder> queryPasswordFolderContainer(String sql) {
        List<PasswordEntryFolder> passwordList = new ArrayList<>();
        CosmosPagedIterable<PasswordEntryFolder> passwords =
                getUserPasswordsContainer().queryItems(sql, new CosmosQueryRequestOptions(), PasswordEntryFolder.class);
        passwords.forEach(password -> {
            passwordList.add(password);
        });
        logger.info("Query complete.");
        return passwordList;
    }

    public static List<PasswordEntryFolder> queryPasswordFolderContainerByUsername(String username) {
        logger.info("Querying " + getUserPasswordsContainer().getId() + " container in " + getStoredPassDb().getId() + " database...");
        String sql =  "SELECT * FROM c WHERE c.masterUsername = '" + username + "'";
        return queryPasswordFolderContainer(sql);
    }

    //TODO: Ensure this functionality only occurs as part of a user account deletion.
    protected static void deleteUserPasswordContainer() {
        logger.info("Deleting user's passwords container...");
        getUserPasswordsContainer().delete();
        logger.info("Container deleted successfully.");
    }

    public static void close() {
        storedPassClient.close();
    }
}
