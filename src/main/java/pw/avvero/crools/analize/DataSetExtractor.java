package pw.avvero.crools.analize;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.Data;
import pw.avvero.crools.definition.Rules;

import java.math.BigDecimal;

@Data
public class DataSetExtractor implements Rules<DataSet> {

    private DataSet dataSet = new DataSet();

    @When("^client country is \"([^\"]*)\"$")
    public void clientCountryIs(String code) throws Throwable {
        dataSet.getCountries().add(code);
    }

    @Then("^group will be \"([^\"]*)\"$")
    public void groupWillBe(String code) throws Throwable {
        dataSet.getGroups().add(code);
    }

    @When("^client language is \"([^\"]*)\"$")
    public void clientLanguageIs(String code) throws Throwable {
        dataSet.getLanguages().add(code);
    }

    @And("^deposit more than (\\d+)$")
    public void depositMoreThan(BigDecimal amount) throws Throwable {
        dataSet.getDepositAmount().add(amount);
        dataSet.getDepositAmount().add(amount.add(BigDecimal.valueOf(-1)));
        dataSet.getDepositAmount().add(amount.add(BigDecimal.valueOf(1)));
    }

    @Override
    public DataSet getResult() {
        return dataSet;
    }
}
