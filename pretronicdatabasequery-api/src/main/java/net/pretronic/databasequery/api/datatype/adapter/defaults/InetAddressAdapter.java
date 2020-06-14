/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 14.06.20, 14:44
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

package net.pretronic.databasequery.api.datatype.adapter.defaults;

import net.pretronic.databasequery.api.datatype.adapter.DataTypeAdapter;
import net.pretronic.databasequery.api.exceptions.DatabaseQueryException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressAdapter implements DataTypeAdapter<InetAddress> {

    @Override
    public Object write(InetAddress value) {
        return value.getHostAddress();
    }

    @Override
    public InetAddress read(Object value) {
        try {
            return InetAddress.getByName((String) value);
        } catch (UnknownHostException e) {
            throw new DatabaseQueryException("Can't convert value to InetAddress", e);
        }
    }
}
