package org.plucklabs.pylons.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.plucklabs.pylons.Pylons;


public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Pylons.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {


    }

}
