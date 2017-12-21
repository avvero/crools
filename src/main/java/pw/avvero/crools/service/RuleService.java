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
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import pw.avvero.crools.analize.DataSet;
import pw.avvero.crools.analize.DataSetExtractor;
import pw.avvero.crools.definition.GroupSelector;
import pw.avvero.crools.definition.Rules;
import pw.avvero.crools.domain.Client;
import pw.avvero.crools.domain.Deposit;
import pw.avvero.crools.impl.NewJavaObjectFactory;

import java.io.IOException;
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

    public DataSet getDataSet() throws IOException {
        DataSetExtractor rules = new DataSetExtractor();
        return execute(rules, "src/main/resources/group_distribution.feature");
    }

    public Map analise(DataSetExtractor rules, String feature) throws IOException {
        Map result = new HashMap();
        DataSet dataSet = getDataSet();
        Map<String, GroupStat> groupStats = new HashMap<>();
        Map<String, Object> distributions = new HashMap<>();
        AtomicInteger variants = new AtomicInteger(0);

        dataSet.getCountries().forEach(country -> {
            dataSet.getLanguages().forEach(language -> {
                dataSet.getDepositAmount().forEach(depositAmount -> {
                    variants.incrementAndGet();
                    Client client = new Client(country, language);
                    Deposit deposit = new Deposit(depositAmount);

                    Set<String> calculateGroups = calculateGroup(client, deposit);
                    if (calculateGroups == null || calculateGroups.isEmpty()) {
                        GroupStat groupStat = groupStats.computeIfAbsent(null, k -> new GroupStat(null));
                        groupStat.setCount(groupStat.getCount() + 1);
                        groupStat.getFacts().add(new HashMap<String, Object>() {
                            {
                                put("client", client);
                                put("deposit", deposit);
                            }
                        });
                    }
                    calculateGroups.forEach(calculatedGroup -> {
                        GroupStat groupStat = groupStats.computeIfAbsent(calculatedGroup, k -> new GroupStat(calculatedGroup));
                        groupStat.setCount(groupStat.getCount() + 1);
                        groupStat.getFacts().add(new HashMap<String, Object>() {
                            {
                                put("client", client);
                                put("deposit", deposit);
                            }
                        });
                    });
                });
            });
        });

        result.put("dataSet", dataSet);
        result.put("variants", variants);
        result.put("groupStats", groupStats.values());
        result.put("distributions", distributions);
        return result;
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
}
