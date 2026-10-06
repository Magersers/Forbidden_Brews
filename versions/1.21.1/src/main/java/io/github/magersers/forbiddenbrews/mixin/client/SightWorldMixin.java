package io.github.magersers.forbiddenbrews.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.magersers.forbiddenbrews.client.SightRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.*;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class SightWorldMixin {
    @Inject(method="renderLevel",at=@At("TAIL"))
    private void brews$draw(DeltaTracker delta,boolean outline,Camera camera,GameRenderer gameRenderer,
        LightTexture light,Matrix4f view,Matrix4f projection,CallbackInfo ci) {
        var pose=new PoseStack();pose.mulPose(view);
        SightRenderer.render(pose,camera.getPosition());
    }
}
