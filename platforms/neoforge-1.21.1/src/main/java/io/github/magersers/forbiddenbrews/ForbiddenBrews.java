package io.github.magersers.forbiddenbrews;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.world.item.CreativeModeTabs;

@Mod(ChaosContent.MOD_ID)
public final class ForbiddenBrews {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, ChaosContent.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, ChaosContent.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ChaosContent.MOD_ID);
    public static final DeferredHolder<Block, ChaosStandBlock> STAND = BLOCKS.register("chaos_brewing_stand", () -> new ChaosStandBlock(ChaosContent.standProperties()));
    public static final DeferredHolder<Item, BlockItem> STAND_ITEM = ITEMS.register("chaos_brewing_stand", () -> new BlockItem(STAND.get(), new Item.Properties().fireResistant()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChaosBrewingBlockEntity>> STAND_TYPE = ENTITIES.register("chaos_brewing_stand",
        () -> BlockEntityType.Builder.of(ChaosBrewingBlockEntity::new, STAND.get()).build(null));

    public ForbiddenBrews(IEventBus bus) {
        ChaosContent.standType = STAND_TYPE;
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus);
        bus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) event.accept(STAND_ITEM.get());
        });
    }
}
