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

package net.pretronic.databasequery.api.collection;

/**
 * The {@link AliasDatabaseCollection} represents a {@link DatabaseCollection} with a custom name in the query.
 * It can be used in queries, like the join query. It may be not included in some implementations.
 */
public interface AliasDatabaseCollection extends DatabaseCollection{

    /**
     * Returns the original database collection.
     *
     * @return original database collection
     */
    DatabaseCollection getOriginal();

}
