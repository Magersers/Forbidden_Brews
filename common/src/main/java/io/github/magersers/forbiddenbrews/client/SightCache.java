package io.github.magersers.forbiddenbrews.client;

import io.github.magersers.forbiddenbrews.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/** Incremental, loaded-chunk-only scan. No block search is performed per frame. */
public final class SightCache {
    private static final int SCAN_BUDGET=8192;
    private static ClientLevel level;
    private static BlockPos origin, nearest;
    private static boolean oreOn,hunterOn;
    private static int radius,side,cursor;
    private static List<BlockPos> ores=List.of(),traps=List.of();
    private static final List<BlockPos> nextOres=new ArrayList<>(),nextTraps=new ArrayList<>();
    public static boolean active(String family) {
        var player=Minecraft.getInstance().player;
        return player!=null && VersionApi.hasEffect(player,ChaosContent.effect(family));
    }
    public static boolean outlines(Entity entity) {
        var player=Minecraft.getInstance().player;
        return player!=null && active("hunter") && SightTargets.enemy(entity)
            && entity.distanceToSqr(player)<=SightTargets.MOB_RADIUS*SightTargets.MOB_RADIUS;
    }
    private static void clear() {
        origin=null;nearest=null;cursor=0;ores=List.of();traps=List.of();nextOres.clear();nextTraps.clear();
    }
    public static void tick() {
        var mc=Minecraft.getInstance();
        boolean ore=active("ore_sight"),hunter=active("hunter");
        if(level!=mc.level || oreOn!=ore || hunterOn!=hunter) {
            clear();level=mc.level;oreOn=ore;hunterOn=hunter;
        }
        if(level==null || mc.player==null || (!ore && !hunter))return;
        BlockPos center=mc.player.blockPosition();
        if(origin!=null && origin.distSqr(center)>64)clear(); // Teleport / rapid movement.
        if(origin==null || cursor==side*side*side) {
            origin=center;radius=(hunter?SightTargets.TRAP_RADIUS:SightTargets.ORE_RADIUS)+1;
            side=radius*2+1;cursor=0;nextOres.clear();nextTraps.clear();
        }
        Vec3 viewer=mc.player.position();
        var pos=new BlockPos.MutableBlockPos();
        int end=Math.min(side*side*side,cursor+SCAN_BUDGET);
        for(;cursor<end;cursor++) {
            int dx=cursor%side-radius,dz=(cursor/side)%side-radius,dy=cursor/(side*side)-radius;
            if(dx*dx+dy*dy+dz*dz>radius*radius)continue;
            pos.set(origin.getX()+dx,origin.getY()+dy,origin.getZ()+dz);
            if(!level.isInWorldBounds(pos) || !level.hasChunkAt(pos))continue;
            var state=level.getBlockState(pos);
            double distance=pos.getCenter().distanceToSqr(viewer);
            if(ore && distance<=SightTargets.ORE_RADIUS*SightTargets.ORE_RADIUS && SightTargets.ore(state))nextOres.add(pos.immutable());
            if(hunter && distance<=SightTargets.TRAP_RADIUS*SightTargets.TRAP_RADIUS && SightTargets.trap(state))nextTraps.add(pos.immutable());
        }
        if(cursor==side*side*side) {ores=List.copyOf(nextOres);traps=List.copyOf(nextTraps);}
        // Re-evaluate the closest cached ore each tick; mining it reveals the next one.
        nearest=null;double best=SightTargets.ORE_RADIUS*SightTargets.ORE_RADIUS;
        if(ore)for(var candidate:ores) {
            double distance=candidate.getCenter().distanceToSqr(viewer);
            if(distance<=best && level.hasChunkAt(candidate) && SightTargets.ore(level.getBlockState(candidate))) {
                best=distance;nearest=candidate;
            }
        }
    }
    public static BlockPos nearestOre() {return active("ore_sight")?nearest:null;}
    public static List<BlockPos> traps() {return active("hunter")?traps:List.of();}
    private SightCache() {}
}
