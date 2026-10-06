package io.github.magersers.forbiddenbrews.client;
import io.github.magersers.forbiddenbrews.*;
import net.minecraft.client.renderer.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
@EventBusSubscriber(modid=ChaosContent.MOD_ID,bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class ClientSetup {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(ForbiddenBrews.MENU.get(),ChaosScreen::new); }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) { event.enqueueWork(()->ItemBlockRenderTypes.setRenderLayer(ForbiddenBrews.WART.get(),RenderType.cutout())); }
}
