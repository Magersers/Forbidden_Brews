package io.github.magersers.forbiddenbrews.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.magersers.forbiddenbrews.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** The world and hand each use a fresh pose; GUI rendering stays upright. */
@Mixin(GameRenderer.class)
public abstract class InversionMixin {
    @Inject(method="bobHurt",at=@At("HEAD"))
    private void brews$invert(PoseStack pose,float partial,CallbackInfo ci) {
        if(Minecraft.getInstance().getCameraEntity() instanceof LivingEntity camera && VersionApi.hasEffect(camera,ChaosContent.inversionEffect.get()))
            pose.mulPose(Axis.ZP.rotationDegrees(180));
    }
}
