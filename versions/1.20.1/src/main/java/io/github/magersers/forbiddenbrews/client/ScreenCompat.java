package io.github.magersers.forbiddenbrews.client;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
public final class ScreenCompat {
    public static void background(Screen screen,GuiGraphics g,int x,int y,float partial) { screen.renderBackground(g); }
}
