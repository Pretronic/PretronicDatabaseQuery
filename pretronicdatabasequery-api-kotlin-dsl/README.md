

# Introduction

//@Todo create

### How to include in your project?

#### Gradle

```groovy
compile group: 'net.pretronic.databasequery', name: 'pretronicdatabasequery-api-kotlin-dsl', version: '1.0.0'
```

#### Maven

```xml
<dependency>
    <groupId>net.pretronic.databasequery</groupId>
    <artifactId>pretronicdatabasequery-api-kotlin-dsl</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```

#### Example
```kotlin
import net.pretronic.databasequery.api.Database
import net.pretronic.databasequery.api.collection.DatabaseCollection
import net.pretronic.databasequery.api.collection.field.FieldOption
import net.pretronic.databasequery.api.datatype.DataType
import net.pretronic.databasequery.api.query.result.QueryResult

class Example {

    fun main(){
        //@Todo setup your database and driver connection
        val database: Database? = null

        //Create a new collection
        val collection : DatabaseCollection = database.createCollection("Employee"){
            field("Id", DataType.INTEGER,FieldOption.PRIMARY_KEY,FieldOption.AUTO_INCREMENT)
            field("FirstName", DataType.STRING,64)
            field("LastName", DataType.STRING,64)
            field("Organisation", DataType.STRING,64)
        }.create()


        //Insert a new row
        val id : Int = collection.insert {
            set("FirstName","Peter")
            set("LastName","Meier")
            set("Organisation","XY")
        }.executeAndGetGeneratedKeyAsInt("Id")


        //Find a row
        val result : QueryResult = collection.find {
            where("Id",10)
            or {
                where("FirstName","Peter")
                where("LastName","Meier")
            }
        }.execute()
    }
}
```
