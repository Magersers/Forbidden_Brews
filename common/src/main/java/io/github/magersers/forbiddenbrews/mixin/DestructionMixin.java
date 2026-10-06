package io.github.magersers.forbiddenbrews.mixin;

import io.github.magersers.forbiddenbrews.Destruction;
import io.github.magersers.forbiddenbrews.Morphs;
import net.minecraft.server.level.*;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class DestructionMixin {
    @Shadow @Final protected ServerPlayer player;
    @Inject(method="destroyBlock",at=@At("HEAD"),cancellable=true)
    private void brews$hardnessLimit(BlockPos pos,CallbackInfoReturnable<Boolean> cir) {
        if(Morphs.form(player)==Morphs.BRUTE && !Destruction.canBreak(player,pos))cir.setReturnValue(false);
    }
    @Inject(method="destroyBlock",at=@At("RETURN"))
    private void brews$smash(BlockPos pos,CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValueZ())Destruction.smash(player,pos);
    }
}
