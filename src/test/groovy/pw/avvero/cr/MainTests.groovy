package pw.avvero.cr

import pw.avvero.cr.domain.Client
import pw.avvero.cr.domain.Deposit
import spock.lang.Specification
import spock.lang.Unroll

class MainTests extends Specification{

    @Unroll
    def "For country #country group will be #group"() {
        expect:
        Main.calculateGroup(new Client(country: country, language: language), new Deposit(amount: depositAmount)) == group
        where:
        country | language | depositAmount | group
        "CHL"   | null     | null          | ["Chile"]
        "RUS"   | null     | null          | ["Russia"]
        "RUS"   | "rus"    | null          | ["Russia"]
        "RUS"   | "eng"    | null          | ["EnRussia"]
        "RUS"   | null     | 999           | ["Russia"]
        "RUS"   | null     | 1000          | ["Russia"]
        "RUS"   | null     | 1001          | ["RichRussia"]
        "RUS"   | "eng"    | 1001          | ["EnRussia"]
    }

    def "Extract dataset"() {
        expect:
        Main.getDataSet() == []
    }

    def "Batch test"() {
        when:
        def result = [:]
        def distributions = [:]
        def dataSet = Main.getDataSet()
        dataSet.countries.each {country ->
            dataSet.languages.each {language ->
                dataSet.depositAmount.each {depositAmount ->
                    def data = [
                            client: new Client(country: country, language: language),
                            depost: new Deposit(amount: depositAmount)
                    ]
                    def group = Main.calculateGroup(data.client, data.depost)
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
