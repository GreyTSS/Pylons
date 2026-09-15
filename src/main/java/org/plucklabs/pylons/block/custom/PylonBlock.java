package org.plucklabs.pylons.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
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
import org.jetbrains.annotations.Nullable;
import org.plucklabs.pylons.PylonLevels;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;
import org.plucklabs.pylons.screen.custom.PylonGUI;


public class PylonBlock extends BaseEntityBlock {
    public static final MapCodec<PylonBlock> CODEC = simpleCodec(PylonBlock::new);

    //Holds the level and allows you to use .range() to get range amount for current level
    public static final EnumProperty<PylonLevels> LEVEL = EnumProperty.create("level", PylonLevels.class);


    public PylonBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any()
                .setValue(LEVEL, PylonLevels.INACTIVE));

    }



    //Set Default level
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {builder.add(LEVEL);}

    //Temp State Check
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // player.sendSystemMessage(Component.literal("LeveL:" + state.getValue(LEVEL).getSerializedName()));
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof PylonBlockEntity blockEntity) {
            PylonGUI.openMenu(player, blockEntity);
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
