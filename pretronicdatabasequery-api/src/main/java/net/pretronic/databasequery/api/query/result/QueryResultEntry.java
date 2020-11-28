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

package net.pretronic.databasequery.api.query.result;

import net.pretronic.libraries.utility.map.IndexCaseIntensiveMap;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public interface QueryResultEntry extends Iterable<Map.Entry<String, Object>> {

    IndexCaseIntensiveMap<Object> asMap();

    <T> T getAsObject(Class<T> clazz);

    <T> T getObject(int index, Class<T> clazz);

    <T> T getObject(String key, Class<T> clazz);

    Object getObject(int index);

    Object getObject(String key);

    String getString(int index);

    String getString(String key);

    int getInt(int index);

    int getInt(String key);

    long getLong(int index);

    long getLong(String key);

    double getDouble(int index);

    double getDouble(String key);

    float getFloat(int index);

    float getFloat(String key);

    byte getByte(int index);

    byte getByte(String key);

    BigDecimal getBigDecimal(int index);

    BigDecimal getBigDecimal(String key);

    boolean getBoolean(int index);

    boolean getBoolean(String key);

    Date getDate(int index);

    Date getDate(String key);

    UUID getUniqueId(int index);

    UUID getUniqueId(String key);

    boolean contains(String key);

    boolean contains(int index);

    <T> T to(Function<QueryResultEntry,T> function);

    @Override
    default Iterator<Map.Entry<String, Object>> iterator() {
        return asMap().entrySet().iterator();
    }
}
