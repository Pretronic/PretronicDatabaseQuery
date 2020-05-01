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

package net.pretronic.databasequery.mongodb.driver;

import com.mongodb.MongoClient;
import com.mongodb.event.ServerHeartbeatFailedEvent;
import com.mongodb.event.ServerHeartbeatStartedEvent;
import com.mongodb.event.ServerHeartbeatSucceededEvent;
import com.mongodb.event.ServerMonitorListener;
import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.driver.DatabaseDriverFactory;
import net.pretronic.databasequery.common.driver.AbstractDatabaseDriver;
import net.pretronic.databasequery.mongodb.MongoDBDatabase;
import net.pretronic.databasequery.mongodb.driver.config.MongoDBDatabaseDriverConfig;
import net.pretronic.libraries.logging.PretronicLogger;

import java.util.concurrent.ExecutorService;

public class MongoDBDatabaseDriver extends AbstractDatabaseDriver implements ServerMonitorListener {

    static {
        DatabaseDriverFactory.registerFactory(MongoDBDatabaseDriver.class, new MongoDBDatabaseDriverFactory());
    }

    private MongoClient client;

    public MongoDBDatabaseDriver(String name, MongoDBDatabaseDriverConfig config, PretronicLogger logger, ExecutorService executorService) {
        super(name, "MongoDB", config, logger, executorService);
    }

    @Override
    public Database getDatabase(String name) {
        return new MongoDBDatabase(name, this);
    }

    @Override
    public boolean isConnected() {
        return this.client != null;
    }

    @Override
    public void connect() {
        if(this.client != null) throw new IllegalArgumentException("Already connected");
        this.client = new MongoClient(getConfig().getMongoClientURI(this));
    }

    @Override
    public void disconnect() {
        this.client.close();
    }

    @Override
    public MongoDBDatabaseDriverConfig getConfig() {
        return (MongoDBDatabaseDriverConfig) super.getConfig();
    }

    @Override
    public void serverHearbeatStarted(ServerHeartbeatStartedEvent event) {

    }

    @Override
    public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent event) {

    }

    @Override
    public void serverHeartbeatFailed(ServerHeartbeatFailedEvent event) {

    }

    public MongoClient getClient() {
        return client;
    }
}
