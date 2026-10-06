package io.github.magersers.forbiddenbrews.mixin;
import io.github.magersers.forbiddenbrews.BrewEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
@Mixin(EnchantmentHelper.class)
public abstract class LootingMixin {
    @Inject(method="getEnchantmentLevel",at=@At("RETURN"),cancellable=true)
    private static void forbiddenBrews$looting(Holder<Enchantment> enchantment, LivingEntity entity,CallbackInfoReturnable<Integer> cir) {
        if(enchantment.is(Enchantments.LOOTING)) cir.setReturnValue(cir.getReturnValue()+BrewEffects.lootingBonus(entity));
    }
}
