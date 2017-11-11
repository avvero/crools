import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GroupSelector {

    public String country;
    public String group;

    public GroupSelector() {
    }

    public GroupSelector(String country) {
        this.country = country;
    }

    @When("^client country is \"([^\"]*)\"$")
    public void clientCountryIs(String code) throws Throwable {
        assert code.equals(country);
    }

    @Then("^group will be \"([^\"]*)\"$")
    public void groupWillBe(String code) throws Throwable {
        group = code;
    }
}
