package io.github.magersers.forbiddenbrews.mixin;

import io.github.magersers.forbiddenbrews.*;
import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.world.entity.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class TruceMobMixin implements TruceMemory {
    @Unique private final Map<UUID,Long> brews$provoked=new HashMap<>();
    @Unique private long brews$broken;
    public Map<UUID,Long> brews$provocations() {return brews$provoked;}
    public long brews$brokenUntil() {return brews$broken;}
    public void brews$brokenUntil(long time) {brews$broken=time;}
    @Inject(method="setTarget",at=@At("HEAD"),cancellable=true)
    private void brews$target(LivingEntity target,CallbackInfo ci) {
        if(Truce.suppresses((Mob)(Object)this,target))ci.cancel();
    }
    @Inject(method="serverAiStep",at={@At("HEAD"),@At("TAIL")})
    private void brews$calm(CallbackInfo ci) {Truce.tick((Mob)(Object)this);}
    @Inject(method="addAdditionalSaveData",at=@At("TAIL"))
    private void brews$save(CompoundTag tag,CallbackInfo ci) {
        if(brews$broken==0 && brews$provoked.isEmpty())return;
        var state=new CompoundTag();state.putLong("BrokenUntil",brews$broken);var entries=new ListTag();
        brews$provoked.forEach((id,until)-> {var e=new CompoundTag();e.putUUID("Id",id);e.putLong("Until",until);entries.add(e);});
        state.put("Provokers",entries);tag.put("ForbiddenBrewsTruce",state);
    }
    @Inject(method="readAdditionalSaveData",at=@At("TAIL"))
    private void brews$load(CompoundTag tag,CallbackInfo ci) {
        brews$provoked.clear();var state=tag.getCompound("ForbiddenBrewsTruce");brews$broken=state.getLong("BrokenUntil");
        var entries=state.getList("Provokers",Tag.TAG_COMPOUND);
        for(int i=0;i<entries.size();i++) {var e=entries.getCompound(i);if(e.hasUUID("Id"))brews$provoked.put(e.getUUID("Id"),e.getLong("Until"));}
    }
}
