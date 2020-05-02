/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 10.04.20, 19:18
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

package net.pretronic.databasequery.mongodb.driver.config;

import net.pretronic.databasequery.mongodb.driver.MongoDBDatabaseDriver;
import net.pretronic.libraries.utility.Validate;

import java.net.InetSocketAddress;

public class MongoDBDatabaseDriverConfigBuilder {

    private static int COUNT = 1;

    private InetSocketAddress address;
    private String name;
    private String connectionString;
    private String username;
    private String password;
    private String authenticationDatabase;
    private boolean srv;
    private Boolean ssl;

    public MongoDBDatabaseDriverConfigBuilder() {
        this.name = "MongoDB-" + COUNT++;
        loadDriverClass();
    }

    private void loadDriverClass() {
        try {
            Class.forName(MongoDBDatabaseDriver.class.getName());
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public MongoDBDatabaseDriverConfigBuilder setAddress(InetSocketAddress address) {
        this.address = address;
        return this;
    }

    public MongoDBDatabaseDriverConfigBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MongoDBDatabaseDriverConfigBuilder setConnectionString(String connectionString) {
        this.connectionString = connectionString;
        return this;
    }

    public MongoDBDatabaseDriverConfigBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public MongoDBDatabaseDriverConfigBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public MongoDBDatabaseDriverConfigBuilder setAuthenticationDatabase(String authenticationDatabase) {
        this.authenticationDatabase = authenticationDatabase;
        return this;
    }

    public MongoDBDatabaseDriverConfigBuilder setSrv(boolean srv) {
        this.srv = srv;
        return this;
    }

    public MongoDBDatabaseDriverConfigBuilder setSsl(boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    public MongoDBDatabaseDriverConfig build() {
        Validate.isTrue(address != null || connectionString != null);
        return new MongoDBDatabaseDriverConfig(address, name, connectionString, username, password, authenticationDatabase, srv, ssl);
    }
}