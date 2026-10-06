package io.github.magersers.forbiddenbrews;

import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;

public final class ChaosStandBlock extends BrewingStandBlock {
    private static final VoxelShape SHAPE = Shapes.or(
        box(0, 0, 0, 16, 4, 16), box(6, 4, 6, 10, 23, 10),
        box(1, 9, 4, 5, 16, 8), box(11, 9, 4, 15, 16, 8),
        box(6, 9, 11, 10, 16, 15));

    public ChaosStandBlock(Properties properties) { super(properties); }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChaosBrewingBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != ChaosContent.standType.get()) return null;
        return (world, pos, blockState, entity) ->
            ChaosBrewingCore.serverTick(world, pos, blockState, (ChaosBrewingBlockEntity) entity);
    }
    @Override public net.minecraft.world.InteractionResult use(BlockState state,Level world,BlockPos pos,Player player,net.minecraft.world.InteractionHand hand,BlockHitResult hit) {
        if(world.isClientSide)return InteractionResult.SUCCESS;
        if(world.getBlockEntity(pos) instanceof ChaosBrewingCore core)player.openMenu(core);
        return InteractionResult.CONSUME;
    }
    @Override public void onRemove(BlockState state,Level world,BlockPos pos,BlockState next,boolean moving) {
        if(state.getBlock()!=next.getBlock() && world.getBlockEntity(pos) instanceof ChaosBrewingCore core) {
            Containers.dropContents(world,pos,core);world.updateNeighbourForOutputSignal(pos,this);
        }
        super.onRemove(state,world,pos,next,moving);
    }
}
