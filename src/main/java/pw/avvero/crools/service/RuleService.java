package pw.avvero.crools.service;

import cucumber.runtime.Backend;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.java.JavaBackend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import pw.avvero.crools.analize.FactDictionary;
import pw.avvero.crools.analize.FactDictionaryExtractor;
import pw.avvero.crools.definition.GroupSelector;
import pw.avvero.crools.definition.Rules;
import pw.avvero.crools.domain.Client;
import pw.avvero.crools.domain.Deposit;
import pw.avvero.crools.impl.NewJavaObjectFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RuleService {

    public <T> T execute(Rules<T> rules, String feature) {
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
        try {
            runtime.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rules.getResult();
    }

    public Set<String> calculateGroup(Client client, Deposit deposit) {
        GroupSelector rules = new GroupSelector(client, deposit);
        return execute(rules, "src/main/resources/group_distribution.feature");
    }

    public FactDictionary getFactDictionary() throws IOException {
        return getFactDictionary("src/main/resources/group_distribution.feature");
    }

    public FactDictionary getFactDictionary(String feature) throws IOException {
        FactDictionaryExtractor rules = new FactDictionaryExtractor();
        return execute(rules, feature);
    }

    public Map analise(String feature) throws IOException {
        Map result = new HashMap();
        FactDictionary factDictionary = getFactDictionary(feature);
        Map<String, GroupStat> groupStats = new HashMap<>();
        Set<Distribution> distributions = new HashSet<>();
        factDictionary.getCountries().forEach(country -> {
            factDictionary.getLanguages().forEach(language -> {
                factDictionary.getDepositAmount().forEach(depositAmount -> {
                    Client client = new Client(country, language);
                    Deposit deposit = new Deposit(depositAmount);
                    Map<String, Object> facts = new HashMap<String, Object>() {{
                        put("client", client);
                        put("deposit", deposit);
                    }};
                    Set<String> calculatedGroups = execute(new GroupSelector(client, deposit), feature);
                    analiseEntry(facts, calculatedGroups, groupStats);
                    distributions.add(new Distribution(facts, calculatedGroups));
                });
            });
        });
        result.put("factDictionary", factDictionary);
        result.put("groupStats", groupStats.values());
        result.put("distributions", distributions);
        return result;
    }

    private void analiseEntry(Map<String, Object> facts, Set<String> calculatedGroups, Map<String, GroupStat> groupStats) {
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
    @NoArgsConstructor
    public class GroupStat {
        private String name;
        private int count;
        private Set<Map> facts = new HashSet<>();

        public GroupStat(String name) {
            this.name = name;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Distribution {
        private Map<String, Object> facts;
        private Set<String> groups;
    }
}
