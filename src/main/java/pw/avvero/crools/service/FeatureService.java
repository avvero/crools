package pw.avvero.crools.service;

import cucumber.runtime.*;
import cucumber.runtime.Runtime;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.java.JavaBackend;
import org.springframework.stereotype.Service;
import pw.avvero.crools.impl.Definition;
import pw.avvero.crools.impl.FactExtractor;
import pw.avvero.crools.impl.NewJavaObjectFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Service
public class FeatureService {

    public <T> T execute(Definition<T> definition, String feature) {
        ClassLoader classLoader = definition.getClass().getClassLoader();
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(definition.getClass());
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);
        NewJavaObjectFactory objectFactory = new NewJavaObjectFactory();
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        JavaBackend javaBackend = new JavaBackend(objectFactory, classFinder);
        Collection<? extends Backend> backends = Collections.singleton(javaBackend);
        Runtime runtime = new Runtime(resourceLoader, classLoader, backends, runtimeOptions);


        //TODO did it dirty
        objectFactory.cacheNewInstance(definition);
        runtimeOptions.getFeaturePaths().clear();
        runtimeOptions.getFeaturePaths().add(feature);
        //
        try {
            runtime.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return definition.getResult();
    }

    public <T> T extractFacts(FactExtractor<T> factExtractor, String feature) throws IOException {
        return execute(factExtractor, feature);
    }


}
