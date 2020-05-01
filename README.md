# PretronicDatabaseQuery
Welcome to the official PretronicDatabaseQuery repository.

### Introduction
The Pretronic database query is a java database framework for reading data from different databases. 
It provides many functionalities for reading, writing and updating database on different remote and local databases. 


### Supported Databases
 * SQL 
    * MySQL
    * MariaDB
    * H2-Portable
    * PostgreSql (planned)
    * MS-SQL (planned)
 * MongoDB (Development / Unstable)


### Example
```java
import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.type.FindQuery;
import net.pretronic.databasequery.sql.dialect.Dialect;
import net.pretronic.databasequery.sql.driver.SQLDatabaseDriver;
import net.pretronic.databasequery.sql.driver.config.SQLDatabaseDriverConfigBuilder;
import net.pretronic.libraries.logging.PretronicLogger;
import net.pretronic.libraries.logging.PretronicLoggerFactory;
import net.pretronic.libraries.logging.bridge.slf4j.SLF4JStaticBridge;
import net.pretronic.libraries.logging.level.LogLevel;
import net.pretronic.libraries.utility.GeneralUtil;

import java.net.InetSocketAddress;

public class Test {

    public static void main(String[] args) {
        PretronicLogger logger = PretronicLoggerFactory.getLogger();
        logger.setLevel(LogLevel.ALL);
        SLF4JStaticBridge.setLogger(logger); //Optional to set logger for slf4j. SLF4J loggger is needed for HikariCP.



        DatabaseDriver databaseDriver = new SQLDatabaseDriver("SQL-Connector",
                new SQLDatabaseDriverConfigBuilder().setAddress(new InetSocketAddress("127.0.0.1", 3306))
                        .setDialect(Dialect.MARIADB)
                        .setUsername("root")
                        .setPassword("<masked>")
                        .build(), logger
                , GeneralUtil.getDefaultExecutorService());

        databaseDriver.connect();

        Database database = databaseDriver.getDatabase("Intern");

        DatabaseCollection collection = database.createCollection("customers")
                .field("id", DataType.INTEGER, FieldOption.AUTO_INCREMENT, FieldOption.PRIMARY_KEY)
                .field("name", DataType.STRING, FieldOption.NOT_NULL)
                .field("firstName", DataType.STRING, FieldOption.NOT_NULL)
                .field("verified", DataType.BOOLEAN, 1, false, FieldOption.NOT_NULL)
                .field("phoneNumber", DataType.STRING)
                .create();

        int generatedId = collection.insert()
                .set("name", "Shepard")
                .set("firstName", "Bill")
                .executeAndGetGeneratedKeyAsInt("id");

        collection.insert()
                .set("name", "Shepard")
                .set("firstName", "Steve")
                .execute();

        collection.insert()
                .set("name", "McCain")
                .set("firstName", "Jack")
                .execute();

        FindQuery byName = collection.find().where("name", "Shepard");
        QueryResult result = byName.execute();

        result.forEach(entry -> {
            int id = entry.getInt("id");
            String name = entry.getString("name");
            String firstName = entry.getString("firstName");
            boolean verified = entry.getBoolean("verified");
            String phoneNumber = entry.getString("phoneNumber");
        });

        collection.update().set("phoneNumber", "+49 000000").where("firstName", "Bill").execute();
        
        collection.delete().where("firstName").execute("Jack");
    }
}
```