package org.plucklabs.pylons.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.plucklabs.pylons.Config;
import org.plucklabs.pylons.event.PylonHologramServerTickEvent;
import org.plucklabs.pylons.util.ModTags;
import org.plucklabs.pylons.util.PillarHelpers;
import org.plucklabs.pylons.util.PylonLevels;
import org.plucklabs.pylons.block.entity.ModBlockEntities;

import java.util.*;


public class PylonBlockEntity extends BlockEntity {
    private double RANGE;
    private boolean INVERTED;

    private static final Set<PylonBlockEntity> LOADED_PYLONS = new HashSet<>();

    private PylonLevels tier = PylonLevels.INACTIVE;
    private ArrayList<StructureTier> structure;
    private Map<PylonLevels,StructureTier> tierMap = new HashMap<>();






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
        AABB effectArea = (new AABB(i, j, k, i, j, k)).inflate(tier.range);
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
    public void setRemoved() {
        super.setRemoved();
        LOADED_PYLONS.remove(this);
    }

    public static Set<PylonBlockEntity> getLoadedPylons() {
        return LOADED_PYLONS;
    }




    /**
     * Initiates nested structure checks, assigning the tier that corresponds with each check, on completion
     * @param level This method is called from a method in the block class, only on server-side,
     *              and thus passes it's serverlevel from there.
     */
    public void checkStructure(ServerLevel level) {
        PylonLevels oldTier = this.tier;


        this.tier = PylonLevels.INACTIVE;
        if (structure == null) return;
        for(StructureTier structureTier : structure) {
            if(structureTier.check(level)) {
                this.tier = structureTier.tier;
            } else {
                if(this.tier != oldTier) {
                    //This resets packets when the tier changes, so if a player is still holding a block they get the updated pillar positions :p
                    PylonHologramServerTickEvent.cache.clear();
                }
                return;
            }
        }
    }

    /**
     * Cache Pillars uses the PillarHelpers pillar prefabs, along with some hardcoded relative coordinates,
     * to create the pillar array desired in game. structure must be an ordered list, as each tier check exits early
     * on failure. Pillars must be cached each time this block is loaded. It's not a huge calculation, so I don't
     * think it's necessary to store them long-term.
     */
    public void cachePillars() {
        structure = new ArrayList<>();
        tierMap.clear();
        BlockPos pos = this.getBlockPos();
        var x = pos.getX();
        var y = pos.getY();
        var z = pos.getZ();

        var tier1Offset = 5;
        var tier2Offset = 6;

        //Tier 1.
        Set<Pillar> tier1Pillars = new HashSet<>();
        tier1Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x,y,z+tier1Offset)));
        tier1Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x,y,z-tier1Offset)));
        tier1Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x+tier1Offset,y,z)));
        tier1Pillars.add(PillarHelpers.CreateTier1Pillar(new BlockPos(x-tier1Offset,y,z)));
        StructureTier tier1Structure = new StructureTier(tier1Pillars, PylonLevels.LEVEL_1);

        structure.add(tier1Structure);
        tierMap.put(tier1Structure.tier, tier1Structure);

        //Tier 2.
        Set<Pillar> tier2Pillars = new HashSet<>();
        tier2Pillars.add(PillarHelpers.CreateTier2Pillar(new BlockPos(x+tier2Offset,y,z+tier2Offset)));
        tier2Pillars.add(PillarHelpers.CreateTier2Pillar(new BlockPos(x-tier2Offset,y,z-tier2Offset)));
        tier2Pillars.add(PillarHelpers.CreateTier2Pillar(new BlockPos(x-tier2Offset,y,z+tier2Offset)));
        tier2Pillars.add(PillarHelpers.CreateTier2Pillar(new BlockPos(x+tier2Offset,y,z-tier2Offset)));

        StructureTier tier2Structure = new StructureTier(tier2Pillars, PylonLevels.LEVEL_2);

        structure.add(tier2Structure);
        tierMap.put(tier2Structure.tier, tier2Structure);

        //Tier 3.
        Set<Pillar> tier3Pillars = new HashSet<>();
        tier3Pillars.add(PillarHelpers.CreateTier3Pillar(pos));

        StructureTier tier3Structure = new StructureTier(tier3Pillars, PylonLevels.LEVEL_3);

        structure.add(tier3Structure);
        tierMap.put(tier3Structure.tier, tier3Structure);
    }

    public Set<BlockPos> getLowestInvalidTier() {
        if(tier.ordinal() == PylonLevels.values().length - 1) {
            return new HashSet<>();

        } else {
            StructureTier nextTier = tierMap.get(PylonLevels.values()[tier.ordinal()+1]);
            if(nextTier == null) return new HashSet<>();
            Set<BlockPos> positions = new HashSet<>();
            for(Pillar pillar : nextTier.pillars) {
                positions.addAll(pillar.positions);
            }
            return positions;

        }
    }


    private AABB getHologramDetectionRange() {
        BlockPos pos = this.getBlockPos();
        double i = pos.getX();
        double j = pos.getY();
        double k = pos.getZ();
        AABB effectArea = (new AABB(i, j, k, i, j, k)).inflate(Config.pylonHologramDetectionRange);
        return effectArea;
    }

    public boolean checkHologramRange(Player player) {
        AABB effectArea = getHologramDetectionRange();
        boolean isInRange = effectArea.contains(player.getX(), player.getY(), player.getZ());
        return isInRange;
    }


    /**
     * Holds a pillar in its entirety. Validates each block before returning its own total validity
     * @param positions A set of BlockPos, stored as a division of areas the that the Pylon checks to
     *                  determine it's level.
     */
    public record Pillar(Set<BlockPos> positions){
        public boolean check(ServerLevel level) {
            boolean passed = true;
            for(BlockPos pos : positions) {
                if(level.getBlockState(pos).is(ModTags.Blocks.PILLAR_MATERIAL)) continue;
                passed = false;
                break;
            }
            return passed;
        }




    }

    /**
     * Contains a set of pillars that are checked in order to validate a tier, prescribing a level if completed.
     * @param pillars A set of Pillars, to be checked for blocks
     * @param tier A PylonLevels enum value to be assigned to the tier variable on valid.
     */
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



    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("tier", tier.getSerializedName());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tier = PylonLevels.valueOf(tag.getString("tier"));

    }
}
