package io.github.magersers.forbiddenbrews;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
public final class VersionApi {
    public static ResourceLocation id(String path) { return new ResourceLocation(ChaosContent.MOD_ID,path); }
    public static MobEffectInstance effect(BrewSpec s) {
        MobEffect effect=ChaosContent.effect(s.family());
        return new MobEffectInstance(effect,s.duration(),s.level()-1);
    }
    public static ItemStack populate(ItemStack stack,BrewSpec spec) {
        stack.removeTagKey("CustomPotionEffects");PotionUtils.setPotion(stack,Potions.EMPTY);
        PotionUtils.setCustomEffects(stack,List.of(effect(spec)));return stack;
    }
    public static ItemStack water() { return PotionUtils.setPotion(new ItemStack(Items.POTION),Potions.WATER); }
    public static boolean isWater(ItemStack stack) { return stack.is(Items.POTION) && PotionUtils.getPotion(stack)==Potions.WATER; }
    public static MobEffectInstance looting(LivingEntity entity) { return entity.getEffect(ChaosContent.lootingEffect.get()); }
    public static MobEffectInstance fortune(LivingEntity entity) { return entity.getEffect(ChaosContent.fortuneEffect.get()); }
    public static ItemStack fortuneTool(ItemStack tool,net.minecraft.world.entity.Entity actor) {
        if(!(actor instanceof LivingEntity miner) || tool.isEmpty())return tool;
        int bonus=BrewEffects.fortuneBonus(miner);if(bonus==0)return tool;
        var copy=tool.copy();var levels=net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantments(copy);
        levels.merge(net.minecraft.world.item.enchantment.Enchantments.BLOCK_FORTUNE,bonus,Integer::sum);
        net.minecraft.world.item.enchantment.EnchantmentHelper.setEnchantments(levels,copy);return copy;
    }
    public static void home(ServerPlayer player) {
        var world=player.getServer().getLevel(player.getRespawnDimension());
        var block=player.getRespawnPosition();Vec3 location=null;
        if(world!=null && block!=null)location=Player.findRespawnPositionAndUseSpawnBlock(world,block,player.getRespawnAngle(),player.isRespawnForced(),true).orElse(null);
        if(location==null) { world=player.getServer().overworld();location=Vec3.atBottomCenterOf(world.getSharedSpawnPos()); }
        HomeSafety.teleport(player,world,location,player.getRespawnAngle());
    }
    public static boolean hasEffect(LivingEntity entity,MobEffect effect) { return entity.hasEffect(effect); }
    public static boolean silkTouch(ItemStack tool,ServerLevel level) {
        return net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH,tool)>0;
    }
    public static ItemStack smelt(ItemStack drop,ServerLevel level) {
        var input=new net.minecraft.world.SimpleContainer(drop.copy());
        return level.getRecipeManager().getRecipeFor(net.minecraft.world.item.crafting.RecipeType.SMELTING,input,level)
            .map(r->r.assemble(input,level.registryAccess())).orElse(ItemStack.EMPTY);
    }
    public static void prepareChunk(ServerLevel level,net.minecraft.core.BlockPos target,Runnable ready,Runnable failed) {
        // Calling getChunkFuture on the server thread invokes managedBlock.
        // Its off-thread branch schedules the request safely without that wait.
        java.util.concurrent.CompletableFuture.supplyAsync(()->level.getChunkSource().getChunkFuture(target.getX()>>4,target.getZ()>>4,net.minecraft.world.level.chunk.ChunkStatus.FULL,true))
            .thenCompose(future->future).whenCompleteAsync((result,error)-> {
                if(error==null && level.getChunkSource().getChunkNow(target.getX()>>4,target.getZ()>>4)!=null)ready.run();else failed.run();
            },level.getServer());
    }
    private VersionApi() {}
}
