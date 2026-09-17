package org.plucklabs.pylons.util;

import net.minecraft.util.StringRepresentable;
import org.plucklabs.pylons.Config;

import java.util.function.Supplier;

public enum PylonLevels implements StringRepresentable {
    INACTIVE("inactive", () -> 0),

    LEVEL_1("one",   () -> Config.pylonRangeLevel1),
    LEVEL_2("two",   () -> Config.pylonRangeLevel2),
    LEVEL_3("three", () -> Config.pylonRangeLevel3);

    public final Supplier<Integer> rangeSupplier;
    public int range = -1;
    public final String levelName;
    PylonLevels(String name, Supplier<Integer> rangeSupplier) {
        this.levelName = name;
        this.rangeSupplier = rangeSupplier;}


    public int range() {
        if(this.range == -1) {
            this.range = rangeSupplier.get();
        }
        return this.range;
    }

    @Override
    public String getSerializedName() {
        return this.levelName;
    }
}
