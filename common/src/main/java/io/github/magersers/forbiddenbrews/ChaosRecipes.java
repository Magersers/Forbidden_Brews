package io.github.magersers.forbiddenbrews;

import java.util.*;
import net.minecraft.world.Container;
import net.minecraft.world.item.*;

/** Server and client use the same definitions. Input components are unordered. */
public final class ChaosRecipes {
    public record Component(Item item, int count) {}
    public record Recipe(String id, BrewSpec source, BrewSpec result, int ticks, List<Component> components) {
        public ItemStack base() { return source == null ? VersionApi.water() : ChaosContent.brew(source); }
        public ItemStack output() { return ChaosContent.brew(result); }
        public boolean acceptsBase(ItemStack bottle) {
            return source==null ? VersionApi.isWater(bottle) : bottle.is(ChaosContent.brewItems.get(source.id()).get());
        }
        public boolean matches(Container inv) {
            ItemStack bottle = inv.getItem(0);
            if (!acceptsBase(bottle)) return false;
            if (!inv.getItem(1).is(ChaosContent.wartItem.get())) return false;
            return allocation(inv) != null;
        }
        // Each slot can hold one material. Extra materials and insufficient counts
        // invalidate the recipe. Counts may be split over several matching slots.
        public int[] allocation(Container inv) {
            int[] used=new int[4];
            for (Component ingredient:components) {
                int remaining=ingredient.count();
                for (int i=0;i<4 && remaining>0;i++) {
                    ItemStack stack=inv.getItem(i+2);
                    if (stack.is(ingredient.item())) {
                        int take=Math.min(remaining,stack.getCount()-used[i]);
                        used[i]+=take;remaining-=take;
                    }
                }
                if (remaining>0) return null;
            }
            for (int i=0;i<4;i++) if (!inv.getItem(i+2).isEmpty() && used[i]==0) return null;
            return used;
        }
        public void consume(Container inv) {
            int[] used=allocation(inv);
            if (used==null || !matches(inv)) throw new IllegalStateException("Recipe changed before completion");
            inv.removeItem(0,1); inv.removeItem(1,1);
            for(int i=0;i<4;i++) inv.removeItem(i+2,used[i]);
        }
    }
    public static final List<Recipe> ALL;
    static {
        var list=new ArrayList<Recipe>();
        add(list,"fortune",1,200,c(Items.RABBIT_FOOT,1),c(Items.GOLD_INGOT,4),c(Items.EMERALD,1));
        add(list,"fortune",2,300,c(Items.RABBIT_FOOT,2),c(Items.DIAMOND,1),c(Items.EMERALD_BLOCK,1));
        add(list,"fortune",3,400,c(Items.GOLDEN_APPLE,1),c(Items.DIAMOND_BLOCK,1),c(Items.NETHER_STAR,1));
        add(list,"looting",1,240,c(Items.GHAST_TEAR,1),c(Items.IRON_INGOT,4),c(Items.BLAZE_ROD,1));
        add(list,"looting",2,340,c(Items.GHAST_TEAR,2),c(Items.DIAMOND,2),c(Items.ENDER_PEARL,2));
        add(list,"looting",3,440,c(Items.NETHER_STAR,1),c(Items.DIAMOND_BLOCK,1),c(Items.NETHERITE_SCRAP,1));
        add(list,"homeward",1,240,c(Items.COMPASS,1),c(Items.ENDER_PEARL,2),c(Items.AMETHYST_SHARD,4));
        add(list,"wild_teleport",1,320,c(Items.CHORUS_FRUIT,4),c(Items.ENDER_PEARL,2),c(Items.AMETHYST_SHARD,8),c(Items.ECHO_SHARD,1));
        add(list,"ore_double",1,360,c(Items.DIAMOND,2),c(Items.RAW_GOLD,4),c(Items.AMETHYST_SHARD,8),c(Items.NETHERITE_SCRAP,1));
        add(list,"hot_pick",1,280,c(Items.MAGMA_CREAM,2),c(Items.BLAST_FURNACE,1),c(Items.BLAZE_ROD,2),c(Items.RAW_IRON,8));
        add(list,"inversion",1,280,c(Items.FERMENTED_SPIDER_EYE,2),c(Items.PHANTOM_MEMBRANE,1),c(Items.AMETHYST_SHARD,6),c(Items.REDSTONE,4));
        add(list,"creeper",1,360,c(Items.TNT,1),c(Items.GUNPOWDER,4),c(Items.SLIME_BALL,2),c(Items.BLAZE_POWDER,2));
        for (BrewSpec spec:BrewSpec.ALL) if (!spec.splash()) {
            list.add(new Recipe(spec.id()+"_to_splash",spec,spec.asSplash(),160,List.of(c(Items.GUNPOWDER,2))));
        }
        ALL=List.copyOf(list);
    }
    private static Component c(Item item,int count) { return new Component(item,count); }
    private static void add(List<Recipe> list,String family,int level,int ticks,Component... parts) {
        BrewSpec source=level==1 ? null : new BrewSpec(family,level-1,false);
        BrewSpec result=new BrewSpec(family,level,false);
        list.add(new Recipe(result.id(),source,result,ticks,List.of(parts)));
    }
    public static Recipe find(Container inv) { return ALL.stream().filter(r->r.matches(inv)).findFirst().orElse(null); }
    public static List<Recipe> available(ItemStack base) {
        return ALL.stream().filter(r->base.isEmpty()?r.source()==null:r.acceptsBase(base)).toList();
    }
    private ChaosRecipes() {}
}
