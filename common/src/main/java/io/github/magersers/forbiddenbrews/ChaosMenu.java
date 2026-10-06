package io.github.magersers.forbiddenbrews;

import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;

public final class ChaosMenu extends AbstractContainerMenu {
    public static final int[][] POS={{120,70},{120,32},{34,52},{34,88},{206,52},{206,88},{206,128},{120,128}};
    private final Container inventory;
    private final ContainerData data;
    public ChaosMenu(int id,Inventory player) { this(id,player,new SimpleContainer(8),new SimpleContainerData(5)); }
    public ChaosMenu(int id,Inventory player,Container inv,ContainerData values) {
        super(ChaosContent.menuType.get(),id);checkContainerSize(inv,8);checkContainerDataCount(values,5);
        inventory=inv;data=values;inv.startOpen(player.player);
        for(int i=0;i<8;i++) {
            final int index=i;
            addSlot(new Slot(inv,i,POS[i][0],POS[i][1]) {
                @Override public boolean mayPlace(ItemStack stack) {
                    if(index==7)return false;
                    if(inv instanceof ChaosBrewingCore core)return core.canPlaceItem(index,stack);
                    if(index==0)return VersionApi.isWater(stack)||stack.getItem() instanceof BrewItem;
                    if(index==1)return stack.is(ChaosContent.wartItem.get());
                    if(index==6)return stack.is(Items.BLAZE_POWDER);
                    return true;
                }
                @Override public int getMaxStackSize() { return index==0||index==7?1:64; }
            });
        }
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(player,col+row*9+9,48+col*18,170+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(player,col,48+col*18,228));
        addDataSlots(values);
    }
    public int progress() { return data.get(0); }
    public int total() { return data.get(1); }
    public int fuel() { return data.get(2); }
    public int activeRecipe() { return data.get(3)-1; }
    public int selectedRecipe() { return data.get(4)-1; }
    @Override public boolean clickMenuButton(Player player,int id) {
        return stillValid(player) && inventory instanceof ChaosBrewingCore core && core.selectRecipe(id);
    }
    @Override public boolean stillValid(Player player) { return inventory.stillValid(player); }
    @Override public void removed(Player player) { super.removed(player);inventory.stopOpen(player); }
    @Override public ItemStack quickMoveStack(Player player,int index) {
        Slot slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;
        ItemStack stack=slot.getItem(), copy=stack.copy();
        if(index<8) { if(!moveItemStackTo(stack,8,44,true))return ItemStack.EMPTY; }
        else {
            int dest=stack.is(ChaosContent.wartItem.get())?1:stack.is(Items.BLAZE_POWDER)?6:
                VersionApi.isWater(stack)||stack.getItem() instanceof BrewItem?0:2;
            if(!moveItemStackTo(stack,dest,dest==2?6:dest+1,false))return ItemStack.EMPTY;
        }
        if(stack.isEmpty())slot.set(ItemStack.EMPTY);else slot.setChanged();
        if(stack.getCount()==copy.getCount())return ItemStack.EMPTY;
        slot.onTake(player,stack);return copy;
    }
}
