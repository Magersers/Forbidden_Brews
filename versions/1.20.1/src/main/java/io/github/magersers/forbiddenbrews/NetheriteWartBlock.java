package io.github.magersers.forbiddenbrews;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
public final class NetheriteWartBlock extends NetherWartBlock {
    public NetheriteWartBlock() { super(Properties.of().mapColor(MapColor.COLOR_PURPLE).noCollission().randomTicks().instabreak().sound(SoundType.NETHER_WART)); }
    @Override public ItemStack getCloneItemStack(BlockGetter level,BlockPos pos,BlockState state) { return new ItemStack(ChaosContent.wartItem.get()); }
}
