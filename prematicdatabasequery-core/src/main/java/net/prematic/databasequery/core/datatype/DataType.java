/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 09.06.19, 14:58
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

package net.prematic.databasequery.core.datatype;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds all possible data types to create a {@link net.prematic.databasequery.core.DatabaseCollection}
 * with the {@link net.prematic.databasequery.core.query.CreateQuery}
 */
public enum DataType {

    DOUBLE,
    DECIMAL,
    FLOAT,
    INTEGER,
    LONG,
    CHAR,
    STRING,
    LONG_TEXT,
    DATE,
    DATETIME,
    TIMESTAMP,
    BINARY,
    BLOB,
    UUID;

    public static final Map<Class<?>, DataType> DATA_TYPES = new HashMap<>();

    static {
        for (DataType value : DataType.values()) {
            for (Class javaClass : value.getJavaClasses()) {
                DATA_TYPES.put(javaClass, value);
            }
        }
    }

    private final Class<?>[] javaClasses;

    DataType(Class... javaClasses) {
        this.javaClasses = javaClasses;
    }

    /**
     * Returns all equivalent java classes for the given data type.
     * @return array with classes
     */
    public Class[] getJavaClasses() {
        return javaClasses;
    }
}