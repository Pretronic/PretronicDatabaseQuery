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
import net.prematic.libraries.utility.Iterators;

import java.util.Collection;

public class MultiDatabaseDriverConfig {

    private final Collection<DatabaseDriverConfig> configs;

    public MultiDatabaseDriverConfig(Collection<DatabaseDriverConfig> configs) {
        this.configs = configs;
    }

    public DatabaseDriverConfig getConfig(Class<? extends DatabaseDriver> databaseDriverClass) {
        return Iterators.findOne(this.configs, config -> config.getDriverName().equals(databaseDriverClass.getName()));
    }

    public <T extends DatabaseDriver> DatabaseDriverConfig getConfig(T databaseDriver) {
        return getConfig(databaseDriver.getClass());
    }

    public MultiDatabaseDriverConfig addConfig(DatabaseDriverConfig config) {
        this.configs.add(config);
        return this;
    }
}