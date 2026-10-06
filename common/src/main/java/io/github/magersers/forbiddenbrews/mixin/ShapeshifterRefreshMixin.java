package io.github.magersers.forbiddenbrews.mixin;

import io.github.magersers.forbiddenbrews.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ShapeshifterRefreshMixin {
    @Inject(method="addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",at=@At("HEAD"))
    private void brews$refresh(MobEffectInstance effect,Entity source,CallbackInfoReturnable<Boolean> ci) {
        var entity=(LivingEntity)(Object)this;
        if(!entity.level().isClientSide && VersionApi.shapeshifter(effect) && VersionApi.effectInstance(entity,"shapeshifter")!=null)
            ((MorphState)entity).brews$data().reroll=true;
    }
}
