package io.github.magersers.forbiddenbrews;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record GravityPayload() implements CustomPacketPayload {
    public static final GravityPayload INSTANCE=new GravityPayload();
    public static final Type<GravityPayload> TYPE=new Type<>(VersionApi.id("gravity_jump"));
    public static final StreamCodec<RegistryFriendlyByteBuf,GravityPayload> CODEC=StreamCodec.unit(INSTANCE);
    public Type<GravityPayload> type() {return TYPE;}
}
