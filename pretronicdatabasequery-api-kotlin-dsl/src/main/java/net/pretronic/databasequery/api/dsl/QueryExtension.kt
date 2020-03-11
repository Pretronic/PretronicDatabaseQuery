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

package net.pretronic.databasequery.api.dsl

import net.pretronic.databasequery.api.query.type.SearchQuery
import net.pretronic.databasequery.api.query.type.join.JoinQuery

fun SearchQuery<*>.or(initializer: SearchQuery<*>.() -> Unit) : SearchQuery<*> {
    val query : SearchQuery<*> = this.newSearchQuery().apply(initializer)
    this.or(query)
    return query
}

fun SearchQuery<*>.and(initializer: SearchQuery<*>.() -> Unit) : SearchQuery<*> {
    val query : SearchQuery<*> = this.newSearchQuery().apply(initializer)
    this.and(query)
    return query
}

fun SearchQuery<*>.not(initializer: SearchQuery<*>.() -> Unit) : SearchQuery<*> {
    val query : SearchQuery<*> = this.newSearchQuery().apply(initializer)
    this.not(query)
    return query
}

fun JoinQuery<*>.join(initializer: JoinQuery<*>.() -> Unit) : JoinQuery<*> {
    return this.apply(initializer)
}
