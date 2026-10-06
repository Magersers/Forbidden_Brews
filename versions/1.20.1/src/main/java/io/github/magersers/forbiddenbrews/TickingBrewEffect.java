package io.github.magersers.forbiddenbrews;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
public abstract class TickingBrewEffect extends MobEffect {
    protected TickingBrewEffect(MobEffectCategory category,int color) {super(category,color);}
    @Override public boolean isDurationEffectTick(int duration,int amplifier) {return duration%80==0;}
    @Override public void applyEffectTick(LivingEntity entity,int amplifier) {Swarm.summon(entity);}
}
