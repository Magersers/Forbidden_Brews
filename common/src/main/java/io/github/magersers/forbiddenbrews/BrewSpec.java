package io.github.magersers.forbiddenbrews;

import java.util.*;

/** Each item encodes a fixed effect; arbitrary potion NBT cannot change it. */
public record BrewSpec(String family, int level, boolean splash) {
    public static final List<BrewSpec> ALL;
    static {
        var specs = new ArrayList<BrewSpec>();
        for (String family : List.of("fortune", "looting", "homeward")) {
            for (int level=1; level <= (family.equals("homeward") ? 1 : 3); level++) {
                specs.add(new BrewSpec(family,level,false));
                specs.add(new BrewSpec(family,level,true));
            }
        }
        ALL = List.copyOf(specs);
    }
    public String id() { return family + "_" + level + (splash ? "_splash" : "_drink"); }
    public int duration() { return family.equals("homeward") ? 1 : (240 - 60*level)*20; }
    public String translation() { return "item.forbidden_brews." + id(); }
    public BrewSpec asSplash() { return new BrewSpec(family,level,true); }
}
