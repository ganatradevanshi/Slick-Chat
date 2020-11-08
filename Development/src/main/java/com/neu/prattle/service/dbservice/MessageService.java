package com.neu.prattle.service.dbservice;

import fse.team2.common.models.mongomodels.Message;

/**
 * Concrete implementation of DatabaseService for Message model.
 */
public class MessageService extends AbstractDatabaseService<Message> {

    protected static DatabaseService<Message> service;

    private MessageService() {
        collection = MongoConnection.getCollection(Message.COLLECTION_NAME, Message.class);
    }

    public static DatabaseService<Message> getInstance() {
        if (service == null) {
            service = new MessageService();
        }
        return service;
    }

}
