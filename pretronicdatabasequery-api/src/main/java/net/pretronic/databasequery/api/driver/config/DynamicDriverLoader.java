package net.pretronic.databasequery.api.driver.config;

import net.pretronic.databasequery.api.driver.DatabaseDriver;

public interface DynamicDriverLoader {

    Class<? extends DatabaseDriver> loadDriver(String driverClassName);

    void loadOptionalDriverDependencies(Class<? extends DatabaseDriver> driverClass,String name);

}
