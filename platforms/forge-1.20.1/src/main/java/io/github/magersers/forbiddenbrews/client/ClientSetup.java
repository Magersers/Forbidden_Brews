package io.github.magersers.forbiddenbrews.client;
import io.github.magersers.forbiddenbrews.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
@Mod.EventBusSubscriber(modid=ChaosContent.MOD_ID,bus=Mod.EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class ClientSetup {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) { event.enqueueWork(()-> {
        MenuScreens.register(ForbiddenBrews.MENU.get(),ChaosScreen::new);
        ItemBlockRenderTypes.setRenderLayer(ForbiddenBrews.WART.get(),RenderType.cutout());
    }); }
}
