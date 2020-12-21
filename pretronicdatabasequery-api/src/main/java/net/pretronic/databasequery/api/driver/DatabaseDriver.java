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
import net.pretronic.databasequery.api.datatype.adapter.defaults.InetAddressAdapter;
import net.pretronic.databasequery.api.datatype.adapter.defaults.InetSocketAddressAdapter;
import net.pretronic.databasequery.api.datatype.adapter.defaults.UUIDDataTypeAdapter;
import net.pretronic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.pretronic.libraries.logging.PretronicLogger;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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

    /**
     * Get the configured logger of this driver.
     *
     * @return logger
     */
    PretronicLogger getLogger();

    /**
     * Returns the executor service for this driver. It is used by async methods in the queries.
     *
     * @return executor service
     */
    ExecutorService getExecutorService();

    /**
     * Gets a database from the connected host.
     *
     * @param name of database
     * @return database instance
     */
    Database getDatabase(String name);

    /**
     * Checks if the database driver is connected.
     *
     * @return if connected
     */
    boolean isConnected();

    /**
     * Connect to the database.
     */
    void connect();

    /**
     * Disconnect from the database.
     */
    void disconnect();


    /**
     * Get all registered data type adapter. They are used by inserting or getting in the {@link net.pretronic.databasequery.api.query.result.QueryResultEntry}.
     *
     * @return registered data type adapter
     */
    Map<Class<?>, DataTypeAdapter<?>> getDataTypeAdapters();

    /**
     * Get a data type adapter or null.
     *
     * @param clazz of the data type adapter
     * @param <T> type of the data type
     * @return data type adapter or null
     */
    <T> DataTypeAdapter<T> getDataTypeAdapter(Class<T> clazz);

    /**
     * Register a {@link DataTypeAdapter} with the given parameters.
     *
     * @param clazz of the data type
     * @param adapter instance
     * @param <T> adapter type
     */
    <T extends R ,R> void registerDataTypeAdapter(Class<T> clazz, DataTypeAdapter<R> adapter);

    /**
     * Unregister, if exist a data type adapter with the class.
     *
     * @param clazz of the data type adapter
     */
    void unregisterDataTypeAdapter(Class<?> clazz);

    /**
     * Registers all default data type adapter. It should be implemented by default in the {@link DatabaseDriver} implementation.
     */
    default void registerDefaultAdapters(){
        registerDataTypeAdapter(UUID.class,new UUIDDataTypeAdapter());
        registerDataTypeAdapter(Inet4Address.class, new InetAddressAdapter());
        registerDataTypeAdapter(Inet6Address.class, new InetAddressAdapter());
        registerDataTypeAdapter(InetSocketAddress.class, new InetSocketAddressAdapter());
    }
}
