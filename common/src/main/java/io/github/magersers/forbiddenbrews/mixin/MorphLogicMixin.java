package io.github.magersers.forbiddenbrews.mixin;

import io.github.magersers.forbiddenbrews.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(LivingEntity.class)
public abstract class MorphLogicMixin implements MorphState {
    @Unique private final MorphState.Data brews$movement=new MorphState.Data();
    public MorphState.Data brews$data() {return brews$movement;}
    @Inject(method="tick",at=@At("HEAD"))
    private void brews$effects(CallbackInfo ci) {Morphs.tick((LivingEntity)(Object)this);}
    @Inject(method="addAdditionalSaveData",at=@At("TAIL"))
    private void brews$save(CompoundTag tag,CallbackInfo ci) {brews$movement.save(tag);tag.putBoolean("ForbiddenBrewsGravityUp",brews$gravityUp());}
    @Inject(method="readAdditionalSaveData",at=@At("TAIL"))
    private void brews$load(CompoundTag tag,CallbackInfo ci) {brews$movement.load(tag);brews$gravityUp(tag.getBoolean("ForbiddenBrewsGravityUp"));}
    @Inject(method="causeFallDamage",at=@At("HEAD"),cancellable=true)
    private void brews$landing(float distance,float scale,DamageSource source,CallbackInfoReturnable<Boolean> cir) {
        if(brews$movement.safeLanding || Morphs.bat(Morphs.form((LivingEntity)(Object)this)))cir.setReturnValue(false);
    }
}
