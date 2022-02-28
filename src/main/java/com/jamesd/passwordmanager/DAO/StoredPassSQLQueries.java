package com.jamesd.passwordmanager.DAO;

import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.jamesd.passwordmanager.Models.User;
import com.jamesd.passwordmanager.Models.WebsitePasswordEntry;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public static void addNewPasswordToDb(String passwordName, String siteUrl, String masterUsername, String passwordUsername, String currentDate, String encryptedPassword) {
        logger.info("Adding new password with website URL to database...");
        WebsitePasswordEntry entry = new WebsitePasswordEntry(passwordName, siteUrl, masterUsername, passwordUsername, currentDate, encryptedPassword);
        getUserPasswordsContainer().createItem(entry);
        logger.info("Password entry created successfully.");
    }

    public static void updatePasswordInDb(WebsitePasswordEntry entry) {
        logger.info("Updating password in database...");
        getUserPasswordsContainer().upsertItem(entry);
        logger.info("Password updated successfully.");
    }

    //TODO: Implement this functionality in controller
    public static void deletePasswordInDb(WebsitePasswordEntry entry) {
        logger.info("Deleting password in database...");
        getUserPasswordsContainer().deleteItem(entry, new CosmosItemRequestOptions());
        logger.info("Password deleted successfully.");
    }

    private static List<WebsitePasswordEntry> queryPasswordsContainer(String sql) {
        List<WebsitePasswordEntry> passwordList = new ArrayList<>();
        CosmosPagedIterable<WebsitePasswordEntry> passwords =
                getUserPasswordsContainer().queryItems(sql, new CosmosQueryRequestOptions(), WebsitePasswordEntry.class);
        passwords.forEach(password -> {
            passwordList.add(password);
        });
        logger.info("Query complete.");
        return passwordList;
    }

    public static List<WebsitePasswordEntry> queryPasswordsContainerByUsername(String username) {
        logger.info("Querying " + getUserPasswordsContainer().getId() + " container in " + getStoredPassDb().getId() + " database...");
        String sql =  "SELECT * FROM c WHERE c.masterUsername = '" + username + "'";
        return queryPasswordsContainer(sql);
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
