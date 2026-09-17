package org.plucklabs.pylons.screen.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.world.entity.EntityType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PylonMobList extends ObjectSelectionList<PylonMobEntry> {
    private final PylonScreen parentScreen;

    public PylonMobList(PylonScreen parentScreen, WidgetData widgetData) {
        super(Minecraft.getInstance(), widgetData.getWidth(), widgetData.getHeight(), widgetData.getY(), 20);
        this.parentScreen = parentScreen;
        this.setX(widgetData.getX());
        refreshList();
    }

    public void sortEntries(boolean includeSelected, boolean reversed) {
        clearEntries();
        List<EntityType<?>> ALL_MONSTERS = PylonScreen.ALL_MONSTERS;
        if (reversed) ALL_MONSTERS = ALL_MONSTERS.reversed();

        Set<EntityType<?>> BLACKLIST = parentScreen.getMenu().blockEntity.getBlackList();

        for (EntityType<?> entityType : ALL_MONSTERS) {
            if (!includeSelected && BLACKLIST.contains(entityType)) {
                continue;
            }
            this.addEntry(new PylonMobEntry(this, entityType));
        }
    }

    public void refreshList() {
        clearEntries();
        for (EntityType<?> entityType : PylonScreen.ALL_MONSTERS) {
            this.addEntry(new PylonMobEntry(this, entityType));
        }
    }

    public void selectMobType(PylonMobEntry entry) {
        setSelected(entry);
    }

    public PylonScreen getParentScreen() {
        return parentScreen;
    }
}
