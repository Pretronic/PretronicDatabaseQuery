/*
 * (C) Copyright 2020 The PretronicDatabaseQuery Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 11.03.20, 20:28
 * @website %web%
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

package net.pretronic.databasequery.api.driver;

import net.pretronic.databasequery.api.driver.config.DatabaseDriverConfig;
import net.pretronic.databasequery.api.driver.config.DynamicDriverLoader;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.logging.PretronicLogger;
import net.pretronic.libraries.logging.PretronicLoggerFactory;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Represents the factory for creating {@link DatabaseDriver}.
 */
public abstract class DatabaseDriverFactory {

    private final static Map<Class<?>,DatabaseDriverFactory> FACTORIES = new HashMap<>();
    private static DynamicDriverLoader DRIVER_LOADER;


    /**
     * Creates a database driver instance.
     *
     * @param name of the driver
     * @param config of the driver
     * @param logger for this driver
     * @param executorService for this driver
     * @return driver
     */
    public abstract DatabaseDriver createDriver(String name, DatabaseDriverConfig<?> config, PretronicLogger logger, ExecutorService executorService);

    /**
     * Creates a database driver config with a document, which should includes information of a {@link DatabaseDriverConfig} implementation.
     * @param config
     * @return the config
     */
    public abstract DatabaseDriverConfig<?> createConfig(Document config);

    /**
     * Returns the dynamic driver loader, if configured.
     *
     * @return driver loader or null
     */
    public static DynamicDriverLoader getDriverLoader() {
        return DRIVER_LOADER;
    }

    /**
     * Set the driver loader instance for the runtime.
     *
     * @param driverLoader to set
     */
    public static void setDriverLoader(DynamicDriverLoader driverLoader) {
        DRIVER_LOADER = driverLoader;
    }

    public static DatabaseDriver create(String name, DatabaseDriverConfig<?> config){
        return create(name, config, PretronicLoggerFactory.getLogger(DatabaseDriver.class));
    }

    public static DatabaseDriver create(String name, DatabaseDriverConfig<?> config, PretronicLogger logger){
        return create(name, config, logger, GeneralUtil.getDefaultExecutorService());
    }

    public static DatabaseDriver create(String name, DatabaseDriverConfig<?> config, PretronicLogger logger, ExecutorService executorService){
        Objects.requireNonNull(name);
        Objects.requireNonNull(config);
        Objects.requireNonNull(logger);
        Objects.requireNonNull(executorService);

        DatabaseDriverFactory factory = FACTORIES.get(config.getDriverClass());
        if(factory == null) throw new IllegalArgumentException("No factory for driver class "+config.getDriverClass()+" found");
        return factory.createDriver(name, config, logger, executorService);
    }


    public static DatabaseDriver create(String name, Document config){
        return create(name, config,PretronicLoggerFactory.getLogger(DatabaseDriver.class));
    }

    public static DatabaseDriver create(String name, Document config, PretronicLogger logger){
        return create(name, config, logger, GeneralUtil.getDefaultExecutorService());
    }

    public static DatabaseDriver create(String name, Document config, PretronicLogger logger, ExecutorService executorService){
        return create(name,create(config), logger,executorService);
    }


    public static DatabaseDriverConfig<?> create(Document config){
        Validate.notNull(config);
        String driverClassName = config.getString("driver");
        if(driverClassName == null){
            throw new IllegalArgumentException("No driver defined");
        }
        Class<?> configClass;
        try{
            configClass = Class.forName(driverClassName);
        }catch (ClassNotFoundException e){
            if(DRIVER_LOADER != null){
                configClass = DRIVER_LOADER.loadDriver(driverClassName);
            }else{
                throw new IllegalArgumentException("Driver "+driverClassName+" is not available");
            }
        }
        return create(configClass,config);
    }

    public static DatabaseDriverConfig<?> create(Class<?> configClass, Document config){
        Objects.requireNonNull(configClass);
        Objects.requireNonNull(config);

        DatabaseDriverFactory factory = FACTORIES.get(configClass);
        if(factory == null) throw new IllegalArgumentException("No factory for driver class "+configClass+" found");
        return factory.createConfig(config);
    }


    public static void registerFactory(Class<? extends DatabaseDriver> driverClass, DatabaseDriverFactory factory) {
        Objects.requireNonNull(driverClass);
        Objects.requireNonNull(factory);
        FACTORIES.put(driverClass,factory);
    }

    public static void unregisterFactory(Class<? extends DatabaseDriver> driverClass) {
        Objects.requireNonNull(driverClass);
        FACTORIES.remove(driverClass);
    }


}
