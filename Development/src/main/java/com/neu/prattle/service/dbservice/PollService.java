package com.neu.prattle.service.dbservice;

import fse.team2.common.models.mongomodels.Poll;

/**
 * Concrete implementation of DatabaseService for {@link Poll} model.
 */
public class PollService extends AbstractDatabaseService<Poll> {
    protected static DatabaseService<Poll> service;

    private PollService() {
        collection = MongoConnection.getCollection(Poll.COLLECTION_NAME, Poll.class);
    }

    /**
     * Return instance of the {@link UserService}
     *
     * @return instance of the {@link UserService}
     */
    public static DatabaseService<Poll> getInstance() {
        if (service == null) {
            service = new PollService();
        }
        return service;
    }
}
