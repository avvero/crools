package pw.avvero.crools.impl.group_destribution.extraction;

import lombok.Data;
import pw.avvero.crools.domain.Client;
import pw.avvero.crools.domain.Deposit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Data
public class FactDictionary {

    private Set<String> countries = new HashSet<>();
    private Set<String> languages = new HashSet<>();
    private Set<BigDecimal> depositAmount = new HashSet<>();
    private Set<String> groups = new HashSet<>();

    public FactDictionary() {
        countries.add(null);
        languages.add(null);
        depositAmount.add(null);
        groups.add(null);
    }

    public void eachVariant(Consumer<Map<String, Object>> c) {
        countries.forEach(country -> {
            //todo required field
//            if (country == null) return;

            languages.forEach(language -> {
                //todo required field
//                if (language == null) return;

                depositAmount.forEach(depositAmount -> {
                    Client client = new Client(country, language);
                    Deposit deposit = new Deposit(depositAmount);
                    Map<String, Object> facts = new HashMap<String, Object>() {{
                        put("client", client);
                        put("deposit", deposit);
                    }};
                    c.accept(facts);
                });
            });
        });
    }
}
