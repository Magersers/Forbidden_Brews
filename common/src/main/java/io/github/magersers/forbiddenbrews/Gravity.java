package io.github.magersers.forbiddenbrews;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class Gravity {
    public static boolean inverted(LivingEntity entity) {
        return entity instanceof Player player && controls(player) && ((MorphState)player).brews$gravityUp();
    }
    public static boolean controls(Player player) {
        return player.isAlive() && !player.isSpectator() && !player.isPassenger()
            && VersionApi.effectInstance(player,"gravity")!=null && !Morphs.bat(Morphs.form(player));
    }
    /** Network handlers call only on the server thread; held jump never repeats. */
    public static void toggle(Player player) {
        if(player.level().isClientSide || !controls(player))return;
        var state=(MorphState)player;var data=state.brews$data();long now=player.level().getGameTime();
        if(data.lastToggle!=Long.MIN_VALUE && now-data.lastToggle<2)return;
        data.lastToggle=now;state.brews$gravityUp(!state.brews$gravityUp());player.fallDistance=0;
        player.setOnGround(false);player.setDeltaMovement(player.getDeltaMovement().x,state.brews$gravityUp()?.1:-.1,player.getDeltaMovement().z);
        player.hurtMarked=true;
        player.displayClientMessage(net.minecraft.network.chat.Component.translatable(state.brews$gravityUp()?"message.forbidden_brews.gravity_up":"message.forbidden_brews.gravity_down"),true);
    }
    public static void tick(LivingEntity entity) {
        if(!(entity instanceof Player player))return;
        var state=(MorphState)player;var data=state.brews$data();
        boolean active=controls(player),up=active && state.brews$gravityUp();
        if(up) {
            player.setOnGround(false);
            var motion=player.getDeltaMovement();
            double rise=player.getY()+player.getBbHeight()>=player.level().getMaxBuildHeight()-1?0:Math.min(.65,motion.y+.08);
            player.setDeltaMovement(motion.x,rise,motion.z);
        }
        if(active) {
            var motion=player.getDeltaMovement();
            if(motion.y<-.65)player.setDeltaMovement(motion.x,-.65,motion.z);
            if(player.getAbilities().flying) {player.getAbilities().flying=false;if(!player.level().isClientSide)player.onUpdateAbilities();}
            player.fallDistance=0;data.safeLanding=!player.onGround();
        } else if(!player.level().isClientSide && state.brews$gravityUp())state.brews$gravityUp(false);
    }
    private Gravity() {}
}
