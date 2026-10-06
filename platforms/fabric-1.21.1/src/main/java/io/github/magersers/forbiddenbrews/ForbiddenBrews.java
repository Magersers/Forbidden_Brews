package io.github.magersers.forbiddenbrews;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ForbiddenBrews implements ModInitializer {
    @Override
    public void onInitialize() {
        var id = ResourceLocation.fromNamespaceAndPath(ChaosContent.MOD_ID, "chaos_brewing_stand");
        var block = Registry.register(BuiltInRegistries.BLOCK, id, new ChaosStandBlock(ChaosContent.standProperties()));
        var item = Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, new Item.Properties().fireResistant()));
        var type = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id,
            BlockEntityType.Builder.of(ChaosBrewingBlockEntity::new, block).build(null));
        ChaosContent.standType = () -> type;
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
            .register(entries -> entries.accept(item));
    }
}
