/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 15.12.19, 20:14
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

package net.pretronic.databasequery.common.query;

import net.pretronic.libraries.utility.annonations.Internal;

/**
 * The {@link EntryOption} represents holder data for entry storing.
 */
@Internal
public enum EntryOption {

    /**
     * This is used, if the data is not defined, like if the default value for creating a {@link net.pretronic.databasequery.api.collection.DatabaseCollection} is not given.
     * It should be taken into account when creating a {@link net.pretronic.databasequery.api.collection.DatabaseCollection} in the implementation.
     */
    NOT_DEFINED,
    /**
     * This is used, if the value is not defined in the query. In the implementation, it is used to check if the value is already definded
     * or if it should be get from the values array in {@link net.pretronic.databasequery.api.query.Query#execute(Object...)}.
     */
    PREPARED
}
