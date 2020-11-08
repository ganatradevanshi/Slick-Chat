package com.neu.prattle.service.dbservice;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.Iterator;

/**
 * This is an abstract implementation of {@link DatabaseService} on model T.
 *
 * @param <T> - Model handled by the service.
 */
abstract class AbstractDatabaseService<T> implements DatabaseService<T> {

    protected MongoCollection<T> collection;

    @Override
    public Iterator<T> findAll() {
        return collection.find().iterator();
    }

    @Override
    public T findById(ObjectId id) {
        return collection.find(Filters.eq("_id", id)).first();
    }

    @Override
    public Iterator<T> findBy(Bson filters) {
        return collection.find(filters).iterator();
    }

    @Override
    public Iterator<T> aggregate(Bson filters, String from, String localField, String foreignField, String as) {
        return collection.aggregate(Arrays.asList(
                Aggregates.match(filters),
                Aggregates.lookup(from, localField, foreignField, as)
        )).iterator();
    }

    @Override
    public void add(T t) {
        collection.insertOne(t);
    }

    @Override
    public void replaceOne(Bson filters, T t) {
        collection.replaceOne(filters, t);
    }

    @Override
    public void deleteById(ObjectId id) {
        collection.deleteOne(Filters.eq("_id", id));
    }

    @Override
    public void updateOne(Bson var1, Bson var2){
       collection.updateOne(var1, var2);
    }

}
