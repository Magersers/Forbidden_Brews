package io.github.magersers.forbiddenbrews;

import net.minecraft.world.entity.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.memory.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.server.level.ServerLevel;

public final class Truce {
    public static boolean suppresses(Mob mob,LivingEntity target) {
        if(target==null || mob.level().isClientSide)return false;
        var memory=(TruceMemory)mob;long now=mob.level().getGameTime();
        memory.brews$provocations().values().removeIf(expiry->expiry<=now);
        if(VersionApi.hasEffect(target,ChaosContent.truceEffect.get()) &&
            !memory.brews$provocations().containsKey(target.getUUID()))return true;
        return VersionApi.hasEffect(mob,ChaosContent.truceEffect.get()) && memory.brews$brokenUntil()<=now;
    }
    public static void hurt(LivingEntity victim,DamageSource source) {
        if(!(victim instanceof Mob mob) || mob.level().isClientSide || !(source.getEntity() instanceof LivingEntity attacker))return;
        var memory=(TruceMemory)mob;long now=mob.level().getGameTime();
        var own=VersionApi.effectInstance(mob,"truce");
        if(own!=null)memory.brews$brokenUntil(now+own.getDuration());
        var protection=VersionApi.effectInstance(attacker,"truce");
        if(protection!=null)memory.brews$provocations().put(attacker.getUUID(),now+protection.getDuration());
    }
    public static void tick(Mob mob) {
        if(!(mob.level() instanceof ServerLevel level))return;
        boolean cleared=false;
        if(suppresses(mob,mob.getTarget())) {mob.setTarget(null);cleared=true;}
        var brain=mob.getBrain();
        if(brain.checkMemory(MemoryModuleType.ATTACK_TARGET,MemoryStatus.REGISTERED)) {
            var target=brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
            if(suppresses(mob,target)) {brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);cleared=true;}
        }
        if(brain.checkMemory(MemoryModuleType.ANGRY_AT,MemoryStatus.REGISTERED)) {
            var id=brain.getMemory(MemoryModuleType.ANGRY_AT).orElse(null);
            if(id!=null && level.getEntity(id) instanceof LivingEntity target && suppresses(mob,target)) {
                brain.eraseMemory(MemoryModuleType.ANGRY_AT);cleared=true;
            }
        }
        if(cleared) {
            mob.getNavigation().stop();mob.setAggressive(false);
            if(brain.checkMemory(MemoryModuleType.WALK_TARGET,MemoryStatus.REGISTERED))brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            if(mob instanceof Creeper creeper && !creeper.isIgnited())creeper.setSwellDir(-1);
        }
    }
    private Truce() {}
}
