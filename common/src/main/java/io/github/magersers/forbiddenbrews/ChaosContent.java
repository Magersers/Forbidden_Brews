package io.github.magersers.forbiddenbrews;

import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;

public final class ChaosContent {
    public static final String MOD_ID = "forbidden_brews";
    public static Supplier<BlockEntityType<ChaosBrewingBlockEntity>> standType;

    private ChaosContent() {}

    public static BlockBehaviour.Properties standProperties() {
        return BlockBehaviour.Properties.of().strength(4.0F, 1200.0F)
            .sound(SoundType.NETHERITE_BLOCK).noOcclusion().lightLevel(state -> 7);
    }
}
