package io.github.magersers.forbiddenbrews;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.flag.FeatureFlags;
import java.util.*;
import net.minecraftforge.registries.*;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
@Mod(ChaosContent.MOD_ID)
public final class ForbiddenBrews {
    private static final DeferredRegister<Block> BLOCKS=DeferredRegister.create(Registries.BLOCK,ChaosContent.MOD_ID);
    private static final DeferredRegister<Item> ITEMS=DeferredRegister.create(Registries.ITEM,ChaosContent.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,ChaosContent.MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,ChaosContent.MOD_ID);
    private static final DeferredRegister<MobEffect> EFFECTS=DeferredRegister.create(Registries.MOB_EFFECT,ChaosContent.MOD_ID);
    public static final RegistryObject<ChaosStandBlock> STAND=BLOCKS.register("chaos_brewing_stand",()->new ChaosStandBlock(ChaosContent.standProperties()));
    public static final RegistryObject<NetheriteWartBlock> WART=BLOCKS.register("netherite_wart",NetheriteWartBlock::new);
    public static final RegistryObject<Item> STAND_ITEM=ITEMS.register("chaos_brewing_stand",()->new BlockItem(STAND.get(),new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> WART_ITEM=ITEMS.register("netherite_wart",()->new BlockItem(WART.get(),new Item.Properties().fireResistant()));
    public static final RegistryObject<BlockEntityType<ChaosBrewingBlockEntity>> STAND_TYPE=ENTITIES.register("chaos_brewing_stand",()->BlockEntityType.Builder.of(ChaosBrewingBlockEntity::new,STAND.get()).build(null));
    public static final RegistryObject<MenuType<ChaosMenu>> MENU=MENUS.register("chaos_stand",()->new MenuType<>(ChaosMenu::new,FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MobEffect> FORTUNE=EFFECTS.register("fortune",BrewEffects.Fortune::new);
    public static final RegistryObject<MobEffect> LOOTING=EFFECTS.register("looting",BrewEffects.Looting::new);
    public static final RegistryObject<MobEffect> HOMEWARD=EFFECTS.register("homeward",BrewEffects.Homeward::new);
    public static final RegistryObject<MobEffect> WILD_TELEPORT=EFFECTS.register("wild_teleport",BrewEffects.WildTeleport::new);
    public static final RegistryObject<MobEffect> ORE_DOUBLE=EFFECTS.register("ore_double",BrewEffects.OreDouble::new);
    public static final RegistryObject<MobEffect> HOT_PICK=EFFECTS.register("hot_pick",BrewEffects.HotPick::new);
    public static final RegistryObject<MobEffect> INVERSION=EFFECTS.register("inversion",BrewEffects.Inversion::new);
    public static final RegistryObject<MobEffect> CREEPER=EFFECTS.register("creeper",BrewEffects.Creeper::new);
    public static final RegistryObject<MobEffect> TRUCE=EFFECTS.register("truce",BrewEffects.Truce::new);
    public static final RegistryObject<MobEffect> SWARM=EFFECTS.register("swarm",BrewEffects.Swarm::new);
    public static final RegistryObject<MobEffect> ORE_SIGHT=EFFECTS.register("ore_sight",BrewEffects.OreSight::new);
    public static final RegistryObject<MobEffect> HUNTER=EFFECTS.register("hunter",BrewEffects.Hunter::new);
    public ForbiddenBrews(FMLJavaModLoadingContext context) {
        var bus=context.getModEventBus();
        ChaosContent.standType=STAND_TYPE;ChaosContent.menuType=MENU;ChaosContent.wartItem=WART_ITEM;
        ChaosContent.fortuneEffect=FORTUNE;ChaosContent.lootingEffect=LOOTING;ChaosContent.homewardEffect=HOMEWARD;
        ChaosContent.wildTeleportEffect=WILD_TELEPORT;ChaosContent.oreDoubleEffect=ORE_DOUBLE;ChaosContent.hotPickEffect=HOT_PICK;
        ChaosContent.inversionEffect=INVERSION;ChaosContent.creeperEffect=CREEPER;
        ChaosContent.truceEffect=TRUCE;ChaosContent.swarmEffect=SWARM;
        ChaosContent.oreSightEffect=ORE_SIGHT;ChaosContent.hunterEffect=HUNTER;
        for(var spec:BrewSpec.ALL)ChaosContent.brewItems.put(spec.id(),ITEMS.register(spec.id(),()->new BrewItem(spec)));
        BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);EFFECTS.register(bus);
        bus.addListener((BuildCreativeModeTabContentsEvent event)-> {
            if(event.getTabKey()==CreativeModeTabs.FUNCTIONAL_BLOCKS)event.accept(STAND_ITEM.get());
            if(event.getTabKey()==CreativeModeTabs.INGREDIENTS)event.accept(WART_ITEM.get());
            if(event.getTabKey()==CreativeModeTabs.FOOD_AND_DRINKS)ChaosContent.brewItems.values().forEach(i->event.accept(i.get().getDefaultInstance()));
        });
    }
}
