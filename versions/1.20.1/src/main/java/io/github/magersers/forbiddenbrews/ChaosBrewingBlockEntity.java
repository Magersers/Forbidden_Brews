package io.github.magersers.forbiddenbrews;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
public final class ChaosBrewingBlockEntity extends ChaosBrewingCore {
    public ChaosBrewingBlockEntity(BlockPos pos,BlockState state) { super(pos,state); }
    @Override public void load(CompoundTag tag) {
        super.load(tag);
        items=NonNullList.withSize(SIZE,ItemStack.EMPTY);
        if(tag.getInt("ChaosInventoryVersion")<2) {
            var old=NonNullList.withSize(5,ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag,old);
            migrateOldInventory(old);
        } else ContainerHelper.loadAllItems(tag,items);
        readState(tag);
    }
    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag,items);writeState(tag);
    }
}
