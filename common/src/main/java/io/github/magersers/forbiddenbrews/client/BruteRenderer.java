package io.github.magersers.forbiddenbrews.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.magersers.forbiddenbrews.VersionApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class BruteRenderer {
    private static final ModelPart ROOT=BruteModelLayer.create().bakeRoot();
    private static void reset() {ROOT.getAllParts().forEach(ModelPart::resetPose);}
    private static RenderType texture() {return RenderType.entityCutoutNoCull(VersionApi.id("textures/entity/steve_brute.png"));}
    public static void render(LivingEntity actor,double x,double y,double z,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light) {
        if(actor.isInvisibleTo(Minecraft.getInstance().player))return;
        reset();var body=ROOT.getChild("body");
        float walk=actor.walkAnimation.position(partial),speed=actor.walkAnimation.speed(partial);
        float sway=(float)Math.cos(walk*.65F)*speed*.65F;
        ROOT.getChild("left_leg").xRot=sway;ROOT.getChild("right_leg").xRot=-sway;
        var left=body.getChild("left_arm");var right=body.getChild("right_arm");
        left.xRot=-sway*.5F;right.xRot=sway*.5F;
        float attack=(float)Math.sin(actor.getAttackAnim(partial)*Math.PI);
        left.xRot-=attack*1.8F;right.xRot-=attack*2.2F;body.xRot=attack*.18F;
        body.getChild("head").yRot=(actor.getYHeadRot()-actor.yBodyRot)*(float)Math.PI/180;
        body.getChild("head").xRot=actor.getXRot()*(float)Math.PI/180;
        pose.pushPose();pose.translate(x,y,z);
        pose.mulPose(Axis.YP.rotationDegrees(180-actor.yBodyRot));pose.scale(-1,-1,1);pose.translate(0,-1.5,0);
        ROOT.render(pose,buffers.getBuffer(texture()),light,OverlayTexture.NO_OVERLAY);pose.popPose();
        if(actor!=Minecraft.getInstance().player && (actor instanceof Player || actor.hasCustomName())) {
            var mc=Minecraft.getInstance();pose.pushPose();pose.translate(x,y+3.15,z);
            pose.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());pose.scale(-.025F,-.025F,.025F);
            var name=actor.getDisplayName();mc.font.drawInBatch(name,-mc.font.width(name)/2F,0,0xFFFFFFFF,false,
                pose.last().pose(),buffers,Font.DisplayMode.NORMAL,0x70000000,light);pose.popPose();
        }
    }
    public static void hand(boolean left,PoseStack pose,MultiBufferSource buffers,int light) {
        reset();var arm=ROOT.getChild("body").getChild(left?"left_arm":"right_arm");
        arm.x=0;arm.y=0;arm.z=0;arm.xRot=0;arm.yRot=0;arm.zRot=0;
        pose.pushPose();pose.scale(.65F,.65F,.65F);
        arm.render(pose,buffers.getBuffer(texture()),light,OverlayTexture.NO_OVERLAY);pose.popPose();
    }
    private BruteRenderer() {}
}
