package org.plucklabs.pylons.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import org.plucklabs.pylons.Pylons;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;

@EventBusSubscriber(modid = Pylons.MODID)
public class MobSpawnEventHandler {
    @SubscribeEvent
    public static void onMobPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event) {
        BlockPos pos = event.getPos();
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        int maxRange = PylonBlockEntity.getMaxRange();

        int minX = i - maxRange;
        int minY = j - maxRange;
        int minZ = k - maxRange;
        int maxX = i + maxRange;
        int maxY = j + maxRange;
        int maxZ = k + maxRange;

        ServerLevelAccessor level = event.getLevel();

        boolean isInRange = false;
        boolean isBlackListed;

        outerLoop:
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    BlockEntity blockEntity = level.getBlockEntity(blockPos);

                    if (blockEntity instanceof PylonBlockEntity pylonBE) {
                        isInRange = pylonBE.checkRange(pos);

                        if (isInRange) {
                            isBlackListed = pylonBE.checkBlacklist(event.getEntityType());

                            if (!isBlackListed) {
                                isInRange = false;
                            }
                            else {
                                break outerLoop;
                            }
                        }
                    }
                }
            }
        }

        // If isBlackListed is set to true, isInRange must be true
        // If isBlackListed is set to false, isInRange is also set to false
        // Therefore isInRange == isBlackListed when out of the loop
        // Thus a single condition is necessary
        if (isInRange) {
            event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
        }
    }
}
