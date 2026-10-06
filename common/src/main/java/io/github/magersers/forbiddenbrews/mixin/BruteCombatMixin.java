package io.github.magersers.forbiddenbrews.mixin;

import io.github.magersers.forbiddenbrews.BruteCombat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class BruteCombatMixin {
    @Unique private float brews$attackCharge;
    @Inject(method="attack",at=@At("HEAD"))
    private void brews$captureCharge(Entity target,CallbackInfo ci) {
        brews$attackCharge=((Player)(Object)this).getAttackStrengthScale(.5F);
    }
    @Redirect(method="attack",at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean brews$impact(Entity target,DamageSource source,float damage) {
        boolean hit=target.hurt(source,damage);
        if(hit)BruteCombat.impact((Player)(Object)this,target,damage,brews$attackCharge);
        return hit;
    }
}
