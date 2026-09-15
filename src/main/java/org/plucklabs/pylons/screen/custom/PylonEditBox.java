package org.plucklabs.pylons.screen.custom;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class PylonEditBox extends EditBox {
    public PylonEditBox(Font font, WidgetData widgetData, Component message) {
        super(font, widgetData.getX(), widgetData.getY(), widgetData.getWidth(), widgetData.getHeight(), message);
    }
}
