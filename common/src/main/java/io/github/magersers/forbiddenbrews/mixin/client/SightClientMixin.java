package io.github.magersers.forbiddenbrews.mixin.client;

import io.github.magersers.forbiddenbrews.client.SightCache;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(Minecraft.class)
public abstract class SightClientMixin {
    @Inject(method="tick",at=@At("TAIL"))
    private void brews$scan(CallbackInfo ci) {SightCache.tick();}
    @Inject(method="shouldEntityAppearGlowing",at=@At("RETURN"),cancellable=true)
    private void brews$outline(Entity entity,CallbackInfoReturnable<Boolean> cir) {
        if(!cir.getReturnValue() && SightCache.outlines(entity))cir.setReturnValue(true);
    }
}
