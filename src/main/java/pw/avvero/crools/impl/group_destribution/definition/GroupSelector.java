package pw.avvero.crools.impl.group_destribution.definition;

import cucumber.api.CucumberOptions;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.util.Assert;
import pw.avvero.crools.domain.Client;
import pw.avvero.crools.domain.Deposit;
import pw.avvero.crools.impl.Definition;
import pw.avvero.crools.impl.group_destribution.extraction.FactDictionary;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static pw.avvero.crools.impl.group_destribution.extraction.FactDictionaryExtractor.UNDEFINED;

@CucumberOptions(
        strict = true,
        plugin = {"pw.avvero.crools.ValidationPlugin"}
)
public class GroupSelector implements Definition<Set<String>> {

    private Client client;
    private Deposit deposit;
    private Set<String> groups = new HashSet<>();
    private FactDictionary factDictionary;

    public GroupSelector(Map<String, Object> facts, FactDictionary factDictionary) {
        this.client = (Client) facts.get("client");
        this.deposit = (Deposit) facts.get("deposit");
        this.factDictionary = factDictionary;
    }

    @When("^client country is \"([^\"]*)\"$")
    public void clientCountryIs(String code) throws Throwable {
        Assert.isTrue(code.equals(client.getCountry()));
    }

    @When("^client country is not \"([^\"]*)\"$")
    public void clientCountryIsNot(String code) throws Throwable {
        Assert.isTrue(!code.equals(client.getCountry()));
    }

    @When("^client country is not defined$")
    public void clientCountryIsNotDefined() throws Throwable {
        Assert.isTrue(UNDEFINED.equals(client.getCountry()) || !factDictionary.getCountries().contains(client.getCountry()));
    }

    @When("^client country no in$")
    public void clientCountryNotIn(DataTable dataTable) throws Throwable {
        Assert.isTrue(!dataTable.raw().stream().flatMap(List::stream).collect(Collectors.toList()).contains(client.getCountry()));
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
        groups.add(code);
    }

    @And("^deposit >= (\\d+)$")
    public void depositMoreThan(BigDecimal amount) throws Throwable {
        Assert.isTrue(deposit != null
                && deposit.getAmount() != null
                && deposit.getAmount().compareTo(amount) >= 0);
    }

    @And("^deposit < (\\d+)$")
    public void depositLessThan(BigDecimal amount) throws Throwable {
        Assert.isTrue(deposit != null
                && deposit.getAmount() != null
                && deposit.getAmount().compareTo(amount) < 0);
    }

    @And("^deposit is null$")
    public void depositIsNull() throws Throwable {
        Assert.isTrue(deposit == null || deposit.getAmount() == null);
    }

    @Override
    public Set<String> getResult() {
        return groups;
    }
}
