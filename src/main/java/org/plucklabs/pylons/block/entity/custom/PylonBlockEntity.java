package org.plucklabs.pylons.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.plucklabs.pylons.util.PillarHelpers;
import org.plucklabs.pylons.util.PylonLevels;
import org.plucklabs.pylons.block.entity.ModBlockEntities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class PylonBlockEntity extends BlockEntity {
    private double RANGE;
    private boolean INVERTED;

    private static Set<PylonBlockEntity> LOADED_PYLONS = new HashSet<>();

    private PylonLevels tier = PylonLevels.INACTIVE;
    private ArrayList<StructureTier> structure;






    private List<EntityType<?>> BLACKLIST;

    private void _initialiseValuesForTestRun() {
        this.setRange(100);
        BLACKLIST = new ArrayList<>();
        BLACKLIST.add(EntityType.ZOMBIE);
        BLACKLIST.add(EntityType.SPIDER);
        BLACKLIST.add(EntityType.SKELETON);
        BLACKLIST.add(EntityType.SLIME);
        BLACKLIST.add(EntityType.COW);
    }




    public PylonLevels getTier(){return tier;}


    public PylonBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.PYLON_BE.get(), pos, blockState);

        _initialiseValuesForTestRun();
    }

    public void setRange(double range) {
        this.RANGE = range;
    }

    public AABB getRange() {
        BlockPos center = this.getBlockPos();
        double i = center.getX();
        double j = center.getY();
        double k = center.getZ();
        AABB effectArea = (new AABB(i, j, k, i, j, k)).inflate(RANGE);
        return effectArea;
    }

    public void setInverted(boolean inverted) {
        this.INVERTED = inverted;
    }

    public boolean isInverted() {
        return INVERTED;
    }

    public boolean checkRange(BlockPos pos) {
        AABB effectArea = getRange();
        boolean isInRange = effectArea.contains(pos.getX(), pos.getY(), pos.getZ());
        return isInRange;
    }

    public void checkStructure(ServerLevel level) {
        this.tier = PylonLevels.INACTIVE;
        if (structure == null) return;
        for(StructureTier structureTier : structure) {
            if(structureTier.check(level)) {
                this.tier = structureTier.tier;
            } else {
                return;
            }
        }
    }

    public boolean checkBlacklist(EntityType<?> entityType) {
        return isInverted() != BLACKLIST.contains(entityType);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        cachePillars();
        LOADED_PYLONS.add(this);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        LOADED_PYLONS.remove(this);
    }

    public static Set<PylonBlockEntity> getLoadedPylons() {
        return LOADED_PYLONS;
    }


    private void cachePillars() {
        structure = new ArrayList<>();
        BlockPos pos = this.getBlockPos();
        var x = pos.getX();
        var y = pos.getY();
        var z = pos.getZ();

        var tier1Offset = 5;
        var tier2Offset = 6;

        Set<Pillar> tier1Pillars = new HashSet<>();
        tier1Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x,y,z+tier1Offset)));
        tier1Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x,y,z-tier1Offset)));
        tier1Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x+tier1Offset,y,z)));
        tier1Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x-tier1Offset,y,z)));

        structure.add(new StructureTier(tier1Pillars, PylonLevels.LEVEL_1));

        Set<Pillar> tier2Pillars = new HashSet<>();
        tier2Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x+tier2Offset,y,z+tier2Offset)));
        tier2Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x-tier2Offset,y,z-tier2Offset)));
        tier2Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x-tier2Offset,y,z+tier2Offset)));
        tier2Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x+tier2Offset,y,z-tier2Offset)));

        structure.add(new StructureTier(tier2Pillars, PylonLevels.LEVEL_2));
        System.out.println(structure);
    }



    public record Pillar(Set<BlockPos> positions){
        public boolean check(ServerLevel level) {
            boolean passed = true;
            for(BlockPos pos : positions) {
                if(level.getBlockState(pos).getBlock() == Blocks.IRON_BLOCK) continue;
                passed = false;
                break;
            }



            return passed;
        }




    }
    public record StructureTier(Set<Pillar> pillars, PylonLevels tier){
        public boolean check(ServerLevel level) {
            boolean passed = true;
            for(Pillar pillar : pillars) {
                if(pillar.check(level)) continue;
                passed = false;
                break;
            }
            return passed;
        }
    }

}
