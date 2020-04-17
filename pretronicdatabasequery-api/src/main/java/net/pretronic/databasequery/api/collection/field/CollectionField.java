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

package net.pretronic.databasequery.api.collection.field;

import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.query.ForeignKey;
import net.pretronic.libraries.utility.annonations.NotNull;
import net.pretronic.libraries.utility.annonations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * The {@link CollectionField} represents a field of a {@link net.pretronic.databasequery.api.collection.DatabaseCollection}.
 * It can be get by using the method {@link net.pretronic.databasequery.api.collection.DatabaseCollection#getField(String)}.
 * Some methods may not be implemented in NoSQL databases.
 */
public interface CollectionField {

    /**
     * Returns the name of this collection field.
     *
     * @return name
     */
    String getName();

    /**
     * Returns the data type of this collection field.
     *
     * @return type of collection field
     */
    DataType getType();

    /**
     * Returns the max data size of this collection field. Some {@link DataType} doesn't have a size.
     *
     * @return size of collection field
     */
    int getSize();

    /**
     * Returns the default value of this collection value.
     *
     * @return default value
     */
    @Nullable
    Object getDefaultValue();

    /**
     * Returns all field options of this collection field.
     *
     * @return options
     */
    Collection<FieldOption> getOptions();

    /**
     * Returns whether this collection field has the {@code createOption} or not
     *
     * @param createOption to check
     * @return belonging of createOption
     */
    default boolean hasFieldOption(FieldOption createOption) {
        return getOptions().contains(createOption);
    }

    /**
     * Sets the new {@code name} of this collection field. It can not be null.
     *
     * @param name of this field
     */
    void setName(@NotNull String name);

    /**
     * Sets the new size of this collection field. Be carefully by reducing of size. Some data may be deleted or an error occurred.
     * The {@code size} must be greater than 0.
     *
     * @param size of this field
     */
    void setSize(int size);

    /**
     * Sets the new default value of this field. If {@code defaultValue} is null, then the default value is disabled.
     *
     * @param defaultValue of this field
     */
    void setDefaultValue(Object defaultValue);

    /**
     * Adds a field option to this field.
     *
     * @param createOption to be added
     */
    void addFieldOption(@NotNull FieldOption createOption);

    /**
     * Removes a field option of this field
     *
     * @param createOption to be removed
     */
    void removeFieldOption(@NotNull FieldOption createOption);

    /**
     * Adds a related {@link ForeignKey} to this field.
     *
     * @param foreignKey to be added
     */
    void addForeignKey(ForeignKey foreignKey);

    /**
     * Removes {@link ForeignKey} of this field.
     */
    void removeForeignKey();

    /**
     * Updates all done updates of this field by the above editing methods.
     */
    void update();

    /**
     * Updates async {@link #update()}.
     *
     * @return future if updated
     */
    CompletableFuture<Void> updateAsync();


    /**
     * Removes this {@link CollectionField} of his related {@link net.pretronic.databasequery.api.collection.DatabaseCollection}.
     */
    void remove();

    /**
     * Removes {@link #remove()} async.
     *
     * @return future if updated
     */
    CompletableFuture<Void> removeAsync();
}
