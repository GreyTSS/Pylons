package org.plucklabs.pylons.util;

import net.minecraft.core.BlockPos;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PillarHelpers {
    private static Set<BlockPos> squareCentered(BlockPos centerPos) {
        Set<BlockPos> square = new HashSet<>();

        for(var i = -1; i <= 1; i++) {
            for(var e = -1; e<= 1; e++) {
                square.add(new BlockPos(centerPos.getX()+e, centerPos.getY(), centerPos.getZ()+i));
            }
        }
        return square;
    }

    private static Set<BlockPos> crossCentered(BlockPos centerPos) {
        Set<BlockPos> cross = new HashSet<>();

        for(var i = -1; i <= 1; i++) {
            for(var e = -1; e<= 1; e++) {
                //Skip Corners, keep center.
                if(i!=0 && e!=0) continue;
                cross.add(new BlockPos(centerPos.getX()+e, centerPos.getY(), centerPos.getZ()+i));
            }
        }
        return cross;
    }


    public static PylonBlockEntity.Pillar CreateTier1Pillar(BlockPos origin) {
        Set<BlockPos> pillar = new HashSet<>();
        pillar.addAll(
                squareCentered(origin)
        );
        pillar.addAll(
                crossCentered(
                        new BlockPos(origin.getX(), origin.getY()+1, origin.getZ())
                )
        );

        return new PylonBlockEntity.Pillar(pillar);

    }

    public static PylonBlockEntity.Pillar CreateTier2Pillar(BlockPos origin) {
        Set<BlockPos> pillar = new HashSet<>();
        pillar.addAll(
                squareCentered(origin)
        );
        pillar.addAll(
                crossCentered(
                        new BlockPos(origin.getX(), origin.getY()+1, origin.getZ())
                )
        );
        pillar.add(new BlockPos(origin.getX(), origin.getY()+1, origin.getZ()));

        return new PylonBlockEntity.Pillar(pillar);

    }






}
