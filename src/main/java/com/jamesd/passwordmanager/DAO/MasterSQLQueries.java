package com.jamesd.passwordmanager.DAO;

import com.jamesd.passwordmanager.Models.Passwords.StoredPassDbKey;
import com.jamesd.passwordmanager.Models.Users.User;
import com.azure.cosmos.CosmosClient;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.jamesd.passwordmanager.Utils.PropertiesUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.entity.StringEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class responsbile for all queries and connections to the master database
 */
public class MasterSQLQueries extends SQLQueries {

    private static CosmosClient masterClient;
    private static CosmosDatabase masterDb;
    private static CosmosContainer usersContainer;
    private static CosmosContainer storedPassKeyContainer;

    protected static Logger logger = LoggerFactory.getLogger(MasterSQLQueries.class);

    /**
     * Default constructor
     */
    public MasterSQLQueries() {

    }

    /**
     * Connects to the server containing the master database, checks if the master database exists and creates it if not
     * and does the same for the container which holds all users who use the application
     * @throws IOException Throws IOException if a connection to the master database server cannot be established
     */
    public static void initialiseUsers() throws IOException {
        masterClient = connectToMasterServer();
        masterDb = createIfNotExistsMasterDb();
        usersContainer = createIfNotExistUsersContainer
                (PropertiesUtil.getProperties().getProperty("masterUserContainer"), "/email");
    }

    /**
     * Creates the container which holds the encrypted key to the passwords database if it does not already exist
     */
    public static void initialiseStoredPassKey() {
        storedPassKeyContainer = createIfNotExistUsersContainer
                (PropertiesUtil.getProperties().getProperty("masterStoredPassKeyContainer"), "/key");
    }

    /**
     * Retrieves the master database server client
     * @return CosmosClient connected to the master database server
     */
    public static CosmosClient getMasterClient() {
        return masterClient;
    }

    /**
     * Retrieves the master database
     * @return CosmosDatabase which contains the master database
     */
    public static CosmosDatabase getMasterDb() {
        return masterDb;
    }

    /**
     * Retrieves the container containing all users who use the application
     * @return CosmosContainer which contains all users using the application
     */
    public static CosmosContainer getUsersContainer() {
        return usersContainer;
    }

    /**
     * Retrieves the encrypted key to unlock the passwords database
     * @return CosmosContainer which contains the encrypted passwords database key
     */
    public static CosmosContainer getStoredPassKeyContainer() {
        return storedPassKeyContainer;
    }

    /**
     * Creates a list of parameters necessary to provide validation and retrieve the key to the master database
     * @return HashMap containing the subscription ID, tenant ID, resource group and account name for the CosmosDB
     * master database
     */
    private static HashMap<String, String> getParams() {
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("subscriptionId", PropertiesUtil.getProperties().getProperty("subscriptionId").replaceAll("\"", ""));
        paramMap.put("tenantId", PropertiesUtil.getProperties().getProperty("tenantId").replaceAll("\"", ""));
        paramMap.put("resourceGroup", PropertiesUtil.getProperties().getProperty("resourceGroup").replaceAll("\"", ""));
        paramMap.put("accountName", PropertiesUtil.getProperties().getProperty("accountName").replaceAll("\"", ""));
        return paramMap;
    }

    /**
     * Sorts the parameters from the HashMap into a JSON formatted String which can be sent as a RESTful request
     * @param params HashMap of validation parameters
     * @return JSON formatted String of validation parameters
     */
    private static String getParamsString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder("{");

        int counter = 0;
        for (HashMap.Entry<String, String> entry : params.entrySet()) {
            result.append("\n\"" + entry.getKey() + "\" : ");
            result.append("\"" + entry.getValue() +"\"");
            counter++;
            if(counter<4) {
                result.append(",");
            }
        }
        result.append("\n}");

