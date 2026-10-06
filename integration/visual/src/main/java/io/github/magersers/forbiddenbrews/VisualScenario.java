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
    private static net.minecraft.world.entity.monster.Husk first,second;
    private static final BlockPos STAND=new BlockPos(1,65,1);
    @SubscribeEvent public static void setup(ServerStartedEvent event) {
        var level=event.getServer().overworld();
        for(int x=-28;x<=28;x++)for(int z=-28;z<=28;z++)level.setBlockAndUpdate(new BlockPos(x,64,z),Blocks.OBSIDIAN.defaultBlockState());
        for(int y=65;y<70;y++) {
            level.setBlockAndUpdate(new BlockPos(-4,y,5),Blocks.CYAN_CONCRETE.defaultBlockState());
            level.setBlockAndUpdate(new BlockPos(5,y,5),Blocks.ORANGE_CONCRETE.defaultBlockState());
        }
        level.setBlockAndUpdate(STAND,ForbiddenBrews.STAND.get().defaultBlockState());
        for(int i=0;i<4;i++) {
            var pos=new BlockPos(i-2,65,3);level.setBlockAndUpdate(pos.below(),Blocks.SOUL_SAND.defaultBlockState());
            level.setBlockAndUpdate(pos,ForbiddenBrews.WART.get().defaultBlockState().setValue(NetherWartBlock.AGE,i));
        }
        level.getGameRules().getRule(net.minecraft.world.level.GameRules.RULE_DOMOBSPAWNING).set(true,level.getServer());
        level.setDayTime(6000);level.setWeatherParameters(6000,0,false,false);
    }
    @SubscribeEvent public static void login(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity() instanceof ServerPlayer p) { player=p;ticks=0;p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.removeAllEffects();p.setHealth(20);
            p.serverLevel().getEntitiesOfClass(net.minecraft.world.entity.Mob.class,p.getBoundingBox().inflate(96),m->m instanceof net.minecraft.world.entity.monster.Enemy).forEach(net.minecraft.world.entity.Entity::discard);p.teleportTo(p.serverLevel(),1.5,65,-1.8,0,12); }
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
            be.setItem(0,ChaosContent.brew(new BrewSpec("truce",1,false)));
        }
        if(ticks==160) {
            var be=(ChaosBrewingBlockEntity)player.serverLevel().getBlockEntity(STAND);
            var recipe=ChaosRecipes.ALL.stream().filter(r->r.result().equals(new BrewSpec("truce",1,true))).findFirst().orElseThrow();
            be.setItem(1,new ItemStack(ForbiddenBrews.WART_ITEM.get(),4));
            for(int i=0;i<recipe.components().size();i++) {
                var c=recipe.components().get(i);be.setItem(i+2,new ItemStack(c.item(),c.count()));
            }
            be.setItem(6,new ItemStack(Items.BLAZE_POWDER,4));
        }
        if(ticks==520) {player.closeContainer();player.teleportTo(player.serverLevel(),1.5,65,-16.5,0,10);}
        if(ticks==550) {
            var bottle=ChaosContent.brew(new BrewSpec("truce",1,false));
            player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,bottle.finishUsingItem(player.serverLevel(),player));
        }
        if(ticks==555) {
            first=net.minecraft.world.entity.EntityType.HUSK.create(player.serverLevel());
            second=net.minecraft.world.entity.EntityType.HUSK.create(player.serverLevel());
            first.moveTo(.5,65,-12.5,180,0);second.moveTo(4.5,65,-10.5,180,0);
            player.serverLevel().addFreshEntity(first);player.serverLevel().addFreshEntity(second);
            first.setTarget(player);second.setTarget(player);
            if(first.getTarget()!=null || second.getTarget()!=null)throw new IllegalStateException("Truce did not pacify nearby mobs");
            System.out.println("SOCIAL_TRUCE_TARGETS_OK");
        }
        if(ticks==580) {
            player.getRandom().setSeed(1234567);
            var bottle=ChaosContent.brew(new BrewSpec("swarm",1,false));
            player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,bottle.finishUsingItem(player.serverLevel(),player));
        }
        if(ticks==600 || ticks==710 || ticks==850) {
            var candidates=player.serverLevel().getEntitiesOfClass(net.minecraft.world.entity.Mob.class,player.getBoundingBox().inflate(32),m->m instanceof net.minecraft.world.entity.monster.Enemy && m.distanceToSqr(player)>100);
            if(!candidates.isEmpty())player.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES,candidates.get(0).getEyePosition());
        }
        if(ticks==620) {
            player.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES,first.getEyePosition());
            player.attack(first);
            if(Truce.suppresses(first,player) || !Truce.suppresses(second,player))throw new IllegalStateException("Truce retaliation must affect only the struck mob");
            first.setTarget(player);System.out.println("SOCIAL_TRUCE_RETALIATION_OK");
        }
        if(ticks==700)first.discard();
        if(ticks==900) {
            var mobs=player.serverLevel().getEntitiesOfClass(net.minecraft.world.entity.Mob.class,player.getBoundingBox().inflate(32),m->m instanceof net.minecraft.world.entity.monster.Enemy);
            if(!player.serverLevel().isDay() || mobs.size()<3)throw new IllegalStateException("Swarm did not create daylight mobs");
            System.out.println("SOCIAL_SWARM_DAYLIGHT_OK "+mobs.size());
        }
        if(ticks==960)new ItemStack(Items.MILK_BUCKET).finishUsingItem(player.serverLevel(),player);
        if(ticks==1130)player.getServer().halt(false);
    }
}
