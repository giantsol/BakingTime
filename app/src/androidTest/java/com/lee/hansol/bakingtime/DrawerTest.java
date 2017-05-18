package com.lee.hansol.bakingtime;

import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.lee.hansol.bakingtime.helpers.DataStorage;
import com.lee.hansol.bakingtime.utils.DataUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class DrawerTest {

    @Rule
    public ActivityTestRule<RecipeDetailActivity> rule = new ActivityTestRule<>(RecipeDetailActivity.class);

    @Before
    public void loadRecipesFromDb_selectFirstRecipe() {
        DataStorage.getInstance().setRecipes(DataUtils.loadRecipesFromDb(rule.getActivity()));
        DataStorage.getInstance().setCurrentRecipeIndex(0);
    }

    @Test
    public void clickStepItem_showDetailFragment() {
        onView(withId(R.id.fragment_recipe_step_list_steps_view))
                .perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.fragment_recipe_step_detail_short_description))
                .check(matches(isDisplayed()));
    }
}
