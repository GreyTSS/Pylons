package org.plucklabs.pylons;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = Pylons.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue PYLON_HOLOGRAM_DETECTION_RANGE = BUILDER.defineInRange("pylon-hologram-detection-range", 8,2,10);
    private static final ModConfigSpec.IntValue PYLON_LEVEL_1_RANGE = BUILDER.defineInRange("level-1-pylon-range", 64,16,2560);
    private static final ModConfigSpec.IntValue PYLON_LEVEL_2_RANGE = BUILDER.defineInRange("level-2-pylon-range", 128,32,2560);
    private static final ModConfigSpec.IntValue PYLON_LEVEL_3_RANGE = BUILDER.defineInRange("level-3-pylon-range", 192,48,2560);
    private static final ModConfigSpec.IntValue PYLON_HOLOGRAM_FLASH_DURATION = BUILDER.defineInRange("hologram_flash_duration", 60, 1, 600);


    static final ModConfigSpec SPEC = BUILDER.build();

    public static int pylonHologramDetectionRange;
    public static int pylonRangeLevel1;
    public static int pylonRangeLevel2;
    public static int pylonRangeLevel3;
    public static int pylonHologramFlashDuration;



    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        pylonRangeLevel1 = PYLON_LEVEL_1_RANGE.get();
        pylonRangeLevel2 = PYLON_LEVEL_2_RANGE.get();
        pylonRangeLevel3 = PYLON_LEVEL_3_RANGE.get();

        pylonHologramDetectionRange = PYLON_HOLOGRAM_DETECTION_RANGE.get();
        pylonHologramFlashDuration = PYLON_HOLOGRAM_FLASH_DURATION.get();
    }
}
