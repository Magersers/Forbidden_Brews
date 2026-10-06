package io.github.magersers.forbiddenbrews;

import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class GravityNetwork {
    public static void register() {
        PayloadTypeRegistry.playC2S().register(GravityPayload.TYPE,GravityPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(GravityPayload.TYPE,(payload,context)->
            context.server().execute(()->Gravity.toggle(context.player())));
    }
    public static void sendJump() {ClientPlayNetworking.send(GravityPayload.INSTANCE);}
    private GravityNetwork() {}
}
