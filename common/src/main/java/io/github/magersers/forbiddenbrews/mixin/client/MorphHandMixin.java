package io.github.magersers.forbiddenbrews.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.magersers.forbiddenbrews.Morphs;
import io.github.magersers.forbiddenbrews.client.BruteRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class MorphHandMixin {
    @Inject(method="renderRightHand",at=@At("HEAD"),cancellable=true)
    private void brews$right(PoseStack pose,MultiBufferSource buffers,int light,AbstractClientPlayer player,CallbackInfo ci) {
        int form=Morphs.form(player);if(form==0)return;
        if(form==Morphs.BRUTE)BruteRenderer.hand(false,pose,buffers,light);ci.cancel();
    }
    @Inject(method="renderLeftHand",at=@At("HEAD"),cancellable=true)
    private void brews$left(PoseStack pose,MultiBufferSource buffers,int light,AbstractClientPlayer player,CallbackInfo ci) {
        int form=Morphs.form(player);if(form==0)return;
        if(form==Morphs.BRUTE)BruteRenderer.hand(true,pose,buffers,light);ci.cancel();
    }
}
