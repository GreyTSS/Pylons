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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.plucklabs.pylons.Config;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;
import org.plucklabs.pylons.event.PylonHologramServerTickEvent;
import org.plucklabs.pylons.networking.packet.HologramPositions;
import org.plucklabs.pylons.util.ModTags;
import org.plucklabs.pylons.util.PylonLevels;


public class PylonBlock extends BaseEntityBlock {

    public static final EnumProperty<PylonLevels> TIER = EnumProperty.create("tier", PylonLevels.class);

    public static final MapCodec<PylonBlock> CODEC = simpleCodec(PylonBlock::new);



    public PylonBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(getStateDefinition().any()
            .setValue(TIER, PylonLevels.INACTIVE)
        );

    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TIER);
    }




    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if(!level.isClientSide()) level.scheduleTick(pos, this, Config.pylonStructureCheckFrequency);
    }

    //Temp State Check
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity entity = level.getBlockEntity(pos);
        if(!level.isClientSide) {
            if (entity instanceof PylonBlockEntity pylon) {
                BlockState blockState = Blocks.IRON_BLOCK.defaultBlockState();
                if(player.getItemInHand(player.getUsedItemHand()).getItem() instanceof BlockItem blockItem && blockItem.getBlock().defaultBlockState().is(ModTags.Blocks.PILLAR_MATERIAL)) {
                    blockState = blockItem.getBlock().defaultBlockState();
                }
                var packet = new HologramPositions((pylon.getTier()!=PylonLevels.LEVEL_3), pylon.getLowestInvalidTier(), blockState);
                pylon.checkStructure((ServerLevel) level);
                PacketDistributor.sendToPlayer((ServerPlayer) player, packet);
                PylonHologramServerTickEvent.timer.put(player.getUUID(), Config.pylonHologramFlashDuration);
                player.sendSystemMessage(Component.literal("Level:" + pylon.getTier().getSerializedName() + "\nRange: "+pylon.getTier().range+"\nIn Holo Range?: "+pylon.checkHologramRange(player)));
            }
        }


        return InteractionResult.SUCCESS;
    }



    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        BlockEntity entity = level.getBlockEntity(pos);
        if(!(entity instanceof PylonBlockEntity pylon)) {
            System.out.println("return");
            return;
        }

        System.out.println("Check");
        pylon.checkStructure(level);
        level.setBlock(pos, level.getBlockState(pos).setValue(TIER, pylon.getTier()), UPDATE_CLIENTS);
        level.scheduleTick(pos, this, Config.pylonStructureCheckFrequency);



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
