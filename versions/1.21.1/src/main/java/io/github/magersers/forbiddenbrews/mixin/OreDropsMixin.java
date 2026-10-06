package io.github.magersers.forbiddenbrews.mixin;

import java.util.List;
import io.github.magersers.forbiddenbrews.OreDrops;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class OreDropsMixin {
    @Inject(method="getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",at=@At("RETURN"),cancellable=true)
    private static void brews$oreDrops(BlockState state,ServerLevel level,BlockPos pos,BlockEntity blockEntity,Entity actor,ItemStack tool,CallbackInfoReturnable<List<ItemStack>> ci) {
        ci.setReturnValue(OreDrops.transform(ci.getReturnValue(),state,level,actor,tool));
    }
}
