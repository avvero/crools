package pw.avvero.crools.definition;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.util.Assert;
import pw.avvero.crools.domain.Client;
import pw.avvero.crools.domain.Deposit;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@CucumberOptions(
        strict = true,
        plugin = {"pw.avvero.crools.ValidationPlugin"}
)
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
        Assert.isTrue(code.equals(client.getCountry()));
    }

    @When("^client country is not \"([^\"]*)\"$")
    public void clientCountryIsNot(String code) throws Throwable {
        Assert.isTrue(!code.equals(client.getCountry()));
    }

    @When("^client language is \"([^\"]*)\"$")
    public void clientLanguageIs(String code) throws Throwable {
        Assert.isTrue(code.equals(client.getLanguage()));
    }

    @When("^client language is not \"([^\"]*)\"$")
    public void clientLanguageIsNot(String code) throws Throwable {
        Assert.isTrue(!code.equals(client.getLanguage()));
    }

    @Then("^group will be \"([^\"]*)\"$")
    public void groupWillBe(String code) throws Throwable {
        group.add(code);
    }

    @And("^deposit more than (\\d+)$")
    public void depositMoreThan(BigDecimal amount) throws Throwable {
        Assert.isTrue(deposit != null
                && deposit.getAmount() != null
                && deposit.getAmount().compareTo(amount) > 0);
    }

    @Override
    public Set<String> getResult() {
        return group;
    }
}
