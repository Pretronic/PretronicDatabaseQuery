/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.10.19, 20:44
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

package net.prematic.databasequery.api;

import net.prematic.databasequery.api.config.DatabaseDriverConfig;
import net.prematic.databasequery.api.datatype.adapter.DataTypeAdapter;
import net.prematic.databasequery.api.datatype.adapter.defaults.UUIDDataTypeAdapter;
import net.prematic.libraries.logging.PrematicLogger;
import net.prematic.libraries.utility.annonations.NotNull;
import net.prematic.libraries.utility.annonations.Nullable;
import net.prematic.libraries.utility.reflect.TypeReference;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public interface DatabaseDriver {

    Map<Class<?>, Creator> CREATORS = new HashMap<>();

    String getName();

    String getType();

    Database getDatabase(String name);

    boolean isConnected();

    void dropDatabase(String name);

    void connect();

    void disconnect();

    Collection<DataTypeAdapter> getDataTypeAdapters();

    default void registerDataTypeAdapter(@NotNull DataTypeAdapter adapter) {
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
        if(writeClass == null) return null;
        for (DataTypeAdapter dataTypeAdapter : getDataTypeAdapters()) {
            TypeReference typeReference = new TypeReference(dataTypeAdapter.getClass());
            Type[] type = typeReference.getGenericInterfaceArgument(0, 0);
            if(type[0] == writeClass) return dataTypeAdapter;
        }
        return null;
    }

    @Nullable
    default DataTypeAdapter getDataTypeAdapterByReadClass(Class<?> readClass) {
        if(readClass == null) return null;
        for (DataTypeAdapter dataTypeAdapter : getDataTypeAdapters()) {
            TypeReference typeReference = new TypeReference(dataTypeAdapter.getClass());
            Type[] type = typeReference.getGenericInterfaceArgument(0, 1);
            if(type[0] == readClass) return dataTypeAdapter;
        }
        return null;
    }

    default void registerDefaultAdapters() {
        registerDataTypeAdapter(new UUIDDataTypeAdapter());
    }

    PrematicLogger getLogger();

    ExecutorService getExecutorService();

    static Creator getCreator(Class<?> databaseDriverClass) {
        return CREATORS.get(databaseDriverClass);
    }

    static void registerCreator(Class<?> databaseDriverClass, Creator creator) {
        CREATORS.put(databaseDriverClass, creator);
    }

    interface Creator {

        DatabaseDriver create(String name, DatabaseDriverConfig config, PrematicLogger logger, ExecutorService executorService, Object... properties);
    }
}