package io.github.magersers.forbiddenbrews;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
public final class ChaosBrewingBlockEntity extends ChaosBrewingCore {
    public ChaosBrewingBlockEntity(BlockPos pos,BlockState state) { super(pos,state); }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag,lookup);
        items=NonNullList.withSize(SIZE,ItemStack.EMPTY);
        if(tag.getInt("ChaosInventoryVersion")<2) {
            var old=NonNullList.withSize(5,ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag,old,lookup);
            migrateOldInventory(old);
        } else ContainerHelper.loadAllItems(tag,items,lookup);
        readState(tag);
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag,lookup);
        ContainerHelper.saveAllItems(tag,items,lookup);writeState(tag);
    }
}
