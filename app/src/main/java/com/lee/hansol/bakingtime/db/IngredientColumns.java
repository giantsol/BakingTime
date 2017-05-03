package com.lee.hansol.bakingtime.db;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface IngredientColumns {
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(INTEGER) @NotNull String RECIPE_ID = "recipe_id";
    @DataType(TEXT) @NotNull String NAME = "name";
    @DataType(INTEGER) @NotNull String QUANTITY = "quantity";
    @DataType(TEXT) @NotNull String MEASURE_UNIT = "measure_unit";
}
