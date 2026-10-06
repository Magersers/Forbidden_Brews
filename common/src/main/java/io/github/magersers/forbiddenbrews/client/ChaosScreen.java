package io.github.magersers.forbiddenbrews.client;

import io.github.magersers.forbiddenbrews.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;

/** Pixel copper/obsidian UI. Recipe hints are client-only; server validates inputs. */
public final class ChaosScreen extends AbstractContainerScreen<ChaosMenu> {
    private int selected;
    public ChaosScreen(ChaosMenu menu,Inventory inv,Component title) {
        super(menu,inv,title);imageWidth=256;imageHeight=256;
    }
    @Override protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.literal("<"),b->selected=Math.floorMod(selected-1,ChaosRecipes.ALL.size())).bounds(leftPos+204,topPos+8,18,16).build());
        addRenderableWidget(Button.builder(Component.literal(">"),b->selected=(selected+1)%ChaosRecipes.ALL.size()).bounds(leftPos+226,topPos+8,18,16).build());
    }
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partial) {
        ScreenCompat.background(this,g,mouseX,mouseY,partial);
        super.render(g,mouseX,mouseY,partial);renderTooltip(g,mouseX,mouseY);
        var recipe=recipe();
        for(int i=0;i<8;i++)if(menu.getSlot(i).getItem().isEmpty() && isHovering(ChaosMenu.POS[i][0],ChaosMenu.POS[i][1],16,16,mouseX,mouseY)) {
            ItemStack ghost=ghost(recipe,i);
            if(!ghost.isEmpty())g.renderTooltip(font,ghost,mouseX,mouseY);
        }
    }
    private ChaosRecipes.Recipe recipe() {
        if(menu.activeRecipe()>=0)selected=menu.activeRecipe();
        return ChaosRecipes.ALL.get(selected);
    }
    private ItemStack ghost(ChaosRecipes.Recipe recipe,int slot) {
        if(slot==0)return recipe.base();
        if(slot==1)return new ItemStack(ChaosContent.wartItem.get());
        if(slot==6)return new ItemStack(Items.BLAZE_POWDER);
        if(slot==7)return recipe.output();
        int i=slot-2;
        return i<recipe.components().size()?new ItemStack(recipe.components().get(i).item(),recipe.components().get(i).count()):ItemStack.EMPTY;
    }
    @Override protected void renderBg(GuiGraphics g,float partial,int mouseX,int mouseY) {
        int x=leftPos,y=topPos;
        g.blit(VersionApi.id("textures/gui/chaos_stand.png"),x,y,0,0,256,256,256,256);
        var recipe=recipe();
        for(int i=0;i<8;i++)if(menu.getSlot(i).getItem().isEmpty()) {
            var stack=ghost(recipe,i);int sx=x+ChaosMenu.POS[i][0],sy=y+ChaosMenu.POS[i][1];
            if(!stack.isEmpty()) {
                g.renderItem(stack,sx,sy);g.renderItemDecorations(font,stack,sx,sy);
                g.fill(sx,sy,sx+16,sy+16,0x70202B3B);
            }
        }
        boolean brewing=menu.progress()>0;
        float fraction=menu.total()==0?0:(float)menu.progress()/menu.total();
        long ticks=minecraft.level==null?System.currentTimeMillis()/50:minecraft.level.getGameTime();
        double phase=(ticks+partial)*.13;
        if(brewing) {
            // Surface bands climb, bubbles orbit and pixels flash on the pipes.
            for(int j=0;j<9;j++) {
                int xx=x+114+j*3,yy=y+105-(int)(fraction*17)-((int)(Math.sin(phase+j*.7)*2));
                g.fill(xx,yy,xx+2,y+107,0xB852DDCE);
            }
            for(int i=0;i<8;i++) {
                double angle=phase+i*Math.PI/4;
                int xx=x+128+(int)(Math.cos(angle)*34),yy=y+79+(int)(Math.sin(angle)*27);
                g.fill(xx,yy,xx+2,yy+2,i%2==0?0xFFEED181:0xFF6AEFDE);
            }
            g.fill(x+83,y+116,x+83+(int)(90*fraction),y+119,0xFF69E7D5);
        }
        int bar=(int)(42*Math.min(1,menu.fuel()/20.0));
        g.fill(x+194,y+152,x+194+bar,y+154,0xFFF2B257);
    }
    @Override protected void renderLabels(GuiGraphics g,int mouseX,int mouseY) {
        g.drawString(font,title,12,12,0xE9CF90,false);
        g.drawCenteredString(font,Component.translatable("gui.forbidden_brews.wart"),128,23,0xB8A3D8);
        g.drawString(font,Component.translatable("gui.forbidden_brews.components"),15,39,0xA9C0D1,false);
        g.drawString(font,Component.translatable("gui.forbidden_brews.components"),185,39,0xA9C0D1,false);
        g.drawCenteredString(font,Component.translatable(menu.progress()>0?"gui.forbidden_brews.brewing":"gui.forbidden_brews.base"),128,95,0x83DBCE);
        g.drawCenteredString(font,Component.translatable("gui.forbidden_brews.result"),128,147,0xBCD6E5);
        g.drawCenteredString(font,Component.translatable("gui.forbidden_brews.fuel"),214,118,0xEFBA68);
        g.drawString(font,playerInventoryTitle,48,158,0xA2B7C7,false);
        // Compact recipe name sits over the lower-left ornament; full name is
        // available by hovering the output ghost.
        String text=Component.translatable(recipe().result().translation()).getString();
        g.drawString(font,font.plainSubstrByWidth(text,78),17,130,0x8DDAD0,false);
        g.drawString(font,(ChaosRecipes.ALL.indexOf(recipe())+1)+"/"+ChaosRecipes.ALL.size(),27,143,0x7C91A5,false);
    }
}
