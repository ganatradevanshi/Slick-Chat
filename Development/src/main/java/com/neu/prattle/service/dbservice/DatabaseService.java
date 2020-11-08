package com.neu.prattle.service.dbservice;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Iterator;

/**
 * This interface represents a DatabaseService responsible for handling database tasks
 *
 * @param <T> - Model to be handled by the service.
 */
public interface DatabaseService<T> {


  /**
   * Return an iterator for model T to iterate over all the entries for model T.
   *
   * @return - return an iterator for model T for all entries.
   */
  Iterator<T> findAll();

  /**
   * @param id
   * @return
   */
  T findById(ObjectId id);

  /**
   * Returns an iterator to iterate over documents matching the provided filters.
   *
   * @param filters - filters to be used for filtering the documents.
   * @return - an iterator to iterate over documents matching the provided filters.
   */
  Iterator<T> findBy(Bson filters);

  /**
   * Returns an iterator to iterate over documents matching the provided filters and aggregated with
   * referenced documents from a different collection.
   *
   * @param filters      - filters to be used for filtering the documents.
   * @param from         - Specifies the collection in the same database to perform the join with.
   *                     The from collection cannot be sharded.
   * @param localField   - Specifies the field from the documents input to the $lookup stage.
   *                     $lookup performs an equality match on the localField to the foreignField
   *                     from the documents of the from collection. If an input document does not
   *                     contain the localField, the $lookup treats the field as having a value of
   *                     null for matching purposes.
   * @param foreignField - Specifies the field from the documents in the from collection. $lookup
   *                     performs an equality match on the foreignField to the localField from the
   *                     input documents. If a document in the from collection does not contain the
   *                     foreignField, the $lookup treats the value as null for matching purposes.
   * @param as           - Specifies the name of the new array field to add to the input documents.
   *                     The new array field contains the matching documents from the from
   *                     collection. If the specified name already exists in the input document, the
   *                     existing field is overwritten.
   * @return - an iterator to iterate over documents matching the provided filters.
   */
  Iterator<T> aggregate(Bson filters, String from, String localField, String foreignField, String as);

  /**
   * Add an instance of T to the database.
   *
   * @param t - instance of type T to be added to the database.
   */
  void add(T t);

  /**
   * Replace the existing document of T with matching filters.
   *
   * @param filters - filters used to retrieve the document.
   * @param t       - new version of the document.
   */
  void replaceOne(Bson filters, T t);

  /**
   * Delete an existing document with provided id.
   *
   * @param id - id of the document to be deleted.
   */
  void deleteById(ObjectId id);

  /**
   * Update an instance of T.
   *
   * @param query  - query that identifies the instance of T.
   * @param update - update to be made to the instance.
   */
  void updateOne(Bson query, Bson update);
}