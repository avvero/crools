package pw.avvero.crools.impl.group_destribution;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Distribution {
    private Map<String, Object> facts;
    private Set<String> groups;
}