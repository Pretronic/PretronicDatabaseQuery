/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 10.04.20, 19:18
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

package net.pretronic.databasequery.mongodb.query.utils;

import net.pretronic.databasequery.mongodb.collection.MongoDBDatabaseCollection;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BuildContext {

    protected final MongoDBDatabaseCollection collection;
    protected final List<Bson> findQuery;
    protected boolean negate;

    private BuildContext(MongoDBDatabaseCollection collection) {
        this.collection = collection;
        this.negate = false;
        this.findQuery = new ArrayList<>();
    }

    public List<Bson> getFindQuery() {
        return findQuery;
    }

    protected void add(Bson bson) {
        findQuery.add(bson);
    }

    protected void addAll(Collection<Bson> bsons) {
        findQuery.addAll(bsons);
    }

    public static BuildContext newContext(MongoDBDatabaseCollection collection) {
        return new BuildContext(collection);
    }
}
