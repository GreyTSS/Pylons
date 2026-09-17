package org.plucklabs.pylons.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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
import org.plucklabs.pylons.screen.custom.PylonGUI;


public class PylonBlock extends BaseEntityBlock {

    public static final EnumProperty<PylonLevels> TIER = EnumProperty.create("tier", PylonLevels.class);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final MapCodec<PylonBlock> CODEC = simpleCodec(PylonBlock::new);

    //Holds the level and allows you to use .range() to get range amount for current level
    public static final EnumProperty<PylonLevels> LEVEL = EnumProperty.create("level", PylonLevels.class);


    public PylonBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(getStateDefinition().any()
            .setValue(TIER, PylonLevels.INACTIVE)
            .setValue(POWERED, false)
        );

    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TIER);
        builder.add(POWERED);
    }


    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if(!level.isClientSide()) level.scheduleTick(pos, this, Config.pylonStructureCheckFrequency);
        if(level.hasNeighborSignal(pos)) {
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(POWERED, true));
        }

    }



    //Temp State Check
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // player.sendSystemMessage(Component.literal("LeveL:" + state.getValue(LEVEL).getSerializedName()));
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof PylonBlockEntity blockEntity) {
            PylonGUI.openMenu(player, blockEntity);
        }
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

            }
        }


        return InteractionResult.SUCCESS;
    }


    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        BlockState blockState = level.getBlockState(pos);
        if(level instanceof ClientLevel cLevel && blockState.hasProperty(TIER)) {
            PylonLevels tier = blockState.getValue(TIER);
            if(tier == PylonLevels.INACTIVE) return;
            if(tier.ordinal() > PylonLevels.INACTIVE.ordinal()) {
                particleCircle(pos,5d,10d,cLevel,random);
            }
            if(tier.ordinal() > PylonLevels.LEVEL_1.ordinal()) {
                particleCircle(pos,8d,20d,cLevel,random);
            }
            if(tier.ordinal() > PylonLevels.LEVEL_2.ordinal()) {
                particleCircle(pos,2d,5d,cLevel,random);
            }
        }


    }


    private static void particleCircle(BlockPos origin, double radius, double divisions, ClientLevel level, RandomSource random) {
        for(double i = 0; i < divisions; i++) {
            double angle = (2 * Math.PI) * (i / divisions);
            double x = origin.getX() + radius * Math.cos(angle);
            double z = origin.getZ() + radius * Math.sin(angle);

            double y = origin.getY() + 1.5;

            level.addParticle(ParticleTypes.SOUL, x+(random.nextDouble()),y+(random.nextDouble()*.3),z+(random.nextDouble()), random.nextDouble()*.015, random.nextDouble()*.015, random.nextDouble()*.015);

        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        BlockEntity entity = level.getBlockEntity(pos);
        if(!(entity instanceof PylonBlockEntity pylon)) {
            return;
        }


        pylon.checkStructure(level);
        level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(TIER, pylon.getTier()));
        level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(POWERED, level.hasNeighborSignal(pos)));
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
