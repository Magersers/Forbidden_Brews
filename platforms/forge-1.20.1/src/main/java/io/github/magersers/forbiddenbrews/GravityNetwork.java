package io.github.magersers.forbiddenbrews;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;
import java.util.Optional;

public final class GravityNetwork {
    private record Jump() {}
    private static final SimpleChannel CHANNEL=NetworkRegistry.newSimpleChannel(VersionApi.id("controls"),()->"1","1"::equals,"1"::equals);
    public static void register() {
        CHANNEL.registerMessage(0,Jump.class,(message,buffer)->{},buffer->new Jump(),(message,context)-> {
            var ctx=context.get();ctx.enqueueWork(()-> {if(ctx.getSender()!=null)Gravity.toggle(ctx.getSender());});ctx.setPacketHandled(true);
        },Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
    public static void sendJump() {CHANNEL.sendToServer(new Jump());}
    private GravityNetwork() {}
}
