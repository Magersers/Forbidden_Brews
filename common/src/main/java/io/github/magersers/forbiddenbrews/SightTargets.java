package io.github.magersers.forbiddenbrews;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Shared classification, without loading any client classes on a server. */
public final class SightTargets {
    public static final int ORE_RADIUS=32, TRAP_RADIUS=24, MOB_RADIUS=32;
    public static boolean ore(BlockState state) {return state.is(OreDrops.ORES);}
    public static boolean trap(BlockState state) {
        return state.is(BlockTags.BUTTONS) || state.is(BlockTags.PRESSURE_PLATES)
            || state.is(Blocks.TRIPWIRE) || state.is(Blocks.TRIPWIRE_HOOK);
    }
    public static boolean enemy(Entity entity) {return entity instanceof Enemy && entity.isAlive();}
    private SightTargets() {}
}
