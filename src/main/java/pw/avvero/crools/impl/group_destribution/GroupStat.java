package pw.avvero.crools.impl.group_destribution;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
public class GroupStat {
    private String name;
    private int count;
    private Set<Map> facts = new HashSet<>();

    public GroupStat(String name) {
        this.name = name;
    }
}