package net.pretronic.databasequery.mongodb.query;

import com.mongodb.client.model.Filters;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.type.join.JoinType;
import net.pretronic.databasequery.common.query.type.AbstractFindQuery;
import net.pretronic.databasequery.mongodb.collection.MongoDBDatabaseCollection;
import org.bson.BsonArray;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class MongoDBFindQuery extends AbstractFindQuery<MongoDBDatabaseCollection> {

    public MongoDBFindQuery(MongoDBDatabaseCollection collection) {
        super(collection);
    }

    @Override
    public QueryResult execute(Object... values) {
        List<Bson> find = new ArrayList<>();
        buildEntries(find, this.entries);
        find.add(Filters.and(Filters.not(new Document("number", 6))));

        System.out.println("find");
        for (Document document : collection.getCollection().aggregate(find)) {
            System.out.println(document);
        }
        System.out.println("end find");
        return null;
    }

    private void buildEntries(List<Bson> find, List<Entry> entries) {
        /*for (Entry entry : entries) {
            if(entry instanceof ConditionEntry) {
                buildConditionEntry(find, (ConditionEntry) entry);
            } else if(entry instanceof OperationEntry) {
                buildOperationEntry(find, (OperationEntry) entry);
            } else if(entry instanceof JoinEntry) {
                buildJoinEntry(find, (JoinEntry) entry);
            } else if(entry instanceof LimitEntry) {
                buildLimitEntry(find, (LimitEntry) entry);
            } else if(entry instanceof OrderByEntry) {
                buildOrderByEntry(find, (OrderByEntry) entry);
            } else if(entry instanceof GroupByEntry) {
                buildGroupByEntry(find, (GroupByEntry) entry);
            }
        }*/
    }

    private void buildConditionEntry(Document find, ConditionEntry entry) {

    }

    private void buildOperationEntry(Document find, OperationEntry entry) {
        switch (entry.getType()) {
            case AND:
            case OR: {
                List<Bson> childFinds = new ArrayList<>();
                for (Entry child : entry.getEntries()) {
                    Document childFind = new Document();
                    //buildEntries(childFind, entry.getEntries());
                    childFinds.add(childFind);
                }
                Document andOr = new Document("$and", childFinds);
                Bson bson = Filters.and(childFinds);

                break;
            }
            case NOT: {


            }
        }
    }

    private void buildJoinEntry(Document find, JoinEntry entry) {

    }

    private void buildLimitEntry(Document find, LimitEntry entry) {

    }

    private void buildOrderByEntry(Document find, OrderByEntry entry) {

    }

    private void buildGroupByEntry(Document find, GroupByEntry entry) {

    }
}
