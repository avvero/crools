package pw.avvero.cr

import spock.lang.Specification
import spock.lang.Unroll

class MainTests extends Specification{

    @Unroll
    def "For country #country group will be #group"() {
        expect:
        Main.calculateGroup(new Client(country: country, language: language)) == group
        where:
        country | language | group
        "CHL"   | null     | "Experiment"
        "RUS"   | null     | "Default"
    }

}
