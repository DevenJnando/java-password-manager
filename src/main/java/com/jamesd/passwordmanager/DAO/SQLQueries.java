package com.jamesd.passwordmanager.DAO;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SQLQueries {

    protected static Logger logger = LoggerFactory.getLogger(SQLQueries.class);

    protected static CosmosClient connect(String endpoint, String key) {
        CosmosClient client = new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .consistencyLevel(ConsistencyLevel.EVENTUAL)
                .contentResponseOnWriteEnabled(true)
                .buildClient();
        return client;
    }

    protected static CosmosDatabase createIfNotExistsDb(CosmosClient client, String dbId) {
        logger.info("Retrieving database with ID: " + dbId);
        CosmosDatabaseResponse dbResponse = client.createDatabaseIfNotExists(dbId);
        CosmosDatabase masterDb = client.getDatabase(dbResponse.getProperties().getId());
        logger.info("Successfully retrieved database with ID: " + dbId);
        return masterDb;
    }

    protected static CosmosContainer createIfNotExistContainer(CosmosDatabase db, String containerId, String partitionKeyPath) {
        logger.info("Retrieving " + containerId + " container");
        CosmosContainerProperties containerProperties = new CosmosContainerProperties
                (containerId, partitionKeyPath);
        CosmosContainerResponse containerResponse = db.createContainerIfNotExists(containerProperties);
        CosmosContainer container = db.getContainer(containerResponse.getProperties().getId());
        return container;
    }
}
