package com.neu.prattle.service.dbservice;

import fse.team2.common.models.mongomodels.SpecificProfilePicture;

/**
 * Concrete implementation of DatabaseService for {@link SpecificProfilePicture} model.
 */
public class SpecificProfilePictureService extends AbstractDatabaseService<SpecificProfilePicture> {

    protected static DatabaseService<SpecificProfilePicture> service;

    private SpecificProfilePictureService() {
        collection = MongoConnection.getCollection(SpecificProfilePicture.COLLECTION_NAME, SpecificProfilePicture.class);
    }

    /**
     * Return instance of the {@link SpecificProfilePictureService}
     *
     * @return instance of the {@link SpecificProfilePictureService}
     */
    public static DatabaseService<SpecificProfilePicture> getInstance() {
        if (service == null) {
            service = new SpecificProfilePictureService();
        }
        return service;
    }
}
