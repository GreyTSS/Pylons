package org.plucklabs.pylons.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.plucklabs.pylons.block.entity.ModBlockEntities;

import java.util.ArrayList;
import java.util.List;

public class PylonBlockEntity extends BlockEntity {
    private double RANGE;

    private static List<PylonBlockEntity> LOADED_PYLONS = new ArrayList<>();

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



    public PylonBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.PYLON_BE.get(), pos, blockState);

        _initialiseValuesForTestRun();
    }

    public void setRange(double range) {
        RANGE = range;
    }

    public AABB getRange() {
        BlockPos center = this.getBlockPos();
        double i = center.getX();
        double j = center.getY();
        double k = center.getZ();
        AABB effectArea = (new AABB(i, j, k, i, j, k)).inflate(RANGE);
        return effectArea;
    }



    public boolean checkRange(BlockPos pos) {
        AABB effectArea = getRange();
        boolean isInRange = effectArea.contains(pos.getX(), pos.getY(), pos.getZ());
        return isInRange;
    }

    public boolean checkBlacklist(EntityType<?> entityType) {
        return BLACKLIST.contains(entityType);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        LOADED_PYLONS.add(this);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        LOADED_PYLONS.remove(this);
    }

    public static List<PylonBlockEntity> getLoadedPylons() {
        return LOADED_PYLONS;
    }
}
