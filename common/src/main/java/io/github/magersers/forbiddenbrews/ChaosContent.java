package io.github.magersers.forbiddenbrews;

import java.util.function.Supplier;
import java.util.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;

public final class ChaosContent {
    public static final String MOD_ID = "forbidden_brews";
    public static Supplier<BlockEntityType<ChaosBrewingBlockEntity>> standType;
    public static Supplier<MenuType<ChaosMenu>> menuType;
    public static Supplier<Item> wartItem;
    public static Supplier<MobEffect> fortuneEffect, lootingEffect, homewardEffect, wildTeleportEffect, oreDoubleEffect, hotPickEffect, inversionEffect, creeperEffect, truceEffect, swarmEffect;
    public static MobEffect effect(String family) {
        return switch(family) {
            case "fortune" -> fortuneEffect.get();
            case "looting" -> lootingEffect.get();
            case "homeward" -> homewardEffect.get();
            case "wild_teleport" -> wildTeleportEffect.get();
            case "ore_double" -> oreDoubleEffect.get();
            case "hot_pick" -> hotPickEffect.get();
            case "inversion" -> inversionEffect.get();
            case "creeper" -> creeperEffect.get();
            case "truce" -> truceEffect.get();
            case "swarm" -> swarmEffect.get();
            default -> throw new IllegalArgumentException("Unknown brew: "+family);
        };
    }
    public static final Map<String,Supplier<? extends Item>> brewItems = new LinkedHashMap<>();
    public static ItemStack brew(BrewSpec spec) { return brewItems.get(spec.id()).get().getDefaultInstance(); }

    private ChaosContent() {}

    public static BlockBehaviour.Properties standProperties() {
        return BlockBehaviour.Properties.of().strength(4.0F, 1200.0F)
            .sound(SoundType.NETHERITE_BLOCK).noOcclusion().lightLevel(state -> 7);
    }
}
