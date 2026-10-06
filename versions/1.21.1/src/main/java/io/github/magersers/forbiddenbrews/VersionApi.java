package io.github.magersers.forbiddenbrews;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.portal.DimensionTransition;
public final class VersionApi {
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(ChaosContent.MOD_ID,path); }
    public static MobEffectInstance effect(BrewSpec s) {
        var holder=BuiltInRegistries.MOB_EFFECT.wrapAsHolder(s.family().equals("fortune")?ChaosContent.fortuneEffect.get():
            s.family().equals("looting")?ChaosContent.lootingEffect.get():ChaosContent.homewardEffect.get());
        return new MobEffectInstance(holder,s.duration(),s.level()-1);
    }
    public static ItemStack populate(ItemStack stack,BrewSpec spec) {
        stack.set(DataComponents.POTION_CONTENTS,new PotionContents(Optional.empty(),Optional.empty(),List.of(effect(spec))));return stack;
    }
    public static ItemStack water() { return PotionContents.createItemStack(Items.POTION,Potions.WATER); }
    public static boolean isWater(ItemStack stack) { return stack.is(Items.POTION) && stack.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY).is(Potions.WATER); }
    public static MobEffectInstance looting(LivingEntity entity) { return entity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(ChaosContent.lootingEffect.get())); }
    public static MobEffectInstance fortune(LivingEntity entity) { return entity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(ChaosContent.fortuneEffect.get())); }
    public static ItemStack fortuneTool(ItemStack tool,net.minecraft.world.entity.Entity actor) {
        if(!(actor instanceof LivingEntity miner) || tool.isEmpty())return tool;
        int bonus=BrewEffects.fortuneBonus(miner);if(bonus==0)return tool;
        var enchantment=actor.level().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
            .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.FORTUNE);
        var copy=tool.copy();copy.enchant(enchantment,net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(enchantment,tool)+bonus);
        return copy;
    }
    public static void home(ServerPlayer player) {
        // true preserves respawn-anchor charges; this is travel, not a respawn.
        var target=player.findRespawnPositionAndUseSpawnBlock(true,DimensionTransition.DO_NOTHING);
        HomeSafety.teleport(player,target.newLevel(),target.pos(),target.yRot());
    }
    private VersionApi() {}
}
