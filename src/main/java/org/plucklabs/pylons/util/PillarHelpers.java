package org.plucklabs.pylons.util;

import net.minecraft.core.BlockPos;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PillarHelpers {

    /*
    Pillar Building Blocks
     */
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




    /*
    Pillar Prefabs
     */
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


    public static PylonBlockEntity.Pillar CreateTier3Pillar(BlockPos origin) {
        var x = origin.getX();
        var y = origin.getY();
        var z = origin.getZ();
        Set<BlockPos> pillar = new HashSet<>();
        pillar.addAll(
                squareCentered(origin)
        );

        //Skips over 0 for I and E, meaning my offsets from the origin will be some combination of +/-1. This ensures
        //I get only corners, and allowing me to nab the extra spikes on the outside of pillar 3 :p
        for(var i = -1; i <= 1; i+=2) {
            for(var e = -1; e<=1; e+=2) {
                pillar.addAll(crossCentered(new BlockPos(x+e,y,z+i)));

                //Spikeys
                pillar.add(new BlockPos(x+(e*2), y, z+(i*2)));
                pillar.add(new BlockPos(x+(e*2), y+1, z+(i*2)));
            }
        }

        //Make sure that it's not asking for a block in the same space as the pylon.
        pillar.remove(origin);

        return new PylonBlockEntity.Pillar(pillar);



    }






}
