/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.12.19, 16:32
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

import net.prematic.databasequery.api.datatype.DataType;
import net.prematic.databasequery.api.query.ForeignKey;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface CollectionField {

    String getName();

    DataType getType();

    int getSize();

    Object getDefaultValue();

    Collection<FieldOption> getOptions();

    default boolean hasCreateOption(FieldOption createOption) {
        return getOptions().contains(createOption);
    }


    void setName(String name);

    void setSize(int size);

    void setDefaultValue(Object defaultValue);

    void addCreateOption(FieldOption createOption);

    void removeCreateOption(FieldOption createOption);

    void addForeignKey(ForeignKey foreignKey);

    void removeForeignKey();


    void update();

    CompletableFuture<Void> updateAsync();


    void remove();

    CompletableFuture<Void> removeAsync();
}