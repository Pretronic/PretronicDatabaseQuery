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

package net.prematic.databasequery.api.config;

import net.prematic.databasequery.api.DatabaseDriver;
import net.prematic.databasequery.api.exceptions.DatabaseQueryException;
import net.prematic.libraries.document.Document;
import net.prematic.libraries.document.WrappedDocument;
import net.prematic.libraries.logging.PrematicLogger;
import net.prematic.libraries.logging.PrematicLoggerFactory;
import net.prematic.libraries.utility.map.caseintensive.CaseIntensiveHashMap;
import net.prematic.libraries.utility.map.caseintensive.CaseIntensiveMap;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class DatabaseDriverConfig<T extends DatabaseDriverConfig> extends WrappedDocument {

    static CaseIntensiveMap<Class<? extends DatabaseDriverConfig>> REGISTRY = new CaseIntensiveHashMap<>();

    public DatabaseDriverConfig(Class<? extends DatabaseDriver> driverClass) {
        super(Document.newDocument().add("driverName", driverClass.getName()));
    }

    public DatabaseDriverConfig(Document original) {
        super(original);
    }

    public Class<?> getDriverClass() {
        try {
            return Class.forName(getDriverName());
        } catch (ClassNotFoundException e) {
            throw new DatabaseQueryException(String.format("Can't get driver class %s.", getDriverClass()));
        }
    }

    public String getDriverName() {
        return getString("driverName");
    }

    public Map<String, Object> getProperties() {
        return getAsMap(String.class, Object.class);
    }

    public Object getProperty(String key) {
        return getDocument(key).toPrimitive().getAsObject();
    }

    public T addProperty(String key, Object value) {
        add(key, value);
        return (T) this;
    }

    public String getHost() {
        return getString("host");
    }

    public T setHost(String host) {
        set("host", host);
        return (T) this;
    }

    public int getPort() {
        return getInt("port");
    }

    public T setPort(int port) {
        set("port", port);
        return (T) this;
    }

    public InetSocketAddress getAddress() {
        return new InetSocketAddress(getHost(), getPort());
    }

    public DatabaseDriver createDatabaseDriver(String name) {
        return createDatabaseDriver(name, PrematicLoggerFactory.getLogger(name));
    }

    public DatabaseDriver createDatabaseDriver(String name, PrematicLogger logger) {
        return createDatabaseDriver(name, logger, Executors.newCachedThreadPool());
    }

    public DatabaseDriver createDatabaseDriver(String name, PrematicLogger logger, ExecutorService executorService) {
        DatabaseDriver.Creator creator = DatabaseDriver.getCreator(getDriverClass());
        return creator.create(name, this, logger, executorService);
    }

    public static Class<? extends DatabaseDriverConfig> getDriverConfigNameByDriverName(String driverName) {
        return REGISTRY.get(driverName);
    }

    public static void registerDriverConfig(String driverName, Class<? extends DatabaseDriverConfig> driverConfigClass) {
        REGISTRY.put(driverName, driverConfigClass);
    }
}