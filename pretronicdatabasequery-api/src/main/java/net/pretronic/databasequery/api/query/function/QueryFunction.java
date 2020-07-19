/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.07.20, 13:22
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

package net.pretronic.databasequery.api.query.function;

import net.pretronic.databasequery.api.query.SearchOrder;

/**
 * The {@link QueryFunction} represents a function, which can be used in {@link net.pretronic.databasequery.api.query.type.FindQuery#getFunction(QueryFunction)}
 * for getting values with a specific function.
 */
public interface QueryFunction {

    static RowNumberQueryFunction rowNumberFunction(String orderField, SearchOrder order ) {
        return new RowNumberQueryFunction(orderField, order);
    }
}