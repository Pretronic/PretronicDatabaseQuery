/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 11.03.20, 20:28
 * @website %web%
 *
 * The PretronicDatabaseQuery Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package net.pretronic.databasequery.api.collection;

import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.query.ForeignKey;
import net.pretronic.databasequery.api.query.QueryGroup;
import net.pretronic.databasequery.api.query.type.CreateQuery;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.entry.DocumentEntry;

import java.io.File;

/**
 * The {@link CollectionCreator} represents a creator of {@link DatabaseCollection}, which creates one or multiple {@link DatabaseCollection} with a document.
 * An example of the document structure in json is on the github repository named table.json.
 */
public class CollectionCreator {

    /**
     *
     * @param database on which the collections will be created
     * @param location of the document file
     */
    public static void create(Database database, File location) {
        create(database,Document.read(location));
    }

    /**
     * Creates all configured {@link DatabaseCollection} with the given information in the document {@code document}
     * on the given database {@code database}.
     *
     * @param database on which the collections will be created
     * @param document which includes the information for creating the database collections
     */
    public static void create(Database database,Document document) {
        QueryGroup group = database.group();
        for (DocumentEntry collection : document.getDocument("collections")) {
            String name = collection.toDocument().getString("name");
            CreateQuery createQuery = database.createCollection(name);

            String type = collection.toDocument().getString("type");
            if(type != null)createQuery.type(DatabaseCollectionType.valueOf(type.toUpperCase()));

            String engine = collection.toDocument().getString("engine");
            if(engine != null) createQuery.engine(engine);

            createFields(createQuery,collection);
            createForeignKeys(collection, createQuery);

            group.add(createQuery);
        }
        group.execute();
    }

    private static void createFields(CreateQuery createQuery, DocumentEntry collection){
        for (DocumentEntry field : collection.toDocument().getDocument("fields")) {
            String fieldName = field.toDocument().getString("name");
            DataType dataType = DataType.valueOf(field.toDocument().getString("type").toUpperCase());
            int fieldSize = field.toDocument().getInt("size");
            Object defaultValue = null;
            if(field.toDocument().contains("default")) {
                defaultValue = field.toDocument().getEntry("default").toPrimitive().getAsObject();
            }
            FieldOption[] options = null;
            if(field.toDocument().contains("options")) {
                options = new FieldOption[field.toDocument().getDocument("options").size()];
                int index = 0;
                for (DocumentEntry option : field.toDocument().getDocument("options")) {
                    options[index] = FieldOption.valueOf(option.toPrimitive().getAsString());
                    index++;
                }
            }
            ForeignKey foreignKey = null;
            if(field.toDocument().contains("foreignKey")) {
                foreignKey = getForeignKey(field.toDocument().getDocument("foreignKey"));
            }
            createQuery.field(fieldName, dataType, fieldSize != 0 ? fieldSize : -1, defaultValue, foreignKey, options);
        }
    }

    private static void createForeignKeys(DocumentEntry collection, CreateQuery createQuery) {
        for (DocumentEntry foreignKeyDocument : collection.toDocument().getDocument("foreignKeys")) {
            String field = foreignKeyDocument.toDocument().getString("field");
            createQuery.foreignKey(field, getForeignKey(foreignKeyDocument.toDocument()));
        }
    }

    private static ForeignKey getForeignKey(Document foreignKeyDocument) {
        String deleteOption = foreignKeyDocument.getString("deleteOption");
        String updateOption = foreignKeyDocument.getString("updateOption");

        String[] reference = foreignKeyDocument.getString("reference").split("\\.");
        if(reference.length != 3) throw new IllegalArgumentException("Invalid reference length");
        return new ForeignKey(reference[0],
                reference[1],
                reference[2],
                deleteOption != null ? ForeignKey.Option.valueOf(deleteOption.toUpperCase()) : null,
                updateOption != null ? ForeignKey.Option.valueOf(updateOption.toUpperCase()) : null);
    }
}
