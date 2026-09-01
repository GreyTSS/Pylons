package org.plucklabs.pylons.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.plucklabs.pylons.block.ModBlocks;

import java.util.concurrent.CompletableFuture;


public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.PYLON.get())
                .pattern("EDE")
                .pattern("EBE")
                .pattern("SSS")
                .define('E', Items.END_STONE)
                .define('D', Items.DIAMOND)
                .define('B', Items.DRAGON_BREATH)
                .define('S', Items.SMOOTH_STONE)
                .unlockedBy("has_dragon_breath", has(Items.DRAGON_BREATH)).save(recipeOutput);

    }
}
