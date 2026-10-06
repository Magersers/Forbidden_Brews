package io.github.magersers.forbiddenbrews;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
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
    private static final BlockPos STAND=new BlockPos(1,65,1);
    @SubscribeEvent public static void setup(ServerStartedEvent event) {
        var level=event.getServer().overworld();
        for(int x=-10;x<=10;x++)for(int z=-20;z<=12;z++)level.setBlockAndUpdate(new BlockPos(x,64,z),Blocks.OBSIDIAN.defaultBlockState());
        for(int y=65;y<70;y++) {
            level.setBlockAndUpdate(new BlockPos(-4,y,5),Blocks.CYAN_CONCRETE.defaultBlockState());
            level.setBlockAndUpdate(new BlockPos(5,y,5),Blocks.ORANGE_CONCRETE.defaultBlockState());
        }
        level.setBlockAndUpdate(STAND,ForbiddenBrews.STAND.get().defaultBlockState());
        for(int i=0;i<4;i++) {
            var pos=new BlockPos(i-2,65,3);level.setBlockAndUpdate(pos.below(),Blocks.SOUL_SAND.defaultBlockState());
            level.setBlockAndUpdate(pos,ForbiddenBrews.WART.get().defaultBlockState().setValue(NetherWartBlock.AGE,i));
        }
        level.setDayTime(6000);level.setWeatherParameters(6000,0,false,false);
    }
    @SubscribeEvent public static void login(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity() instanceof ServerPlayer p) { player=p;ticks=0;p.teleportTo(p.serverLevel(),1.5,65,-1.8,0,12); }
    }
    @SubscribeEvent public static void tick(TickEvent.ServerTickEvent event) {
        if(event.phase!=TickEvent.Phase.END || player==null)return;
        ticks++;
        if(ticks==80) {
            var be=(ChaosBrewingBlockEntity)player.serverLevel().getBlockEntity(STAND);
            be.clearContent();be.progress=0;be.recipeIndex=-1;be.selectedRecipe=-1;be.fuel=0;
            player.openMenu(be);
        }
        if(ticks==120) {
            var be=(ChaosBrewingBlockEntity)player.serverLevel().getBlockEntity(STAND);
            be.setItem(0,ChaosContent.brew(new BrewSpec("creeper",1,false)));
        }
        if(ticks==160) {
            var be=(ChaosBrewingBlockEntity)player.serverLevel().getBlockEntity(STAND);
            var recipe=ChaosRecipes.ALL.stream().filter(r->r.result().equals(new BrewSpec("creeper",1,true))).findFirst().orElseThrow();
            be.setItem(1,new ItemStack(ForbiddenBrews.WART_ITEM.get(),4));
            for(int i=0;i<recipe.components().size();i++) {
                var c=recipe.components().get(i);be.setItem(i+2,new ItemStack(c.item(),c.count()));
            }
            be.setItem(6,new ItemStack(Items.BLAZE_POWDER,4));
        }
        if(ticks==520) {player.closeContainer();player.teleportTo(player.serverLevel(),1.5,65,-1.8,0,12);}
        if(ticks==550) {
            var bottle=ChaosContent.brew(new BrewSpec("inversion",1,false));
            player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,bottle.finishUsingItem(player.serverLevel(),player));
        }
        if(ticks==630)new ItemStack(Items.MILK_BUCKET).finishUsingItem(player.serverLevel(),player);
        if(ticks==660 || ticks==720)for(int x=-1;x<=3;x++)for(int y=65;y<=67;y++)
            player.serverLevel().setBlockAndUpdate(new BlockPos(x,y,ticks==660?0:4),Blocks.LIME_STAINED_GLASS.defaultBlockState());
        if(ticks==680) {
            var bottle=ChaosContent.brew(new BrewSpec("creeper",1,false));
            player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,bottle.finishUsingItem(player.serverLevel(),player));
        }
        if(ticks==740) {
            player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,ChaosContent.brew(new BrewSpec("creeper",1,true)));
            player.getMainHandItem().use(player.serverLevel(),player,net.minecraft.world.InteractionHand.MAIN_HAND);
        }
        if(ticks==741)player.teleportTo(player.serverLevel(),1.5,65,-13.8,0,12);
        if(ticks==860)player.getServer().halt(false);
    }
}
