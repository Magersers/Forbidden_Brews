package io.github.magersers.forbiddenbrews;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Isolated dev world only; this source is excluded from release builds. */
@Mod.EventBusSubscriber(modid=ChaosContent.MOD_ID)
public final class VisualScenario {
    private static ServerPlayer player;
    private static int ticks;
    private static Mob combatTarget,combatNear,dragonActor;
    private static final BlockPos STAND=new BlockPos(1,65,1);
    private static final BlockPos NEAR=new BlockPos(0,66,-3), FAR=new BlockPos(4,66,-3), SMASH=new BlockPos(1,66,-20);
    @SubscribeEvent public static void setup(ServerStartedEvent event) {
        var level=event.getServer().overworld();
        for(int x=-28;x<=28;x++)for(int z=-28;z<=28;z++) {
            level.setBlockAndUpdate(new BlockPos(x,64,z),Blocks.OBSIDIAN.defaultBlockState());
            for(int y=65;y<74;y++)level.setBlock(new BlockPos(x,y,z),Blocks.AIR.defaultBlockState(),2);
        }
        for(int y=65;y<70;y++) {
            level.setBlockAndUpdate(new BlockPos(-4,y,5),Blocks.CYAN_CONCRETE.defaultBlockState());
            level.setBlockAndUpdate(new BlockPos(5,y,5),Blocks.ORANGE_CONCRETE.defaultBlockState());
        }
        level.setBlockAndUpdate(STAND,ForbiddenBrews.STAND.get().defaultBlockState());
        for(int i=0;i<4;i++) {
            var pos=new BlockPos(i-2,65,3);level.setBlockAndUpdate(pos.below(),Blocks.SOUL_SAND.defaultBlockState());
            level.setBlockAndUpdate(pos,ForbiddenBrews.WART.get().defaultBlockState().setValue(NetherWartBlock.AGE,i));
        }
        for(int x=-6;x<=8;x++)for(int y=65;y<=72;y++)level.setBlockAndUpdate(new BlockPos(x,y,-5),Blocks.DEEPSLATE_BRICKS.defaultBlockState());
        level.setBlockAndUpdate(NEAR,Blocks.DIAMOND_ORE.defaultBlockState());level.setBlockAndUpdate(FAR,Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(-4,67,-3),Blocks.IRON_ORE.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(-2,69,-3),Blocks.COPPER_ORE.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(2,69,-3),Blocks.EMERALD_ORE.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(6,68,-3),Blocks.REDSTONE_ORE.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(0,71,-3),Blocks.LAPIS_ORE.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(-5,70,-3),Blocks.GOLD_ORE.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(7,71,-3),Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(1,65,14),Blocks.ANCIENT_DEBRIS.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(1,65,17),Blocks.DIAMOND_ORE.defaultBlockState());
        level.getGameRules().getRule(net.minecraft.world.level.GameRules.RULE_DOMOBSPAWNING).set(false,level.getServer());
        level.setDayTime(6000);level.setWeatherParameters(6000,0,false,false);
    }
    @SubscribeEvent public static void login(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity() instanceof ServerPlayer p) {
            player=p;ticks=0;p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.removeAllEffects();Morphs.tick(p);p.setHealth(20);
            p.serverLevel().getEntitiesOfClass(Mob.class,p.getBoundingBox().inflate(96)).forEach(Entity::discard);
            p.serverLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,p.getBoundingBox().inflate(96)).forEach(Entity::discard);
            p.getInventory().clearContent();p.teleportTo(p.serverLevel(),1.5,65,-1.8,0,12);
        }
    }
    private static void drink(String family) {
        var bottle=ChaosContent.brew(new BrewSpec(family,1,false));
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,bottle.finishUsingItem(player.serverLevel(),player));Morphs.tick(player);
    }
    private static void clear() {
        new ItemStack(Items.MILK_BUCKET).finishUsingItem(player.serverLevel(),player);Morphs.tick(player);
        player.teleportTo(player.serverLevel(),1.5,65,-16.5,180,0);player.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);player.fallDistance=0;
    }
    @SubscribeEvent public static void tick(TickEvent.ServerTickEvent event) {
        if(event.phase!=TickEvent.Phase.END || player==null)return;
        ticks++;
        if(java.nio.file.Files.exists(java.nio.file.Path.of("073-capture.flag"))) {
            if(ticks==20) {
                var be=(ChaosBrewingBlockEntity)player.serverLevel().getBlockEntity(STAND);
                be.clearContent();be.progress=0;be.recipeIndex=-1;be.selectedRecipe=-1;be.fuel=0;
                be.selectRecipe(ChaosRecipes.ALL.indexOf(ChaosRecipes.ALL.stream().filter(r->r.result().equals(new BrewSpec("juggernaut",1,false))).findFirst().orElseThrow()));
                player.openMenu(be);
            }
            if(ticks==70) {
                var be=(ChaosBrewingBlockEntity)player.serverLevel().getBlockEntity(STAND);
                be.setItem(1,new ItemStack(ForbiddenBrews.WART_ITEM.get()));be.setItem(2,new ItemStack(Items.NETHERITE_INGOT));be.setItem(3,new ItemStack(Items.IRON_BLOCK));
            }
            if(ticks==120) {
                player.closeContainer();player.teleportTo(player.serverLevel(),1.5,65,-16.5,0,2);drink("juggernaut");player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(Items.NETHERITE_AXE));
                combatTarget=EntityType.COW.create(player.serverLevel());combatNear=EntityType.COW.create(player.serverLevel());
                combatTarget.setPos(1.5,65,-14.5);combatNear.setPos(2.5,65,-14.5);
                for(var cow:new Mob[]{combatTarget,combatNear}) {cow.setNoAi(true);player.serverLevel().addFreshEntity(cow);}
            }
            if(ticks==170) {
                player.attack(combatTarget);
                if(combatNear.getHealth()>=combatNear.getMaxHealth() || combatNear.getDeltaMovement().horizontalDistance()<.5)
                    throw new IllegalStateException("Native Brute area impact failed");
                System.out.println("073_NATIVE_AREA_ATTACK_OK");
            }
            if(ticks==210) {
                combatTarget.discard();combatNear.discard();player.removeAllEffects();Morphs.tick(player);
                player.teleportTo(player.serverLevel(),8,70,-16.5,90,-8);
                dragonActor=EntityType.COW.create(player.serverLevel());dragonActor.setNoAi(true);dragonActor.setNoGravity(true);dragonActor.setPos(-12,72,-16.5);player.serverLevel().addFreshEntity(dragonActor);
                ChaosContent.brew(new BrewSpec("shapeshifter",1,false)).finishUsingItem(player.serverLevel(),dragonActor);
                var data=((MorphState)dragonActor).brews$data();data.randomForm=Morphs.formOf(EntityType.ENDER_DRAGON);data.reroll=false;data.previousDuration=2400;Morphs.tick(dragonActor);
                System.out.println("073_NATIVE_DRAGON_FORM_OK "+Morphs.pool(player.level()).size());
            }
            if(ticks==290) {dragonActor.discard();player.teleportTo(player.serverLevel(),1.5,65,-16.5,0,0);drink("gravity");Gravity.toggle(player);}
            if(ticks==340) {Gravity.toggle(player);player.teleportTo(player.serverLevel(),1.5,69,-16.5,0,0);}
            if(ticks==380) {Gravity.toggle(player);new ItemStack(Items.MILK_BUCKET).finishUsingItem(player.serverLevel(),player);Morphs.tick(player);player.teleportTo(player.serverLevel(),1.5,65,-16.5,0,0);}
            if(ticks==450)player.getServer().halt(false);
            return;
        }
        if(java.nio.file.Files.exists(java.nio.file.Path.of("sight-morph.flag"))) {
            if(ticks==20 || ticks==160 || ticks==280 || ticks==400) {
                player.serverLevel().setBlockAndUpdate(new BlockPos(1,67,-11),Blocks.AIR.defaultBlockState());
                player.removeAllEffects();Morphs.tick(player);player.teleportTo(player.serverLevel(),1.5,65,-10.5,0,4);
                player.getRandom().setSeed(12);
                drink(ticks==160?"juggernaut":ticks==400?"shapeshifter":"ore_sight");
            }
            if(ticks==80)drink("juggernaut");
            // A cramped shaft/falling ceiling must not cancel an existing Brute.
            if(ticks==100 || ticks==175)player.serverLevel().setBlockAndUpdate(new BlockPos(1,67,-11),Blocks.STONE.defaultBlockState());
            if(ticks==180 || ticks==420)drink("ore_sight");
            if(ticks==300)drink("shapeshifter");
            if(ticks==140 || ticks==240 || ticks==360 || ticks==480) {
                int form=Morphs.form(player);boolean brute=ticks<280;
                if(!VersionApi.hasEffect(player,ChaosContent.effect("ore_sight")) || (brute?form!=Morphs.BRUTE:form<3))
                    throw new IllegalStateException("Sight/morph effects failed to stack: "+ticks+" form="+form);
                if(brute && player.getMaxHealth()!=40)throw new IllegalStateException("Combined Brute lost health bonus");
                if(brute && Morphs.fits(player,Morphs.BRUTE))throw new IllegalStateException("Mine ceiling did not obstruct the Brute fixture");
                System.out.println("SIGHT_MORPH_SERVER_STACK_OK "+ticks+" form="+form+" maxHealth="+player.getMaxHealth());
            }
            if(ticks==520) {player.removeAllEffects();Morphs.tick(player);}
            if(ticks==580)player.getServer().halt(false);
            return;
        }
        if(java.nio.file.Files.exists(java.nio.file.Path.of("stand-capture.flag"))) {
            if(ticks==20) {
                var pos=new BlockPos(0,65,1);player.serverLevel().setBlockAndUpdate(pos,Blocks.CRAFTING_TABLE.defaultBlockState());
                player.openMenu(new net.minecraft.world.SimpleMenuProvider((id,inventory,p)->new net.minecraft.world.inventory.CraftingMenu(id,inventory,
                    net.minecraft.world.inventory.ContainerLevelAccess.create(player.serverLevel(),pos)),net.minecraft.network.chat.Component.translatable("container.crafting")));
                var menu=player.containerMenu;
                Item[] grid={Items.AMETHYST_SHARD,Items.BREWING_STAND,Items.AMETHYST_SHARD,Items.AIR,Items.NETHERITE_INGOT,Items.AIR,Items.OBSIDIAN,Items.BLAZE_ROD,Items.OBSIDIAN};
                for(int i=0;i<9;i++)menu.getSlot(i+1).set(new ItemStack(grid[i]));
                menu.slotsChanged(menu.getSlot(1).container);menu.broadcastChanges();
                if(!menu.getSlot(0).getItem().is(ForbiddenBrews.STAND_ITEM.get()))throw new IllegalStateException("Native crafting recipe did not produce the Chaos Stand");
                System.out.println("NATIVE_CHAOS_STAND_CRAFTING_OK");
            }
            if(ticks==100) {player.closeContainer();player.serverLevel().setBlockAndUpdate(new BlockPos(0,65,1),Blocks.AIR.defaultBlockState());}
            if(ticks>=110 && ticks<=230) {
                double angle=(ticks-110)*0.006;
                player.teleportTo(player.serverLevel(),1.5+Math.sin(angle)*2.8,65,1.5-Math.cos(angle)*2.8,0,0);
                player.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES,new net.minecraft.world.phys.Vec3(1.5,65.65,1.5));
            }
            if(ticks==280)player.getServer().halt(false);
            return;
        }
        if(ticks==80) {
            var be=(ChaosBrewingBlockEntity)player.serverLevel().getBlockEntity(STAND);
            be.clearContent();be.progress=0;be.recipeIndex=-1;be.selectedRecipe=-1;be.fuel=0;player.openMenu(be);
        }
        if(ticks==120) {
            var be=(ChaosBrewingBlockEntity)player.serverLevel().getBlockEntity(STAND);be.setItem(0,ChaosContent.brew(new BrewSpec("juggernaut",1,false)));
        }
        if(ticks==160) {
            var be=(ChaosBrewingBlockEntity)player.serverLevel().getBlockEntity(STAND);
            var recipe=ChaosRecipes.ALL.stream().filter(r->r.result().equals(new BrewSpec("juggernaut",1,true))).findFirst().orElseThrow();
            be.setItem(1,new ItemStack(ForbiddenBrews.WART_ITEM.get(),4));
            for(int i=0;i<recipe.components().size();i++) {var c=recipe.components().get(i);be.setItem(i+2,new ItemStack(c.item(),c.count()));}
            be.setItem(6,new ItemStack(Items.BLAZE_POWDER,4));
        }
        if(ticks==520) {player.closeContainer();player.teleportTo(player.serverLevel(),1.5,65,-16.5,0,4);}
        if(ticks==560) {drink("ore_sight");drink("hunter");}
        if(ticks==660)player.serverLevel().setBlockAndUpdate(NEAR,Blocks.AIR.defaultBlockState());
        if(ticks==700)clear();
        if(ticks==720)drink("bat");
        if(ticks==850)clear();
        if(ticks==880) {drink("juggernaut");player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,new ItemStack(Items.NETHERITE_PICKAXE));}
        if(ticks==910)for(var pos:Destruction.plane(SMASH,new net.minecraft.world.phys.Vec3(0,0,-1)))player.serverLevel().setBlockAndUpdate(pos,Blocks.STONE.defaultBlockState());
        if(ticks==920)player.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES,SMASH.getCenter());
        if(ticks==990) {
            if(Destruction.plane(SMASH,new net.minecraft.world.phys.Vec3(0,0,-1)).stream().anyMatch(p->!player.serverLevel().getBlockState(p).isAir()))throw new IllegalStateException("Real client did not mine the four-by-four wall");
            int drops=player.serverLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new net.minecraft.world.phys.AABB(SMASH).inflate(6)).stream()
                .filter(e->e.getItem().is(Items.COBBLESTONE)).mapToInt(e->e.getItem().getCount()).sum();
            // The moving client may collect some of the native drops before this check.
            // Count both remaining item entities and the initially empty player inventory.
            drops+=player.getInventory().items.stream().filter(s->s.is(Items.COBBLESTONE)).mapToInt(ItemStack::getCount).sum();
            if(drops!=16)throw new IllegalStateException("Expected native loot for 16 blocks: "+drops);
            System.out.println("REMAINING_SERVER_SMASH_AND_LOOT_OK "+drops);
        }
        if(ticks==1030)clear();
        if(ticks==1060) {player.getRandom().setSeed(12);drink("shapeshifter");System.out.println("REMAINING_SERVER_RANDOM_FORM "+Morphs.type(Morphs.form(player)).getDescription());}
        if(ticks==1200)clear();
        if(ticks==1230)drink("gravity");
        if(ticks==1560)clear();
        if(ticks==1800)player.getServer().halt(false);
    }
}
