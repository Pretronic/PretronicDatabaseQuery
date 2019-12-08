/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.12.19, 15:56
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

package net.prematic.databasequery.api.driver;

import net.prematic.databasequery.api.Database;
import net.prematic.databasequery.api.datatype.adapter.DataTypeAdapter;
import net.prematic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.prematic.libraries.logging.PrematicLogger;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

public interface DatabaseDriver {

    String getName();

    String getType();

    DatabaseDriverConfig<?> getConfig();

    PrematicLogger getLogger();

    ExecutorService getExecutorService();


    Database getDatabase(String name);

    boolean isConnected();

    void connect();

    void disconnect();


    Collection<DataTypeAdapter<?>> getDataTypeAdapters();

    <T> DataTypeAdapter<T> getDataTypeAdapter(Class<T> clazz);

    <T> void registerDataTypeAdapter(Class<T> clazz, DataTypeAdapter<T> adapter);

    void unregisterDataTypeAdapter(Class<?> clazz);

}