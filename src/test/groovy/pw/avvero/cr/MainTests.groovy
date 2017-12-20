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

    def "butch"() {
        when:
        def resultMap = [:]
        Variants.COUNTRIES.each {
            def country = it
            Variants.LANGUAGES.each {
                def language = it
                def group = Main.calculateGroup(new Client(country: country, language: language))
                def n = resultMap[group] ? resultMap[group] : 0
                resultMap[group] = ++n
            }
        }
        println resultMap
        then:
        1 == 1
    }

}
