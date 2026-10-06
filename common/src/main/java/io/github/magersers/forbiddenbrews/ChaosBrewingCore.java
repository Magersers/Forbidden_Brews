package io.github.magersers.forbiddenbrews;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.*;

/** Eight server-owned slots. Progress resets if recipe inputs change. */
public abstract class ChaosBrewingCore extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int SIZE=8;
    protected NonNullList<ItemStack> items=NonNullList.withSize(SIZE,ItemStack.EMPTY);
    protected int progress, fuel, recipeIndex=-1;
    private static final int[] TOP={1,2,3,4,5}, SIDE={0,6}, BOTTOM={7};
    protected final ContainerData data=new ContainerData() {
        public int get(int i) { return switch(i) {
            case 0 -> progress; case 1 -> recipeIndex<0?0:ChaosRecipes.ALL.get(recipeIndex).ticks();
            case 2 -> fuel; case 3 -> recipeIndex+1; default -> 0; }; }
        public void set(int i,int value) { switch(i) { case 0 -> progress=value; case 2 -> fuel=value; case 3 -> recipeIndex=value-1; } }
        public int getCount() { return 4; }
    };
    protected ChaosBrewingCore(BlockPos pos,BlockState state) { super(ChaosContent.standType.get(),pos,state); }
    protected NonNullList<ItemStack> getItems() { return items; }
    protected void setItems(NonNullList<ItemStack> value) { items=value; }
    @Override protected Component getDefaultName() { return Component.translatable("block.forbidden_brews.chaos_brewing_stand"); }
    @Override protected AbstractContainerMenu createMenu(int id,Inventory inv) { return new ChaosMenu(id,inv,this,data); }
    @Override public int getContainerSize() { return SIZE; }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int i) { return items.get(i); }
    @Override public ItemStack removeItem(int i,int count) { var result=ContainerHelper.removeItem(items,i,count);if(!result.isEmpty())setChanged();return result; }
    @Override public ItemStack removeItemNoUpdate(int i) { return ContainerHelper.takeItem(items,i); }
    @Override public void setItem(int i,ItemStack stack) { items.set(i,stack);stack.setCount(Math.min(stack.getCount(),stack.getMaxStackSize()));setChanged(); }
    @Override public boolean stillValid(Player player) { return level!=null && level.getBlockEntity(worldPosition)==this && player.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5)<=64; }
    @Override public void clearContent() { items.clear(); setChanged(); }
    @Override public boolean canPlaceItem(int i,ItemStack stack) {
        if(i==7)return false;
        if(i==0)return VersionApi.isWater(stack) || stack.getItem() instanceof BrewItem;
        if(i==1)return stack.is(ChaosContent.wartItem.get());
        if(i==6)return stack.is(Items.BLAZE_POWDER);
        return ChaosRecipes.ALL.stream().anyMatch(r->r.components().stream().anyMatch(c->stack.is(c.item())));
    }
    @Override public int[] getSlotsForFace(Direction direction) { return direction==Direction.DOWN?BOTTOM:direction==Direction.UP?TOP:SIDE; }
    @Override public boolean canPlaceItemThroughFace(int i,ItemStack stack,Direction face) { return canPlaceItem(i,stack); }
    @Override public boolean canTakeItemThroughFace(int i,ItemStack stack,Direction face) { return i==7; }
    public static void serverTick(Level level,BlockPos pos,BlockState state,ChaosBrewingCore be) {
        var recipe=ChaosRecipes.find(be);
        if(recipe==null || !be.getItem(7).isEmpty()) {
            if(be.progress!=0 || be.recipeIndex!=-1) { be.progress=0;be.recipeIndex=-1;be.setChanged(); }
            return;
        }
        int index=ChaosRecipes.ALL.indexOf(recipe);
        if(be.recipeIndex!=index) { be.progress=0;be.recipeIndex=index; }
        if(be.fuel==0 && be.getItem(6).is(Items.BLAZE_POWDER)) { be.removeItem(6,1);be.fuel=20; }
        if(be.fuel==0)return;
        be.progress++;
        if(be.progress>=recipe.ticks()) {
            recipe.consume(be);be.items.set(7,recipe.output());be.fuel--;be.progress=0;be.recipeIndex=-1;
            level.playSound(null,pos,SoundEvents.BREWING_STAND_BREW,SoundSource.BLOCKS,.8F,1.05F);
        }
        be.setChanged();
    }
    protected void readState(CompoundTag tag) {
        fuel=Math.max(0,Math.min(20,tag.getInt("Fuel")));
        progress=Math.max(0,tag.getInt("ChaosProgress"));
        recipeIndex=-1;
        String id=tag.getString("ChaosRecipe");
        for(int i=0;i<ChaosRecipes.ALL.size();i++) if(ChaosRecipes.ALL.get(i).id().equals(id))recipeIndex=i;
        if(recipeIndex<0 || progress>=ChaosRecipes.ALL.get(recipeIndex).ticks())progress=0;
    }
    protected void writeState(CompoundTag tag) {
        tag.putInt("ChaosInventoryVersion",2);tag.putInt("Fuel",fuel);tag.putInt("ChaosProgress",progress);
        if(recipeIndex>=0)tag.putString("ChaosRecipe",ChaosRecipes.ALL.get(recipeIndex).id());
    }
    /** Preserve every item when upgrading the previous five-slot vanilla stand. */
    protected void migrateOldInventory(NonNullList<ItemStack> old) {
        items=NonNullList.withSize(SIZE,ItemStack.EMPTY);
        items.set(0,old.get(0));items.set(6,old.get(4));items.set(7,old.get(1));
        items.set(2,old.get(2));items.set(3,old.get(3));
        progress=0;recipeIndex=-1;
    }
}
