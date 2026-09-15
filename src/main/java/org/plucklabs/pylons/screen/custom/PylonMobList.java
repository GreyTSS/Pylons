package org.plucklabs.pylons.screen.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.world.entity.EntityType;

public class PylonMobList extends ObjectSelectionList<PylonMobEntry> {
    private final PylonScreen parentScreen;

    public PylonMobList(PylonScreen parentScreen, WidgetData widgetData) {
        super(Minecraft.getInstance(), widgetData.getWidth(), widgetData.getHeight(), widgetData.getY(), 20);
        this.parentScreen = parentScreen;
        this.setX(widgetData.getX());
        refreshList();
    }

    public void refreshList() {
        clearEntries();
        for (EntityType<?> entityType : parentScreen.ALL_MONSTERS) {
            this.addEntry(new PylonMobEntry(this, entityType));
        }
    }

    public void selectMobType(PylonMobEntry entry) {
        setSelected(entry);
        parentScreen.selectMobType(entry);
    }

    public PylonScreen getParentScreen() {
        return parentScreen;
    }
}
