/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 11.01.20, 15:04
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

package net.pretronic.databasequery.sql.dialect.defaults;

import net.pretronic.databasequery.common.DatabaseDriverEnvironment;

import java.io.File;

public class H2PortableDialect extends AbstractDialect {

    public H2PortableDialect() {
        super("H2Portable", "org.h2.Driver", "h2:file", DatabaseDriverEnvironment.LOCAL);
    }

    @Override
    public String createConnectionString(String connectionString, Object host) {
        if(connectionString != null) {
            return connectionString;
        } else  {
            File location;
            if(host instanceof File) {
                location = (File) host;
            } else {
                location = new File("./");
            }
            String path = location.getPath().trim();
            if(path.isEmpty()) {
                path = "./";
            } else if(!path.startsWith("./") || !path.startsWith("~/")) {
                path = "./" + path;
            }
            if(!path.endsWith("\\/")) {
                path += "/";
            }
            path+="%s";
            return "jdbc:h2:file:" + path + ";MODE=Mysql;";
        }
    }
}
