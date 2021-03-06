/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 16.04.20, 00:01
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
import net.pretronic.libraries.utility.Convert;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * The {@link UUIDDataTypeAdapter} represents an implementation of {@link DataTypeAdapter}, which converts a object to a uuid and back.
 * For more information, see {@link DataTypeAdapter}.
 */
public class UUIDDataTypeAdapter implements DataTypeAdapter<UUID> {

    @Override
    public Object write(UUID value) {
        byte[] uuidBytes = new byte[16];
        ByteBuffer.wrap(uuidBytes)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(value.getMostSignificantBits())
                .putLong(value.getLeastSignificantBits());
        return uuidBytes;
    }

    @Override
    public UUID read(Object value) {
        return Convert.toUUID(value);
    }
}
