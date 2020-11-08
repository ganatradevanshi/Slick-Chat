package com.neu.prattle.service.dbservice;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationOptions;
import com.neu.prattle.utils.ConfigUtils;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;

import fse.team2.common.models.mongomodels.AuthenticationData;
import fse.team2.common.models.mongomodels.Group;
import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.models.mongomodels.Poll;
import fse.team2.common.models.mongomodels.SpecificProfilePicture;
import fse.team2.common.models.mongomodels.UserActivityData;
import fse.team2.common.models.mongomodels.UserModel;
import fse.team2.common.models.mongomodels.UserResponse;
import fse.team2.common.models.mongomodels.preferences.DefaultProfilePicture;
import fse.team2.common.models.mongomodels.preferences.DoNotDisturb;
import fse.team2.common.models.mongomodels.preferences.Preference;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * This class provides a static methods to return an mongo collection for a specified POJO.
 */
public class MongoConnection {

  public static final String DATABASE_NAME = "slick_chat";
  private static MongoClient client;

  private MongoConnection() {
  }

  /**
   * Return the instance of mongodb client.
   *
   * @return - returns an instance of mongodb client.
   */
  private static MongoClient getClient() {
    if (client == null) {
      CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
          fromProviders(PojoCodecProvider.builder()
              .register(UserModel.class)
              .register(Preference.class)
              .register(Message.class)
              .register(Group.class)
              .register(AuthenticationData.class)
              .register(DoNotDisturb.class)
              .register(DefaultProfilePicture.class)
              .register(SpecificProfilePicture.class)
              .register(UserResponse.class)
              .register(Poll.class)
              .register(UserActivityData.class)
              .build()));

      String connectionStringValue = ConfigUtils.getInstance().getPropertyValue("db.connection_string");
      ConnectionString connectionString = new ConnectionString(connectionStringValue);
      MongoClientSettings settings = MongoClientSettings.builder()
          .applyConnectionString(connectionString)
          .codecRegistry(pojoCodecRegistry)
          .build();

      client = MongoClients.create(settings);
    }
    return client;
  }

  /**
   * Returns the mongo database instance for the application.
   *
   * @return - mongo database instance for the application.
   */
  private static MongoDatabase getDatabase() {
    return getClient().getDatabase(DATABASE_NAME);
  }


  /**
   * Returns a {@link MongoCollection} instance for the provided collection name.
   *
   * @param collectionName    - name of the collection to be retrieved.
   * @param modelClass        - POJO for the collection to be mapped to.
   * @param validationOptions - validation options to be added to the collection while creation.
   * @return - {@link MongoCollection} instance for the collection specified.
   */
  public static MongoCollection getCollection(String collectionName, Class<?> modelClass, ValidationOptions validationOptions) {
    if (!collectionExists(collectionName)) {
      getDatabase().createCollection(collectionName, new CreateCollectionOptions().
          validationOptions(validationOptions));
    }
    return getDatabase().getCollection(collectionName, modelClass);
  }

  /**
   * Returns a {@link MongoCollection} instance for the provided collection name.
   *
   * @param collectionName - name of the collection to be retrieved.
   * @param modelClass     - POJO for the collection to be mapped to.
   * @return - {@link MongoCollection} instance for the collection specified.
   */
  public static MongoCollection getCollection(String collectionName, Class<?> modelClass) {
    if (!collectionExists(collectionName)) {
      getDatabase().createCollection(collectionName);
    }
    return getDatabase().getCollection(collectionName, modelClass);
  }

  /**
   * Returns true if a collection with the provided name already exists.
   *
   * @param collectionName - name of the collection to be checked against.
   * @return - true if the collection already exists.
   */
  private static boolean collectionExists(String collectionName) {
    return getDatabase().listCollectionNames().into(new ArrayList<>()).contains(collectionName);
  }
}
