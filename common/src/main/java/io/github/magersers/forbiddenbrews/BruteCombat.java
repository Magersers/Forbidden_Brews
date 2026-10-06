package io.github.magersers.forbiddenbrews;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;

/** One shockwave per successful, charged melee hit. Native hurt handles armor and protection. */
public final class BruteCombat {
    public static final double RADIUS=3;
    public static void impact(Player player,Entity target,float damage,float charge) {
        if(!(player.level() instanceof ServerLevel level) || Morphs.form(player)!=Morphs.BRUTE
            || charge<.9F || !(target instanceof Mob primary))return;
        var center=primary.position();
        for(var mob:level.getEntitiesOfClass(Mob.class,primary.getBoundingBox().inflate(RADIUS))) {
            if(!mob.isAlive() || mob.isSpectator() || mob.isAlliedTo(player) || player.isAlliedTo(mob)
                || mob instanceof TamableAnimal pet && player.getUUID().equals(pet.getOwnerUUID())
                || mob.position().distanceToSqr(center)>RADIUS*RADIUS || !player.hasLineOfSight(mob))continue;
            if(mob!=primary && !mob.hurt(player.damageSources().playerAttack(player),damage*.6F))continue;
            mob.knockback(1.5,player.getX()-mob.getX(),player.getZ()-mob.getZ());
            mob.hurtMarked=true;
        }
        level.sendParticles(ParticleTypes.SWEEP_ATTACK,primary.getX(),primary.getY()+primary.getBbHeight()*.5,primary.getZ(),1,0,0,0,0);
        level.playSound(null,primary.blockPosition(),SoundEvents.IRON_GOLEM_ATTACK,SoundSource.PLAYERS,1,.7F);
    }
    private BruteCombat() {}
}
