package io.github.magersers.forbiddenbrews.mixin;

import io.github.magersers.forbiddenbrews.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(Player.class)
public abstract class MorphPlayerMixin {
    @Inject(method="jumpFromGround",at=@At("HEAD"),cancellable=true)
    private void brews$jump(CallbackInfo ci) {if(Gravity.controls((Player)(Object)this))ci.cancel();}
    @Inject(method="causeFallDamage",at=@At("HEAD"),cancellable=true)
    private void brews$landing(float distance,float scale,DamageSource source,CallbackInfoReturnable<Boolean> cir) {
        Player player=(Player)(Object)this;
        if(((MorphState)player).brews$data().safeLanding || Morphs.bat(Morphs.form(player)) || Gravity.controls(player))cir.setReturnValue(false);
    }
}
