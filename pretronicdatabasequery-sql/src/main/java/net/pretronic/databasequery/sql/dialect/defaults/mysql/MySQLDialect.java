/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.05.20, 15:17
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

package net.pretronic.databasequery.sql.dialect.defaults.mysql;

import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.exceptions.DatabaseQueryException;
import net.pretronic.databasequery.common.DatabaseDriverEnvironment;
import net.pretronic.databasequery.sql.DataTypeInformation;
import net.pretronic.databasequery.sql.dialect.DialectDefaultSettings;
import net.pretronic.databasequery.sql.dialect.defaults.AbstractDialect;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class MySQLDialect extends AbstractDialect {

    public MySQLDialect() {
        this("MySQL", "com.mysql.cj.jdbc.Driver", "mysql",
                new DialectDefaultSettings(3306, TimeUnit.MINUTES.toMillis(5)), DatabaseDriverEnvironment.REMOTE,
                true, "`", "`");
    }

    public MySQLDialect(String name, String driverName, String protocol, DialectDefaultSettings defaultSettings, DatabaseDriverEnvironment environment, boolean dynamicDependencies, String firstBackTick, String secondBackTick) {
        super(name, driverName, protocol, defaultSettings, environment, dynamicDependencies, firstBackTick, secondBackTick);
    }



    @Override
    public String createConnectionString(String connectionString, Object host) {
        if(connectionString != null) {
            return connectionString;
        } else if(host instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) host;
            int port = address.getPort();
            if(port == 0) {
                port = getDefaultSettings().getDefaultPort();
            }
            return String.format("jdbc:%s://%s:%s", getProtocol(), address.getHostName(), port);
        }
        throw new DatabaseQueryException("Can't match jdbc url for dialect " + getName());
    }

    @Override
    protected void registerDataTypeInformation() {
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.DOUBLE).names("DOUBLE"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.FLOAT).names("REAL"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.LONG).names("BIGINT").defaultSize(8));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.LONG_TEXT).names("LONGTEXT").sizeAble(false));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.DATETIME).names("DATETIME"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.BINARY).names("BINARY"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.UUID).names("BINARY").defaultSize(16));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.DOCUMENT).names("LONGTEXT").sizeAble(false));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.BOOLEAN).names("BIT").defaultSize(1));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.DECIMAL).names("DECIMAL"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.INTEGER).names("INTEGER", "INT"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.CHAR).names("CHAR").defaultSize(1));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.STRING).names("VARCHAR").defaultSize(255));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.DATE).names("DATE"));
        this.dataTypeInformation.add(new DataTypeInformation().dataType(DataType.TIMESTAMP).names("TIMESTAMP"));
    }
}
