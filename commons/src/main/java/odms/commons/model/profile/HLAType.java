package odms.commons.model.profile;

import java.util.HashSet;
import java.util.Set;

public class HLAType {
    private Set<String> g1c1 = new HashSet<>();
    private Set<String> g1c2 = new HashSet<>();
    private Set<String> g2c1 = new HashSet<>();
    private Set<String> g2c2 = new HashSet<>();

    public HLAType() {}

    public HLAType(Set<String> g1c1, Set<String> g1c2, Set<String> g2c1, Set<String> g2c2) {
        this.g1c1 = g1c1;
        this.g1c2 = g1c2;
        this.g2c1 = g2c1;
        this.g2c2 = g2c2;
    }

    public void setG1c1(Set<String> g1c1) { this.g1c1 = g1c1; }
    public void setG1c2(Set<String> g1c2) { this.g1c2 = g1c2; }
    public void setG2c1(Set<String> g2c1) { this.g2c1 = g2c1; }
    public void setG2c2(Set<String> g2c2) { this.g2c2 = g2c2; }
    public Set<String> getG1c1() { return g1c1; }
    public Set<String> getG1c2() { return g1c2; }
    public Set<String> getG2c1() { return g2c1; }
    public Set<String> getG2c2() { return g2c2; }
}

