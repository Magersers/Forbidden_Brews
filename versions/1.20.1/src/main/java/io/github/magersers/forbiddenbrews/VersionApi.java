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
        MobEffect effect=s.family().equals("fortune")?MobEffects.LUCK:s.family().equals("looting")?ChaosContent.lootingEffect.get():ChaosContent.homewardEffect.get();
        return new MobEffectInstance(effect,s.duration(),s.level()-1);
    }
    public static ItemStack populate(ItemStack stack,BrewSpec spec) {
        stack.removeTagKey("CustomPotionEffects");PotionUtils.setPotion(stack,Potions.EMPTY);
        PotionUtils.setCustomEffects(stack,List.of(effect(spec)));return stack;
    }
    public static ItemStack water() { return PotionUtils.setPotion(new ItemStack(Items.POTION),Potions.WATER); }
    public static boolean isWater(ItemStack stack) { return stack.is(Items.POTION) && PotionUtils.getPotion(stack)==Potions.WATER; }
    public static MobEffectInstance looting(LivingEntity entity) { return entity.getEffect(ChaosContent.lootingEffect.get()); }
    public static void home(ServerPlayer player) {
        var world=player.getServer().getLevel(player.getRespawnDimension());
        var block=player.getRespawnPosition();Vec3 location=null;
        if(world!=null && block!=null)location=Player.findRespawnPositionAndUseSpawnBlock(world,block,player.getRespawnAngle(),player.isRespawnForced(),true).orElse(null);
        if(location==null) { world=player.getServer().overworld();location=Vec3.atBottomCenterOf(world.getSharedSpawnPos()); }
        HomeSafety.teleport(player,world,location,player.getRespawnAngle());
    }
    private VersionApi() {}
}
