/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 08.12.19, 16:15
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

package net.prematic.databasequery.api.driver.config;

import net.prematic.databasequery.api.driver.DatabaseDriver;
import net.prematic.libraries.document.Document;
import net.prematic.libraries.document.DocumentRegistry;
import net.prematic.libraries.utility.interfaces.Castable;

public interface DatabaseDriverConfig<T extends DatabaseDriverConfig<T>> extends Castable<T> {

    String getName();

    Class<? extends DatabaseDriver> getDriverClass();

    String getConnectionString();

    Document toDocument();

    static void registerDocumentAdapter(){
        DocumentRegistry.getDefaultContext().registerAdapter(DatabaseDriverConfig.class,new DatabaseDriverConfigDocumentAdapter());
    }

}
