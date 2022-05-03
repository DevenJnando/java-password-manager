package com.jamesd.passwordmanager.DAO;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class containing common methods used by all subclasses of SQLQueries
 */
public abstract class SQLQueries {

    protected static Logger logger = LoggerFactory.getLogger(SQLQueries.class);

    /**
     * Connects to a CosmosDB server using the specified endpoint and master key
     * @param endpoint Database server endpoint
     * @param key Key used to unlock the database server
     * @return CosmosClient connected to the specified database server
     */
    protected static CosmosClient connect(String endpoint, String key) {
        CosmosClient client = new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .consistencyLevel(ConsistencyLevel.EVENTUAL)
                .contentResponseOnWriteEnabled(true)
                .buildClient();
        return client;
    }

    /**
     * Creates a CosmosDB database if it does not already exist
     * @param client CosmosClient connected to the server to retrieve/create a database in
     * @param dbId ID of the database to retrieve/create
     * @return CosmosDatabase object containing the specified database
     */
    protected static CosmosDatabase createIfNotExistsDb(CosmosClient client, String dbId) {
        logger.info("Retrieving database with ID: " + dbId);
        CosmosDatabaseResponse dbResponse = client.createDatabaseIfNotExists(dbId);
        CosmosDatabase masterDb = client.getDatabase(dbResponse.getProperties().getId());
        logger.info("Successfully retrieved database with ID: " + dbId);
        return masterDb;
    }

    /**
     * Creates a CosmosDB container if it does not already exist
     * @param db CosmosDatabase the container should exist in
     * @param containerId ID of the container to retrieve/create
     * @param partitionKeyPath Partition key of the container
     * @return
     */
    protected static CosmosContainer createIfNotExistContainer(CosmosDatabase db, String containerId, String partitionKeyPath) {
        logger.info("Retrieving " + containerId + " container");
        CosmosContainerProperties containerProperties = new CosmosContainerProperties
                (containerId, partitionKeyPath);
        CosmosContainerResponse containerResponse = db.createContainerIfNotExists(containerProperties);
        CosmosContainer container = db.getContainer(containerResponse.getProperties().getId());
        return container;
    }
}
