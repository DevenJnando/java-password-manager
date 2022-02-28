package com.jamesd.passwordmanager.DAO;

import com.jamesd.passwordmanager.Models.StoredPassDbKey;
import com.jamesd.passwordmanager.Models.User;
import com.azure.cosmos.CosmosClient;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
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

public class MasterSQLQueries extends SQLQueries {

    private static CosmosClient masterClient;
    private static CosmosDatabase masterDb;
    private static CosmosContainer usersContainer;
    private static CosmosContainer storedPassKeyContainer;

    protected static Logger logger = LoggerFactory.getLogger(MasterSQLQueries.class);

    public MasterSQLQueries() {

    }

    public static void initialiseUsers() throws IOException {
        masterClient = connectToMasterServer();
        masterDb = createIfNotExistsMasterDb();
        usersContainer = createIfNotExistUsersContainer
                (PropertiesUtil.getProperties().getProperty("masterUserContainer"), "/email");
    }

    public static void initialiseStoredPassKey() {
        storedPassKeyContainer = createIfNotExistUsersContainer
                (PropertiesUtil.getProperties().getProperty("masterStoredPassKeyContainer"), "/key");
    }

    public static CosmosClient getMasterClient() {
        return masterClient;
    }

    public static CosmosDatabase getMasterDb() {
        return masterDb;
    }

    public static CosmosContainer getUsersContainer() {
        return usersContainer;
    }

    public static CosmosContainer getStoredPassKeyContainer() {
        return storedPassKeyContainer;
    }

    private static HashMap<String, String> getParams() {
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("subscriptionId", PropertiesUtil.getProperties().getProperty("subscriptionId").replaceAll("\"", ""));
        paramList.put("tenantId", PropertiesUtil.getProperties().getProperty("tenantId").replaceAll("\"", ""));
        paramList.put("resourceGroup", PropertiesUtil.getProperties().getProperty("resourceGroup").replaceAll("\"", ""));
        paramList.put("accountName", PropertiesUtil.getProperties().getProperty("accountName").replaceAll("\"", ""));
        return paramList;
    }

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

    private static CosmosDatabase createIfNotExistsMasterDb() {
        String dbId = PropertiesUtil.getProperties().getProperty("masterPassDbName");
        return createIfNotExistsDb(getMasterClient(), dbId);
    }

    private static CosmosContainer createIfNotExistUsersContainer(String usersContainerId, String partitionKeyPath) {
        return createIfNotExistContainer(getMasterDb(), usersContainerId, partitionKeyPath);
    }

    public static void addUserToDb(String username, String email, String encryptedPassword) {
        logger.info("Adding new User to the database...");
        User user = new User(username, email, encryptedPassword);
        getUsersContainer().createItem(user);
        logger.info("Successfully added new user " + user.getUsername() + " to the database.");
    }

    public static void updateUserInDb(User user) {
        logger.info("Updating user " + user.getId() + " in the database...");
        getUsersContainer().upsertItem(user);
        logger.info("Successfully updated user " + user.getId() + " in database.");
    }

    public static void deleteUserInDb(User user) {
        logger.info("Deleting user in user's container (master db)...");
        StoredPassSQLQueries.deleteUserPasswordContainer();
        getUsersContainer().deleteItem(user, new CosmosItemRequestOptions());
        logger.info("Successfully deleted user " + user.getUsername() + " from the database.");
    }

    private static List<User> queryUsersContainer(String sql) {
        List<User> usersList = new ArrayList<>();
        CosmosPagedIterable<User> users = getUsersContainer().queryItems(sql, new CosmosQueryRequestOptions(), User.class);
        if(users.iterator().hasNext()){
            User user = users.iterator().next();
            usersList.add(user);
        }
        logger.info("Query complete.");
        return usersList;
    }

    public static List<User> queryUsersByEmail(String email) {
        logger.info("Querying " + getUsersContainer() + " container in " + getMasterDb() + " database...");
        String sql =  "SELECT * FROM c WHERE c.email = '" + email + "'";
        return queryUsersContainer(sql);
    }

    public static List<User> queryUsersByUsername(String username) {
        logger.info("Querying " + getUsersContainer() + " container in " + getMasterDb() + " database...");
        String sql =  "SELECT * FROM c WHERE c.username = '" + username + "'";
        return queryUsersContainer(sql);
    }

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

    public static void close() {
        masterClient.close();
    }

}
