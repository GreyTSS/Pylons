package org.plucklabs.pylons.util;

import net.minecraft.util.StringRepresentable;
import org.plucklabs.pylons.Config;

import java.util.function.Supplier;

public enum PylonLevels implements StringRepresentable {
    INACTIVE("inactive", () -> 0),

    LEVEL_1("one",   () -> Config.pylonRangeLevel1),
    LEVEL_2("two",   () -> Config.pylonRangeLevel2),
    LEVEL_3("three", () -> Config.pylonRangeLevel3);

    public final Supplier<Integer> range;
    public final String levelName;
    PylonLevels(String name, Supplier<Integer> range) {
        this.levelName = name;
        this.range = range;}


    public int range() {return this.range.get();}

    @Override
    public String getSerializedName() {
        return this.levelName;
    }
}
