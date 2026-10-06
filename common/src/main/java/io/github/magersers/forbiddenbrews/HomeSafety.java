package io.github.magersers.forbiddenbrews;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.*;

public final class HomeSafety {
    private static boolean safe(ServerLevel level,ServerPlayer player,Vec3 p) {
        // Vanilla forced respawns sit 0.1 blocks above the floor. Account for
        // this clearance while still requiring a supporting collision surface.
        BlockPos feet=BlockPos.containing(p),floor=BlockPos.containing(p.x,p.y-.2,p.z);
        var ground=level.getBlockState(floor);
        if(!level.getWorldBorder().isWithinBounds(feet) || p.y<level.getMinBuildHeight() || p.y+2>=level.getMaxBuildHeight())return false;
        if(ground.is(Blocks.MAGMA_BLOCK)||ground.is(Blocks.CACTUS)||ground.is(Blocks.CAMPFIRE)||ground.is(Blocks.SOUL_CAMPFIRE))return false;
        var support=ground.getCollisionShape(level,floor);
        if(support.isEmpty() || floor.getY()+support.max(net.minecraft.core.Direction.Axis.Y)<p.y-.2)return false;
        for(BlockPos cell:new BlockPos[]{feet,feet.above()}) {
            var state=level.getBlockState(cell);
            if(!state.getFluidState().isEmpty()||state.is(Blocks.FIRE)||state.is(Blocks.SOUL_FIRE)||state.is(Blocks.POWDER_SNOW))return false;
        }
        return level.noCollision(player,new AABB(p.x-.3,p.y,p.z-.3,p.x+.3,p.y+1.8,p.z+.3));
    }
    public static void teleport(ServerPlayer player,ServerLevel desired,Vec3 location,float yaw) {
        if(desired==null || !safe(desired,player,location)) {
            desired=player.getServer().overworld();
            BlockPos spawn=desired.getSharedSpawnPos();location=null;
            outer:for(int radius=0;radius<=16;radius++)for(int dx=-radius;dx<=radius;dx++)for(int dz=-radius;dz<=radius;dz++) {
                if(radius>0 && Math.max(Math.abs(dx),Math.abs(dz))!=radius)continue;
                int x=spawn.getX()+dx,z=spawn.getZ()+dz;
                Vec3 candidate=new Vec3(x+.5,desired.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,x,z),z+.5);
                if(safe(desired,player,candidate)){location=candidate;break outer;}
            }
            if(location==null) { player.displayClientMessage(Component.translatable("message.forbidden_brews.home_unsafe"),true);return; }
        }
        player.stopRiding();player.teleportTo(desired,location.x,location.y,location.z,yaw,0);
        player.setDeltaMovement(Vec3.ZERO);player.fallDistance=0;
    }
    private HomeSafety() {}
}
