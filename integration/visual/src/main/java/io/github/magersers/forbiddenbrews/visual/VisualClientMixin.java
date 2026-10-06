package io.github.magersers.forbiddenbrews.visual;

import io.github.magersers.forbiddenbrews.client.ChaosScreen;
import io.github.magersers.forbiddenbrews.*;
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
    @Unique private boolean brews$connected,brews$world,brews$baseShot,brews$upgradeShot;
    @Unique private int brews$frame;
    @Unique private long brews$last=-1;
    @Unique private int brews$diagnostic;
    @Unique private int brews$screenTicks;
    @Inject(method="tick",at=@At("TAIL"))
    private void brews$connect(CallbackInfo ci) {
        Minecraft mc=(Minecraft)(Object)this;
        if(!brews$connected && mc.screen instanceof TitleScreen) {
            brews$connected=true;
            ConnectScreen.startConnecting(mc.screen,mc,ServerAddress.parseString("127.0.0.1:25584"),new ServerData("Forbidden Brews visual test","127.0.0.1:25584",false),false);
        }
        if(mc.screen instanceof ChaosScreen screen) {
            brews$screenTicks++;
            int x=(screen.width-256)/2,y=(screen.height-256)/2;
            if(brews$screenTicks==5 || brews$screenTicks==50)screen.mouseClicked(x+128,y+136,0);
            if(brews$screenTicks==25)screen.mouseClicked(x+182,y+168,0);
            if(brews$screenTicks==65)screen.mouseClicked(x+128,y+76,0);
            if(brews$screenTicks==35 && !ChaosRecipes.ALL.get(screen.getMenu().selectedRecipe()).result().equals(new BrewSpec("hot_pick",1,false)))throw new IllegalStateException("Hot Pick picker choice was not synchronized");
            if(brews$screenTicks==95 && !ChaosRecipes.ALL.get(screen.getMenu().selectedRecipe()).result().equals(new BrewSpec("hot_pick",1,true)))throw new IllegalStateException("Splash picker choice was not synchronized");
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
            ((MouseCoordinates)mc.mouseHandler).brews$setX(5);
            ((MouseCoordinates)mc.mouseHandler).brews$setY(5);
            brews$last=mc.level.getGameTime();name=String.format("chaos-ui-%03d.png",brews$frame++);
            if((brews$screenTicks>=10 && brews$screenTicks<25 && !brews$baseShot) ||
                (brews$screenTicks>=55 && brews$screenTicks<65 && !brews$upgradeShot)) {
                boolean base=brews$screenTicks<25;
                try(var shot=Screenshot.takeScreenshot(mc.getMainRenderTarget())) {
                    shot.writeToFile(Path.of(mc.gameDirectory.getAbsolutePath(),base?"chaos-picker-base.png":"chaos-picker-upgrades.png"));
                }
                if(base)brews$baseShot=true;else brews$upgradeShot=true;
            }
        }
        if(name!=null) {
            try(var screenshot=Screenshot.takeScreenshot(mc.getMainRenderTarget())) { screenshot.writeToFile(Path.of(mc.gameDirectory.getAbsolutePath(),name)); }
            System.out.println("FORBIDDEN_BREWS_CAPTURE "+name);
        }
        if(brews$frame>=220)mc.stop();
    }
}
