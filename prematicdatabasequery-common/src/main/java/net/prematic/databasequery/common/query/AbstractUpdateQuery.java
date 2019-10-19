/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.10.19, 20:45
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

package net.prematic.databasequery.common.query;

import net.prematic.databasequery.api.DatabaseCollection;
import net.prematic.databasequery.api.query.UpdateQuery;
import net.prematic.databasequery.common.QueryOperator;
import net.prematic.databasequery.common.query.helper.SearchQueryHelper;

public abstract class AbstractUpdateQuery extends SearchQueryHelper<UpdateQuery> implements UpdateQuery {

    public AbstractUpdateQuery(DatabaseCollection collection) {
        super(collection);
    }

    @Override
    public UpdateQuery set(String field, Object value) {
        addEntry(new QueryEntry(QueryOperator.SET).addData("field", field).addDataIfNotNull("value", value));
        return this;
    }
}