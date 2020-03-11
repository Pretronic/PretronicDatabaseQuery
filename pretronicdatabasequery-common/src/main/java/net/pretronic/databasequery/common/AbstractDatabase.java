/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 18.12.19, 15:55
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

package net.pretronic.databasequery.common;

import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.libraries.logging.PretronicLogger;

public abstract class AbstractDatabase<T extends DatabaseDriver> implements Database {

    private final String name;
    private final T driver;

    public AbstractDatabase(String name, T driver) {
        this.name = name;
        this.driver = driver;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public T getDriver() {
        return this.driver;
    }

    public PretronicLogger getLogger() {
        return this.driver.getLogger();
    }
}
