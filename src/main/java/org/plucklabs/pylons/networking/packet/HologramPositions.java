package org.plucklabs.pylons.networking.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.plucklabs.pylons.Pylons;

import java.util.HashSet;
import java.util.Set;

public record HologramPositions(boolean draw, Set<BlockPos> positions) implements CustomPacketPayload {
    public static final Type<HologramPositions> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Pylons.MODID, "hologram_positions"));


    //Instructs server and client on how to pack and unpack the set into ByteBufs for network transfer.
    public static final StreamCodec<ByteBuf, HologramPositions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            HologramPositions::draw,

            ByteBufCodecs.collection(
                HashSet::new,
                BlockPos.STREAM_CODEC,
                60
            ),
            HologramPositions::positions,
            HologramPositions::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
