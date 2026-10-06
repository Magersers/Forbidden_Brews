package io.github.magersers.forbiddenbrews.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderType;

/** Dedicated line batch: see through terrain, never write terrain depth. */
final class SightRenderType extends RenderType {
    static final RenderType LINES=new SightRenderType();
    private SightRenderType() {
        super("forbidden_brews_sight",DefaultVertexFormat.POSITION_COLOR_NORMAL,VertexFormat.Mode.LINES,
            256,false,false,SightRenderType::begin,SightRenderType::end);
    }
    private static void begin() {
        RENDERTYPE_LINES_SHADER.setupRenderState();DEFAULT_LINE.setupRenderState();
        NO_CULL.setupRenderState();NO_DEPTH_TEST.setupRenderState();COLOR_WRITE.setupRenderState();MAIN_TARGET.setupRenderState();
        RenderSystem.disableDepthTest();
    }
    private static void end() {
        MAIN_TARGET.clearRenderState();COLOR_WRITE.clearRenderState();NO_DEPTH_TEST.clearRenderState();
        NO_CULL.clearRenderState();DEFAULT_LINE.clearRenderState();RENDERTYPE_LINES_SHADER.clearRenderState();
        RenderSystem.enableDepthTest();
    }
}
