package pw.avvero.cr;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GroupSelector {

    public Client client;
    public String group;

    public GroupSelector() {
    }

    public GroupSelector(Client client) {
        this.client = client;
    }

    @When("^client country is \"([^\"]*)\"$")
    public void clientCountryIs(String code) throws Throwable {
        assert code.equals(client.getCountry());
    }

    @Then("^group will be \"([^\"]*)\"$")
    public void groupWillBe(String code) throws Throwable {
        group = code;
    }
}
