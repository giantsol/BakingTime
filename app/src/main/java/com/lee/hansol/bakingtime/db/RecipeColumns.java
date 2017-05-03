package com.lee.hansol.bakingtime.db;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.Constraints;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.UniqueConstraint;

import static net.simonvt.schematic.annotation.ConflictResolutionType.REPLACE;
import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

@Constraints(
    unique = @UniqueConstraint(
            name = "UNIQUE_RECIPE_ID",
            columns = {RecipeColumns.RECIPE_ID},
            onConflict = REPLACE)
)
public interface RecipeColumns {
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(INTEGER) @NotNull String RECIPE_ID = "recipe_id";
    @DataType(TEXT) @NotNull String NAME = "name";
    @DataType(INTEGER) @NotNull String SERVINGS = "servings";
    @DataType(TEXT) String IMAGE_URL = "image_url";
}
