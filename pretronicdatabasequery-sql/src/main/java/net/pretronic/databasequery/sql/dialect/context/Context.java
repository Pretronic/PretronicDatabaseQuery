/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.05.20, 20:09
 * @web %web%
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

package net.pretronic.databasequery.sql.dialect.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Context {

    private final StringBuilder queryBuilder;
    private final List<Object> preparedValues;
    private final Collection<String> additionalExecutedQueries;

    public Context() {
        this.queryBuilder = new StringBuilder();
        this.preparedValues = new ArrayList<>();
        this.additionalExecutedQueries = new ArrayList<>();
    }

    public StringBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public List<Object> getPreparedValues() {
        return preparedValues;
    }

    public Collection<String> getAdditionalExecutedQueries() {
        return additionalExecutedQueries;
    }
}