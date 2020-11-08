package com.neu.prattle.service.dbservice;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import fse.team2.common.models.mongomodels.Group;

/**
 * Concrete implementation of DatabaseService for User model.
 */
public class GroupService extends AbstractDatabaseService<Group> {

  protected static DatabaseService<Group> service;

  private GroupService() {
    collection = MongoConnection.getCollection(Group.COLLECTION_NAME, Group.class);
    addIndexes();
  }

  /**
   * Add indexes to the user collection to ensure unique constraints.
   */
  private void addIndexes() {
    IndexOptions indexOptions = new IndexOptions().unique(true);
    collection.createIndex(Indexes.ascending(Group.GROUPNAME_FIELD), indexOptions);
  }

  /**
   * Return instance of the {@link GroupService}
   *
   * @return instance of the {@link GroupService}
   */
  public static DatabaseService<Group> getInstance() {
    if (service == null) {
      service = new GroupService();
    }
    return service;
  }


}
