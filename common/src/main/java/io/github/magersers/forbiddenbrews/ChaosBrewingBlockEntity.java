package io.github.magersers.forbiddenbrews;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Keeps the vanilla brewing inventory/menu, fuel, automation and save format. */
public final class ChaosBrewingBlockEntity extends BrewingStandBlockEntity {
    public ChaosBrewingBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ChaosContent.standType.get();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.forbidden_brews.chaos_brewing_stand");
    }
}
