package pw.avvero.cr.test;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class DataSet {

    private Set<String> countries = new HashSet<>();
    private Set<String> languages = new HashSet<>();
    private Set<String> groups = new HashSet<>();

}
