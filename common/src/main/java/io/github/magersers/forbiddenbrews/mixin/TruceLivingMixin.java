package io.github.magersers.forbiddenbrews.mixin;

import io.github.magersers.forbiddenbrews.Truce;
import net.minecraft.world.entity.*;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class TruceLivingMixin {
    @Inject(method="canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z",at=@At("HEAD"),cancellable=true)
    private void brews$attack(LivingEntity target,CallbackInfoReturnable<Boolean> cir) {
        if((Object)this instanceof Mob mob && Truce.suppresses(mob,target))cir.setReturnValue(false);
    }
    @Inject(method="hurt",at=@At("HEAD"),cancellable=true)
    private void brews$preventAttack(DamageSource source,float amount,CallbackInfoReturnable<Boolean> cir) {
        if(source.getEntity() instanceof Mob mob && Truce.suppresses(mob,(LivingEntity)(Object)this))cir.setReturnValue(false);
    }
    @Inject(method="hurt",at=@At("RETURN"))
    private void brews$retaliate(DamageSource source,float amount,CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValueZ())Truce.hurt((LivingEntity)(Object)this,source);
    }
}
