package io.github.magersers.forbiddenbrews;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.*;
import net.minecraft.stats.Stats;
public final class BrewItem extends PotionItem {
    public final BrewSpec spec;
    public BrewItem(BrewSpec value) { super(new Item.Properties().stacksTo(1));spec=value; }
    @Override public ItemStack getDefaultInstance() { return VersionApi.populate(new ItemStack(this),spec); }
    @Override public Component getName(ItemStack stack) { return Component.translatable(spec.translation()); }
    @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity entity) {
        if(spec.family().equals("wild_teleport") && !spec.splash()) {
            if(entity instanceof net.minecraft.server.level.ServerPlayer player)RandomTeleport.drink(player,stack);
            return stack;
        }
        VersionApi.populate(stack,spec);return super.finishUsingItem(stack,level,entity);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tips, TooltipFlag flag) {
        super.appendHoverText(VersionApi.populate(stack.copy(),spec),context,tips,flag);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand) {
        if(!spec.splash())return super.use(level,player,hand);
        var stack=player.getItemInHand(hand);
        level.playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.SPLASH_POTION_THROW,SoundSource.PLAYERS,.5F,.9F);
        if(!level.isClientSide) {
            var projectile=new ThrownPotion(level,player);
            projectile.setItem(VersionApi.populate(stack.copyWithCount(1),spec));
            projectile.shootFromRotation(player,player.getXRot(),player.getYRot(),-20,.5F,1);
            level.addFreshEntity(projectile);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        if(!player.getAbilities().instabuild)stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
}
