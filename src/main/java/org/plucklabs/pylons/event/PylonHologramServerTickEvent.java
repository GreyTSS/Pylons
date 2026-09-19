package org.plucklabs.pylons.event;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.plucklabs.pylons.Config;
import org.plucklabs.pylons.Pylons;
import org.plucklabs.pylons.block.ModBlocks;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;
import org.plucklabs.pylons.networking.packet.HologramPositions;
import org.plucklabs.pylons.util.ModTags;
import org.plucklabs.pylons.util.PylonLevels;

import java.util.*;

@EventBusSubscriber(modid = Pylons.MODID)
public class PylonHologramServerTickEvent {

    //Right-clicking a pylon causes a structure display pulse, duration defined by config value. This map stores the progress
    //of each Player's active pulse countdown.
    public static final Map<UUID, Integer> displayPulsing = new HashMap<>();

    //Packet limiter forces a cooldown on the player when holding a pylon and displaying structure holograms in relation to targeted block.
    //Since it defaults to player block pos when no block is targeted, it fires packets on every microscopic movement the player makes unless
    //placed on a cooldown. Currently, cooldown is PACKET_TICK_COOLDOWN, but will later be moved to config.
    public static final Map<UUID, Integer> packetLimiter = new HashMap<>();
    public static final int PACKET_TICK_COOLDOWN = 20;

    //Data stores the current state that the client should hold, where as data holds the previous state the client should hold.
    //Packets are only ever fired to a client when there is a differance between current and previous states for that Player.
    public static final Map<UUID, HologramPositions> cache = new HashMap<>();
    public static final Map<UUID, HologramPositions> data = new HashMap<>();

    //DeltaAdded is used to calculate which players need to be sent packets.
    public static final Map<UUID, HologramPositions> deltaAdded = new HashMap<>();

    //This value is used to clear the DataAttachment on the Player that the client uses to display a hologram.
    private static final HologramPositions DEFAULT_BLANK_HOLOGRAM = new HologramPositions(new BlockPos(0,0,0), PylonLevels.INACTIVE, Blocks.IRON_BLOCK.defaultBlockState());

    /**
     * The pre-tick event is used to tick-down the two maps that store cooldowns. One for the pulsing display triggered
     * when you shift right-click a Pylon, and the other to enforce packets don't fire too frequently when holding
     * an unplaced pylon and previewing pillar locations.
     * @param event ServerTickEvent.pre
     */
    @SubscribeEvent
    public static void tick(ServerTickEvent.Pre event) {
        if(displayPulsing.isEmpty() && packetLimiter.isEmpty()) {
            return;
        }


        //Pulse
        Set<UUID> resets = new HashSet<>();
        displayPulsing.entrySet().removeIf((entry) -> {
            int newValue = entry.getValue() - 1;
            entry.setValue(newValue);
            if (newValue <= 0) {
                event.getServer().getPlayerList().getPlayer(entry.getKey()).sendSystemMessage(Component.literal("Removed!"));
                resets.add(entry.getKey());
                return true;
            }
            return false;
        });

        for(UUID uuid : resets) {
            if(event.getServer().getPlayerList().getPlayer(uuid) instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, DEFAULT_BLANK_HOLOGRAM);
                data.put(uuid, DEFAULT_BLANK_HOLOGRAM);
            }

        }
        //Preview
        packetLimiter.entrySet().removeIf((entry) -> {
            int newValue = entry.getValue()-1;
            entry.setValue(newValue);
            return newValue <= 0;
        });
        
        
    }

    /**
     * The post tick event fires after new Data has been collected in the Player tick event, and compares cached data
     * to current state data, determining which Players need to be updated with fresh packets.
     * @param event ServerTickEvent.Post
     */
    @SubscribeEvent
    public static void tickPost(ServerTickEvent.Post event) {

        deltaAdded.clear();

        //Evaluate each entry against the last
        data.forEach((uuid, currentPacket) ->{
           HologramPositions cachedPacket = cache.get(uuid);

           if(!currentPacket.equals(cachedPacket)) {
               deltaAdded.put(uuid, currentPacket);
           }

        });

        cache.forEach((uuid, cachedPacket) -> {
            if(!data.containsKey(uuid) && !displayPulsing.containsKey(uuid)) {
                deltaAdded.put(uuid, DEFAULT_BLANK_HOLOGRAM);
            }
        });


        //For each one changed, update client with proper packet.
        for(UUID uuid : deltaAdded.keySet()) {
            if(!(event.getServer().getPlayerList().getPlayer(uuid) instanceof  ServerPlayer player)) continue;
            PacketDistributor.sendToPlayer(player, data.get(uuid));
        }


        cache.clear();
        cache.putAll(data);

    }


    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Post event) {

        //Early Exits
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (PylonBlockEntity.getLoadedPylons().isEmpty()) {
            displayPulsing.clear();
            cache.clear();
            data.clear();
            return;
        }


        UUID uuid = player.getUUID();
        //Cache values
        if (player.getMainHandItem().getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock().defaultBlockState().is(ModTags.Blocks.PILLAR_MATERIAL)) {
                Set<PylonBlockEntity> entities = Set.copyOf(PylonBlockEntity.getLoadedPylons());
                for (PylonBlockEntity pylon : entities) {
                    if (pylon.checkHologramRange(player)) {

                        displayPulsing.remove(uuid);
                        var tier = pylon.getLowestInvalidTier();
                        System.out.println(tier.getSerializedName());
                        cacheInfo(data, player.getUUID(), new HologramPositions(pylon.getBlockPos(), pylon.getLowestInvalidTier(), blockItem.getBlock().defaultBlockState()));
                        return;
                    }
                }
                resetIfNotPulsing(uuid);
            } else if (blockItem.getBlock() == ModBlocks.PYLON.get()) {
                HitResult hit = player.pick(5.0D, 1.0f, false);
                BlockPos pos;
                if (hit instanceof BlockHitResult blockHit) {
                    pos = blockHit.getBlockPos().relative(blockHit.getDirection());
                } else {
                    pos = player.blockPosition();
                }
                cacheWithCooldown(data, uuid, new HologramPositions(pos, PylonLevels.LEVEL_1, Blocks.IRON_BLOCK.defaultBlockState()));
            }

        } else {
            resetIfNotPulsing(uuid);
        }
    }

    public static void resetIfNotPulsing(UUID uuid) {
        if(displayPulsing.containsKey(uuid)) return;
        cacheInfo(data, uuid, DEFAULT_BLANK_HOLOGRAM);
    }

    
    private static void cacheInfo(Map<UUID, HologramPositions> map, UUID uuid, HologramPositions hologramPositions) {
        if(hologramPositions == DEFAULT_BLANK_HOLOGRAM) {
            displayPulsing.remove(uuid);
        }

            map.put(uuid, hologramPositions);

    }

    private static void cacheWithCooldown(Map<UUID, HologramPositions> map, UUID uuid, HologramPositions hologramPositions) {
        if(!packetLimiter.containsKey(uuid)) {
            cacheInfo(map, uuid, hologramPositions);
            packetLimiter.put(uuid, PACKET_TICK_COOLDOWN);
        }
    }


    public static void pulseHologram(ServerPlayer player, HologramPositions packet){
        displayPulsing.put(player.getUUID(), Config.pylonHologramFlashDuration);
        PacketDistributor.sendToPlayer(player, packet);
    }

    
    
    
}
