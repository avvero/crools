import cucumber.runtime.*;
import cucumber.runtime.Runtime;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.java.JavaBackend;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class Main {

    public static void main(String[] strings) throws IOException {
        calculateGroup("CHL");
    }

    public static String calculateGroup(String country) throws IOException {
        Class clazz = Main.class;
        ClassLoader classLoader = clazz.getClassLoader();
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(clazz);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);
        NewJavaObjectFactory objectFactory = new NewJavaObjectFactory();
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        JavaBackend javaBackend = new JavaBackend(objectFactory, classFinder);
        Collection<? extends Backend> backends = Collections.singleton(javaBackend);
        Runtime runtime = new Runtime(resourceLoader, classLoader, backends, runtimeOptions);


        //TODO dirty
        GroupSelector rules = new GroupSelector(country);
        objectFactory.cacheNewInstance(rules);
        runtimeOptions.getFeaturePaths().clear();
        runtimeOptions.getFeaturePaths().add("src/main/resources/group_distribution.feature");
        //
        runtime.run();
        return rules.group;
    }

}
