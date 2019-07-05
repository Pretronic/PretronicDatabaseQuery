/*
 * (C) Copyright 2019 The PrematicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 11.06.19, 21:21
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

package net.prematic.databasequery.core.datatype.adapter.defaults;

import net.prematic.databasequery.core.datatype.adapter.DataTypeAdapter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class UUIDDataTypeAdapter implements DataTypeAdapter<UUID, byte[]> {

    @Override
    public byte[] write(UUID value) {
        byte[] uuidBytes = new byte[16];
        ByteBuffer.wrap(uuidBytes)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(value.getMostSignificantBits())
                .putLong(value.getLeastSignificantBits());
        return uuidBytes;
    }

    @Override
    public UUID read(byte[] value) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(value);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }
}