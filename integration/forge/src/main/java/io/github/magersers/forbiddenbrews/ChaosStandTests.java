package io.github.magersers.forbiddenbrews;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.alchemy.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraftforge.gametest.*;

@GameTestHolder(ChaosContent.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ChaosStandTests {
    @GameTest(template="empty", timeoutTicks=40)
    public static void craftRequiresNetherite(GameTestHelper h) {
        var player=h.makeMockPlayer();
        var grid=new TransientCraftingContainer(new CraftingMenu(0, player.getInventory()),3,3);
        Item[] items={Items.AMETHYST_SHARD,Items.BREWING_STAND,Items.AMETHYST_SHARD,
            Items.AIR,Items.NETHERITE_INGOT,Items.AIR,Items.OBSIDIAN,Items.BLAZE_ROD,Items.OBSIDIAN};
        for (int i=0;i<9;i++) grid.setItem(i,new ItemStack(items[i]));
        var recipe=h.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING,grid,h.getLevel());
        h.assertTrue(recipe.isPresent(),"Recipe must match with netherite");
        h.assertTrue(recipe.get().assemble(grid,h.getLevel().registryAccess()).is(ForbiddenBrews.STAND_ITEM.get()),"Recipe must produce chaos stand");
        grid.setItem(4,ItemStack.EMPTY);
        h.assertFalse(recipe.get().matches(grid,h.getLevel()),"Netherite cannot be omitted");
        grid.setItem(4,new ItemStack(Items.IRON_INGOT));
        h.assertFalse(recipe.get().matches(grid,h.getLevel()),"Iron cannot replace netherite");
        h.succeed();
    }

    @GameTest(template="empty", timeoutTicks=40)
    public static void inventorySurvivesSerialization(GameTestHelper h) {
        var rel=new BlockPos(1,1,1);
        h.setBlock(rel,ForbiddenBrews.STAND.get());
        var be=(ChaosBrewingBlockEntity)h.getBlockEntity(rel);
        h.assertTrue(be.getType()==ForbiddenBrews.STAND_TYPE.get(),"Must use custom registered block entity type");
        be.setItem(3,new ItemStack(Items.NETHER_WART,5));
        be.setItem(4,new ItemStack(Items.BLAZE_POWDER,3));
        var tag=be.saveWithFullMetadata();
        h.assertTrue(tag.getString("id").equals("forbidden_brews:chaos_brewing_stand"),"Save id must be custom type");
        var restored=BlockEntity.loadStatic(be.getBlockPos(),be.getBlockState(),tag);
        h.assertTrue(restored instanceof ChaosBrewingBlockEntity,"Reload must retain custom block entity");
        h.assertTrue(((ChaosBrewingBlockEntity)restored).getItem(3).getCount()==5,"Ingredients must survive reload");
        h.assertTrue(((ChaosBrewingBlockEntity)restored).getItem(4).getCount()==3,"Fuel must survive reload");
        var player=h.makeMockPlayer();
        player.setPos(be.getBlockPos().getX()+.5,be.getBlockPos().getY()+.5,be.getBlockPos().getZ()+.5);
        var menu=be.createMenu(0,player.getInventory(),player);
        h.assertTrue(menu instanceof BrewingStandMenu && menu.stillValid(player),"Vanilla brewing menu must remain open");
        h.succeed();
    }

    @GameTest(template="empty", timeoutTicks=40)
    public static void registeredTickerBrewsThreeBottles(GameTestHelper h) {
        var rel=new BlockPos(1,1,1);h.setBlock(rel,ForbiddenBrews.STAND.get());
        var pos=h.absolutePos(rel);var level=h.getLevel();
        var be=(ChaosBrewingBlockEntity)h.getBlockEntity(rel);
        for (int i=0;i<3;i++)be.setItem(i,PotionUtils.setPotion(new ItemStack(Items.POTION),Potions.WATER));
        be.setItem(3,new ItemStack(Items.NETHER_WART));be.setItem(4,new ItemStack(Items.BLAZE_POWDER));
        var ticker=ForbiddenBrews.STAND.get().getTicker(level,be.getBlockState(),ForbiddenBrews.STAND_TYPE.get());
        h.assertTrue(ticker!=null,"Custom block must have a server ticker");
        for (int i=0;i<402;i++)ticker.tick(level,pos,level.getBlockState(pos),be);
        for (int i=0;i<3;i++)h.assertTrue(PotionUtils.getPotion(be.getItem(i))==Potions.AWKWARD,"Each bottle must brew awkward potion");
        h.assertTrue(be.getItem(3).isEmpty(),"Brewing must consume ingredient");
        h.assertTrue(be.getItem(4).isEmpty(),"Brewing must consume one powder and retain internal fuel");
        h.succeed();
    }
}
