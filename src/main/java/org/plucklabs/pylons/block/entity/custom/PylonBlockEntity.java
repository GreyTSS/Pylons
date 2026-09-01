package org.plucklabs.pylons.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.plucklabs.pylons.block.entity.ModBlockEntities;

import java.util.List;

public class PylonBlockEntity extends BlockEntity {
    private static int MAX_RANGE;

    private double RANGE;

    private List<EntityType<?>> BLACKLIST;



    public PylonBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.PYLON_BE.get(), pos, blockState);
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

    public static int getMaxRange() {
        return MAX_RANGE;
    }
}
