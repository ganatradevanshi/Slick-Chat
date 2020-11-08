package com.neu.prattle.service.dbservice;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import fse.team2.common.models.mongomodels.UserModel;

/**
 * Concrete implementation of DatabaseService for User model.
 */
public class UserService extends AbstractDatabaseService<UserModel> {

  protected static DatabaseService<UserModel> service;

  private UserService() {
    collection = MongoConnection.getCollection(UserModel.COLLECTION_NAME, UserModel.class);
    addIndexes();
  }

  /**
   * Add indexes to the user collection to ensure unique constraints.
   */
  private void addIndexes() {
    IndexOptions indexOptions = new IndexOptions().unique(true);
    collection.createIndex(Indexes.ascending(UserModel.USERNAME_FIELD), indexOptions);
  }

  /**
   * Return instance of the {@link UserService}
   *
   * @return instance of the {@link UserService}
   */
  public static DatabaseService<UserModel> getInstance() {
    if (service == null) {
      service = new UserService();
    }
    return service;
  }
}
