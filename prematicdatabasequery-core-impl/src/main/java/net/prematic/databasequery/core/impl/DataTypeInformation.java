/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 26.05.19, 20:35
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

package net.prematic.databasequery.core.impl;

import net.prematic.databasequery.core.datatype.DataType;

public class DataTypeInformation {

    private final DataType dataType;
    private final String name;
    private final boolean sizeAble;
    private final int defaultSize;

    public DataTypeInformation(DataType dataType, String name, boolean sizeAble, int defaultSize) {
        this.dataType = dataType;
        this.name = name;
        this.sizeAble = sizeAble;
        this.defaultSize = defaultSize;
    }

    public DataTypeInformation(DataType dataType, String name) {
        this.dataType = dataType;
        this.name = name;
        this.sizeAble = true;
        this.defaultSize = -1;
    }

    public DataTypeInformation(DataType dataType, String name, boolean sizeAble) {
        this.dataType = dataType;
        this.name = name;
        this.sizeAble = sizeAble;
        this.defaultSize = -1;
    }

    public DataTypeInformation(DataType dataType, String name, int defaultSize) {
        this.dataType = dataType;
        this.name = name;
        this.defaultSize = defaultSize;
        this.sizeAble = true;
    }

    public DataType getDataType() {
        return dataType;
    }

    public String getName() {
        return name;
    }

    public boolean isSizeAble() {
        return sizeAble;
    }

    public int getDefaultSize() {
        return defaultSize;
    }
}
