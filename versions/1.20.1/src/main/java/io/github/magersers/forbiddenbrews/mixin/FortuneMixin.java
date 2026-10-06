package io.github.magersers.forbiddenbrews.mixin;

import io.github.magersers.forbiddenbrews.VersionApi;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Use a temporary tool copy for block loot; never alter the player's tool. */
@Mixin(Block.class)
public abstract class FortuneMixin {
    @ModifyVariable(method="getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
        at=@At("HEAD"),argsOnly=true,ordinal=0)
    private static ItemStack forbiddenBrews$fortune(ItemStack tool,BlockState state,ServerLevel level,BlockPos pos,
        BlockEntity blockEntity,Entity actor,ItemStack original) {
        return VersionApi.fortuneTool(tool,actor);
    }
}
