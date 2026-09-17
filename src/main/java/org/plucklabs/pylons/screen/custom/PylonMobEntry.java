package org.plucklabs.pylons.screen.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

public class PylonMobEntry extends ObjectSelectionList.Entry<PylonMobEntry> {
    private final EntityType<?> entityType;
    private final PylonMobList mobList;
    private final Component entityName;

    private boolean isBlacklisted;

    public PylonMobEntry(PylonMobList mobList, EntityType<?> entityType, boolean isBlacklisted) {
        this.mobList = mobList;
        this.entityType = entityType;
        this.entityName = entityType.getDescription();
        this.isBlacklisted = isBlacklisted;
    }

    @Override
    public boolean mouseClicked(double p_331676_, double p_330254_, int p_331536_) {
        boolean result = super.mouseClicked(p_331676_, p_330254_, p_331536_);
        isBlacklisted = !isBlacklisted;
        mobList.toggleBlacklist(entityType);
        return result;
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

        if (isBlacklisted) guiGraphics.drawString(mc.font, entityName, left, top, 0xFF0000);
        else guiGraphics.drawString(mc.font, entityName, left, top, 0xFFFFFF);
    }
}
