package net.pretronic.databasequery.api.driver.config;

import net.pretronic.databasequery.api.driver.DatabaseDriver;

/**
 * The {@link DynamicDriverLoader} represents the interface for dynamic driver loader implementing.
 * If you use the PretronicDependency library, you could implement this interface for you own database query implementation
 * to download their dependencies at runtime.
 */
public interface DynamicDriverLoader {

    /**
     * Loads a driver with the given class name. It loads all classes of the dependency.
     *
     * @param driverClassName of the driver
     * @return driver class
     */
    Class<? extends DatabaseDriver> loadDriver(String driverClassName);

    /**
     * Loads all optional dependencies of a database driver.
     *
     * @param driverClass of the driver
     * @param name of the optional dependency group to load
     */
    void loadOptionalDriverDependencies(Class<? extends DatabaseDriver> driverClass,String name);

}
