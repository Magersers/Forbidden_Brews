package io.github.magersers.forbiddenbrews.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.magersers.forbiddenbrews.client.SightRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.*;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class SightWorldMixin {
    @Inject(method="renderLevel",at=@At("TAIL"))
    private void brews$draw(PoseStack pose,float partial,long endTime,boolean outline,Camera camera,
        GameRenderer gameRenderer,LightTexture light,Matrix4f projection,CallbackInfo ci) {
        SightRenderer.render(pose,camera.getPosition());
    }
}
