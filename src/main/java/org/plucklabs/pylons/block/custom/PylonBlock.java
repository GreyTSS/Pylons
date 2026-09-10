package org.plucklabs.pylons.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;
import org.plucklabs.pylons.networking.packet.HologramPositions;
import org.plucklabs.pylons.util.PylonLevels;


public class PylonBlock extends BaseEntityBlock {
    public static final MapCodec<PylonBlock> CODEC = simpleCodec(PylonBlock::new);



    public PylonBlock(Properties properties) {
        super(properties);


    }

    //Temp State Check
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity entity = level.getBlockEntity(pos);
        if(!level.isClientSide) {
            if (entity instanceof PylonBlockEntity pylon) {
                pylon.checkStructure((ServerLevel) level);
                PacketDistributor.sendToPlayer((ServerPlayer) player,new HologramPositions((pylon.getTier()!=PylonLevels.LEVEL_3), pylon.getLowestInvalidTier()));
                player.sendSystemMessage(Component.literal("Level:" + pylon.getTier().getSerializedName() + "\nRange: "+pylon.getTier().range));
            }
        }


        return InteractionResult.SUCCESS;
    }



    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PylonBlockEntity(blockPos, blockState);
    }





    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {return CODEC;}

    //Rendering
    @Override
    protected RenderShape getRenderShape(BlockState state) {return RenderShape.MODEL;}


}
