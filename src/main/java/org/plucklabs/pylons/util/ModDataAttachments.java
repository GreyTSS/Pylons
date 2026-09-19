package org.plucklabs.pylons.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.plucklabs.pylons.Pylons;
import org.plucklabs.pylons.networking.packet.HologramPositions;

import java.util.HashSet;
import java.util.function.Supplier;

public class ModDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Pylons.MODID);

    public static final Supplier<AttachmentType<HologramPositions>> PYLON_HOLOGRAM = ATTACHMENT_TYPES.register("pylon_hologram", () -> AttachmentType.<HologramPositions>builder(() -> new HologramPositions(new BlockPos(0,0,0), PylonLevels.INACTIVE, Blocks.IRON_BLOCK.defaultBlockState())).build());


    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
