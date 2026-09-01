package org.plucklabs.pylons.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.plucklabs.pylons.Pylons;

public class ModItems {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.createItems(Pylons.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
