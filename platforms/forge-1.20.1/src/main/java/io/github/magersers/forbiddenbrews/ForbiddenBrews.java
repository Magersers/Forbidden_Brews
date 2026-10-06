package io.github.magersers.forbiddenbrews;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.world.item.CreativeModeTabs;

@Mod(ChaosContent.MOD_ID)
public final class ForbiddenBrews {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ChaosContent.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ChaosContent.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ChaosContent.MOD_ID);
    public static final RegistryObject<ChaosStandBlock> STAND = BLOCKS.register("chaos_brewing_stand", () -> new ChaosStandBlock(ChaosContent.standProperties()));
    public static final RegistryObject<Item> STAND_ITEM = ITEMS.register("chaos_brewing_stand", () -> new BlockItem(STAND.get(), new Item.Properties().fireResistant()));
    public static final RegistryObject<BlockEntityType<ChaosBrewingBlockEntity>> STAND_TYPE = ENTITIES.register("chaos_brewing_stand",
        () -> BlockEntityType.Builder.of(ChaosBrewingBlockEntity::new, STAND.get()).build(null));

    public ForbiddenBrews() {
        ChaosContent.standType = STAND_TYPE;
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus);
        bus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) event.accept(STAND_ITEM);
        });
    }
}
