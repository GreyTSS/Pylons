package org.plucklabs.pylons.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import org.plucklabs.pylons.Pylons;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = Pylons.MODID)
public class MobSpawnEventHandler {

    /**
     *
     *
     * @param event
     */
    @SubscribeEvent
    public static void onSpawnPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event) {
        BlockPos pos = event.getPos();
        EntityType<?> entityType = event.getEntityType();
        Set<PylonBlockEntity> loadedPylons = PylonBlockEntity.getLoadedPylons();

        boolean cancelSpawn = false;
        for (PylonBlockEntity pylon : loadedPylons) {
            if (pylon.checkRange(pos) && pylon.checkBlacklist(entityType)) {
                cancelSpawn = true;
                break;
            }
        }

        if (cancelSpawn) {
            event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
        }
    }
}
