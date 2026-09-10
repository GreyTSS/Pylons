package org.plucklabs.pylons.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.plucklabs.pylons.Pylons;

public class ModTags {

    public static class Blocks {
        public static final TagKey<Block> PILLAR_MATERIAL = createTag("pillar_material");
        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(Pylons.MODID, name));
        }
    }

    public static class Items {
        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Pylons.MODID, name));
        }
    }
}
