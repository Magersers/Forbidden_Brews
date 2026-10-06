package io.github.magersers.forbiddenbrews.mixin;

import io.github.magersers.forbiddenbrews.Morphs;
import net.minecraft.world.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MorphDimensionsMixin {
    @Inject(method="getDimensions",at=@At("RETURN"),cancellable=true)
    private void brews$size(Pose pose,CallbackInfoReturnable<EntityDimensions> cir) {
        var dims=Morphs.dimensions(Morphs.form((LivingEntity)(Object)this));
        if(dims!=null && pose!=Pose.SLEEPING)cir.setReturnValue(dims);
    }
}
