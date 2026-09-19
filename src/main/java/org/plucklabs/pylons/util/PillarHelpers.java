package org.plucklabs.pylons.util;

import net.minecraft.core.BlockPos;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;

import java.util.ArrayList;
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
        pillar.add(new BlockPos(origin.getX(), origin.getY()+2, origin.getZ()));
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

    public static ArrayList<PylonBlockEntity.StructureTier> createStructure(BlockPos pos) {

        ArrayList<PylonBlockEntity.StructureTier> structure = new ArrayList<>();


        var x = pos.getX();
        var y = pos.getY();
        var z = pos.getZ();

        var tier1Offset = 5;
        var tier2Offset = 6;


        structure.add(createTier1(x,y,z, tier1Offset));
        structure.add(createTier2(x,y,z, tier2Offset));
        structure.add(createTier3(pos));
        return structure;
    }


    public static Set<BlockPos> getPlacementPillars(BlockPos pos) {
        Set<BlockPos> blocks = new HashSet<>();
        PylonBlockEntity.StructureTier tier = createTier1(pos);
        for(PylonBlockEntity.Pillar pillar : tier.pillars()) {
            blocks.addAll(pillar.positions());
        }
        return blocks;
    }


    public static PylonBlockEntity.StructureTier createTier1(BlockPos pos) {return createTier1(pos.getX(), pos.getY(), pos.getZ(), 5);}
    public static PylonBlockEntity.StructureTier createTier1(int x, int y, int z, int offset) {
        //Tier 1.
        Set<PylonBlockEntity.Pillar> tier1Pillars = new HashSet<>();
        tier1Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x,y,z+offset)));
        tier1Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x,y,z-offset)));
        tier1Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x+offset,y,z)));
        tier1Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x-offset,y,z)));
        return new PylonBlockEntity.StructureTier(tier1Pillars, PylonLevels.LEVEL_1);

    }


    public static PylonBlockEntity.StructureTier createTier2(int x, int y, int z, int offset) {
        //Tier 2.
        Set<PylonBlockEntity.Pillar> tier2Pillars = new HashSet<>();
        tier2Pillars.add(PillarHelpers.CreateTier2Pillar(new BlockPos(x+offset,y,z+offset)));
        tier2Pillars.add(PillarHelpers.CreateTier2Pillar(new BlockPos(x-offset,y,z-offset)));
        tier2Pillars.add(PillarHelpers.CreateTier2Pillar(new BlockPos(x-offset,y,z+offset)));
        tier2Pillars.add(PillarHelpers.CreateTier2Pillar(new BlockPos(x+offset,y,z-offset)));
        return new PylonBlockEntity.StructureTier(tier2Pillars, PylonLevels.LEVEL_2);
    }

    public static PylonBlockEntity.StructureTier createTier3(BlockPos pos) {
        Set<PylonBlockEntity.Pillar> tier3Pillars = new HashSet<>();
        tier3Pillars.add(PillarHelpers.CreateTier3Pillar(pos));
        return new PylonBlockEntity.StructureTier(tier3Pillars, PylonLevels.LEVEL_3);
    }









}
