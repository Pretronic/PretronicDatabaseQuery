/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 17.09.19, 13:32
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

package net.prematic.databasequery.core.config;

import net.prematic.databasequery.core.DatabaseDriver;
import net.prematic.libraries.document.simple.SimpleDocument;

import java.util.Map;

public class DocumentDatabaseDriverConfig<T extends DocumentDatabaseDriverConfig> extends SimpleDocument implements DatabaseDriverConfig<DocumentDatabaseDriverConfig> {

    private final Class<? extends DatabaseDriver> driverClass;

    public DocumentDatabaseDriverConfig(Class<? extends DatabaseDriver> driverClass) {
        super(null);
        this.driverClass = driverClass;
    }

    @Override
    public Class<? extends DatabaseDriver> getDriverClass() {
        return this.driverClass;
    }

    @Override
    public Map<String, Object> getProperties() {
        return getAsMap(String.class, Object.class);
    }

    @Override
    public Object getProperty(String key) {
        return getDocument(key).toPrimitive().getAsObject();
    }

    @Override
    public T addProperty(String key, Object value) {
        add(key, value);
        return (T) this;
    }

    @Override
    public String getHost() {
        return getString("host");
    }

    @Override
    public T setHost(String host) {
        set("host", host);
        return (T) this;
    }

    @Override
    public int getPort() {
        return getInt("port");
    }

    @Override
    public T setPort(int port) {
        set("port", port);
        return (T) this;
    }
}
