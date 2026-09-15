package org.plucklabs.pylons.screen.custom;

import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public enum WidgetData {
    SORT_BY(7, 5, 44, 24),
    SEARCH_BOX(53, 5, 124, 24),
    INCLUDE_SELECTED(179, 5, 44, 24),
    RANGE_SCROLL_BAR(7, 187, 164, 24),
    CONFIRM(173, 187, 24, 24),
    CANCEL(199, 187, 24, 24),
    MOB_LIST(7, 31, 216, 154),
    SHOW_ON_MAP(7, 187, 24, 24);

    private int x;
    private int y;
    private int width;
    private int height;

    private static int xOffset = 0;
    private static int yOffset = 0;

    public static void setOffset(int newX, int newY) {
        xOffset = newX;
        yOffset = newY;
    }

    public int getX() {
        return this.x + xOffset;
    }
    public int getY() {
        return this.y + yOffset;
    }
    public int getWidth() {
        return this.width;
    }
    public int getHeight() {
        return this.height;
    }

    public void setPos(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    public void setSize(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
    }

    private static final String[] MAP_MOD_IDs = {
            "xaeroworldmap",
            "journeymap",
            "voxelmap",
            "ftbchunks"
    };

    public static boolean compatibleMapIsInstalled() {
        boolean status = false;
        for (String id : MAP_MOD_IDs) {
            if (ModList.get().isLoaded(id)) {
                status = true;
                break;
            }
        }
        return status;
    }

    public static void loadValues() {
        if (compatibleMapIsInstalled()) {
            RANGE_SCROLL_BAR.setPos(33, 187);
            RANGE_SCROLL_BAR.setSize(138, 24);
        }
    }

    WidgetData(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
