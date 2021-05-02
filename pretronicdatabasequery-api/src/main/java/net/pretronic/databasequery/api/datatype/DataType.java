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

package net.pretronic.databasequery.api.datatype;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.query.type.CreateQuery;
import net.pretronic.libraries.document.Document;

import java.util.Date;

/**
 * This class holds all possible data types to create a {@link DatabaseCollection}
 * with the {@link CreateQuery}
 *
 */
public enum DataType {

    DOUBLE(double.class),
    DECIMAL(double.class),
    FLOAT(float.class),
    INTEGER(int.class),
    LONG(long.class),
    CHAR(char.class),
    STRING(String.class),
    LONG_TEXT(String.class),
    DATE(Date.class),
    DATETIME,
    TIMESTAMP(long.class),
    BINARY(byte.class, byte[].class),
    UUID(java.util.UUID.class),
    DOCUMENT(Document.class),
    BOOLEAN(boolean.class);

    private final Class<?>[] javaClasses;

    DataType(Class<?>... javaClasses) {
        this.javaClasses = javaClasses;
    }

    /**
     * Returns all equivalent java classes for the given data type.
     * @return array with classes
     */
    public Class<?>[] getJavaClasses() {
        return javaClasses;
    }

    public static DataType getDataTypeByClass(Class<?> clazz) {
        for (DataType dataType : DataType.values()) {
            for (Class<?> javaClass : dataType.getJavaClasses()) {
                if(clazz == javaClass) return dataType;
            }
        }
        return null;
    }
}
