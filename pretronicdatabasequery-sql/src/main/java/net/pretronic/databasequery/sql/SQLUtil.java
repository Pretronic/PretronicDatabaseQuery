/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 12.01.20, 15:22
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

package net.pretronic.databasequery.sql;

import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.datatype.adapter.DataTypeAdapter;
import net.pretronic.libraries.utility.map.Pair;
import net.pretronic.libraries.utility.reflect.Primitives;

import java.util.List;

public final class SQLUtil {

    public static PreparedStatementConsumer getSelectConsumer(DatabaseCollection collection, Pair<String, List<Object>> data) {
        return preparedStatement -> {
            System.out.println("selectConsumer: " + data.getValue());
            for (int i = 1; i <= data.getValue().size(); i++) {
                Object value = data.getValue().get(i-1);
                if(value != null && !Primitives.isPrimitive(value)) {
                    DataTypeAdapter adapter = collection.getDatabase().getDriver().getDataTypeAdapter(value.getClass());
                    if(adapter != null) {
                        value = adapter.write(value);
                    } else {
                        value = value.toString();
                    }
                }
                preparedStatement.setObject(i, value);
            }
        };
    }
}
