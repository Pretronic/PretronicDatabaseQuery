package net.pretronic.databasequery.driverloader;

import net.pretronic.libraries.dependency.DependencyGroup;

import java.util.Collection;

public class DriverLoaderConfig {

    private final String name;
    private final String driverClass;
    private final DependencyGroup required;
    private final Collection<DependencyGroup> optional;

    public DriverLoaderConfig(String name, String driverClass, DependencyGroup required,Collection<DependencyGroup> optional) {
        this.name = name;
        this.driverClass = driverClass;
        this.required = required;
        this.optional = optional;
        this.loaded = false;
    }

    private boolean loaded;

    public String getName() {
        return name;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public DependencyGroup getRequired() {
        return required;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public Collection<DependencyGroup> getOptional() {
        return optional;
    }
}
