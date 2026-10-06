package io.github.magersers.forbiddenbrews.mixin.client;

import io.github.magersers.forbiddenbrews.*;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class GravityInputMixin {
    @Unique private boolean brews$jumpHeld;
    @Inject(method="tick",at=@At("TAIL"))
    private void brews$jump(CallbackInfo ci) {
        var mc=(Minecraft)(Object)this;io.github.magersers.forbiddenbrews.client.MorphRenderer.world(mc.level);
        boolean down=mc.options.keyJump.isDown();
        if(down && !brews$jumpHeld && mc.screen==null && mc.player!=null && Gravity.controls(mc.player))GravityNetwork.sendJump();
        brews$jumpHeld=down;
    }
}
