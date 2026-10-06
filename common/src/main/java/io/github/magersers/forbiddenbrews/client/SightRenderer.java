package io.github.magersers.forbiddenbrews.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.magersers.forbiddenbrews.SightTargets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SightRenderer {
    public static void render(PoseStack pose,Vec3 camera) {
        var mc=Minecraft.getInstance();
        if(mc.level==null || mc.player==null)return;
        var ore=SightCache.nearestOre();var traps=SightCache.traps();
        if(ore==null && traps.isEmpty())return;
        var buffers=mc.renderBuffers().bufferSource();
        if(ore!=null && mc.level.hasChunkAt(ore) && SightTargets.ore(mc.level.getBlockState(ore))) {
            pose.pushPose();pose.translate(ore.getX()-camera.x,ore.getY()-camera.y,ore.getZ()-camera.z);
            mc.getBlockRenderer().renderSingleBlock(mc.level.getBlockState(ore),pose,
                type->buffers.getBuffer(SightRenderType.ORE),LightTexture.FULL_BRIGHT,OverlayTexture.NO_OVERLAY);
            pose.popPose();buffers.endBatch(SightRenderType.ORE);
        }
        var lines=buffers.getBuffer(SightRenderType.LINES);
        pose.pushPose();pose.translate(-camera.x,-camera.y,-camera.z);
        if(ore!=null && mc.level.hasChunkAt(ore) && SightTargets.ore(mc.level.getBlockState(ore)))
            LevelRenderer.renderLineBox(pose,lines,new AABB(ore).inflate(.015),.32F,.8F,1F,1F);
        for(var pos:traps) {
            if(pos.getCenter().distanceToSqr(mc.player.position())>SightTargets.TRAP_RADIUS*SightTargets.TRAP_RADIUS
                || !mc.level.hasChunkAt(pos) || !SightTargets.trap(mc.level.getBlockState(pos)))continue;
            // Whole-cell locator plus the actual button/plate/string outline.
            LevelRenderer.renderLineBox(pose,lines,new AABB(pos).deflate(.02),.65F,.36F,.08F,1F);
            mc.level.getBlockState(pos).getShape(mc.level,pos).forAllBoxes((x1,y1,z1,x2,y2,z2)->
                LevelRenderer.renderLineBox(pose,lines,new AABB(x1,y1,z1,x2,y2,z2).move(pos).inflate(.01),1F,.78F,.3F,1F));
        }
        pose.popPose();buffers.endBatch(SightRenderType.LINES);
    }
    private SightRenderer() {}
}
