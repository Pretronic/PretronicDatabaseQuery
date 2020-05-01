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

import net.pretronic.databasequery.api.query.PreparedValue;
import net.pretronic.databasequery.common.query.EntryOption;
import net.pretronic.databasequery.mongodb.collection.MongoDBDatabaseCollection;
import net.pretronic.libraries.utility.Validate;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BuildContext {

    protected final BuildContext parent;
    protected final Object[] values;
    protected final AtomicInteger valuePosition;
    protected final MongoDBDatabaseCollection collection;
    protected final List<Bson> findQuery;
    protected boolean negate;

    private BuildContext(BuildContext parent, Object[] values, AtomicInteger valuePosition, MongoDBDatabaseCollection collection) {
        this.parent = parent;
        this.values = values;
        this.valuePosition = valuePosition;
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

    protected Object nextValue() {
        System.out.println("nextvale");
        int position = valuePosition.get();
        if(position >= values.length) throw new IllegalArgumentException("No prepared value for index " + position);
        valuePosition.incrementAndGet();
        System.out.println("---");
        System.out.println(values.length);
        System.out.println(Arrays.toString(values));
        System.out.println(valuePosition.get());
        System.out.println(position);
        System.out.println("---");
        return values[position];
    }

    protected <T> T getValue(T value) {
        System.out.println("getvalue:"+value);
        if(value == EntryOption.PREPARED) return (T) nextValue();
        return value;
    }

    public static BuildContext newContext(BuildContext parent) {
        Validate.notNull(parent);
        return new BuildContext(parent, parent.values, parent.valuePosition, parent.collection);
    }

    public static BuildContext newContext(Object[] values, MongoDBDatabaseCollection collection) {
        return new BuildContext(null, values, new AtomicInteger(), collection);
    }
}
