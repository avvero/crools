package pw.avvero.crools.analize;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.Data;
import org.springframework.util.Assert;
import pw.avvero.crools.definition.Rules;

import java.math.BigDecimal;

@Data
public class FactDictionaryExtractor implements Rules<FactDictionary> {

    private FactDictionary factDictionary = new FactDictionary();

    @When("^client country is \"([^\"]*)\"$")
    public void clientCountryIs(String code) throws Throwable {
        factDictionary.getCountries().add(code);
        factDictionary.getCountries().add("foo");
    }

    @When("^client country is not \"([^\"]*)\"$")
    public void clientCountryIsNot(String code) throws Throwable {
        factDictionary.getCountries().add("not_" + code);
        factDictionary.getCountries().add("foo");
    }

    @Then("^group will be \"([^\"]*)\"$")
    public void groupWillBe(String code) throws Throwable {
        factDictionary.getGroups().add(code);
    }

    @When("^client language is \"([^\"]*)\"$")
    public void clientLanguageIs(String code) throws Throwable {
        factDictionary.getLanguages().add(code);
        factDictionary.getLanguages().add("foo");
    }

    @When("^client language is not\"([^\"]*)\"$")
    public void clientLanguageIsNot(String code) throws Throwable {
        factDictionary.getLanguages().add(code);
        factDictionary.getLanguages().add("foo");
    }

    @And("^deposit more than (\\d+)$")
    public void depositMoreThan(BigDecimal amount) throws Throwable {
        factDictionary.getDepositAmount().add(amount);
        factDictionary.getDepositAmount().add(amount.add(BigDecimal.valueOf(-1)));
        factDictionary.getDepositAmount().add(amount.add(BigDecimal.valueOf(1)));
    }

    @Override
    public FactDictionary getResult() {
        return factDictionary;
    }
}
