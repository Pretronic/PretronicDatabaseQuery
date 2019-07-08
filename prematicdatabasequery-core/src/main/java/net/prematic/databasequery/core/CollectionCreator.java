/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.07.19, 17:02
 *
 * The PrematicDatabaseQuery Project is under the Apache License, version 2.0 (the "License");
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

package net.prematic.databasequery.core;

import net.prematic.databasequery.core.datatype.DataType;
import net.prematic.databasequery.core.query.CreateQuery;
import net.prematic.databasequery.core.query.option.CreateOption;
import net.prematic.libraries.document.Document;
import net.prematic.libraries.document.DocumentEntry;

import java.io.File;

public class CollectionCreator {

    private final Document document;

    public CollectionCreator(Document document) {
        this.document = document;
    }

    public CollectionCreator(File file) {
        this.document = Document.read(file);
    }

    void createDatabaseCollections(Database database) {
        for (DocumentEntry collection : this.document.getDocument("collections")) {
            String name = collection.toDocument().getString("name");
            CreateQuery createQuery = database.createCollection(name);

            String type = collection.toDocument().getString("type");
            if(type != null)createQuery.collectionType(DatabaseCollection.Type.valueOf(type.toUpperCase()));

            String engine = collection.toDocument().getString("engine");
            if(engine != null) createQuery.engine(engine);

            for (DocumentEntry field : collection.toDocument().getDocument("fields")) {
                String fieldName = field.toDocument().getString("name");
                DataType dataType = DataType.valueOf(field.toDocument().getString("dataType").toUpperCase());
                int fieldSize = field.toDocument().getInt("fieldSize");
                Object defaultValue = null;
                if(field.toDocument().contains("default")) {
                    defaultValue = field.toDocument().getEntry("default").toPrimitive().getAsObject();
                }
                CreateOption[] options = null;
                if(field.toDocument().contains("options")) {
                    options = new CreateOption[field.toDocument().getDocument("options").size()];
                    int index = 0;
                    for (DocumentEntry option : field.toDocument().getDocument("options")) {
                        options[index] = CreateOption.valueOf(option.toPrimitive().getAsString());
                        index++;
                    }
                }
                ForeignKey foreignKey = null;
                if(field.toDocument().contains("foreignKey")) {
                    foreignKey = getForeignKey(field.toDocument().getDocument("foreignKey"));
                }
                createQuery.attribute(fieldName, dataType, fieldSize != 0 ? fieldSize : -1, defaultValue, foreignKey, options);
            }
            for (DocumentEntry foreignKeyDocument : collection.toDocument().getDocument("foreignKeys")) {
                String field = foreignKeyDocument.toDocument().getString("field");

                createQuery.foreignKey(field, getForeignKey(foreignKeyDocument.toDocument()));
            }
            createQuery.execute();
        }
    }

    private ForeignKey getForeignKey(Document foreignKeyDocument) {
        String deleteOption = foreignKeyDocument.getString("deleteOption");
        String updateOption = foreignKeyDocument.getString("updateOption");

        String[] reference = foreignKeyDocument.getString("reference").split("\\.");
        if(reference.length != 3) {
            //@Todo throw exception
        }
        return new ForeignKey(reference[0],
                reference[1],
                reference[2],
                deleteOption != null ? ForeignKey.Option.valueOf(deleteOption.toUpperCase()) : null,
                updateOption != null ? ForeignKey.Option.valueOf(updateOption.toUpperCase()) : null);
    }
}