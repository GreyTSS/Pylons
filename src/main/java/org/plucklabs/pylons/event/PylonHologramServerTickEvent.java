package org.plucklabs.pylons.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.plucklabs.pylons.Config;
import org.plucklabs.pylons.Pylons;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;
import org.plucklabs.pylons.networking.packet.HologramPositions;
import org.plucklabs.pylons.util.ModTags;

import java.util.*;

@EventBusSubscriber(modid = Pylons.MODID)
public class PylonHologramServerTickEvent {

    public static final Map<UUID, Integer> timer = new HashMap<>();
    public static final Map<UUID, HologramPositions> cache = new HashMap<>();
    public static final Map<UUID, HologramPositions> data = new HashMap<>();
    public static final Map<UUID, HologramPositions> deltaAdded = new HashMap<>();
    public static final Map<UUID, HologramPositions> deltaRemoved = new HashMap<>();

    @SubscribeEvent
    public static void tick(ServerTickEvent.Pre event) {

        Map<UUID, Integer> newTimer = new HashMap<>();
        for(UUID key : timer.keySet()) {
            if(!(event.getServer().getPlayerList().getPlayer(key) instanceof ServerPlayer player)) continue;
            System.out.println(player.getName().getString() + ": "+timer.get(key));

            newTimer.put(key, (timer.get(key) - 1));
            if(timer.get(key) <= 0) {
                HologramPositions position = new HologramPositions(false, new HashSet<>());
                PacketDistributor.sendToPlayer(player, position);
                newTimer.remove(key);

                cache.remove(key);

            }
        }

        timer.clear();
        timer.putAll(newTimer);

    }

    @SubscribeEvent
    public static void tickPost(ServerTickEvent.Post event) {
        deltaAdded.clear();
        deltaRemoved.clear();






        deltaAdded.putAll(data);
        deltaAdded.keySet().removeAll(cache.keySet());


        deltaRemoved.putAll(cache);
        deltaRemoved.keySet().removeAll(data.keySet());

        for(UUID uuid : deltaAdded.keySet()) {
            if(!(event.getServer().getPlayerList().getPlayer(uuid) instanceof  ServerPlayer player)) continue;
            PacketDistributor.sendToPlayer(player, data.get(uuid));
        }

        for(UUID uuid : data.keySet()) {
            timer.put(uuid,Config.pylonHologramFlashDuration);
        }



    }


    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {

        //Early Exit
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if(PylonBlockEntity.getLoadedPylons().isEmpty()) {
            timer.clear();
            cache.clear();
            data.clear();
            return;
        }




        //Cache values

        if(player.getMainHandItem().getItem() instanceof BlockItem blockItem && blockItem.getBlock().defaultBlockState().is(ModTags.Blocks.PILLAR_MATERIAL)) {
            for(PylonBlockEntity pylon : PylonBlockEntity.getLoadedPylons()) {
                if(pylon.checkHologramRange(player)) {
                    data.put(player.getUUID(), new HologramPositions(true, pylon.getLowestInvalidTier()));
                    break;
                }
            }
        }








    }




}
