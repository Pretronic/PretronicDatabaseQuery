/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.05.19, 21:32
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

package net.prematic.databasequery.core.impl.query.result;

import net.prematic.databasequery.core.query.result.QueryResultEntry;
import net.prematic.libraries.utility.reflect.UnsafeInstanceCreator;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

public class SimpleQueryResultEntry implements QueryResultEntry {

    private final Map<DefaultQueryResultEntryKey, Object> results;

    public SimpleQueryResultEntry(Map<DefaultQueryResultEntryKey, Object> results) {
        this.results = results;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T getObject(int index, Class<T> clazz) {
        return ((T) getObject(index));
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        return (T) getObject(key);
    }

    @Override
    public Object getObject(int index) {
        for (Map.Entry<DefaultQueryResultEntryKey, Object> entry : this.results.entrySet()) {
            if(entry.getKey().getIndex() == index) return entry.getValue();
        }
        return null;
    }

    @Override
    public Object getObject(String key) {
        for (Map.Entry<DefaultQueryResultEntryKey, Object> entry : this.results.entrySet()) {
            if(entry.getKey().getKeyName().equalsIgnoreCase(key)) return entry.getValue();
        }
        return null;
    }

    @Override
    public String getString(int index) {
        return (String) getObject(index);
    }

    @Override
    public String getString(String key) {
        return (String) getObject(key);
    }

    @Override
    public int getInt(int index) {
        return (int) getObject(index);
    }

    @Override
    public int getInt(String key) {
        return (int) getObject(key);
    }

    @Override
    public long getLong(int index) {
        return (long) getObject(index);
    }

    @Override
    public long getLong(String key) {
        return (long) getObject(key);
    }

    @Override
    public double getDouble(int index) {
        return (double) getObject(index);
    }

    @Override
    public double getDouble(String key) {
        return (double) getObject(key);
    }

    @Override
    public float getFloat(int index) {
        return (float) getObject(index);
    }

    @Override
    public float getFloat(String key) {
        return (float) getObject(key);
    }

    @Override
    public byte getByte(int index) {
        return (byte) getObject(index);
    }

    @Override
    public byte getByte(String key) {
        return (byte) getObject(key);
    }

    @Override
    public boolean getBoolean(int index) {
        return (boolean) getObject(index);
    }

    @Override
    public boolean getBoolean(String key) {
        return (boolean) getObject(key);
    }

    @Override
    public Date getDate(int index) {
        return (Date) getObject(index);
    }

    @Override
    public Date getDate(String key) {
        return (Date) getObject(key);
    }

    @Override
    public boolean contains(String key) {
        try {
            return getObject(key) != null;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public boolean contains(int index) {
        try {
            return getObject(index) != null;
        } catch (Exception exception) {
            return false;
        }
    }


    public class DefaultQueryResultEntryKey {

        private final String keyName;
        private final int index;

        public DefaultQueryResultEntryKey(String keyName, int index) {
            this.keyName = keyName;
            this.index = index;
        }

        public String getKeyName() {
            return keyName;
        }

        public int getIndex() {
            return index;
        }
    }
}
