package io.github.magersers.forbiddenbrews;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.*;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

/** Bounded search; distant chunk generation never blocks the server tick. */
public final class RandomTeleport {
    private static final Set<UUID> PENDING=new HashSet<>(); // server-thread only
    static BlockPos chooseTarget(ServerPlayer player) {
        int dx=player.getRandom().nextInt(4097)-2048,dz=player.getRandom().nextInt(4097)-2048;
        if(Math.max(Math.abs(dx),Math.abs(dz))<256)dx=dx<0?-256:256;
        return BlockPos.containing(player.getX()+dx,0,player.getZ()+dz);
    }
    public static void teleport(ServerPlayer player) {
        if(!PENDING.add(player.getUUID()))return;
        player.displayClientMessage(Component.translatable("message.forbidden_brews.teleport_search"),true);
        attempt(player,player.serverLevel(),0);
    }
    private static void attempt(ServerPlayer player,ServerLevel level,int tried) {
        if(player.isRemoved() || !player.isAlive() || player.serverLevel()!=level) { PENDING.remove(player.getUUID());return; }
        if(tried>=12) {
            PENDING.remove(player.getUUID());
            player.displayClientMessage(Component.translatable("message.forbidden_brews.teleport_unsafe"),true);return;
        }
        BlockPos target=chooseTarget(player);
        if(!level.getWorldBorder().isWithinBounds(target)) { level.getServer().execute(()->attempt(player,level,tried+1));return; }
        VersionApi.prepareChunk(level,target,()-> {
            if(player.isRemoved() || !player.isAlive() || player.serverLevel()!=level) { PENDING.remove(player.getUUID());return; }
            Vec3 landing=findLanding(level,player,target.getX(),target.getZ());
            if(landing==null) { attempt(player,level,tried+1);return; }
            PENDING.remove(player.getUUID());
            HomeSafety.teleport(player,level,landing,player.getYRot());
            level.sendParticles(ParticleTypes.PORTAL,landing.x,landing.y+1,landing.z,64,.5,.8,.5,.15);
            level.playSound(null,landing.x,landing.y,landing.z,SoundEvents.ENDERMAN_TELEPORT,SoundSource.PLAYERS,1,1);
        },()-> { PENDING.remove(player.getUUID());player.displayClientMessage(Component.translatable("message.forbidden_brews.teleport_unsafe"),true); });
    }
    static Vec3 findLanding(ServerLevel level,ServerPlayer player,int x,int z) {
        int top=level.dimensionType().hasCeiling()?Math.min(level.getMaxBuildHeight()-3,level.dimensionType().logicalHeight()-3):
            level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,x,z);
        for(int y=top;y>level.getMinBuildHeight();y--) {
            Vec3 candidate=new Vec3(x+.5,y,z+.5);
            if(HomeSafety.safe(level,player,candidate))return candidate;
        }
        return null;
    }
    private RandomTeleport() {}
}
