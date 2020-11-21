/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.11.20, 16:17
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

package net.pretronic.databasequery.sql.dialect;

public class DialectDefaultSettings {

    private final int defaultPort;
    private final long defaultConnectionExpire;

    public DialectDefaultSettings(int defaultPort, long defaultConnectionExpire) {
        this.defaultPort = defaultPort;
        this.defaultConnectionExpire = defaultConnectionExpire;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public long getDefaultConnectionExpire() {
        return defaultConnectionExpire;
    }
}
