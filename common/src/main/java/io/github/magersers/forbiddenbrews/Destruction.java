package io.github.magersers.forbiddenbrews;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public final class Destruction {
    private static final Set<UUID> ACTIVE=new HashSet<>();
    public static boolean canBreak(ServerPlayer player,BlockPos pos) {
        var level=player.serverLevel();var state=level.getBlockState(pos);
        float hardness=state.getDestroySpeed(level,pos);
        return !state.is(net.minecraft.world.level.block.Blocks.BEDROCK) && hardness>=0
            && hardness<=net.minecraft.world.level.block.Blocks.OBSIDIAN.defaultBlockState().getDestroySpeed(level,pos);
    }
    /** A single four-by-four face, selected by the dominant viewing axis. */
    public static List<BlockPos> plane(BlockPos center,net.minecraft.world.phys.Vec3 look) {
        var result=new ArrayList<BlockPos>(16);
        double x=Math.abs(look.x),y=Math.abs(look.y),z=Math.abs(look.z);
        for(int a=-1;a<=2;a++)for(int b=-1;b<=2;b++)
            result.add(y>=x && y>=z?center.offset(a,0,b):x>=z?center.offset(0,a,b):center.offset(a,b,0));
        return result;
    }
    public static void smash(ServerPlayer player,BlockPos center) {
        if(Morphs.form(player)!=Morphs.BRUTE || ACTIVE.contains(player.getUUID()))return;
        var data=((MorphState)player).brews$data();long now=player.level().getGameTime();
        if(data.lastSmash!=Long.MIN_VALUE && now-data.lastSmash<8)return;
        data.lastSmash=now;ACTIVE.add(player.getUUID());
        try {
            for(var pos:plane(center,player.getLookAngle())) {
                if(pos.equals(center) || !player.serverLevel().hasChunkAt(pos) || !player.serverLevel().getWorldBorder().isWithinBounds(pos)
                    || pos.getCenter().distanceToSqr(player.getEyePosition())>64)continue;
                var block=player.serverLevel().getBlockState(pos);
                if(block.isAir() || !canBreak(player,pos))continue;
                player.gameMode.destroyBlock(pos); // Native drops, tool durability, protection and loader break events.
            }
        } finally {ACTIVE.remove(player.getUUID());}
    }
    private Destruction() {}
}
