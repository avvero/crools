package pw.avvero.cr.test;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.Data;
import pw.avvero.cr.Rules;

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

    @Override
    public DataSet getResult() {
        return dataSet;
    }
}
