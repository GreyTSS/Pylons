package org.plucklabs.pylons.screen.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

public class PylonMobEntry extends ObjectSelectionList.Entry<PylonMobEntry> {
    private final PylonScreen parentScreen;
    private final EntityType<?> entityType;
    private final PylonMobList mobList;

    private final Component entityName;

    public PylonMobEntry(PylonMobList mobList, EntityType<?> entityType) {
        this.mobList = mobList;
        this.entityType = entityType;
        this.parentScreen = mobList.getParentScreen();
        this.entityName = entityType.getDescription();
    }

    @Override
    public Component getNarration() {
        return entityName;
    }

    @Override
    public void render(GuiGraphics guiGraphics,
                       int index,
                       int top, int left,
                       int width, int height,
                       int mouseX, int mouseY,
                       boolean hovering,
                       float partialTick)
    {
        Minecraft mc = Minecraft.getInstance();
        guiGraphics.drawString(mc.font, entityName, left, top, 0xFFFFFF);
    }
}
