package pw.avvero.crools.impl.group_destribution;

import pw.avvero.crools.domain.Client;
import pw.avvero.crools.domain.Deposit;
import pw.avvero.crools.impl.FeatureAnalyzer;
import pw.avvero.crools.impl.group_destribution.definition.GroupSelector;
import pw.avvero.crools.impl.group_destribution.extraction.FactDictionary;
import pw.avvero.crools.impl.group_destribution.extraction.FactDictionaryExtractor;
import pw.avvero.crools.service.FeatureService;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GroupDistributionImpl extends FeatureAnalyzer {

    private String feature;

    public GroupDistributionImpl(String feature) {
        this.feature = feature;
    }

    @Override
    public Map analyse(FeatureService featureService) throws IOException {
        Map result = new HashMap();
        FactDictionary factDictionary = featureService.extractFacts(new FactDictionaryExtractor(), feature);
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
                    Set<String> calculatedGroups = featureService.execute(new GroupSelector(client, deposit), feature);
                    analyseEntry(facts, calculatedGroups, groupStats);
                    distributions.add(new Distribution(facts, calculatedGroups));
                });
            });
        });
        result.put("factDictionary", factDictionary);
        result.put("groupStats", groupStats.values());
        result.put("distributions", distributions);
        return result;
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
}
