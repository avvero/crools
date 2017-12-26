package pw.avvero.crools

import pw.avvero.crools.domain.Client
import pw.avvero.crools.domain.Deposit
import pw.avvero.crools.impl.group_destribution.definition.GroupSelector
import pw.avvero.crools.impl.group_destribution.extraction.FactDictionaryExtractor
import pw.avvero.crools.service.FeatureService
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class MainTests extends Specification{

    public static final String FEATURE = "src/main/resources/group_distribution.feature"

    @Shared
    def featureService = new FeatureService()

    @Unroll
    def "For country #country group will be #group"() {
        expect:
        featureService.execute(new GroupSelector(new Client(country: country, language: language),
                new Deposit(amount: depositAmount)), FEATURE) == [group] as Set
        where:
        country | language | depositAmount | group
        "CHL"   | null     | null          | "Chile"
        "RUS"   | null     | null          | "Russia"
        "RUS"   | "rus"    | null          | "Russia"
        "RUS"   | "eng"    | null          | "EnRussia"
        "RUS"   | null     | 999           | "Russia"
        "RUS"   | null     | 1000          | "Russia"
        "RUS"   | null     | 1001          | "RichRussia"
        "RUS"   | "eng"    | 1001          | "EnRussia"
    }

    def "Extract dataset"() {
        expect:
        ruleService.extractFacts() == []
    }

    def "Batch test"() {
        when:
        def result = [:]
        def distributions = [:]
        def dataSet = featureService.extractFacts(new FactDictionaryExtractor(), FEATURE)
        dataSet.countries.each {country ->
            dataSet.languages.each {language ->
                dataSet.depositAmount.each {depositAmount ->
                    def data = [
                            client: new Client(country: country, language: language),
                            depost: new Deposit(amount: depositAmount)
                    ]
                    def group = featureService.execute(new GroupSelector(data.client, data.depost), FEATURE);
                    def key = group
                    if (result[key]){
                        result[key] << data
                    } else {
                        result[key] = [data]
                    }
                    // distributions
                    if (distributions[depositAmount]) {
                        distributions[depositAmount].addAll(group)
                    } else {
                        distributions[depositAmount] = group as Set
                    }
                }
            }
        }
        then:
//        result.collectEntries {key, value -> [key, value.size()]} == [:]
        distributions == [:]
    }

}
