package io.github.magersers.forbiddenbrews.mixin;

import io.github.magersers.forbiddenbrews.Morphs;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MorphPlayerDimensionsMixin {
    @Inject(method="getDimensions",at=@At("RETURN"),cancellable=true)
    private void brews$size(Pose pose,CallbackInfoReturnable<EntityDimensions> cir) {
        var dims=Morphs.dimensions(Morphs.form((Player)(Object)this));
        if(dims!=null && pose!=Pose.SLEEPING)cir.setReturnValue(dims);
    }
    @Inject(method="getStandingEyeHeight",at=@At("RETURN"),cancellable=true)
    private void brews$eyes(Pose pose,EntityDimensions dims,CallbackInfoReturnable<Float> cir) {
        int form=Morphs.form((Player)(Object)this);
        if(form!=0)cir.setReturnValue(form==Morphs.BRUTE?2.5F:Morphs.bat(form)?.45F:dims.height*.85F);
    }
}
