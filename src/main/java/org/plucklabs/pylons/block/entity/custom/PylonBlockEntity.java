package org.plucklabs.pylons.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.plucklabs.pylons.block.entity.ModBlockEntities;

import java.util.Set;

public class PylonBlockEntity extends BlockEntity {





    public PylonBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.PYLON_BE.get(), pos, blockState);
    }









}
