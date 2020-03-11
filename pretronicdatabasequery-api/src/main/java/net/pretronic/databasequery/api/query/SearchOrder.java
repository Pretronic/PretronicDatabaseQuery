/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 11.03.20, 20:28
 * @website %web%
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

package net.pretronic.databasequery.api.query;

import net.pretronic.databasequery.api.query.type.FindQuery;

/**
 * The {@link SearchOrder} defines the order direction in a {@link FindQuery
 */
public enum SearchOrder {

    /**
     * ASK stands for ascending.
     * <p>Ascending is used for increasing in size, alphabetically or importance.</p>>
     *
     * <p>Example: -2, -1, 0, 1, 2, 3</p>>
     *
     */
    ASC(),

    /**
     * ASK stands for descending. This
     * <p>Ascending is used for decreasing in size, alphabetically or importance.</p>>
     *
     * <p>Example: 3, 2, 1, 0, -1, -2</p>>
     *
     */
    DESC()

}
