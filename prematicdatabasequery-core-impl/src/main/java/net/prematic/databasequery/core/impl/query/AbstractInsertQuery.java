/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.05.19, 21:51
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

package net.prematic.databasequery.core.impl.query;

import net.prematic.databasequery.core.QueryOperator;
import net.prematic.databasequery.core.impl.query.helper.QueryHelper;
import net.prematic.databasequery.core.query.InsertQuery;

public abstract class AbstractInsertQuery extends QueryHelper implements InsertQuery {

    @Override
    public InsertQuery set(String field, Object value) {
        addEntry(new QueryEntry(QueryOperator.SET).addData("field", field).addDataIfNotNull("value", value));
        return this;
    }
}