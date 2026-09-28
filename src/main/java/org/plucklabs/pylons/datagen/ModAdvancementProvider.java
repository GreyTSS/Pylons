package org.plucklabs.pylons.datagen;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.RecipeCraftedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.plucklabs.pylons.Pylons;
import org.plucklabs.pylons.block.ModBlocks;
import org.plucklabs.pylons.item.ModItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {
    public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new AdvancementGenerator()));
    }

    public static final class AdvancementGenerator implements AdvancementProvider.AdvancementGenerator {

        @Override
        public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {
            Advancement.Builder builder = Advancement.Builder.advancement();
            builder.parent(AdvancementSubProvider.createPlaceholder("minecraft:end/dragon_breath"));

            builder.display(
                    new ItemStack(ModBlocks.PYLON.get()),
                    Component.translatable("advancements.pylon.craft_pylon.title"),
                    Component.translatable("advancements.pylon.craft_pylon.description"),
                    null,
                    AdvancementType.TASK,
                    true,
                    true,
                    false
            );

            builder.rewards(AdvancementRewards.Builder.experience(100));
            builder.addCriterion("craft_pylon", RecipeCraftedTrigger.TriggerInstance.craftedItem(ModBlocks.PYLON.getId()));
            builder.requirements(AdvancementRequirements.allOf(List.of("craft_pylon")));
            builder.save(consumer, ResourceLocation.fromNamespaceAndPath(Pylons.MODID, "pylon_advancement"), existingFileHelper);
        }
    }

}
