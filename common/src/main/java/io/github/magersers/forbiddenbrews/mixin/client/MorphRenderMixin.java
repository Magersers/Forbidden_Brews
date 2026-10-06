package io.github.magersers.forbiddenbrews.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.magersers.forbiddenbrews.Gravity;
import io.github.magersers.forbiddenbrews.client.MorphRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class MorphRenderMixin {
    @Unique private boolean brews$gravityRendering;
    @Inject(method="render",at=@At("HEAD"),cancellable=true)
    private void brews$render(Entity entity,double x,double y,double z,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light,CallbackInfo ci) {
        if(!brews$gravityRendering && entity instanceof LivingEntity living && Gravity.inverted(living)) {
            pose.pushPose();brews$gravityRendering=true;
            try {
                pose.translate(x,y+entity.getBbHeight(),z);pose.mulPose(Axis.ZP.rotationDegrees(180));pose.translate(-x,-y,-z);
                ((EntityRenderDispatcher)(Object)this).render(entity,x,y,z,yaw,partial,pose,buffers,light);
            } finally {brews$gravityRendering=false;pose.popPose();}
            ci.cancel();return;
        }
        if(MorphRenderer.render(entity,x,y,z,yaw,partial,pose,buffers,light))ci.cancel();
    }
}
