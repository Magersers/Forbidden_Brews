package io.github.magersers.forbiddenbrews;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;

/** A bounded extra spawn wave; daylight is allowed, terrain and world rules are not bypassed. */
public final class Swarm {
    public static final int LIMIT=16;
    public static boolean summon(LivingEntity host) {
        if(!(host.level() instanceof ServerLevel level) || !host.isAlive() ||
            level.getDifficulty()==Difficulty.PEACEFUL || !level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING))return false;
        if(level.getEntitiesOfClass(Mob.class,host.getBoundingBox().inflate(32),mob->mob instanceof Enemy).size()>=LIMIT)return false;
        List<EntityType<? extends Mob>> types=level.dimension()==Level.NETHER?
            List.of(EntityType.BLAZE,EntityType.MAGMA_CUBE,EntityType.WITHER_SKELETON):level.dimension()==Level.END?
            List.of(EntityType.ENDERMAN):List.of(EntityType.CREEPER,EntityType.SPIDER,EntityType.HUSK);
        var random=host.getRandom();
        for(int attempt=0;attempt<12;attempt++) {
            double angle=random.nextDouble()*Math.PI*2,radius=12+random.nextInt(13);
            int x=(int)Math.floor(host.getX()+Math.cos(angle)*radius),z=(int)Math.floor(host.getZ()+Math.sin(angle)*radius);
            if(level.getChunkSource().getChunkNow(x>>4,z>>4)==null)continue;
            var type=types.get(random.nextInt(types.size()));
            for(int dy=8;dy>=-8;dy--) {
                var feet=new BlockPos(x,host.blockPosition().getY()+dy,z);
                if(!safeFloor(level,feet))continue;
                var mob=type.create(level);if(mob==null)continue;
                mob.moveTo(x+.5,feet.getY(),z+.5,random.nextFloat()*360,0);
                if(!level.noCollision(mob) || level.containsAnyLiquid(mob.getBoundingBox())) {mob.discard();continue;}
                VersionApi.prepareSwarmMob(mob,level);
                if(!level.noCollision(mob) || level.containsAnyLiquid(mob.getBoundingBox())) {mob.discard();continue;}
                // Vanilla mobs remain subject to normal AI and despawning.
                if(level.addFreshEntity(mob)) {
                    level.sendParticles(ParticleTypes.SMOKE,mob.getX(),mob.getY()+.6,mob.getZ(),10,.4,.5,.4,.01);
                    return true;
                }
            }
        }
        return false;
    }
    private static boolean safeFloor(ServerLevel level,BlockPos feet) {
        if(feet.getY()<level.getMinBuildHeight()+1 || feet.getY()+3>=level.getMaxBuildHeight() || !level.getWorldBorder().isWithinBounds(feet) || !level.isPositionEntityTicking(feet))return false;
        var floor=feet.below();var state=level.getBlockState(floor);
        if(!state.isFaceSturdy(level,floor,net.minecraft.core.Direction.UP) || !state.getFluidState().isEmpty())return false;
        if(state.is(Blocks.MAGMA_BLOCK)||state.is(Blocks.CACTUS)||state.is(Blocks.CAMPFIRE)||state.is(Blocks.SOUL_CAMPFIRE))return false;
        for(int y=0;y<3;y++) {
            var cell=level.getBlockState(feet.above(y));
            if(!cell.getFluidState().isEmpty() || cell.is(Blocks.FIRE)||cell.is(Blocks.SOUL_FIRE)||cell.is(Blocks.POWDER_SNOW)||cell.is(Blocks.WITHER_ROSE)||cell.is(Blocks.SWEET_BERRY_BUSH))return false;
        }
        return true;
    }
    private Swarm() {}
}
