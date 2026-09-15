package org.plucklabs.pylons.screen.custom;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class PylonButton extends Button {
    protected PylonButton(WidgetData widgetData, Component message, OnPress onPress) {
        super(widgetData.getX(), widgetData.getY(), widgetData.getWidth(), widgetData.getHeight(), message, onPress, DEFAULT_NARRATION);
    }
}
