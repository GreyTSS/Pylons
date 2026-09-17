package org.plucklabs.pylons.screen.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.world.entity.EntityType;
import org.plucklabs.pylons.block.entity.custom.PylonBlockEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PylonMobList extends ObjectSelectionList<PylonMobEntry> {
    private final PylonScreen parentScreen;
    private final Set<EntityType<?>> blacklist;

    public PylonMobList(PylonScreen parentScreen, WidgetData widgetData) {
        super(Minecraft.getInstance(), widgetData.getWidth(), widgetData.getHeight(), widgetData.getY(), 20);
        this.parentScreen = parentScreen;
        this.blacklist = parentScreen.getMenu().blockEntity.getMutableBlacklist();
        this.setX(widgetData.getX());
        refreshList();
    }

    public void sortEntries(boolean includeSelected, boolean reversed) {
        clearEntries();
        List<EntityType<?>> ALL_MONSTERS = PylonScreen.ALL_MONSTERS;
        if (reversed) ALL_MONSTERS = ALL_MONSTERS.reversed();

        for (EntityType<?> entityType : ALL_MONSTERS) {
            if (!includeSelected && blacklist.contains(entityType)) {
                continue;
            }
            this.addEntry(new PylonMobEntry(this, entityType, blacklist.contains(entityType)));
        }
    }

    public void refreshList() {
        clearEntries();
        for (EntityType<?> entityType : PylonScreen.ALL_MONSTERS) {
            this.addEntry(new PylonMobEntry(this, entityType, blacklist.contains(entityType)));
        }
    }

    public void selectMobType(PylonMobEntry entry) {
        setSelected(entry);
    }

    public PylonScreen getParentScreen() {
        return parentScreen;
    }

    public Set<EntityType<?>> getBlacklist() {
        return blacklist;
    }

    public void toggleBlacklist(EntityType<?> entityType) {
        if (blacklist.contains(entityType)) blacklist.remove(entityType);
        else blacklist.add(entityType);
    }
}
