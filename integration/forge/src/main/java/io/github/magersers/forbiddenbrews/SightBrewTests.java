package io.github.magersers.forbiddenbrews;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.gametest.*;

@GameTestHolder(ChaosContent.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SightBrewTests {
    @GameTest(template="empty",timeoutTicks=40)
    public static void sightDrinkSplashAndMilk(GameTestHelper h) {
        var player=h.makeMockPlayer();var target=h.spawn(EntityType.COW,new BlockPos(1,1,1));
        for(String family:new String[]{"ore_sight","hunter"}) {
            var spec=new BrewSpec(family,1,false);var effect=ChaosContent.effect(family);
            var drink=ChaosContent.brew(spec);
            h.assertTrue(drink.finishUsingItem(h.getLevel(),player).is(Items.GLASS_BOTTLE),"Native glass bottle after "+family);
            h.assertTrue(player.getEffect(effect).getDuration()==2400 && effect.getCategory()==MobEffectCategory.BENEFICIAL,"Two-minute beneficial "+family);
            new ItemStack(Items.MILK_BUCKET).finishUsingItem(h.getLevel(),player);
            h.assertFalse(VersionApi.hasEffect(player,effect),"Milk clears "+family);
            var projectile=new ThrownPotion(h.getLevel(),target.getX(),target.getY(),target.getZ()) {
                public void hit(Entity entity) {super.onHit(new EntityHitResult(entity));}
            };
            projectile.setItem(ChaosContent.brew(spec.asSplash()));projectile.hit(target);
            h.assertTrue(target.getEffect(effect).getDuration()==2400,"Direct splash carries full "+family+" duration");
            h.assertFalse(target.isCurrentlyGlowing(),"Potion does not set a global glowing flag");
            target.removeEffect(effect);
            var options=ChaosRecipes.available(ChaosContent.brew(spec));
            h.assertTrue(options.size()==1 && options.get(0).result().equals(spec.asSplash()),"Only contextual splash recipe for "+family);
        }
        h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=20)
    public static void sightClassifiesOresTrapsAndHostiles(GameTestHelper h) {
        for(var block:new net.minecraft.world.level.block.Block[]{Blocks.IRON_ORE,Blocks.DEEPSLATE_DIAMOND_ORE,Blocks.NETHER_QUARTZ_ORE,Blocks.ANCIENT_DEBRIS})
            h.assertTrue(SightTargets.ore(block.defaultBlockState()),"Ore tag contains "+block);
        for(var block:new net.minecraft.world.level.block.Block[]{Blocks.STONE_BUTTON,Blocks.OAK_BUTTON,Blocks.POLISHED_BLACKSTONE_BUTTON,Blocks.STONE_PRESSURE_PLATE,Blocks.OAK_PRESSURE_PLATE,Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE,Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,Blocks.TRIPWIRE,Blocks.TRIPWIRE_HOOK})
            h.assertTrue(SightTargets.trap(block.defaultBlockState()),"Hunter locates "+block);
        h.assertFalse(SightTargets.trap(Blocks.LEVER.defaultBlockState()) || SightTargets.ore(Blocks.STONE.defaultBlockState()),"Ordinary blocks do not become targets");
        var husk=EntityType.HUSK.create(h.getLevel());var cow=EntityType.COW.create(h.getLevel());
        h.assertTrue(SightTargets.enemy(husk) && !SightTargets.enemy(cow),"Hostiles highlighted, passive mobs excluded");
        husk.setHealth(0);h.assertFalse(SightTargets.enemy(husk),"Dead enemy excluded");h.succeed();
    }
}
