package io.github.magersers.forbiddenbrews;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
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
    private static final BlockPos STAND=new BlockPos(1,65,1);
    private static final BlockPos NEAR=new BlockPos(0,66,-3), FAR=new BlockPos(4,66,-3);
    @SubscribeEvent public static void setup(ServerStartedEvent event) {
        var level=event.getServer().overworld();
        // Reset the reused test area, including the previous effect demo.
        for(int x=-28;x<=28;x++)for(int z=-28;z<=28;z++) {
            level.setBlockAndUpdate(new BlockPos(x,64,z),Blocks.OBSIDIAN.defaultBlockState());
            for(int y=65;y<73;y++)level.setBlock(new BlockPos(x,y,z),Blocks.AIR.defaultBlockState(),2);
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
        // Every subject is hidden behind this solid wall from the capture position.
        for(int x=-6;x<=8;x++)for(int y=65;y<=72;y++)level.setBlockAndUpdate(new BlockPos(x,y,-5),Blocks.DEEPSLATE_BRICKS.defaultBlockState());
        level.setBlockAndUpdate(NEAR,Blocks.DIAMOND_ORE.defaultBlockState());
        level.setBlockAndUpdate(FAR,Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(-3,66,-2),Blocks.OBSIDIAN.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(-3,66,-3),Blocks.STONE_BUTTON.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(-2,65,-3),Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(6,65,-2),Blocks.OBSIDIAN.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(6,65,-3),Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING,Direction.NORTH));
        level.setBlockAndUpdate(new BlockPos(6,65,-4),Blocks.TRIPWIRE.defaultBlockState());
        level.getGameRules().getRule(net.minecraft.world.level.GameRules.RULE_DOMOBSPAWNING).set(false,level.getServer());
        level.setDayTime(6000);level.setWeatherParameters(6000,0,false,false);
    }
    @SubscribeEvent public static void login(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity() instanceof ServerPlayer p) {
            player=p;ticks=0;p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.removeAllEffects();p.setHealth(20);
            p.serverLevel().getEntitiesOfClass(Mob.class,p.getBoundingBox().inflate(96)).forEach(Entity::discard);
            p.teleportTo(p.serverLevel(),1.5,65,-1.8,0,12);
        }
    }
    private static void drink(String family) {
        var bottle=ChaosContent.brew(new BrewSpec(family,1,false));
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,bottle.finishUsingItem(player.serverLevel(),player));
    }
    @SubscribeEvent public static void tick(TickEvent.ServerTickEvent event) {
        if(event.phase!=TickEvent.Phase.END || player==null)return;
        ticks++;
        if(ticks==80) {
            var be=(ChaosBrewingBlockEntity)player.serverLevel().getBlockEntity(STAND);
            be.clearContent();be.progress=0;be.recipeIndex=-1;be.selectedRecipe=-1;be.fuel=0;player.openMenu(be);
        }
        if(ticks==120) {
            var be=(ChaosBrewingBlockEntity)player.serverLevel().getBlockEntity(STAND);
            be.setItem(0,ChaosContent.brew(new BrewSpec("hunter",1,false)));
        }
        if(ticks==160) {
            var be=(ChaosBrewingBlockEntity)player.serverLevel().getBlockEntity(STAND);
            var recipe=ChaosRecipes.ALL.stream().filter(r->r.result().equals(new BrewSpec("hunter",1,true))).findFirst().orElseThrow();
            be.setItem(1,new ItemStack(ForbiddenBrews.WART_ITEM.get(),4));
            for(int i=0;i<recipe.components().size();i++) {
                var c=recipe.components().get(i);be.setItem(i+2,new ItemStack(c.item(),c.count()));
            }
            be.setItem(6,new ItemStack(Items.BLAZE_POWDER,4));
        }
        if(ticks==520) {player.closeContainer();player.teleportTo(player.serverLevel(),1.5,65,-16.5,0,4);}
        if(ticks==560)drink("ore_sight");
        if(ticks==660)player.serverLevel().setBlockAndUpdate(NEAR,Blocks.AIR.defaultBlockState());
        if(ticks==700) {
            var husk=EntityType.HUSK.create(player.serverLevel());husk.setNoAi(true);husk.moveTo(2.5,65,-2,180,0);player.serverLevel().addFreshEntity(husk);
            var cow=EntityType.COW.create(player.serverLevel());cow.setNoAi(true);cow.moveTo(5,65,-2,180,0);player.serverLevel().addFreshEntity(cow);
            var far=EntityType.HUSK.create(player.serverLevel());far.setNoAi(true);far.setNoGravity(true);far.moveTo(40.5,65,-2,180,0);player.serverLevel().addFreshEntity(far);
        }
        if(ticks==720)drink("hunter");
        if(ticks==950)new ItemStack(Items.MILK_BUCKET).finishUsingItem(player.serverLevel(),player);
        if(ticks==1130)player.getServer().halt(false);
    }
}
