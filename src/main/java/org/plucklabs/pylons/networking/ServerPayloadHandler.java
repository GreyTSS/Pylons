package org.plucklabs.pylons.networking;

import com.mojang.math.Transformation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.joml.Vector3f;
import org.plucklabs.pylons.networking.packet.HologramPositions;
import org.plucklabs.pylons.util.ModDataAttachments;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

//Handling Packets from Server -> Client
public class ServerPayloadHandler {

    private static final Map<UUID, HashSet<Display.BlockDisplay>> ACTIVE_BLOCK_DISPLAYS = new HashMap<>();


    //On Server
    public static void handleDataOnMain(HologramPositions hologramPositions, IPayloadContext context) {
        context.player().sendSystemMessage(Component.literal("Position Count: " + hologramPositions.positions().size()));

        HologramPositions attachment = context.player().getData(ModDataAttachments.PYLON_HOLOGRAM);

        clearDisplays(context.player().getUUID());

        if(hologramPositions.draw() && !hologramPositions.positions().isEmpty()) {
            var level = context.player().level();
            if(!(level instanceof ClientLevel clientLevel)) return;

            BlockState targetState = Blocks.IRON_BLOCK.defaultBlockState();

            //Matrix Transformation for Z-Fighting
            float scaleFactor = 0.98f;
            float offset = (1.0f - scaleFactor) / 2.0f;

            Transformation transformation = new Transformation(
                    new Vector3f(offset,offset,offset),
                    null,
                    new Vector3f(scaleFactor, scaleFactor, scaleFactor),
                    null
            );

            //Tag for Brightness
            CompoundTag brightness = new CompoundTag();
                brightness.putInt("block", 15);
                brightness.putInt("sky", 15);



            for(BlockPos pos : hologramPositions.positions()) {
                //Create a block display
                Display.BlockDisplay display = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level);

                //Create NBT Data to attach
                CompoundTag nbt = new CompoundTag();
                nbt.put("block_state", NbtUtils.writeBlockState(targetState));

                //Glow Red
                nbt.putBoolean("glowing", true);
                nbt.putInt("glow_color_override", 0xFFFF0000);
                nbt.put("brightness", brightness);

                //Add matrix to NBT
                Transformation.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, transformation)
                        .result()
                        .ifPresent(tag -> nbt.put("transformation", tag));

                //Configure Display
                display.load(nbt);
                display.setGlowingTag(true);
                //display.set
                display.setPos(pos.getX(), pos.getY(), pos.getZ());
                System.out.println("Added: ["+pos.getX()+", "+pos.getY()+ ", "+pos.getZ()+"]");

                //Display it
                clientLevel.addEntity(display);
                ACTIVE_BLOCK_DISPLAYS.computeIfAbsent(context.player().getUUID(), k -> new HashSet<>()).add(display);
            }
        };

    }

    private static void clearDisplays(UUID uuid) {
        if(ACTIVE_BLOCK_DISPLAYS.get(uuid) == null || ACTIVE_BLOCK_DISPLAYS.get(uuid).isEmpty()) return;
        for(Display.BlockDisplay display : ACTIVE_BLOCK_DISPLAYS.get(uuid)) {
            display.discard();
        }
        ACTIVE_BLOCK_DISPLAYS.remove(uuid);
    }

}
