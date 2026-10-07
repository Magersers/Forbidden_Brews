package io.github.magersers.forbiddenbrews;

import java.util.*;
import java.util.function.BooleanSupplier;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.*;
import net.minecraft.world.level.border.WorldBorder;
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
        return chooseTarget(player,player.serverLevel().getWorldBorder());
    }
    static BlockPos chooseTarget(ServerPlayer player,WorldBorder border) {
        int px=player.blockPosition().getX(),pz=player.blockPosition().getZ();
        int minX=Math.max(px-2048,(int)Math.ceil(border.getMinX()+1)),maxX=Math.min(px+2048,(int)Math.floor(border.getMaxX()-1));
        int minZ=Math.max(pz-2048,(int)Math.ceil(border.getMinZ()+1)),maxZ=Math.min(pz+2048,(int)Math.floor(border.getMaxZ()-1));
        if(minX>maxX || minZ>maxZ)return player.blockPosition();
        int x=minX+player.getRandom().nextInt(maxX-minX+1),z=minZ+player.getRandom().nextInt(maxZ-minZ+1);
        if(Math.max(Math.abs(x-px),Math.abs(z-pz))<256) {
            int farther=px+(x<px?-256:256);
            if(farther>=minX && farther<=maxX)x=farther;
        }
        return new BlockPos(x,0,z);
    }
    /** Keep the original bottle until a destination is ready. Moving/dropping
     * it out of this inventory cancels the drink rather than giving a free trip. */
    public static void drink(ServerPlayer player,ItemStack bottle) {
        start(player,()-> {
            var inventory=player.getInventory();int slot=-1;
            for(int i=0;i<inventory.getContainerSize();i++)if(inventory.getItem(i)==bottle){slot=i;break;}
            if(slot<0 || bottle.isEmpty())return false;
            CriteriaTriggers.CONSUME_ITEM.trigger(player,bottle.copy());
            player.awardStat(Stats.ITEM_USED.get(bottle.getItem()));
            if(!player.getAbilities().instabuild) {
                bottle.shrink(1);
                var glass=new ItemStack(Items.GLASS_BOTTLE);
                if(bottle.isEmpty())inventory.setItem(slot,glass);
                else if(!inventory.add(glass))player.drop(glass,false);
                inventory.setChanged();player.containerMenu.broadcastChanges();
            }
            return true;
        });
    }
    public static void teleport(ServerPlayer player) {
        start(player,()->true);
    }
    private static void start(ServerPlayer player,BooleanSupplier commit) {
        if(!PENDING.add(player.getUUID()))return;
        player.displayClientMessage(Component.translatable("message.forbidden_brews.teleport_search"),true);
        attempt(player,player.serverLevel(),0,player.serverLevel().getGameTime(),commit);
    }
    static boolean pending(ServerPlayer player) {return PENDING.contains(player.getUUID());}
    private static void attempt(ServerPlayer player,ServerLevel level,int tried,long started,BooleanSupplier commit) {
        if(player.isRemoved() || !player.isAlive() || player.serverLevel()!=level) { PENDING.remove(player.getUUID());return; }
        if(tried>=24 || level.getGameTime()-started>=600) {
            PENDING.remove(player.getUUID());
            player.displayClientMessage(Component.translatable("message.forbidden_brews.teleport_unsafe"),true);return;
        }
        BlockPos target=chooseTarget(player);
        if(!level.getWorldBorder().isWithinBounds(target)) { level.getServer().execute(()->attempt(player,level,tried+1,started,commit));return; }
        VersionApi.prepareChunk(level,target,()-> {
            if(player.isRemoved() || !player.isAlive() || player.serverLevel()!=level) { PENDING.remove(player.getUUID());return; }
            Vec3 landing=findNearbyLanding(level,player,target.getX(),target.getZ());
            if(landing==null) { attempt(player,level,tried+1,started,commit);return; }
            PENDING.remove(player.getUUID());
            if(!commit.getAsBoolean())return;
            HomeSafety.teleport(player,level,landing,player.getYRot());
            level.sendParticles(ParticleTypes.PORTAL,landing.x,landing.y+1,landing.z,64,.5,.8,.5,.15);
            level.playSound(null,landing.x,landing.y,landing.z,SoundEvents.ENDERMAN_TELEPORT,SoundSource.PLAYERS,1,1);
        },()->attempt(player,level,tried+1,started,commit));
    }
    static Vec3 findNearbyLanding(ServerLevel level,ServerPlayer player,int x,int z) {
        // Search the entire prepared chunk without synchronously generating its neighbours.
        int minX=(x>>4)<<4,minZ=(z>>4)<<4;
        for(int radius=0;radius<=15;radius++)for(int dx=-radius;dx<=radius;dx++)for(int dz=-radius;dz<=radius;dz++) {
            if(Math.max(Math.abs(dx),Math.abs(dz))!=radius)continue;
            int nx=x+dx,nz=z+dz;
            if(nx<minX || nx>minX+15 || nz<minZ || nz>minZ+15)continue;
            Vec3 landing=findLanding(level,player,nx,nz);
            if(landing!=null)return landing;
        }
        return null;
    }
    static Vec3 findLanding(ServerLevel level,ServerPlayer player,int x,int z) {
        int top=level.dimensionType().hasCeiling()?Math.min(level.getMaxBuildHeight()-3,level.dimensionType().logicalHeight()-3):
            level.getHeight(Heightmap.Types.MOTION_BLOCKING,x,z);
        for(int y=top;y>level.getMinBuildHeight();y--) {
            Vec3 candidate=new Vec3(x+.5,y,z+.5);
            if(HomeSafety.safe(level,player,candidate))return candidate;
        }
        return null;
    }
    private RandomTeleport() {}
}
