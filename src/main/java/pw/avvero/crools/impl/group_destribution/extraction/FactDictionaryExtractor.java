package pw.avvero.crools.impl.group_destribution.extraction;

import cucumber.api.CucumberOptions;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.Data;
import org.springframework.util.Assert;
import pw.avvero.crools.impl.FactExtractor;

import java.math.BigDecimal;

@Data
@CucumberOptions(
        strict = true,
        plugin = {"pw.avvero.crools.ValidationPlugin"}
)
public class FactDictionaryExtractor implements FactExtractor<FactDictionary> {
    
    public static final String ANY = "any";
    public static final String OTHER = "other";

    private FactDictionary factDictionary = new FactDictionary();

    @When("^client country is \"([^\"]*)\"$")
    public void clientCountryIs(String code) throws Throwable {
        factDictionary.getCountries().add(code);
        factDictionary.getCountries().add(ANY);
    }

    @When("^client country is not \"([^\"]*)\"$")
    public void clientCountryIsNot(String code) throws Throwable {
        factDictionary.getCountries().add("not_" + code);
        factDictionary.getCountries().add(ANY);
    }

    @When("^client country is not defined$")
    public void clientCountryIsNotDefined() throws Throwable {
    }

    @Then("^group will be \"([^\"]*)\"$")
    public void groupWillBe(String code) throws Throwable {
        factDictionary.getGroups().add(code);
    }

    @When("^client language is \"([^\"]*)\"$")
    public void clientLanguageIs(String code) throws Throwable {
        factDictionary.getLanguages().add(code);
        factDictionary.getLanguages().add(ANY);
    }

    @When("^client language is not \"([^\"]*)\"$")
    public void clientLanguageIsNot(String code) throws Throwable {
        clientLanguageIs(code);
    }

    @And("^deposit >= (\\d+)$")
    public void depositMoreThan(BigDecimal amount) throws Throwable {
        factDictionary.getDepositAmount().add(amount);
        factDictionary.getDepositAmount().add(amount.add(BigDecimal.valueOf(-1)));
        factDictionary.getDepositAmount().add(amount.add(BigDecimal.valueOf(1)));
    }

    @And("^deposit < (\\d+)$")
    public void depositLessThan(BigDecimal amount) throws Throwable {
        depositMoreThan(amount);
    }

    @And("^deposit is null$")
    public void depositIsNull() throws Throwable {
        factDictionary.getDepositAmount().add(null);
    }

    @Override
    public FactDictionary getResult() {
        return factDictionary;
    }
}
