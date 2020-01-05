/*
 * (C) Copyright 2020 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 04.01.20, 22:20
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

package net.prematic.databasequery.api.query;

import java.util.Arrays;
import java.util.List;

public final class PreparedValue {

    private final List<Object> values;

    private PreparedValue(List<Object> values) {
        this.values = values;
    }

    public List<Object> getValues() {
        return values;
    }


    public static PreparedValue of(Object... values) {
        return new PreparedValue(Arrays.asList(values));
    }
}