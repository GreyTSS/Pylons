package org.plucklabs.pylons;

import net.minecraft.util.StringRepresentable;

public enum PylonLevels implements StringRepresentable {
    INACTIVE("inactive", 0),

    LEVEL_1("one",   Config.pylonRangeLevel1),
    LEVEL_2("two",   Config.pylonRangeLevel2),
    LEVEL_3("three", Config.pylonRangeLevel3);

    public final int range;
    public final String levelName;
    PylonLevels(String name, int range) {
        this.levelName = name;
        this.range = range;}


    public int range() {return range;}

    @Override
    public String getSerializedName() {
        return this.levelName;
    }
}
