package io.github.magersers.forbiddenbrews;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
public final class ForbiddenBrews implements ModInitializer {
    public static ChaosStandBlock STAND;
    public static NetheriteWartBlock WART;
    @Override public void onInitialize() {
        STAND=Registry.register(BuiltInRegistries.BLOCK,VersionApi.id("chaos_brewing_stand"),new ChaosStandBlock(ChaosContent.standProperties()));
        WART=Registry.register(BuiltInRegistries.BLOCK,VersionApi.id("netherite_wart"),new NetheriteWartBlock());
        var item=Registry.register(BuiltInRegistries.ITEM,VersionApi.id("chaos_brewing_stand"),new BlockItem(STAND,new Item.Properties().fireResistant()));
        var wart=Registry.register(BuiltInRegistries.ITEM,VersionApi.id("netherite_wart"),new BlockItem(WART,new Item.Properties().fireResistant()));
        var type=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,VersionApi.id("chaos_brewing_stand"),BlockEntityType.Builder.of(ChaosBrewingBlockEntity::new,STAND).build(null));
        var menu=Registry.register(BuiltInRegistries.MENU,VersionApi.id("chaos_stand"),new MenuType<>(ChaosMenu::new,FeatureFlags.VANILLA_SET));
        var fortune=Registry.register(BuiltInRegistries.MOB_EFFECT,VersionApi.id("fortune"),new BrewEffects.Fortune());
        var looting=Registry.register(BuiltInRegistries.MOB_EFFECT,VersionApi.id("looting"),new BrewEffects.Looting());
        var home=Registry.register(BuiltInRegistries.MOB_EFFECT,VersionApi.id("homeward"),new BrewEffects.Homeward());
        ChaosContent.standType=()->type;ChaosContent.menuType=()->menu;ChaosContent.wartItem=()->wart;
        ChaosContent.fortuneEffect=()->fortune;ChaosContent.lootingEffect=()->looting;ChaosContent.homewardEffect=()->home;
        for(var spec:BrewSpec.ALL) {
            var brew=Registry.register(BuiltInRegistries.ITEM,VersionApi.id(spec.id()),new BrewItem(spec));
            ChaosContent.brewItems.put(spec.id(),()->brew);
        }
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(e->e.accept(item));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(e->e.accept(wart));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(e->ChaosContent.brewItems.values().forEach(i->e.accept(i.get().getDefaultInstance())));
    }
}
