package io.github.magersers.forbiddenbrews.mixin;
import io.github.magersers.forbiddenbrews.BrewEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(EnchantmentHelper.class)
public abstract class LootingMixin {
    @Inject(method="getMobLooting",at=@At("RETURN"),cancellable=true)
    private static void forbiddenBrews$looting(LivingEntity entity,CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValue()+BrewEffects.lootingBonus(entity));
    }
}
