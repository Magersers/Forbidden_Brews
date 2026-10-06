package io.github.magersers.forbiddenbrews.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.magersers.forbiddenbrews.client.MorphRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class MorphRenderMixin {
    @Inject(method="render",at=@At("HEAD"),cancellable=true)
    private void brews$render(Entity entity,double x,double y,double z,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light,CallbackInfo ci) {
        if(MorphRenderer.render(entity,x,y,z,yaw,partial,pose,buffers,light))ci.cancel();
    }
}
