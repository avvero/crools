package pw.avvero.crools.impl.group_destribution.extraction;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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
}
