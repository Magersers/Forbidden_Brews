package io.github.magersers.forbiddenbrews.mixin;

import io.github.magersers.forbiddenbrews.Gravity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class GravityConnectionMixin {
    @Shadow public ServerPlayer player;
    @Shadow private boolean clientIsFloating;
    @Shadow private int aboveGroundTickCount;
    @Inject(method="tick",at=@At("HEAD"))
    private void brews$allowPotionFlight(CallbackInfo ci) {
        if(Gravity.controls(player)) {clientIsFloating=false;aboveGroundTickCount=0;}
    }
}
