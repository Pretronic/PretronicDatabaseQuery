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

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.event.ServerMonitorListener;
import net.pretronic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.databasequery.api.driver.config.RemoteDatabaseDriverConfig;
import net.pretronic.databasequery.mongodb.driver.MongoDBDatabaseDriver;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.annotations.DocumentIgnoreBooleanValue;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class MongoDBDatabaseDriverConfig implements RemoteDatabaseDriverConfig<MongoDBDatabaseDriverConfig> {

    private final Class<MongoDBDatabaseDriver> driver = MongoDBDatabaseDriver.class;

    private final InetSocketAddress address;
    private final String name;
    private final String connectionString;
    private final String user;
    private final String password;
    private final String authenticationDatabase;

    @DocumentIgnoreBooleanValue(ignore = false)
    private final boolean srv;

    @DocumentIgnoreBooleanValue(ignore = false)
    private final Boolean ssl;

    public MongoDBDatabaseDriverConfig(InetSocketAddress address, String name, String connectionString, String user, String password, String authenticationDatabase, boolean srv, Boolean ssl) {
        this.address = address;
        this.name = name;
        this.connectionString = connectionString;
        this.user = user;
        this.password = password;
        this.authenticationDatabase = authenticationDatabase;
        this.srv = srv;
        this.ssl = ssl;
    }

    @Override
    public InetAddress getHost() {
        return address.getAddress();
    }

    @Override
    public int getPort() {
        return address.getPort();
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.address;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<? extends DatabaseDriver> getDriverClass() {
        return this.driver;
    }

    @Override
    public String getConnectionString() {
        return this.connectionString;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getAuthenticationDatabase() {
        return authenticationDatabase;
    }

    public boolean isSrv() {
        return srv;
    }

    public boolean isSsl() {
        return ssl;
    }

    @Override
    public Document toDocument() {
        return Document.newDocument(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <N extends MongoDBDatabaseDriverConfig> N getAs(Class<N> aClass) {
        return (N) this;
    }

    @Override
    public MongoDBDatabaseDriverConfig copy() {
        throw new UnsupportedOperationException();
    }

    public MongoClientURI getMongoClientURI(ServerMonitorListener monitorListener) {
        String uri = buildMongoURI();
        return new MongoClientURI(uri, new MongoClientOptions.Builder().addServerMonitorListener(monitorListener));
    }

    private String buildMongoURI() {
        if(this.connectionString != null) return connectionString;

        StringBuilder uriBuilder = new StringBuilder().append("mongodb");

        if(srv) uriBuilder.append("+srv");
        uriBuilder.append("://");

        if(user != null) {
            uriBuilder.append(user);
            if(password != null) uriBuilder.append(":").append(password);
            uriBuilder.append("@");
        }

        uriBuilder.append(address.getHostName());
        if(!srv && address.getPort() > 0) uriBuilder.append(":").append(address.getPort());

        if(authenticationDatabase != null) uriBuilder.append("/?authSource=").append(authenticationDatabase);


        if(this.ssl != null) {
            if(authenticationDatabase != null) uriBuilder.append("&");
            else uriBuilder.append("/?");
            uriBuilder.append("ssl=").append(ssl);
        }

        return uriBuilder.toString();
    }
}
