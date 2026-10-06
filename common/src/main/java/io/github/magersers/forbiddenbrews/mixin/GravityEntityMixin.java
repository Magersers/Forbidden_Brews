package io.github.magersers.forbiddenbrews.mixin;

import io.github.magersers.forbiddenbrews.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class GravityEntityMixin {
    @Inject(method="isNoGravity",at=@At("RETURN"),cancellable=true)
    private void brews$reverseGravity(CallbackInfoReturnable<Boolean> cir) {
        if((Object)this instanceof Player player && ((MorphState)player).brews$data()!=null
            && ((MorphState)player).brews$gravityUp() && Gravity.controls(player))cir.setReturnValue(true);
    }
}
