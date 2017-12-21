package pw.avvero.crools.analize;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
public class DataSet {

    private Set<String> countries = new HashSet<>();
    private Set<String> languages = new HashSet<>();
    private Set<BigDecimal> depositAmount = new HashSet<>();
    private Set<String> groups = new HashSet<>();

    public DataSet() {
//        countries.add(null);
//        languages.add(null);
        depositAmount.add(null);
        groups.add(null);
    }
}
