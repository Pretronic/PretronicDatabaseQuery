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

package net.pretronic.databasequery.api.driver.config;

import net.pretronic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.DocumentRegistry;
import net.pretronic.libraries.utility.interfaces.Castable;
import net.pretronic.libraries.utility.interfaces.Copyable;

/**
 * The {@link DatabaseDriverConfig} represents the base {@link DatabaseDriver} config-
 * @param <T>
 */
public interface DatabaseDriverConfig<T extends DatabaseDriverConfig<T>> extends Castable<T>, Copyable<T> {

    /**
     * Name of Database driver
     *
     * @return name
     */
    String getName();

    /**
     * Returns the database driver class, which can be configured via this config.
     *
     * @return driver class
     */
    Class<? extends DatabaseDriver> getDriverClass();

    /**
     * Returns the connection string of this config for connecting to the database.
     *
     * @return connection string
     */
    String getConnectionString();

    /**
     * Returns this {@link DatabaseDriverConfig} as an document.
     *
     * @return document
     */
    Document toDocument();

    /**
     * Register the default document database driver adapter.
     */
    static void registerDocumentAdapter(){
        DocumentRegistry.getDefaultContext().registerAdapter(DatabaseDriverConfig.class,new DatabaseDriverConfigDocumentAdapter());
    }
}
