package org.plucklabs.pylons.networking;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.plucklabs.pylons.networking.packet.HologramPositions;

//Handling Packets from Server -> Client
public class ServerPayloadHandler {

    //On Server
    public static void handleDataOnMain(HologramPositions hologramPositions, IPayloadContext context) {
        context.player().sendSystemMessage(Component.literal("Position Count: " + hologramPositions.positions().size()));

    }
}
