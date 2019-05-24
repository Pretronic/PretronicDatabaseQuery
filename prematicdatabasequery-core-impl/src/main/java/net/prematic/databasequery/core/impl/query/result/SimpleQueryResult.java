/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.05.19, 21:32
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

package net.prematic.databasequery.core.impl.query.result;

import net.prematic.databasequery.core.query.result.QueryResult;
import net.prematic.databasequery.core.query.result.QueryResultEntry;
import java.util.List;

public class SimpleQueryResult implements QueryResult {

    private final List<QueryResultEntry> entries;

    public SimpleQueryResult(List<QueryResultEntry> entries) {
        this.entries = entries;
    }

    @Override
    public QueryResultEntry getFirst() {
        return entries.get(0);
    }

    @Override
    public QueryResultEntry getLast() {
        return entries.get(entries.size()-1);
    }

    @Override
    public QueryResultEntry get(int index) {
        return entries.get(index);
    }

    @Override
    public List<QueryResultEntry> asList() {
        return this.entries;
    }
}
