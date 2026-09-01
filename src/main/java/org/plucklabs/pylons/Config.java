package org.plucklabs.pylons;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = Pylons.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();


    private static final ModConfigSpec.IntValue PYLON_LEVEL_1_RANGE = BUILDER.defineInRange("level-1-pylon-range", 32,16,2560);
    private static final ModConfigSpec.IntValue PYLON_LEVEL_2_RANGE = BUILDER.defineInRange("level-2-pylon-range", 64,32,2560);
    private static final ModConfigSpec.IntValue PYLON_LEVEL_3_RANGE = BUILDER.defineInRange("level-3-pylon-range", 72,48,2560);
    private static final ModConfigSpec.IntValue PYLON_LEVEL_4_RANGE = BUILDER.defineInRange("level-4-pylon-range", 128,64,2560);
    private static final ModConfigSpec.IntValue PYLON_LEVEL_5_RANGE = BUILDER.defineInRange("level-5-pylon-range", 196,70,2560);



    static final ModConfigSpec SPEC = BUILDER.build();



    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    }
}
