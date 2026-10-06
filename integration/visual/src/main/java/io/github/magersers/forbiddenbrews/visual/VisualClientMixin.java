package io.github.magersers.forbiddenbrews.visual;

import io.github.magersers.forbiddenbrews.client.ChaosScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.nio.file.Path;

@Mixin(Minecraft.class)
public abstract class VisualClientMixin {
    @Unique private boolean brews$connected,brews$world;
    @Unique private int brews$frame;
    @Unique private long brews$last=-1;
    @Unique private int brews$diagnostic;
    @Inject(method="tick",at=@At("TAIL"))
    private void brews$connect(CallbackInfo ci) {
        Minecraft mc=(Minecraft)(Object)this;
        if(!brews$connected && mc.screen instanceof TitleScreen) {
            brews$connected=true;
            ConnectScreen.startConnecting(mc.screen,mc,ServerAddress.parseString("127.0.0.1:25584"),new ServerData("Forbidden Brews visual test","127.0.0.1:25584",false),false);
        }
    }
    @Inject(method="runTick",at=@At("TAIL"))
    private void brews$capture(boolean render,CallbackInfo ci) throws Exception {
        Minecraft mc=(Minecraft)(Object)this;
        if(!render)return;
        if(mc.player==null || mc.level==null) {
            if(++brews$diagnostic%120==0) {
                System.out.println("VISUAL_SCREEN "+mc.screen);
                try(var screenshot=Screenshot.takeScreenshot(mc.getMainRenderTarget())) { screenshot.writeToFile(Path.of(mc.gameDirectory.getAbsolutePath(),"visual-loading.png")); }
            }
            return;
        }
        String name=null;
        if(!brews$world && mc.screen==null && mc.player.tickCount>30) { name="chaos-world.png";brews$world=true; }
        if(mc.screen instanceof ChaosScreen && mc.level.getGameTime()>=brews$last+2) {
            mc.getToasts().clear();
            org.lwjgl.glfw.GLFW.glfwSetCursorPos(mc.getWindow().getWindow(),5,5);
            brews$last=mc.level.getGameTime();name=String.format("chaos-ui-%03d.png",brews$frame++);
        }
        if(name!=null) {
            try(var screenshot=Screenshot.takeScreenshot(mc.getMainRenderTarget())) { screenshot.writeToFile(Path.of(mc.gameDirectory.getAbsolutePath(),name)); }
            System.out.println("FORBIDDEN_BREWS_CAPTURE "+name);
        }
        if(brews$frame>=160)mc.stop();
    }
}
