package io.github.magersers.forbiddenbrews.mixin;
import io.github.magersers.forbiddenbrews.Truce;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Multipart dragon hits do not pass through LivingEntity.hurt. */
@Mixin(EnderDragon.class)
public abstract class TruceDragonMixin {
    @Inject(method="canAttack",at=@At("HEAD"),cancellable=true)
    private void brews$attack(LivingEntity target,CallbackInfoReturnable<Boolean> cir) {
        if(Truce.suppresses((EnderDragon)(Object)this,target))cir.setReturnValue(false);
    }
    @Inject(method="hurt(Lnet/minecraft/world/entity/boss/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z",at=@At("RETURN"))
    private void brews$retaliate(EnderDragonPart part,DamageSource source,float amount,CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValueZ())Truce.hurt((EnderDragon)(Object)this,source);
    }
}
