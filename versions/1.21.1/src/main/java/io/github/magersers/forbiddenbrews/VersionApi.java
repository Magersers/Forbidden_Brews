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
        var holder=BuiltInRegistries.MOB_EFFECT.wrapAsHolder(ChaosContent.effect(s.family()));
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
    public static boolean hasEffect(LivingEntity entity,MobEffect effect) { return entity.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect)); }
    public static boolean silkTouch(ItemStack tool,ServerLevel level) {
        var silk=level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
            .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH);
        return net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(silk,tool)>0;
    }
    public static ItemStack smelt(ItemStack drop,ServerLevel level) {
        var input=new net.minecraft.world.item.crafting.SingleRecipeInput(drop.copy());
        return level.getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.SMELTING,input,level)
            .map(r->r.value().assemble(input,level.registryAccess())).orElse(ItemStack.EMPTY);
    }
    public static void prepareChunk(ServerLevel level,net.minecraft.core.BlockPos target,Runnable ready,Runnable failed) {
        // Calling getChunkFuture on the server thread invokes managedBlock.
        // Its off-thread branch schedules the request safely without that wait.
        java.util.concurrent.CompletableFuture.supplyAsync(()->level.getChunkSource().getChunkFuture(target.getX()>>4,target.getZ()>>4,net.minecraft.world.level.chunk.status.ChunkStatus.FULL,true))
            .thenCompose(future->future).whenCompleteAsync((result,error)-> {
                if(error==null && level.getChunkSource().getChunkNow(target.getX()>>4,target.getZ()>>4)!=null)ready.run();else failed.run();
            },level.getServer());
    }
    public static MobEffectInstance effectInstance(LivingEntity entity,String family) {return entity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(ChaosContent.effect(family)));}
    public static void prepareSwarmMob(net.minecraft.world.entity.Mob mob,ServerLevel level) {
        net.minecraft.world.entity.SpawnGroupData group=mob instanceof net.minecraft.world.entity.monster.Zombie?
            new net.minecraft.world.entity.monster.Zombie.ZombieGroupData(false,false):null;
        mob.finalizeSpawn(level,level.getCurrentDifficultyAt(mob.blockPosition()),net.minecraft.world.entity.MobSpawnType.MOB_SUMMONED,group);
        // Keep each potion wave to exactly one mob, including potential jockeys.
        mob.getPassengers().forEach(net.minecraft.world.entity.Entity::discard);mob.ejectPassengers();mob.stopRiding();
    }
    public static net.minecraft.world.entity.EntityDimensions bruteDimensions() {return net.minecraft.world.entity.EntityDimensions.scalable(1.4F,2.9F).withEyeHeight(2.5F);}
    private VersionApi() {}
}
