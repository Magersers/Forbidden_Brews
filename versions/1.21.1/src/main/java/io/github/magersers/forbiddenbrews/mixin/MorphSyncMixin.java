package io.github.magersers.forbiddenbrews.mixin;

import io.github.magersers.forbiddenbrews.MorphState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.syncher.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MorphSyncMixin implements MorphState {
    @Unique private static final EntityDataAccessor<Integer> brews$FORM=SynchedEntityData.defineId(LivingEntity.class,EntityDataSerializers.INT);
    @Unique private static final EntityDataAccessor<Boolean> brews$UP=SynchedEntityData.defineId(LivingEntity.class,EntityDataSerializers.BOOLEAN);
    @Inject(method="defineSynchedData",at=@At("TAIL"))
    private void brews$define(SynchedEntityData.Builder data,CallbackInfo ci) {data.define(brews$FORM,0);data.define(brews$UP,false);}
    public int brews$form() {return ((LivingEntity)(Object)this).getEntityData().get(brews$FORM);}
    public void brews$form(int form) {((LivingEntity)(Object)this).getEntityData().set(brews$FORM,form);}
    public boolean brews$gravityUp() {return ((LivingEntity)(Object)this).getEntityData().get(brews$UP);}
    public void brews$gravityUp(boolean up) {((LivingEntity)(Object)this).getEntityData().set(brews$UP,up);}
}
