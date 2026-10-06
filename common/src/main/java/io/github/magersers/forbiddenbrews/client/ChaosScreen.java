package io.github.magersers.forbiddenbrews.client;

import io.github.magersers.forbiddenbrews.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;

/** Pixel copper/obsidian UI. Recipe hints are client-only; server validates inputs. */
public final class ChaosScreen extends AbstractContainerScreen<ChaosMenu> {
    private int selected=-1;
    private boolean choosing;
    public ChaosScreen(ChaosMenu menu,Inventory inv,Component title) {
        super(menu,inv,title);imageWidth=256;imageHeight=256;
    }
    /** Click the empty result cell, or the empty base cell, to open the picker. */
    @Override public boolean mouseClicked(double mouseX,double mouseY,int button) {
        if(choosing) {
            if(button==0) {
                var options=ChaosRecipes.available(menu.getSlot(0).getItem());
                int columns=options.size()>3?2:1,height=pickerHeight(options.size()),width=columns==2?104:212;
                for(int i=0;i<options.size();i++)if(isHovering(22+(i%columns)*108,64+(i/columns)*(height+2),width,height,mouseX,mouseY)) {
                    selected=ChaosRecipes.ALL.indexOf(options.get(i));
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId,selected);
                    choosing=false;return true;
                }
            }
            choosing=false;return true;
        }
        if(button==0 && menu.progress()==0 && menu.getCarried().isEmpty() &&
            ((menu.getSlot(7).getItem().isEmpty() && isHovering(120,128,16,16,mouseX,mouseY)) ||
             (menu.getSlot(0).getItem().isEmpty() && isHovering(120,70,16,16,mouseX,mouseY)))) {
            choosing=true;return true;
        }
        return super.mouseClicked(mouseX,mouseY,button);
    }
    @Override public boolean keyPressed(int key,int scan,int modifiers) {
        if(choosing && key==256) { choosing=false;return true; }
        return super.keyPressed(key,scan,modifiers);
    }
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partial) {
        ScreenCompat.background(this,g,mouseX,mouseY,partial);
        super.render(g,mouseX,mouseY,partial);
        if(menu.progress()>0)choosing=false;
        if(choosing) { renderPicker(g,mouseX,mouseY);return; }
        renderTooltip(g,mouseX,mouseY);
        var recipe=recipe();
        if(recipe==null)return;
        for(int i=0;i<8;i++)if(menu.getSlot(i).getItem().isEmpty() && isHovering(ChaosMenu.POS[i][0],ChaosMenu.POS[i][1],16,16,mouseX,mouseY)) {
            ItemStack ghost=ghost(recipe,i);
            if(!ghost.isEmpty())g.renderTooltip(font,ghost,mouseX,mouseY);
        }
    }
    private ChaosRecipes.Recipe recipe() {
        var output=menu.getSlot(7).getItem();
        if(output.getItem() instanceof BrewItem brew)
            return ChaosRecipes.ALL.stream().filter(r->r.result().equals(brew.spec)).findFirst().orElse(null);
        if(menu.activeRecipe()>=0)return ChaosRecipes.ALL.get(menu.activeRecipe());
        var available=ChaosRecipes.available(menu.getSlot(0).getItem());
        if(menu.selectedRecipe()>=0 && available.contains(ChaosRecipes.ALL.get(menu.selectedRecipe())))selected=menu.selectedRecipe();
        if(selected<0 || !available.contains(ChaosRecipes.ALL.get(selected)))selected=available.isEmpty()?-1:ChaosRecipes.ALL.indexOf(available.get(0));
        return selected<0?null:ChaosRecipes.ALL.get(selected);
    }
    private static int pickerHeight(int count) {return count>8?32:count>3?40:25;}
    private void renderPicker(GuiGraphics g,int mouseX,int mouseY) {
        g.pose().pushPose();g.pose().translate(0,0,300);
        int x=leftPos,y=topPos;
        var options=ChaosRecipes.available(menu.getSlot(0).getItem());
        int columns=options.size()>3?2:1,height=pickerHeight(options.size()),width=columns==2?104:212;
        int rows=(options.size()+columns-1)/columns;
        int bottom=options.isEmpty()?y+103:y+66+rows*(height+2);
        g.fill(x+8,y+30,x+248,Math.max(y+155,bottom+8),0xC00B1320);
        g.fill(x+16,y+44,x+240,bottom,0xFF0B1523);
        g.renderOutline(x+16,y+44,224,bottom-y-44,0xFFD7B373);
        g.drawCenteredString(font,Component.translatable("gui.forbidden_brews.picker"),x+128,y+51,0xFFE9CF90);
        if(options.isEmpty())g.drawCenteredString(font,Component.translatable("gui.forbidden_brews.no_recipes"),x+128,y+77,0xFFB6C8D4);
        ItemStack hovered=ItemStack.EMPTY;
        for(int i=0;i<options.size();i++) {
            var option=options.get(i);int xx=x+22+(i%columns)*108,yy=y+64+(i/columns)*(height+2);
            boolean hover=isHovering(xx-x,yy-y,width,height,mouseX,mouseY);
            g.fill(xx,yy,xx+width,yy+height,hover?0xFF344A57:0xFF172739);
            g.renderOutline(xx,yy,width,height,hover?0xFF83E4CE:menu.selectedRecipe()==ChaosRecipes.ALL.indexOf(option)?0xFFE9CF90:0xFF405369);
            var stack=option.output();g.renderItem(stack,xx+6,yy+(height-16)/2);
            String name=columns==2?Component.translatable("effect.forbidden_brews."+option.result().family()).getString():stack.getHoverName().getString();
            int color=hover?0xFFEBD294:0xFFD4E2E8;
            String first=font.plainSubstrByWidth(name,width-30);
            if(columns==2 && first.length()<name.length() && first.lastIndexOf(' ')>0)first=first.substring(0,first.lastIndexOf(' '));
            g.drawString(font,first,xx+28,yy+(columns==2?(height==32?7:10):8),color,false);
            if(columns==2 && first.length()<name.length())g.drawString(font,font.plainSubstrByWidth(name.substring(first.length()).stripLeading(),width-30),xx+28,yy+(height==32?18:21),color,false);
            if(hover)hovered=stack;
        }
        if(!hovered.isEmpty())g.renderTooltip(font,hovered,mouseX,mouseY);
        g.pose().popPose();
    }
    private ItemStack ghost(ChaosRecipes.Recipe recipe,int slot) {
        if(recipe==null)return ItemStack.EMPTY;
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
        g.drawCenteredString(font,Component.translatable(!menu.getSlot(7).getItem().isEmpty()?"gui.forbidden_brews.ready":menu.progress()>0?"gui.forbidden_brews.result":"gui.forbidden_brews.choose"),128,147,0xBCD6E5);
        g.drawCenteredString(font,Component.translatable("gui.forbidden_brews.fuel"),214,118,0xEFBA68);
        g.drawString(font,playerInventoryTitle,48,158,0xA2B7C7,false);
        // Compact recipe name sits over the lower-left ornament; full name is
        // available by hovering the output ghost.
        var recipe=recipe();
        if(recipe!=null) {
            String text=Component.translatable(recipe.result().translation()).getString();
            g.drawString(font,font.plainSubstrByWidth(text,78),17,130,0x8DDAD0,false);
            if(!recipe.result().instant()) {
                g.drawString(font,recipe.result().durationLabel(),27,143,0x7C91A5,false);
            }
        }
    }
}
