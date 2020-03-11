/*
 * (C) Copyright 2019 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.12.19, 20:44
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

package net.pretronic.databasequery.common.query.result;

import net.pretronic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.map.IndexCaseIntensiveLinkedHashMap;
import net.pretronic.libraries.utility.map.IndexCaseIntensiveMap;
import net.pretronic.libraries.utility.reflect.UnsafeInstanceCreator;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

public class DefaultQueryResultEntry implements QueryResultEntry {

    private final DatabaseDriver driver;
    private final IndexCaseIntensiveMap<Object> results;

    public DefaultQueryResultEntry(DatabaseDriver driver, IndexCaseIntensiveMap<Object> results) {
        this.driver = driver;
        this.results = results;
    }

    public DefaultQueryResultEntry(DatabaseDriver driver) {
        this(driver, new IndexCaseIntensiveLinkedHashMap<>());
    }

    @Override
    public IndexCaseIntensiveMap<Object> asMap() {
        return this.results;
    }

    @Override
    public <T> T getAsObject(Class<T> clazz) {
        try {
            T object = UnsafeInstanceCreator.newInstance(clazz);
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if(contains(field.getName())) field.set(object, getObject(field.getName()));
            }
            return object;
        } catch (Exception ignored) {
            throw new IllegalArgumentException("Can't create object for class " + clazz);
        }
    }

    @Override
    public <T> T getObject(int index, Class<T> clazz) {
        return this.driver.getDataTypeAdapter(clazz).read(getObject(index));
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        return this.driver.getDataTypeAdapter(clazz).read(getObject(key));
    }

    @Override
    public Object getObject(int index) {
        return this.results.getIndex(index);
    }

    @Override
    public Object getObject(String key) {
        return this.results.get(key.toLowerCase());
    }

    @Override
    public String getString(int index) {
        return Convert.toString(getObject(index));
    }

    @Override
    public String getString(String key) {
        return Convert.toString(getObject(key));
    }

    @Override
    public int getInt(int index) {
        return Convert.toInteger(getObject(index));
    }

    @Override
    public int getInt(String key) {
        return Convert.toInteger(getObject(key));
    }

    @Override
    public long getLong(int index) {
        return Convert.toLong(getObject(index));
    }

    @Override
    public long getLong(String key) {
        return Convert.toLong(getObject(key));
    }

    @Override
    public double getDouble(int index) {
        return Convert.toDouble(getObject(index));
    }

    @Override
    public double getDouble(String key) {
        return Convert.toDouble(getObject(key));
    }

    @Override
    public float getFloat(int index) {
        return Convert.toFloat(getObject(index));
    }

    @Override
    public float getFloat(String key) {
        return Convert.toFloat(getObject(key));
    }

    @Override
    public byte getByte(int index) {
        return Convert.toByte(getObject(index));
    }

    @Override
    public byte getByte(String key) {
        return Convert.toByte(getObject(key));
    }

    @Override
    public boolean getBoolean(int index) {
        return Convert.toBoolean(getObject(index));
    }

    @Override
    public boolean getBoolean(String key) {
        return Convert.toBoolean(getObject(key));
    }

    @Override
    public Date getDate(int index) {
        return Convert.toDate(getObject(index));
    }

    @Override
    public Date getDate(String key) {
        return Convert.toDate(getObject(key));
    }

    @Override
    public UUID getUniqueId(int index) {
        return Convert.toUUID(getObject(index));
    }

    @Override
    public UUID getUniqueId(String key) {
        return Convert.toUUID(getObject(key));
    }

    @Override
    public boolean contains(String key) {
        return this.results.containsKey(key);
    }

    @Override
    public boolean contains(int index) {
        return this.results.containsIndex(index);
    }

    @Override
    public <T> T to(Function<QueryResultEntry, T> function) {
        return function.apply(this);
    }

    @Internal
    public DefaultQueryResultEntry addEntry(String key, Object value) {
        this.results.put(key, value);
        return this;
    }
}
