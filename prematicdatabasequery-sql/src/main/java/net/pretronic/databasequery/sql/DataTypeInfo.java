/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.12.19, 16:27
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

package net.pretronic.databasequery.sql;

import net.prematic.databasequery.api.datatype.DataType;

public class DataTypeInfo {

    private DataType dataType;
    private String[] names;
    private boolean sizeAble;
    private int defaultSize;

    public DataTypeInfo() {
        this.sizeAble = true;
        this.defaultSize = 0;
    }

    public DataType getDataType() {
        return dataType;
    }

    public String getName() {
        return this.names[0];
    }

    public String[] getNames() {
        return names;
    }

    public boolean isSizeAble() {
        return defaultSize != -1 || sizeAble;
    }

    public int getDefaultSize() {
        return defaultSize;
    }

    public DataTypeInfo dataType(DataType dataType) {
        this.dataType = dataType;
        return this;
    }

    public DataTypeInfo names(String... names) {
        this.names = names;
        return this;
    }

    public DataTypeInfo sizeAble(boolean sizeAble) {
        this.sizeAble = sizeAble;
        return this;
    }

    public DataTypeInfo defaultSize(int defaultSize) {
        this.defaultSize = defaultSize;
        return this;
    }
}
