/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.05.19 09:46
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

package net.prematic.databasequery.core;

import net.prematic.databasequery.core.datatype.DataTypeAdapter;
import net.prematic.databasequery.core.datatype.adapters.UUIDDataTypeAdapter;
import net.prematic.libraries.utility.annonations.Nullable;

import java.util.Collection;
import java.util.Iterator;

public interface DatabaseDriver {

    String getName();

    String getType();

    Database getDatabase(String name);

    boolean isConnected();

    void dropDatabase(String name);

    void connect();

    void disconnect();

    Collection<DataTypeAdapter> getDataTypeAdapters();

    default void registerDataTypeAdapter(DataTypeAdapter adapter) {
        getDataTypeAdapters().add(adapter);
    }

    default boolean unregisterDataTypeAdapter(Class<? extends DataTypeAdapter> adapterClass) {
        Iterator<DataTypeAdapter> iterator = getDataTypeAdapters().iterator();
        while (iterator.hasNext()) {
            DataTypeAdapter dataTypeAdapter = iterator.next();
            if(dataTypeAdapter.getClass() == adapterClass) {
                getDataTypeAdapters().remove(dataTypeAdapter);
                return true;
            }
        }
        return false;
    }

    @Nullable
    default DataTypeAdapter getDataTypeAdapterByWriteClass(Class<?> writeClass) {
        for (DataTypeAdapter dataTypeAdapter : getDataTypeAdapters()) {
            if(dataTypeAdapter.getWriteClass() == writeClass) return dataTypeAdapter;
        }
        return null;
    }

    @Nullable
    default DataTypeAdapter getDataTypeAdapterByReadClass(Class<?> readClass) {
        for (DataTypeAdapter dataTypeAdapter : getDataTypeAdapters()) {
            if(dataTypeAdapter.getReadClass() == readClass) return dataTypeAdapter;
        }
        return null;
    }

    default void registerDefaultAdapters() {
        registerDataTypeAdapter(new UUIDDataTypeAdapter());
    }
}