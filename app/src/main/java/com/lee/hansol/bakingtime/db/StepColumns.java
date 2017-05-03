package com.lee.hansol.bakingtime.db;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface StepColumns {
    @DataType(INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(INTEGER) @NotNull String RECIPE_ID = "recipe_id";
    @DataType(INTEGER) @NotNull String STEP_ORDER = "step_order";
    @DataType(TEXT) String SHORT_DESCRIPTION = "short_description";
    @DataType(TEXT) String DESCRIPTION = "description";
    @DataType(TEXT) String VIDEO_URL = "video_url";
    @DataType(TEXT) String THUMBNAIL_URL = "thumbnail_url";
}
