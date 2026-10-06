package io.github.magersers.forbiddenbrews.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.magersers.forbiddenbrews.Morphs;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;

public final class MorphRenderer {
    private record Proxy(int form,LivingEntity model) {}
    private static final Map<LivingEntity,Proxy> MODELS=new WeakHashMap<>();
    private static net.minecraft.world.level.Level world;
    public static void world(net.minecraft.world.level.Level next) {if(world!=next) {MODELS.clear();world=next;}}
    public static boolean render(Entity entity,double x,double y,double z,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light) {
        if(!(entity instanceof LivingEntity actor))return false;
        int form=Morphs.form(actor);if(form==0)return false;
        if(form==Morphs.BRUTE) {BruteRenderer.render(actor,x,y,z,yaw,partial,pose,buffers,light);return true;}
        var type=Morphs.type(form);if(type==null)return false;
        var cached=MODELS.get(actor);
        if(cached==null || cached.form()!=form || cached.model().level()!=actor.level()) {
            var model=type.create(actor.level());if(model==null)return false;
            if(model instanceof Bat bat)bat.setResting(false);
            cached=new Proxy(form,model);MODELS.put(actor,cached);
        }
        var model=cached.model();
        if(model.tickCount!=actor.tickCount)model.walkAnimation.update(actor.walkAnimation.speed(),1);
        model.tickCount=actor.tickCount;model.setPos(actor.position());model.xo=actor.xo;model.yo=actor.yo;model.zo=actor.zo;
        model.setYRot(actor.getYRot());model.setXRot(actor.getXRot());model.yRotO=actor.yRotO;model.xRotO=actor.xRotO;
        model.yBodyRot=actor.yBodyRot;model.yBodyRotO=actor.yBodyRotO;model.yHeadRot=actor.yHeadRot;model.yHeadRotO=actor.yHeadRotO;
        model.attackAnim=actor.attackAnim;model.oAttackAnim=actor.oAttackAnim;model.hurtTime=actor.hurtTime;
        model.setInvisible(actor.isInvisible());model.setGlowingTag(actor.isCurrentlyGlowing());
        if(actor instanceof Player || actor.hasCustomName()) {model.setCustomName(actor.getDisplayName());model.setCustomNameVisible(true);}
        else model.setCustomName(null);
        for(var slot:List.of(EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND,EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET))model.setItemSlot(slot,actor.getItemBySlot(slot));
        Minecraft.getInstance().getEntityRenderDispatcher().render(model,x,y,z,yaw,partial,pose,buffers,light);
        return true;
    }
    private MorphRenderer() {}
}
