/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.12.19, 15:56
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

package net.pretronic.databasequery.sql.driver.datasource;

import net.prematic.libraries.utility.Validate;
import net.pretronic.databasequery.sql.driver.SQLDatabaseDriver;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public interface SQLDataSourceFactory {

    Map<Class<? extends DataSource>, SQLDataSourceFactory> FACTORIES = new HashMap<>();


    DataSource createDataSource(SQLDatabaseDriver driver);


    static DataSource create(SQLDatabaseDriver driver) {
        Validate.notNull(driver);
        SQLDataSourceFactory factory = FACTORIES.get(driver.getConfig().getDataSourceClass());
        Validate.notNull(factory);
        return factory.createDataSource(driver);
    }

    static void register(Class<? extends DataSource> dataSourceClass, SQLDataSourceFactory factory) {
        FACTORIES.put(dataSourceClass, factory);
    }
}