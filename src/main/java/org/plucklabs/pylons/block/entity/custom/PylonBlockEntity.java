package org.plucklabs.pylons.block.entity.custom;

import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.plucklabs.pylons.Config;
import org.plucklabs.pylons.event.PylonHologramServerTickEvent;
import org.plucklabs.pylons.util.ModTags;
import org.plucklabs.pylons.util.PillarHelpers;
import org.plucklabs.pylons.util.PylonLevels;
import org.plucklabs.pylons.block.entity.ModBlockEntities;

import java.util.*;
import java.util.stream.Collectors;


public class PylonBlockEntity extends BlockEntity {
    private double RANGE;
    private boolean INVERTED;

    private static final Set<PylonBlockEntity> LOADED_PYLONS = new HashSet<>();

    private PylonLevels tier = PylonLevels.INACTIVE;
    private ArrayList<StructureTier> structure;
    private Map<PylonLevels,StructureTier> tierMap = new HashMap<>();

    private Set<EntityType<?>> BLACKLIST = new HashSet<>();

    public void _initialiseValuesForTestRun() {
        this.setRange(100);
        BLACKLIST = new HashSet<>();
        BLACKLIST.add(EntityType.ZOMBIE);
        BLACKLIST.add(EntityType.SPIDER);
        BLACKLIST.add(EntityType.SKELETON);
        BLACKLIST.add(EntityType.SLIME);
        BLACKLIST.add(EntityType.COW);
        this.setChanged();
    }




    public PylonLevels getTier(){return tier;}


    public PylonBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.PYLON_BE.get(), pos, blockState);
    }

    public void setRange(double range) {
        this.RANGE = range;
    }

    public AABB getRange(BlockPos origin) {;
        double range = this.tier.range();
        return new AABB(origin).inflate(range);
    }

    public void setInverted(boolean inverted) {
        this.INVERTED = inverted;
    }

    public boolean isInverted() {
        return INVERTED;
    }

    /*
     * Turned this into a sub method so that it can retain simplicity while allowing for range to be determined from a different origin
     * (for the sake of Sable compatibility)
     */
    public boolean checkRange(BlockPos pos) {
        return checkRangeFrom(pos, this.getBlockPos());
    }

    /*
     * Essentially the same as the old checkRange function, grabbing the bounding box relative to the supplied origin.
     * Make note of the .getX/Y/Z() functions returning ints. Turned the full position into a bounding box with the hopes it
     * would negate spawns that are partially inside the range but the origin point may not be.
     */
    public boolean checkRangeFrom(BlockPos pos, BlockPos origin) {
        int distX = Math.abs(pos.getX() - origin.getX());
        int distY = Math.abs(pos.getY() - origin.getY());
        return distX * distX + distY * distY <= RANGE * RANGE;
    }


    public void blacklistMob(EntityType<?> entityType) {
        BLACKLIST.add(entityType);
        this.setChanged();
    }

    public boolean checkBlacklist(EntityType<?> entityType) {
        return isInverted() != BLACKLIST.contains(entityType);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        cachePillars();
        LOADED_PYLONS.add(this);
        //_initialiseValuesForTestRun();
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
    public void checkStructure(ServerLevel level, ArrayList<StructureTier> struct) {
        PylonLevels oldTier = this.tier;
        this.tier = PylonLevels.INACTIVE;
        var blockState = level.getBlockState(getBlockPos());
        if(!blockState.hasProperty(BlockStateProperties.POWERED) || !blockState.getValue(BlockStateProperties.POWERED)) {
            if (struct == null) return;
            for (StructureTier structureTier : struct) {
                if (structureTier.check(level)) {
                    this.tier = structureTier.tier;
                } else {
                    if (this.tier != oldTier) {
                        //This resets packets when the tier changes, so if a player is still holding a block they get the updated pillar positions :p
                        PylonHologramServerTickEvent.cache.clear();
                    }
                    return;
                }
            }
        }
    }

    public void checkStructure(ServerLevel level) {
        checkStructure(level, structure);
    }


    /**
     * Cache Pillars uses the PillarHelpers pillar prefabs, along with some hardcoded relative coordinates,
     * to create the pillar array desired in game. structure must be an ordered list, as each tier check exits early
     * on failure. Pillars must be cached each time this block is loaded. It's not a huge calculation, so I don't
     * think it's necessary to store them long-term.
     */
    public void cachePillars() {
        //structure = new ArrayList<>();
        tierMap.clear();
        BlockPos pos = this.getBlockPos();
        structure = PillarHelpers.createStructure(pos);
        for(StructureTier tier : structure) {
            tierMap.put(tier.tier,tier);
        }
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

    public void setBlacklist(Set<EntityType<?>> blacklist) {
        this.BLACKLIST.clear();
        this.BLACKLIST.addAll(blacklist);
        this.setChanged();
    }

    public Set<EntityType<?>> getBlackList() {
        return Set.copyOf(BLACKLIST);
    }

    public Set<EntityType<?>> getMutableBlacklist() {
        return new HashSet<>(BLACKLIST);
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
        System.out.println("Current Blacklist Pre Save"+BLACKLIST);
        ListTag list = new ListTag();
        for (EntityType<?> entityType : BLACKLIST) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            list.add(StringTag.valueOf(id.toString()));
            //Minecraft.getInstance().player.sendSystemMessage(Component.literal(id.toString()));
        }
        System.out.println("Saved: "+list);
        tag.put("entities", list);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        Set<EntityType<?>> nbtBlacklist = new HashSet<>();
        Set<String> nbtBlacklistDisplay = new HashSet<>();
        ListTag list = tag.getList("entities", Tag.TAG_STRING);

        for (Tag sTag : list) {
            ResourceLocation id = ResourceLocation.parse(sTag.getAsString());
            //System.out.println(BuiltInRegistries.ENTITY_TYPE.containsKey(id));
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(id);
            nbtBlacklist.add(entityType);
            nbtBlacklistDisplay.add(entityType.getDescriptionId());
        }
        System.out.println("Loaded" + nbtBlacklistDisplay);
        setBlacklist(nbtBlacklist);
    }
}
