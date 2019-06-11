/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 11.06.19, 21:20
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

package net.prematic.databasequery.core.datatype.adapter;

import java.lang.reflect.ParameterizedType;

public abstract class DataTypeAdapter<W, R> {

    private final Class<W> writeClass;
    private final Class<R> readClass;

    public DataTypeAdapter() {
        this.writeClass = (Class<W>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.readClass = (Class<R>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    public Class<W> getWriteClass() {
        return writeClass;
    }

    public Class<R> getReadClass() {
        return readClass;
    }

    public abstract R write(W value);

    public abstract W read(R value);
}