        String resultString = result.toString();
        return resultString;
    }

    /**
     * Reads the response from the azure function after a request has been sent and returns the result as a String
     * object
     * @param response HttpResponse object sent by the azure function
     * @return String object of the response
     * @throws IOException Throws IOException if the body of the HttpResponse cannot be read
     */
    private static String readResponse(HttpResponse response) throws IOException {
        String result;
        BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int read = bis.read();
        while(read != -1) {
            buf.write((byte) read);
            read = bis.read();
        }
        result = buf.toString();
        return result;
    }

    /**
     * Retrieves the master database key (not something to do in a production environment, however it serves my purposes
     * in this POC application) and uses it to connect to the master database server
     * @return CosmosClient connected to the master database server
     * @throws IOException Throws IOException if the HttpResponse body from the azure function cannot be read
     */
    private static CosmosClient connectToMasterServer() throws IOException {
        logger.info("Using Azure Cosmos DB endpoint: " + PropertiesUtil.getProperties().getProperty("masterPassDb"));
        logger.info("Unlocking...");
        String body = getParamsString(getParams());
        StringEntity entity = new StringEntity(body, ContentType.APPLICATION_FORM_URLENCODED);
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost postRequest = new HttpPost(PropertiesUtil.getProperties().getProperty("dbFunctionUrl").replaceAll("\"", ""));
        postRequest.setEntity(entity);
        HttpResponse response = client.execute(postRequest);
        String key = readResponse(response).split(":")[1];
        logger.info("Successfully retrieved master key.");
        return connect(PropertiesUtil.getProperties().getProperty("masterPassDb"), key);
    }

    /**
     * Creates the master database CosmosDatabase object if it does not already exist
     * @return CosmosDatabase object of the master database
     */
    private static CosmosDatabase createIfNotExistsMasterDb() {
        String dbId = PropertiesUtil.getProperties().getProperty("masterPassDbName");
        return createIfNotExistsDb(getMasterClient(), dbId);
    }

    /**
     * Creates the master database container which holds all users who use the application if it does not already exist
     * @param usersContainerId String ID of the CosmosDB users container in the master database
     * @param partitionKeyPath String partition key which users are sorted by (email in this case)
     * @return CosmosContainer object containing the container which holds all users who use the application
     */
    private static CosmosContainer createIfNotExistUsersContainer(String usersContainerId, String partitionKeyPath) {
        return createIfNotExistContainer(getMasterDb(), usersContainerId, partitionKeyPath);
    }

    /**
     * Adds a new user into the master database user container
     * @param username Username of the user to be added
     * @param email Email of the user to be added
     * @param encryptedPassword Encrypted master password of the user to be added
     */
    public static void addUserToDb(String username, String email, String phoneNumber, String encryptedPassword,
                                   List<HashMap<String, String>> recognisedDevices) {
        logger.info("Adding new User to the database...");
        User user = new User(username, email, phoneNumber, encryptedPassword, recognisedDevices);
        getUsersContainer().createItem(user);
        logger.info("Successfully added new user " + user.getUsername() + " to the database.");
    }

    /**
     * Updates an already existing user in the master database
     * @param user User object which is to be updated (should always be the currently logged-in user)
     */
    public static void updateUserInDb(User user) {
        logger.info("Updating user " + user.getId() + " in the database...");
        getUsersContainer().upsertItem(user);
        logger.info("Successfully updated user " + user.getId() + " in database.");
    }

    /**
     * Removes an already existing user from the master database
     * @param user User object to be deleted (should always be the currently logged-in user)
     */
    public static void deleteUserInDb(User user) {
        logger.info("Deleting user in user's container (master db)...");
        StoredPassSQLQueries.deleteUserPasswordContainer();
        getUsersContainer().deleteItem(user, new CosmosItemRequestOptions());
        logger.info("Successfully deleted user " + user.getUsername() + " from the database.");
    }

    /**
     * Sends a Kusto request to the master database users container and returns the response as a List of User objects
     * @param kql Kusto request to be sent to the master database container
     * @return Returns a List of User objects retrieved from the master database user container
     */
    private static List<User> queryUsersContainer(String kql) {
        List<User> usersList = new ArrayList<>();
        CosmosPagedIterable<User> users = getUsersContainer().queryItems(kql, new CosmosQueryRequestOptions(), User.class);
        if(users.iterator().hasNext()){
            User user = users.iterator().next();
            usersList.add(user);
        }
        logger.info("Query complete.");
        return usersList;
    }

    /**
     * Retrieves a List of User objects by looking for a specific user email
     * @param email The email String to query the container with
     * @return Returns a List of User objects with the specified email
     */
    public static List<User> queryUsersByEmail(String email) {
        logger.info("Querying " + getUsersContainer() + " container in " + getMasterDb() + " database...");
        String sql =  "SELECT * FROM c WHERE c.email = '" + email + "'";
        return queryUsersContainer(sql);
    }

    /**
     * Retrieves a List of User objects by looking for a specific user username
     * @param username The username String to query the container with
     * @return Returns a List of User objects with the specified username
     */
    public static List<User> queryUsersByUsername(String username) {
        logger.info("Querying " + getUsersContainer() + " container in " + getMasterDb() + " database...");
        String sql =  "SELECT * FROM c WHERE c.username = '" + username + "'";
        return queryUsersContainer(sql);
    }

    /**
     * Retrieves the password database key from the master database password key container
     * @return List of StoredPassDbKey objects (should only ever be one)
     */
    public static List<StoredPassDbKey> queryEncryptedStoredPassKey() {
        logger.info("Retrieving stored pass key...");
        String sql = "SELECT * FROM c";
        List<StoredPassDbKey> storedPassList = new ArrayList<>();
        CosmosPagedIterable<StoredPassDbKey> storedPassKey = getStoredPassKeyContainer().queryItems
                (sql, new CosmosQueryRequestOptions(), StoredPassDbKey.class);
        if(storedPassKey.iterator().hasNext()) {
            StoredPassDbKey entry = storedPassKey.iterator().next();
            storedPassList.add(entry);
        }
        logger.info("Successfully retrieved stored pass key.");
        return storedPassList;
    }

    /**
     * Closes the connection to the master database server
     */
    public static void close() {
        masterClient.close();
    }

}
