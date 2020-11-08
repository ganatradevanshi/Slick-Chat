package com.neu.prattle.service.dbservice;

import fse.team2.common.models.mongomodels.UserActivityData;

/**
 * Concrete implementation of DatabaseService for {@link UserActivityData} model.
 */
public class UserActivityService extends AbstractDatabaseService<UserActivityData> {

  protected static DatabaseService<UserActivityData> service;

  private UserActivityService() {
    collection = MongoConnection.getCollection(UserActivityData.COLLECTION_NAME, UserActivityData.class);
  }

  /**
   * Return instance of the {@link UserActivityService}
   *
   * @return instance of the {@link UserActivityService}
   */
  public static DatabaseService<UserActivityData> getInstance() {
    if (service == null) {
      service = new UserActivityService();
    }
    return service;
  }
}
