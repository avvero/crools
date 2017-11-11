import spock.lang.Specification
import spock.lang.Unroll

class MainTests extends Specification{

    @Unroll
    def "For country #country group will be #group"() {
        expect:
        Main.calculateGroup(country) == group
        where:
        country | group
        "CHL"   | "Experiment"
        "RUS"   | "Default"
    }

}
