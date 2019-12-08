/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.12.19, 17:05
 * @website %web%
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

package net.prematic.databasequery.api.collection.field;

import net.prematic.databasequery.api.collection.DatabaseCollection;
import net.prematic.databasequery.api.datatype.DataType;
import net.prematic.databasequery.api.query.ForeignKey;

public interface FieldBuilder {

    FieldBuilder name(String name);

    FieldBuilder type(DataType type);

    FieldBuilder size(int size);

    FieldBuilder defaultValue(Object value);

    FieldBuilder foreignKey(ForeignKey foreignKey);

    FieldBuilder options(FieldOption... options);


    default FieldBuilder foreignKey(DatabaseCollection collection, String otherField, ForeignKey.Option deleteOption, ForeignKey.Option updateOption){
        return foreignKey(ForeignKey.of(collection,otherField,deleteOption,updateOption));
    }

    default FieldBuilder foreignKey(DatabaseCollection collection, String otherField, ForeignKey.Option option){
        return foreignKey(ForeignKey.of(collection,otherField,option));
    }

    default FieldBuilder foreignKey(DatabaseCollection collection, String otherField){
        return foreignKey(ForeignKey.of(collection,otherField));
    }
}
