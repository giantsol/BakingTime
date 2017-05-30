package com.lee.hansol.bakingtime

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.lee.hansol.bakingtime.adapters.RecipesRecyclerViewAdapter
import com.lee.hansol.bakingtime.helpers.DataStorage
import com.lee.hansol.bakingtime.loaders.RecipesLoaderFromDb
import com.lee.hansol.bakingtime.loaders.RecipesLoaderFromInternet
import com.lee.hansol.bakingtime.models.Recipe
import com.lee.hansol.bakingtime.utils.ToastUtils.isToastShowingWithMsg
import com.lee.hansol.bakingtime.utils.ToastUtils.toast
import com.lee.hansol.bakingtime.utils.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Array<Recipe>>, RecipesRecyclerViewAdapter.OnRecipeItemClickListener {
    private lateinit var recipesAdapter: RecipesRecyclerViewAdapter
    private val LOADER_ID_LOAD_RECIPES = 111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialize()
    }

    private fun initialize() {
        initializeViewVisibilities()
        initializeMainRecyclerView()
        startLoaderForRecipes()
    }

    private fun initializeViewVisibilities() {
        activity_main_recyclerview.visibility = View.INVISIBLE
        activity_main_progressbar.visibility = View.INVISIBLE
        activity_main_error_textview.visibility = View.INVISIBLE
    }

    private fun initializeMainRecyclerView() {
        val colNum = resources.getInteger(R.integer.main_grid_column_number)
        val grid = GridLayoutManager(this, colNum, LinearLayoutManager.VERTICAL, false)
        recipesAdapter = RecipesRecyclerViewAdapter(this, this)
        activity_main_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = grid
            adapter = recipesAdapter
        }
    }

    private fun startLoaderForRecipes() {
        showOnlyProgressBar()
        val loader = supportLoaderManager.getLoader<Array<Recipe>>(LOADER_ID_LOAD_RECIPES)
        if (loader == null)
            supportLoaderManager.initLoader(LOADER_ID_LOAD_RECIPES, null, this)
        else
            supportLoaderManager.restartLoader(LOADER_ID_LOAD_RECIPES, null, this)
    }

    private fun showOnlyProgressBar() {
        activity_main_recyclerview.visibility = View.INVISIBLE
        activity_main_progressbar.visibility = View.VISIBLE
        activity_main_error_textview.visibility = View.INVISIBLE
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Array<Recipe>> {
        if (User.hasInternetConnection(this))
            return RecipesLoaderFromInternet(this)
        else
            return RecipesLoaderFromDb(this)
    }

    override fun onLoadFinished(loader: Loader<Array<Recipe>>, recipes: Array<Recipe>) {
        if (recipes.isEmpty())
            if (loader is RecipesLoaderFromInternet)
                showErrorWhileLoadingFromInternet()
            else
                showErrorWhileLoadingFromDb()
        else {
            DataStorage.getInstance().setRecipes(recipes) // TODO
            recipesAdapter.notifyDataSetChanged()
            showRecipesView()
        }
    }

    private fun showErrorWhileLoadingFromInternet() {
        activity_main_recyclerview.visibility = View.INVISIBLE
        activity_main_progressbar.visibility = View.INVISIBLE
        activity_main_error_textview.visibility = View.VISIBLE
        activity_main_error_textview.text = getString(R.string.text_error_loading_from_internet)
    }

    private fun showErrorWhileLoadingFromDb() {
        activity_main_recyclerview.visibility = View.INVISIBLE
        activity_main_progressbar.visibility = View.INVISIBLE
        activity_main_error_textview.visibility = View.VISIBLE
        activity_main_error_textview.text = getString(R.string.text_error_loading_from_db)
    }

    private fun showRecipesView() {
        activity_main_recyclerview.visibility = View.VISIBLE
        activity_main_progressbar.visibility = View.INVISIBLE
        activity_main_error_textview.visibility = View.INVISIBLE
    }

    override fun onLoaderReset(loader: Loader<Array<Recipe>>) {
        loader.cancelLoad()
    }

    override fun onRecipeItemClick(recipeIndex: Int) {
        DataStorage.getInstance().currentRecipeIndex = recipeIndex // TODO
        startActivity(Intent(this, RecipeDetailActivity::class.java))
    }

    override fun onBackPressed() {
        val quitString = getString(R.string.toast_really_exit_app)
        if (isToastShowingWithMsg(quitString))
            super.onBackPressed()
        else
            toast(this, quitString)
    }
}
