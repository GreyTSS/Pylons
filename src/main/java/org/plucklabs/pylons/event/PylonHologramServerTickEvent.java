package org.plucklabs.pylons.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
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
import org.plucklabs.pylons.util.PylonLevels;

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
        if(timer.isEmpty()) return;

        timer.entrySet().removeIf((entry) -> {
            int newValue = entry.getValue()-1;
            entry.setValue(newValue);
            if(newValue <= 0) {
                UUID key = entry.getKey();
                if(event.getServer().getPlayerList().getPlayer(key) instanceof ServerPlayer player) {
                    PacketDistributor.sendToPlayer(player, new HologramPositions(false, new HashSet<>(), Blocks.IRON_BLOCK.defaultBlockState()));
                }
                return true;
            }
            return false;
        });
    }

    @SubscribeEvent
    public static void tickPost(ServerTickEvent.Post event) {
        deltaAdded.clear();
        deltaRemoved.clear();






        data.forEach((uuid, currentPacket) ->{
           HologramPositions cachedPacket = cache.get(uuid);

           if(!currentPacket.equals(cachedPacket)) {
               deltaAdded.put(uuid, currentPacket);
           }
        });

        cache.forEach((uuid, currentPacket) ->{
            HologramPositions cachedPacket = data.get(uuid);

            if(!currentPacket.equals(cachedPacket)) {
                deltaRemoved.put(uuid, currentPacket);
            }
        });


        for(UUID uuid : deltaAdded.keySet()) {
            if(!(event.getServer().getPlayerList().getPlayer(uuid) instanceof  ServerPlayer player)) continue;
            PacketDistributor.sendToPlayer(player, data.get(uuid));
        }

        for(UUID uuid : deltaRemoved.keySet()) {
            timer.put(uuid,Config.pylonHologramFlashDuration);
        }

        cache.clear();
        cache.putAll(data);

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
                    var tier = pylon.getTier();
                    data.put(player.getUUID(), new HologramPositions((pylon.getTier()!= PylonLevels.LEVEL_3), pylon.getLowestInvalidTier(),blockItem.getBlock().defaultBlockState()));
                    timer.remove(player.getUUID());
                    break;
                } else {
                    data.put(player.getUUID(), new HologramPositions(false, new HashSet<>(), Blocks.IRON_BLOCK.defaultBlockState()));
                }
            }
        } else {
            data.put(player.getUUID(), new HologramPositions(false, new HashSet<>(), Blocks.IRON_BLOCK.defaultBlockState()));
        }








    }




}
