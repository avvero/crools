package pw.avvero.cr

import pw.avvero.cr.domain.Client
import spock.lang.Specification
import spock.lang.Unroll

class MainTests extends Specification{

    @Unroll
    def "For country #country group will be #group"() {
        expect:
        Main.calculateGroup(new Client(country: country, language: language)) == group
        where:
        country | language | group
        "CHL"   | null     | "Chile"
        "RUS"   | null     | "Russia"
        "RUS"   | "rus"    | "Russia"
        "RUS"   | "eng"    | "EnRussia"
    }

    def "Extract dataset"() {
        expect:
        Main.getDataSet() == []
    }

}
