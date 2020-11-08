package com.neu.prattle.service.dbservice;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import fse.team2.common.models.mongomodels.AuthenticationData;

/**
 * Concrete implementation of DatabaseService for AuthenticationData model.
 */
public class AuthenticationDataService extends AbstractDatabaseService<AuthenticationData> {

    protected static DatabaseService<AuthenticationData> service;

    private AuthenticationDataService() {
        collection = MongoConnection.getCollection(AuthenticationData.COLLECTION_NAME, AuthenticationData.class);
        addIndexes();
    }

    /**
     * Return instance of the {@link UserService}
     *
     * @return instance of the {@link UserService}
     */
    public static DatabaseService<AuthenticationData> getInstance() {
        if (service == null) {
            service = new AuthenticationDataService();
        }
        return service;
    }

    /**
     * Add indexes to the authentication data collection to ensure unique constraints.
     */
    private void addIndexes() {
        IndexOptions indexOptions = new IndexOptions().unique(true);
        collection.createIndex(Indexes.ascending(AuthenticationData.ENTITY_ID), indexOptions);
    }
}
