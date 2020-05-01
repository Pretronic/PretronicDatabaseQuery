/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 15.12.19, 20:18
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

package net.pretronic.databasequery.common.query.type;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.query.type.DeleteQuery;
import net.pretronic.databasequery.api.query.type.ReplaceQuery;

/**
 * The {@link AbstractReplaceQuery} represents the base implementation of {@link ReplaceQuery}.
 * @param <C> collection implementation type
 */
public abstract class AbstractReplaceQuery<C extends DatabaseCollection> extends AbstractChangeAndSearchQuery<ReplaceQuery, C> implements ReplaceQuery {

    public AbstractReplaceQuery(C collection) {
        super(collection);
    }
}
