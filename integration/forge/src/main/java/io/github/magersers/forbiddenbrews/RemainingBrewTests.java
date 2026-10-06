package io.github.magersers.forbiddenbrews;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.gametest.*;

@GameTestHolder(ChaosContent.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RemainingBrewTests {
    private static Player player(GameTestHelper h) {
        var p=h.makeMockPlayer();var at=h.absolutePos(new BlockPos(1,1,1));p.setPos(at.getX()+.5,200,at.getZ()+.5);
        for(int x=-2;x<=2;x++)for(int y=0;y<=4;y++)for(int z=-2;z<=2;z++)h.getLevel().setBlockAndUpdate(new BlockPos(at.getX()+x,200+y,at.getZ()+z),Blocks.AIR.defaultBlockState());
        return p;
    }
    private static void drink(GameTestHelper h,LivingEntity entity,String family) {
        ChaosContent.brew(new BrewSpec(family,1,false)).finishUsingItem(h.getLevel(),entity);Morphs.tick(entity);
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void batFlightDimensionsInventoryAndRestoration(GameTestHelper h) {
        var p=player(h);p.setHealth(17);p.getInventory().setItem(8,new ItemStack(Items.EMERALD,9));
        p.getAbilities().mayfly=false;p.getAbilities().flying=false;
        drink(h,p,"bat");
        h.assertTrue(Morphs.form(p)==Morphs.BAT && p.getAbilities().mayfly && p.getAbilities().flying,"Bat grants flight");
        h.assertTrue(Math.abs(p.getBbWidth()-.5)<.01 && Math.abs(p.getBbHeight()-.9)<.01 && Math.abs(p.getEyeHeight()-.45)<.01,"Bat collision and camera size");
        h.assertTrue(p.getHealth()==17 && p.getInventory().getItem(8).getCount()==9,"Health and inventory stay with original player");
        new ItemStack(Items.MILK_BUCKET).finishUsingItem(h.getLevel(),p);Morphs.tick(p);
        h.assertTrue(Morphs.form(p)==0 && !p.getAbilities().mayfly && !p.getAbilities().flying && Math.abs(p.getBbHeight()-1.8)<.01,"Milk restores player dimensions and flight permissions");
        h.assertFalse(p.causeFallDamage(40,1,p.damageSources().fall()),"Safe first landing after flight removal");
        p.getAbilities().mayfly=true;drink(h,p,"bat");p.removeAllEffects();Morphs.tick(p);
        h.assertTrue(p.getAbilities().mayfly && !p.getAbilities().flying,"Existing non-potion flight permission restored");h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void randomFormRefreshSaveAndPriority(GameTestHelper h) {
        var p=player(h);p.getRandom().setSeed(76543);drink(h,p,"shapeshifter");
        int first=Morphs.form(p);h.assertTrue(first>=3 && Morphs.type(first)!=null,"Random real mob selected");
        for(int i=0;i<10;i++)p.getEffect(ForbiddenBrews.SHAPESHIFTER.get()).tick(p,()->{});
        Morphs.tick(p);h.assertTrue(Morphs.form(p)==first,"Same form throughout effect");
        var tag=p.saveWithoutId(new net.minecraft.nbt.CompoundTag());var loaded=player(h);loaded.load(tag);Morphs.tick(loaded);
        h.assertTrue(Morphs.form(loaded)==first,"Saved form survives reload");
        drink(h,p,"shapeshifter");h.assertTrue(Morphs.form(p)!=first,"Fresh drink rerolls the mob");
        drink(h,p,"bat");h.assertTrue(Morphs.form(p)==Morphs.BAT,"Bat overrides random appearance");
        drink(h,p,"juggernaut");h.assertTrue(Morphs.form(p)==Morphs.BRUTE && !p.getAbilities().flying,"Brute overrides bat and releases flight: form="+Morphs.form(p)+", fits="+Morphs.fits(p,Morphs.BRUTE)+", pos="+p.position()+", hp="+p.getHealth());
        p.removeAllEffects();Morphs.tick(p);h.assertTrue(Morphs.form(p)==0,"Milk/expiry return original form");h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void remainingSplashPotionsCarryNativeEffects(GameTestHelper h) {
        var target=h.spawn(EntityType.COW,new BlockPos(1,1,1));target.setNoGravity(true);target.setNoAi(true);target.setPos(target.position().add(0,20,0));
        for(String family:new String[]{"bat","juggernaut","shapeshifter","gravity"}) {
            var projectile=new ThrownPotion(h.getLevel(),target.getX(),target.getY(),target.getZ()) {
                public void hit(Entity entity) {super.onHit(new EntityHitResult(entity));}
            };
            var spec=new BrewSpec(family,1,false);projectile.setItem(ChaosContent.brew(spec.asSplash()));projectile.hit(target);Morphs.tick(target);
            h.assertTrue(VersionApi.effectInstance(target,family).getDuration()==2400,"Direct splash duration "+family);
            if(!family.equals("gravity"))h.assertTrue(Morphs.form(target)>0,"Splash transforms living entity "+family);
            h.assertTrue(ChaosRecipes.available(ChaosContent.brew(spec)).size()==1,"Contextual splash recipe "+family);
            target.removeAllEffects();Morphs.tick(target);h.assertTrue(Morphs.form(target)==0,"Removal restores mob "+family);
        }
        h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=30)
    public static void gravityToggleDebouncePhysicsAndCleanup(GameTestHelper h) {
        var p=player(h);drink(h,p,"gravity");var state=(MorphState)p;
        Gravity.toggle(p);h.assertTrue(state.brews$gravityUp(),"First jump reverses gravity");
        Gravity.toggle(p);h.assertTrue(state.brews$gravityUp(),"Duplicate packets in same tick ignored");
        Morphs.tick(p);h.assertTrue(p.getDeltaMovement().y>0 && p.isNoGravity(),"Upward acceleration replaces ordinary falling");
        h.runAfterDelay(2,()-> {
            Gravity.toggle(p);Morphs.tick(p);h.assertFalse(state.brews$gravityUp() || p.isNoGravity(),"Second distinct jump restores downward gravity");
            h.assertTrue(p.getDeltaMovement().y<0,"Immediate descent on second jump");
            p.removeAllEffects();Morphs.tick(p);Gravity.toggle(p);h.assertFalse(state.brews$gravityUp(),"Requests without effect are rejected");
            h.assertFalse(p.causeFallDamage(40,1,p.damageSources().fall()),"Safe landing after effect ends in air");
            p.setNoGravity(true);drink(h,p,"gravity");p.removeAllEffects();Morphs.tick(p);h.assertTrue(p.isNoGravity(),"Original no-gravity flag is untouched");h.succeed();
        });
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void bruteSmashUsesSixteenBlocksNativeLootAndProtection(GameTestHelper h) {
        var level=h.getLevel();var p=new ServerPlayer(level.getServer(),level,new com.mojang.authlib.GameProfile(UUID.randomUUID(),"smash-test"));
        var channel=new io.netty.channel.embedded.EmbeddedChannel();
        var connection=new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND) {
            @Override public io.netty.channel.Channel channel() {return channel;}
            @Override public void send(net.minecraft.network.protocol.Packet<?> packet) {}
            @Override public void send(net.minecraft.network.protocol.Packet<?> packet,net.minecraft.network.PacketSendListener listener) {}
        };
        p.connection=new net.minecraft.server.network.ServerGamePacketListenerImpl(level.getServer(),connection,p);
        var at=h.absolutePos(new BlockPos(1,1,1));var center=new BlockPos(at.getX(),200,at.getZ()+40);
        for(int x=-3;x<=4;x++)for(int y=0;y<=5;y++)for(int z=-6;z<=1;z++)level.setBlockAndUpdate(center.offset(x,y,z),Blocks.AIR.defaultBlockState());
        p.setPos(center.getX()+.5,center.getY(),center.getZ()-3.5);p.setYRot(0);p.setXRot(0);
        p.getInventory().setItem(0,new ItemStack(Items.NETHERITE_PICKAXE));p.getInventory().selected=0;drink(h,p,"juggernaut");
        h.assertTrue(Morphs.form(p)==Morphs.BRUTE && Math.abs(p.getBbHeight()-2.9)<.01,"Brute model/collision height: form="+Morphs.form(p)+", height="+p.getBbHeight()+", fits="+Morphs.fits(p,Morphs.BRUTE)+", pos="+p.position()+", hp="+p.getHealth());
        var plane=Destruction.plane(center,new Vec3(0,0,1));h.assertTrue(plane.size()==16 && new HashSet<>(plane).size()==16,"Exactly four by four");
        for(var pos:plane)level.setBlockAndUpdate(pos,Blocks.STONE.defaultBlockState());
        var protectedPos=center.offset(2,2,0);level.setBlockAndUpdate(protectedPos,Blocks.BEDROCK.defaultBlockState());
        var outside=center.offset(3,0,0);level.setBlockAndUpdate(outside,Blocks.STONE.defaultBlockState());
        var denied=center.offset(-1,2,0);
        java.util.function.Consumer<net.minecraftforge.event.level.BlockEvent.BreakEvent> listener=e->{if(e.getPos().equals(denied))e.setCanceled(true);};
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.addListener(listener);
        try {
            h.assertTrue(p.gameMode.destroyBlock(center),"Actual successful mining triggers smash");
            long broken=plane.stream().filter(pos->level.getBlockState(pos).isAir()).count();
            h.assertTrue(broken==14 && level.getBlockState(denied).is(Blocks.STONE),"Native protection event leaves its block: "+broken);
            h.assertTrue(level.getBlockState(protectedPos).is(Blocks.BEDROCK) && level.getBlockState(outside).is(Blocks.STONE),"Unbreakable and outside blocks survive");
            h.assertTrue(p.getMainHandItem().getDamageValue()==14,"Native durability for every broken block");
        } finally {net.minecraftforge.common.MinecraftForge.EVENT_BUS.unregister(listener);channel.finishAndReleaseAll();}
        h.succeed();
    }
}
