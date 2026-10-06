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
        for(int x=-6;x<=7;x++)for(int z=-5;z<=7;z++)level.setBlockAndUpdate(new BlockPos(x,64,z),Blocks.OBSIDIAN.defaultBlockState());
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
            be.setItem(0,ChaosRecipes.ALL.get(1).base());
        }
        if(ticks==160) {
            var be=(ChaosBrewingBlockEntity)player.serverLevel().getBlockEntity(STAND);
            var recipe=ChaosRecipes.ALL.get(1);be.setItem(1,new ItemStack(ForbiddenBrews.WART_ITEM.get(),4));
            for(int i=0;i<recipe.components().size();i++) {
                var c=recipe.components().get(i);be.setItem(i+2,new ItemStack(c.item(),c.count()));
            }
            be.setItem(6,new ItemStack(Items.BLAZE_POWDER,4));
        }
        if(ticks==600)player.getServer().halt(false);
    }
}
