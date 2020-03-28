package net.pretronic.databasequery.mongodb.collection;

import com.mongodb.client.MongoCollection;
import net.pretronic.databasequery.api.collection.AliasDatabaseCollection;
import net.pretronic.databasequery.api.collection.DatabaseCollectionType;
import net.pretronic.databasequery.api.collection.field.CollectionField;
import net.pretronic.databasequery.api.query.QueryGroup;
import net.pretronic.databasequery.api.query.QueryTransaction;
import net.pretronic.databasequery.api.query.type.*;
import net.pretronic.databasequery.common.collection.AbstractDatabaseCollection;
import net.pretronic.databasequery.mongodb.MongoDBDatabase;
import net.pretronic.databasequery.mongodb.query.MongoDBFindQuery;
import net.pretronic.databasequery.mongodb.query.MongoDBInsertQuery;
import org.bson.Document;

import java.util.Collection;

public class MongoDBDatabaseCollection extends AbstractDatabaseCollection<MongoDBDatabase> {

    private final MongoCollection<Document> collection;

    public MongoDBDatabaseCollection(String name, MongoDBDatabase database, DatabaseCollectionType type) {
        super(name, database, type);
        this.collection = database.getDatabase().getCollection(name);
    }

    @Override
    public long getSize() {
        return this.collection.countDocuments();
    }

    @Override
    public InsertQuery insert() {
        return new MongoDBInsertQuery(this);
    }

    @Override
    public FindQuery find() {
        return new MongoDBFindQuery(this);
    }

    @Override
    public UpdateQuery update() {
        return null;
    }

    @Override
    public ReplaceQuery replace() {
        return null;
    }

    @Override
    public DeleteQuery delete() {
        return null;
    }

    @Override
    public void drop() {
        this.collection.drop();
    }

    @Override
    public void clear() {

    }

    @Override
    public QueryTransaction transact() {
        return null;
    }

    @Override
    public QueryGroup group() {
        return null;
    }

    @Override
    public Collection<CollectionField> getFields() {
        return null;
    }

    @Override
    public CollectionField getField(String name) {
        return null;
    }

    @Override
    public boolean hasField(String name) {
        return false;
    }

    @Override
    public CollectionField addField(String name) {
        return null;
    }

    @Override
    public AliasDatabaseCollection as(String alias) {
        return null;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }
}
