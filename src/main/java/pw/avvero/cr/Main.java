package pw.avvero.cr;

import cucumber.runtime.*;
import cucumber.runtime.Runtime;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.java.JavaBackend;
import pw.avvero.cr.domain.Client;
import pw.avvero.cr.domain.Deposit;
import pw.avvero.cr.rules.GroupSelector;
import pw.avvero.cr.test.DataSet;
import pw.avvero.cr.test.DataSetExtractor;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class Main {

    public static void main(String[] strings) throws IOException {
        Set<String> result = calculateGroup(new Client("CHL", null), null);
    }

    public static Set<String> calculateGroup(Client client, Deposit deposit) throws IOException {
        GroupSelector rules = new GroupSelector(client, deposit);
        return execute(rules, "src/main/resources/group_distribution.feature");
    }

    public static DataSet getDataSet() throws IOException {
        DataSetExtractor rules = new DataSetExtractor();
        return execute(rules, "src/main/resources/group_distribution.feature");
    }

    public static <T> T execute (Rules<T> rules, String feature) throws IOException {
        ClassLoader classLoader = rules.getClass().getClassLoader();
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(rules.getClass());
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);
        NewJavaObjectFactory objectFactory = new NewJavaObjectFactory();
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        JavaBackend javaBackend = new JavaBackend(objectFactory, classFinder);
        Collection<? extends Backend> backends = Collections.singleton(javaBackend);
        Runtime runtime = new Runtime(resourceLoader, classLoader, backends, runtimeOptions);


        //TODO did it dirty
        objectFactory.cacheNewInstance(rules);
        runtimeOptions.getFeaturePaths().clear();
        runtimeOptions.getFeaturePaths().add(feature);
        //
        runtime.run();
        return rules.getResult();
    }
}
