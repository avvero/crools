package pw.avvero.cr.rules;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import pw.avvero.cr.Rules;
import pw.avvero.cr.domain.Client;

public class GroupSelector implements Rules<String> {

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

    @When("^client language is \"([^\"]*)\"$")
    public void clientLanguageIs(String code) throws Throwable {
        assert code.equals(client.getLanguage());
    }

    @Then("^group will be \"([^\"]*)\"$")
    public void groupWillBe(String code) throws Throwable {
        group = code;
    }

    @Override
    public String getResult() {
        return group;
    }
}
