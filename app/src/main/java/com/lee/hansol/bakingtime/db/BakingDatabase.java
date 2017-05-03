package com.lee.hansol.bakingtime.db;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = BakingDatabase.VERSION)
public final class BakingDatabase {

    public static final int VERSION = 1;

    @Table(RecipeColumns.class) public static final String RECIPES = "recipes";
    @Table(IngredientColumns.class) public static final String INGREDIENTS = "ingredients";
    @Table(StepColumns.class) public static final String STEPS = "steps";
}
