package io.github.magersers.forbiddenbrews.client;
import io.github.magersers.forbiddenbrews.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.gui.screens.MenuScreens;
public final class ForbiddenBrewsClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        MenuScreens.register(ChaosContent.menuType.get(),ChaosScreen::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ForbiddenBrews.WART,RenderType.cutout());
    }
}
