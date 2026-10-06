package io.github.magersers.forbiddenbrews;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.world.entity.player.Player;

public final class GravityNetwork {
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(GravityPayload.TYPE,GravityPayload.CODEC,(payload,context)->
            context.enqueueWork(()->Gravity.toggle(context.player())));
    }
    public static void sendJump() {PacketDistributor.sendToServer(GravityPayload.INSTANCE);}
    private GravityNetwork() {}
}
