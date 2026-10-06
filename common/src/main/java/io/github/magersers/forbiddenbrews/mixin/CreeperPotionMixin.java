package io.github.magersers.forbiddenbrews.mixin;

import io.github.magersers.forbiddenbrews.CreeperBlast;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownPotion.class)
public abstract class CreeperPotionMixin {
    @Inject(method="onHit",at=@At("HEAD"),cancellable=true)
    private void brews$detonate(HitResult hit,CallbackInfo ci) {
        if(CreeperBlast.splash((ThrownPotion)(Object)this))ci.cancel();
    }
}
