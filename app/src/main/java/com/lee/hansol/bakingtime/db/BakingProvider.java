package com.lee.hansol.bakingtime.db;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = BakingProvider.AUTHORITY, database = BakingDatabase.class)
public final class BakingProvider {

    public static final String AUTHORITY = "com.lee.hansol.bakingtime.BakingProvider";

    @TableEndpoint(table = BakingDatabase.RECIPES) public static class Recipes {
        @ContentUri(
                path = "recipes",
                type = "vnd.android.cursor.dir/recipes")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/recipes");
    }

    @TableEndpoint(table = BakingDatabase.INGREDIENTS) public static class Ingredients {
        @ContentUri(
                path = "ingredients",
                type = "vnd.android.cursor.dir/ingredients")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/ingredients");
    }

    @TableEndpoint(table = BakingDatabase.STEPS) public static class Steps {
        @ContentUri(
                path = "steps",
                type = "vnd.android.cursor.dir/steps")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/steps");
    }
}
