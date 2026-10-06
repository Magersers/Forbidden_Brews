package io.github.magersers.forbiddenbrews;

import java.util.*;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Post-process actual ore loot, after vanilla loot tables and Fortune. */
public final class OreDrops {
    public static final TagKey<Block> ORES=TagKey.create(Registries.BLOCK,VersionApi.id("ores"));
    public static List<ItemStack> transform(List<ItemStack> drops,BlockState state,ServerLevel level,Entity actor,ItemStack tool) {
        if(!(actor instanceof LivingEntity miner) || !state.is(ORES) || VersionApi.silkTouch(tool,level))return drops;
        boolean twice=VersionApi.hasEffect(miner,ChaosContent.oreDoubleEffect.get());
        boolean smelt=VersionApi.hasEffect(miner,ChaosContent.hotPickEffect.get());
        if(!twice && !smelt)return drops;
        var result=new ArrayList<ItemStack>();
        for(var drop:drops) {
            var output=smelt?VersionApi.smelt(drop,level):ItemStack.EMPTY;
            var template=output.isEmpty()?drop:output;
            // Whole ore blocks (notably ancient debris) can be placed again.
            // Double only resources, or their smelted output, to avoid a loop.
            boolean wholeOre=template.getItem() instanceof BlockItem blockItem && blockItem.getBlock().defaultBlockState().is(ORES);
            int count=drop.getCount()*(twice && !wholeOre?2:1)*(output.isEmpty()?1:output.getCount());
            while(count>0) {
                var stack=template.copy();int size=Math.min(count,stack.getMaxStackSize());
                stack.setCount(size);result.add(stack);count-=size;
            }
        }
        return result;
    }
    private OreDrops() {}
}
