package pw.avvero.cr.rules;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import pw.avvero.cr.Rules;
import pw.avvero.cr.domain.Client;
import pw.avvero.cr.domain.Deposit;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupSelector implements Rules<Set<String>> {

    public Client client;
    public Deposit deposit;
    public Set<String> group = new HashSet<>();

    public GroupSelector() {
    }

    public GroupSelector(Client client, Deposit deposit) {
        this.client = client;
        this.deposit = deposit;
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
        group.add(code);
    }

    @Override
    public Set<String> getResult() {
        return group;
    }

    @And("^deposit more than (\\d+)$")
    public void depositMoreThan(BigDecimal amount) throws Throwable {
        assert deposit !=null
                && deposit.getAmount() !=null
                && deposit.getAmount().compareTo(amount) > 0;
    }
}
