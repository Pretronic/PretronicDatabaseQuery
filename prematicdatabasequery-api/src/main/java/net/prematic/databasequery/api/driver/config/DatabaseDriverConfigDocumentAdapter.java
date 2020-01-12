/*
 * (C) Copyright 2020 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 11.01.20, 22:53
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

import net.prematic.databasequery.api.driver.DatabaseDriverFactory;
import net.prematic.libraries.document.adapter.DocumentAdapter;
import net.prematic.libraries.document.entry.DocumentBase;
import net.prematic.libraries.document.entry.DocumentEntry;
import net.prematic.libraries.utility.reflect.TypeReference;

public class DatabaseDriverConfigDocumentAdapter implements DocumentAdapter<DatabaseDriverConfig> {

    @Override
    public DatabaseDriverConfig read(DocumentBase base, TypeReference<DatabaseDriverConfig> typeReference) {
        if(!base.isObject()) throw new IllegalArgumentException("Database driver config must be an object");
        return DatabaseDriverFactory.create(base.toDocument());
    }

    @Override
    public DocumentEntry write(String key, DatabaseDriverConfig config) {
        return config.toDocument();
    }
}
