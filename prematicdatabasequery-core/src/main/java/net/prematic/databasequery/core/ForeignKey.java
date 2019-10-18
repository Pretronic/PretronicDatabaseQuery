/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 07.05.19, 13:58
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

public class ForeignKey {

    private final String database, collection, field;
    private final Option deleteOption, updateOption;

    public ForeignKey(String database, String collection, String field, Option deleteOption, Option updateOption) {
        this.database = database;
        this.collection = collection;
        this.field = field;
        this.deleteOption = deleteOption;
        this.updateOption = updateOption;
    }

    public String getDatabase() {
        return database;
    }

    public String getCollection() {
        return collection;
    }

    public String getField() {
        return field;
    }

    public Option getDeleteOption() {
        return deleteOption;
    }

    public Option getUpdateOption() {
        return updateOption;
    }

    public enum Option {

        DEFAULT,
        CASCADE,
        SET_NULL

    }

    public static ForeignKey of(DatabaseCollection collection, String field, Option deleteOption, Option updateOption){
        return new ForeignKey(collection.getDatabase().getName(),collection.getName(),field,deleteOption,updateOption);
    }

    public static ForeignKey of(DatabaseCollection collection, String field, Option option){
        return new ForeignKey(collection.getDatabase().getName(),collection.getName(),field,option,option);
    }

    public static ForeignKey of(DatabaseCollection collection, String field){
        return of(collection,field,Option.DEFAULT,Option.DEFAULT);
    }
}
