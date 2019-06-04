/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.05.19, 21:49
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

package net.prematic.databasequery.core.impl.query;

import net.prematic.databasequery.core.DataType;
import net.prematic.databasequery.core.DatabaseCollectionType;
import net.prematic.databasequery.core.ForeignKey;
import net.prematic.databasequery.core.QueryOperator;
import net.prematic.databasequery.core.impl.query.helper.QueryHelper;
import net.prematic.databasequery.core.query.CreateQuery;
import net.prematic.databasequery.core.query.option.CreateOption;

public abstract class AbstractCreateQuery extends QueryHelper implements CreateQuery {

    private final String collectionName;

    public AbstractCreateQuery(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    @Override
    public CreateQuery with(String field, DataType dataType, int fieldSize, Object defaultValue, ForeignKey foreignKey, CreateOption... createOptions) {
        getEntries().add(new QueryEntry(QueryOperator.CREATE)
                .addData("field", field)
                .addData("dataType", dataType)
                .addDataIfNotNull("fieldSize", fieldSize)
                .addDataIfNotNull("defaultValue", defaultValue)
                .addDataIfNotNull("foreignKey", foreignKey)
                .addDataIfNotNull("createOptions", createOptions));
        return this;
    }

    @Override
    public CreateQuery withEngine(String engine) {
        getEntries().add(new QueryEntry(QueryOperator.ENGINE).addData("engine", engine));
        return this;
    }

    @Override
    public CreateQuery withCollectionType(DatabaseCollectionType collectionType) {
        getEntries().add(new QueryEntry(QueryOperator.COLLECTION_TYPE).addData("collectionType", collectionType));
        return this;
    }
}