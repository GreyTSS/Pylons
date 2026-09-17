package org.plucklabs.pylons.event;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import org.plucklabs.pylons.Pylons;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;

import java.util.List;
import java.util.Set;

import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.getBlockPos;

@EventBusSubscriber(modid = Pylons.MODID)
public class MobSpawnEventHandler {
    @SubscribeEvent
    public static void onSpawnPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event) {
        if (event.getSpawnType() == MobSpawnType.SPAWNER) {
            return;
        }

        BlockPos pos = event.getPos();
        EntityType<?> entityType = event.getEntityType();
        Set<PylonBlockEntity> loadedPylons = PylonBlockEntity.getLoadedPylons();

        boolean cancelSpawn = false;
        for (PylonBlockEntity pylon : loadedPylons) {


            /*
             * This example was taken entirely from the SableCompanion compatability advice from their github readMe
             * https://github.com/ryanhcode/sable-companion
             */
            Vec3 position = pylon.getBlockPos().getBottomCenter();
            SubLevelAccess subLevelAccess = SableCompanion.INSTANCE.getContaining(pylon.getLevel(), pylon.getBlockPos());
            Boolean sublevelPresent = (subLevelAccess!=null);
            if (sublevelPresent) {

                Pose3dc pose = subLevelAccess.logicalPose();

                // Transform the position to global space
                position = pose.transformPosition(position);
            }
            //System.out.println(pylon.getRange(pylon.getBlockPos()));
            //System.out.println("Pylon Tier: "+pylon.getTier().name()+"\nBlacklist: "+ pylon.getBlackList() + "\nTarget: "+event.getEntityType());
            if((!sublevelPresent && pylon.checkRange(pos)) || (sublevelPresent && pylon.checkRangeFrom(pos, BlockPos.containing(position)))) {
                if (pylon.checkBlacklist(entityType)) {
                    cancelSpawn = true;
                    break;
                }

            }
        }

        if (cancelSpawn) {
            event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
        }
    }
}
