package org.plucklabs.pylons.networking.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.plucklabs.pylons.Pylons;
import org.plucklabs.pylons.util.PylonLevels;

import java.util.HashSet;
import java.util.Set;

public record HologramPositions(BlockPos origin, PylonLevels pillarLevel, BlockState blockState) implements CustomPacketPayload {
    public static final Type<HologramPositions> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Pylons.MODID, "hologram_positions"));


    //Instructs server and client on how to pack and unpack the set into ByteBufs for network transfer.
    public static final StreamCodec<FriendlyByteBuf, HologramPositions> STREAM_CODEC = StreamCodec.composite(

            BlockPos.STREAM_CODEC,
            HologramPositions::origin,
            NeoForgeStreamCodecs.enumCodec(PylonLevels.class),
            HologramPositions::pillarLevel,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY),
            HologramPositions::blockState,
            HologramPositions::new
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
