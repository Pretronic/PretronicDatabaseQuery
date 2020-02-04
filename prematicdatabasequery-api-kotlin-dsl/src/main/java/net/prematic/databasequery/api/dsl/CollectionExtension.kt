/*
 * (C) Copyright 2020 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 04.02.20, 15:28
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

package net.prematic.databasequery.api.dsl

import net.prematic.databasequery.api.collection.DatabaseCollection
import net.prematic.databasequery.api.query.QueryGroup
import net.prematic.databasequery.api.query.type.*

fun DatabaseCollection.insert(initializer: InsertQuery.() -> Unit) : InsertQuery {
    return this.insert().apply(initializer)
}

fun DatabaseCollection.find(initializer: FindQuery.() -> Unit) : FindQuery {
    return this.find().apply(initializer)
}

fun DatabaseCollection.update(initializer: UpdateQuery.() -> Unit) : UpdateQuery {
    return this.update().apply(initializer)
}

fun DatabaseCollection.replace(initializer: ReplaceQuery.() -> Unit) : ReplaceQuery {
    return this.replace().apply(initializer)
}

fun DatabaseCollection.delete(initializer: DeleteQuery.() -> Unit) : DeleteQuery {
    return this.delete(initializer).apply(initializer)
}

fun DatabaseCollection.group(initializer: QueryGroup.() -> Unit) : QueryGroup {
    return this.group().apply(initializer)
}


