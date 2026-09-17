package org.plucklabs.pylons.event.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.plucklabs.pylons.Pylons;
import org.plucklabs.pylons.networking.client.ClientPayloadHandler;
import org.plucklabs.pylons.networking.packet.HologramPositions;

@EventBusSubscriber(modid = Pylons.MODID)
public class RegisterPayloadsEvent {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent  event) {
        final PayloadRegistrar registrar = event.registrar("1")
                .executesOn(HandlerThread.MAIN);

        registrar.playToClient(HologramPositions.TYPE, HologramPositions.STREAM_CODEC, ClientPayloadHandler::handleDataOnMain);
    }
}
