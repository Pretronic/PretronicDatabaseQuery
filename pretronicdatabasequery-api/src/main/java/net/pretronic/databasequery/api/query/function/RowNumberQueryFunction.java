/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 19.07.20, 13:22
 * @web %web%
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

package net.pretronic.databasequery.api.query.function;

import net.pretronic.databasequery.api.query.SearchOrder;

public class RowNumberQueryFunction implements QueryFunction {

    private final String orderField;
    private final SearchOrder order;

    protected RowNumberQueryFunction(String orderField, SearchOrder order) {
        this.orderField = orderField;
        this.order = order;
    }

    public String getOrderField() {
        return orderField;
    }

    public SearchOrder getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o instanceof RowNumberQueryFunction) {
            RowNumberQueryFunction function = ((RowNumberQueryFunction) o);
            return orderField.equals(function.orderField) && order == function.order;
        }
        return false;
    }
}
