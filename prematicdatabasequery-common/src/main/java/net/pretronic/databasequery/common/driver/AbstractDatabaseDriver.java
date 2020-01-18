/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 16.12.19, 15:03
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

package net.pretronic.databasequery.common.driver;

import net.prematic.databasequery.api.datatype.adapter.DataTypeAdapter;
import net.prematic.databasequery.api.driver.DatabaseDriver;
import net.prematic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.prematic.libraries.logging.PrematicLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public abstract class AbstractDatabaseDriver implements DatabaseDriver {

    private final String name;
    private final String type;
    private final DatabaseDriverConfig<?> config;
    private final PrematicLogger logger;
    private final ExecutorService executorService;
    private final Map<Class<?>, DataTypeAdapter<?>> dataTypeAdapters;

    public AbstractDatabaseDriver(String name, String type, DatabaseDriverConfig<?> config, PrematicLogger logger, ExecutorService executorService) {
        this.name = name;
        this.type = type;
        this.config = config;
        this.logger = logger;
        this.executorService = executorService;
        this.dataTypeAdapters = new HashMap<>();

        registerDefaultAdapters();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public DatabaseDriverConfig<?> getConfig() {
        return this.config;
    }

    @Override
    public PrematicLogger getLogger() {
        return this.logger;
    }

    @Override
    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    @Override
    public Map<Class<?>, DataTypeAdapter<?>> getDataTypeAdapters() {
        return this.dataTypeAdapters;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> DataTypeAdapter<T> getDataTypeAdapter(Class<T> clazz) {
        return (DataTypeAdapter<T>) this.dataTypeAdapters.get(clazz);
    }

    @Override
    public <T> void registerDataTypeAdapter(Class<T> clazz, DataTypeAdapter<T> adapter) {
        this.dataTypeAdapters.put(clazz, adapter);
    }

    @Override
    public void unregisterDataTypeAdapter(Class<?> clazz) {
        this.dataTypeAdapters.remove(clazz);
    }
}
