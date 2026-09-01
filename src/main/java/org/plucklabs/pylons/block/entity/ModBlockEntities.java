package org.plucklabs.pylons.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.plucklabs.pylons.Pylons;
import org.plucklabs.pylons.block.ModBlocks;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Pylons.MODID);


    //Pylon Block Entity
    public static final Supplier<BlockEntityType<PylonBlockEntity>> PYLON_BE = BLOCK_ENTITIES.register(
            "pylon_be",
            () -> BlockEntityType.Builder.of(
                    PylonBlockEntity::new,
                    ModBlocks.PYLON.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}