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
            //System.out.println(pylon.getRange(pylon.getBlockPos()));
            //System.out.println("Pylon Tier: "+pylon.getTier().name()+"\nBlacklist: "+ pylon.getBlackList() + "\nTarget: "+event.getEntityType());
            if (pylon.checkRange(pos)) {
                if (pylon.checkBlacklist(entityType)) {
                    cancelSpawn = true;
                    System.out.println("Suppressed!");
                    break;
                } else {
                    System.out.println("In range but unlisted");
                }

            }
        }

        if (cancelSpawn) {
            event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
        }
    }
}
