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

package net.pretronic.databasequery.api.driver;

import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.datatype.adapter.DataTypeAdapter;
import net.pretronic.databasequery.api.datatype.adapter.defaults.UUIDDataTypeAdapter;
import net.pretronic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.pretronic.libraries.logging.PretronicLogger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public interface DatabaseDriver {

    /**
     * Get the name of this diver (Only used for logging and identifying this driver)
     *
     * @return The name as string
     */
    String getName();

    /**
     * Get the database type of this driver.
     *
     * <p>Example: MySql, MongoDB, H2, SqLite</p>
     *
     * @return The type name as string
     */
    String getType();

    /**
     * Get the driver configuration, see {@link DatabaseDriverConfig} for more information.
     *
     * @return The configuration object
     */
    DatabaseDriverConfig<?> getConfig();

    PretronicLogger getLogger();

    ExecutorService getExecutorService();


    Database getDatabase(String name);

    boolean isConnected();

    void connect();

    void disconnect();


    Map<Class<?>, DataTypeAdapter<?>> getDataTypeAdapters();

    <T> DataTypeAdapter<T> getDataTypeAdapter(Class<T> clazz);

    <T> void registerDataTypeAdapter(Class<T> clazz, DataTypeAdapter<T> adapter);

    void unregisterDataTypeAdapter(Class<?> clazz);

    default void registerDefaultAdapters(){
        registerDataTypeAdapter(UUID.class,new UUIDDataTypeAdapter());
    }
}
