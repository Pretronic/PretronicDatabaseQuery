package net.pretronic.databasequery.driverloader;

import net.pretronic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.databasequery.api.driver.config.DynamicDriverLoader;
import net.pretronic.libraries.dependency.DependencyGroup;
import net.pretronic.libraries.dependency.DependencyGroupAdapter;
import net.pretronic.libraries.dependency.DependencyManager;
import net.pretronic.libraries.document.DocumentRegistry;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class PretronicDependencyDriverLoader implements DynamicDriverLoader {

    private final static Collection<DriverLoaderConfig> DRIVER_LOADER_CONFIGS = new ArrayList<>();
    private final static Collection<DependencyGroup> LOADED_GROUPS = new ArrayList<>();
    private static DependencyManager DEPENDENCY_MANAGER;
    private static Consumer<DependencyGroup> CLASS_LOADER;

    public static void setClassLoader(Consumer<DependencyGroup> classLoader) {
        CLASS_LOADER = classLoader;
    }

    public static DependencyManager getDependencyManager() {
        return DEPENDENCY_MANAGER;
    }

    public static void setDependencyManager(DependencyManager dependencyManager) {
        DEPENDENCY_MANAGER = dependencyManager;
        DocumentRegistry.getDefaultContext().registerAdapter(DependencyGroup.class,new DependencyGroupAdapter(dependencyManager));
    }

    public static void registerDriver(DriverLoaderConfig config){
        Validate.notNull(config);
        DRIVER_LOADER_CONFIGS.add(config);
    }

    public static void registerDefaults(){
        DRIVER_LOADER_CONFIGS.add(DocumentFileType.JSON.getReader()
                .read(PretronicDependencyDriverLoader.class.getClassLoader().getResourceAsStream("drivers/sql-driver.json"))
                .getAsObject(DriverLoaderConfig.class));
    }

    @SuppressWarnings("unchecked")
    public Class<? extends DatabaseDriver> loadDriver(String driverClassName) {
        DriverLoaderConfig config = Iterators.findOne(DRIVER_LOADER_CONFIGS, config1 -> config1.getDriverClass().equalsIgnoreCase(driverClassName));
        if(config == null) throw new IllegalArgumentException("Driver not found");

        if(!config.isLoaded()){
            config.getRequired().install();
            CLASS_LOADER.accept(config.getRequired());
            config.setLoaded(true);
        }

        try {
            return (Class<? extends DatabaseDriver>) Class.forName(config.getDriverClass());
        } catch (ClassNotFoundException e) {
            config.setLoaded(false);
            throw new IllegalArgumentException("Could not load driver class");
        }
    }

    @Override
    public void loadOptionalDriverDependencies(Class<? extends DatabaseDriver> driverClass, String name) {
        DriverLoaderConfig config = Iterators.findOne(DRIVER_LOADER_CONFIGS, config1 -> config1.getDriverClass().equalsIgnoreCase(driverClass.getName()));
        if(config == null) throw new IllegalArgumentException("Driver loader config not found");
        DependencyGroup optional = Iterators.findOne(config.getOptional(), dependencyGroup -> dependencyGroup.getName().equalsIgnoreCase(name));
        if(optional == null) throw new IllegalArgumentException("Dependencies not found");
        if(LOADED_GROUPS.contains(optional)) return;
        optional.install();
        CLASS_LOADER.accept(optional);
        LOADED_GROUPS.add(optional);
    }
}
