package pw.avvero.crools.service;

import cucumber.api.java.en.*;
import cucumber.runtime.*;
import cucumber.runtime.Runtime;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.java.JavaBackend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import pw.avvero.crools.impl.Definition;
import pw.avvero.crools.impl.FactExtractor;
import pw.avvero.crools.impl.NewJavaObjectFactory;
import pw.avvero.crools.impl.group_destribution.Distribution;
import pw.avvero.crools.impl.group_destribution.GroupStat;
import pw.avvero.crools.impl.group_destribution.definition.GroupSelector;
import pw.avvero.crools.impl.group_destribution.extraction.FactDictionary;
import pw.avvero.crools.impl.group_destribution.extraction.FactDictionaryExtractor;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

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

    public Map analyse(String feature) throws IOException {
        Map result = new HashMap();
        FactDictionary factDictionary = extractFacts(new FactDictionaryExtractor(), feature);
        Map<String, GroupStat> groupStats = new HashMap<>();
        Set<Distribution> distributions = new HashSet<>();

        factDictionary.eachVariant(facts -> {
            Set<String> calculatedGroups = execute(new GroupSelector(facts, factDictionary), feature);
            analyseEntry(facts, calculatedGroups, groupStats);
            distributions.add(new Distribution(facts, calculatedGroups));
        });

        result.put("factDictionary", factDictionary);
        result.put("groupStats", groupStats.values());
        result.put("distributions", distributions);
        result.put("expressions", getExpressions(GroupSelector.class));
        return result;
    }

    private Object getExpressions(Class<GroupSelector> clazz) {
        Set<ExpressionUsage> expressions = new HashSet<>();
        for (Method method : clazz.getMethods()){
            When when = method.getAnnotation(When.class);
            if (when != null) {
                expressions.add(new ExpressionUsage(when.value(), "When", 0));
            }
            Then then = method.getAnnotation(Then.class);
            if (then != null) {
                expressions.add(new ExpressionUsage(then.value(), "Then", 0));
            }
            Given given = method.getAnnotation(Given.class);
            if (given != null) {
                expressions.add(new ExpressionUsage(given.value(), "Given", 0));
            }
            But but = method.getAnnotation(But.class);
            if (but != null) {
                expressions.add(new ExpressionUsage(but.value(), "But", 0));
            }
            And and = method.getAnnotation(And.class);
            if (and != null) {
                expressions.add(new ExpressionUsage(and.value(), "And", 0));
            }
        }
        return expressions;
    }

    private void analyseEntry(Map<String, Object> facts, Set<String> calculatedGroups, Map<String, GroupStat> groupStats) {
        if (calculatedGroups == null || calculatedGroups.isEmpty()) {
            populateStatistics(null, groupStats, facts);
        } else {
            calculatedGroups.forEach(calculatedGroup -> populateStatistics(calculatedGroup, groupStats, facts));
        }
    }

    private void populateStatistics(String calculatedGroup, Map<String, GroupStat> groupStats, Map<String, Object> facts) {
        GroupStat groupStat = groupStats.computeIfAbsent(calculatedGroup, k -> new GroupStat(calculatedGroup));
        groupStat.setCount(groupStat.getCount() + 1);
        groupStat.getFacts().add(facts);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class ExpressionUsage {
        private String value;
        private String step;
        private int usage;
    }
}
