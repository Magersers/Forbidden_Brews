package io.github.magersers.forbiddenbrews;

import java.util.*;

/** Each item encodes a fixed effect; arbitrary potion NBT cannot change it. */
public record BrewSpec(String family, int level, boolean splash) {
    public static final List<BrewSpec> ALL;
    static {
        var specs = new ArrayList<BrewSpec>();
        for (String family : List.of("fortune", "looting", "homeward", "wild_teleport", "ore_double", "hot_pick", "inversion", "creeper")) {
            for (int level=1; level <= (family.equals("fortune") || family.equals("looting") ? 3 : 1); level++) {
                specs.add(new BrewSpec(family,level,false));
                specs.add(new BrewSpec(family,level,true));
            }
        }
        ALL = List.copyOf(specs);
    }
    public String id() { return family + "_" + level + (splash ? "_splash" : "_drink"); }
    public boolean instant() { return family.equals("homeward") || family.equals("wild_teleport") || family.equals("creeper"); }
    public int duration() { return instant() ? 1 : (switch(level) { case 1 -> 120; case 2 -> 300; default -> 480; })*20; }
    public String translation() { return "item.forbidden_brews." + id(); }
    public BrewSpec asSplash() { return new BrewSpec(family,level,true); }
}
