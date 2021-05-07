package mobi.largemind.giphy.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;
import mobi.largemind.giphy.R;
import mobi.largemind.giphy.databinding.ActivityMainBinding;
import mobi.largemind.giphy.glide.GlideApp;
import mobi.largemind.giphy.util.EndlessScrollListener;
import mobi.largemind.giphy.viewmodel.GiphyUiModel;
import mobi.largemind.giphy.viewmodel.GiphyViewModel;
import mobi.largemind.giphy.viewmodel.Query;
import mobi.largemind.giphy.viewmodel.UiState;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private GiphyViewModel viewModel;
    private ActivityMainBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private GiphyAdapter adapter;
    private boolean keyboardVisible = false;

    EndlessScrollListener endlessScrollListener = new EndlessScrollListener() {
        @Override
        public void onLoadMore() {
            Query currentQuery = viewModel.getQuery().getValue();
            if (currentQuery == null) {
                currentQuery = Query.trending();
            }
            viewModel.setQuery(currentQuery);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(GiphyViewModel.class);
        viewModel.getGifs().observe(this, uiState -> {
            Log.d("MainActivity", "Current state: " + uiState);
            binding.swipeRefresh.setRefreshing(uiState.getState() == UiState.State.LOADING);
            switch (uiState.getState()) {
                case LOADING:
                    endlessScrollListener.setLoading(true);
                    break;
                case LOADED:
                    Log.d("MainActivity", "Current size: " + uiState.getData().size());
                    endlessScrollListener.setLoading(false);
                    adapter.add(uiState.getData());
                    break;
                case COMPLETED:
                case ERROR:
                    // Don't clear endlessScrollListener loading, so we don't load more items
            }
        });

        RequestBuilder<Drawable> requestBuilder = GlideApp.with(this).asDrawable();
        ViewPreloadSizeProvider<GiphyUiModel> preloadSizeProvider = new ViewPreloadSizeProvider<>();
        adapter = new GiphyAdapter(requestBuilder, preloadSizeProvider);

        RecyclerViewPreloader<GiphyUiModel> preloader =
                new RecyclerViewPreloader<>(GlideApp.with(this), adapter, preloadSizeProvider, 8);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(adapter);
        binding.recycler.addOnScrollListener(endlessScrollListener);
        binding.recycler.setRecyclerListener(
                holder -> {
                    if (holder instanceof GiphyAdapter.GifViewHolder) {
                        GiphyAdapter.GifViewHolder gifViewHolder = (GiphyAdapter.GifViewHolder) holder;
                        GlideApp.with(MainActivity.this).clear(gifViewHolder.getGifView());
                    }
                });
        binding.recycler.addOnScrollListener(keyboardScrollListener);
        binding.recycler.addOnScrollListener(preloader);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            Query currentQuery = viewModel.getQuery().getValue();
            if (currentQuery == null) {
                currentQuery = Query.trending();
            }

            Query newQuery;
            switch (currentQuery.getType()) {
                case SEARCH:
                    newQuery = Query.search(currentQuery.getQuery(), true);
                    break;
                case TRENDING:
                default:
                    newQuery = Query.trending(true);
            }
            viewModel.setQuery(newQuery);
        });

        binding.recycler.addOnLayoutChangeListener(layoutChangeListener);
    }

    private View.OnLayoutChangeListener layoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            keyboardVisible = ViewCompat.getRootWindowInsets(v).isVisible(WindowInsetsCompat.Type.ime());
            ViewCompat.setOnApplyWindowInsetsListener(binding.recycler, (view, insets) -> {
                keyboardVisible = insets.toWindowInsets().isVisible(WindowInsetsCompat.Type.ime());
                return insets;
            });
        }
    };

    private RecyclerView.OnScrollListener keyboardScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
            Log.d("MainActivity", "State: " + newState + " keyboard: " + keyboardVisible);
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING && keyboardVisible) {
                ViewCompat.getWindowInsetsController(recyclerView).hide(WindowInsetsCompat.Type.ime());
            }
        }

        @Override
        public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    @Override
    protected void onDestroy() {
        binding.recycler.removeOnScrollListener(endlessScrollListener);
        handler.removeCallbacks(debounceRunnable);
        binding.recycler.removeOnLayoutChangeListener(layoutChangeListener);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("MainActivity", "onQueryTextSubmit: " + query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("MainActivity", "onQueryTextChange: " + newText);
                if (!newText.isEmpty()) {
                    debounce(newText);
                }
                return true;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                handler.removeCallbacks(debounceRunnable);
                debouncedValue = "";
                viewModel.setQuery(Query.trending());
                return true;
            }
        });

        // handle rotations
        Query query = viewModel.getQuery().getValue();
        if (query != null && query.getType() == Query.Type.SEARCH && !query.getQuery().isEmpty()) {
            searchItem.expandActionView();
            searchView.setQuery(query.getQuery(), false);
        }

        return true;
    }

    private String debouncedValue = "";

    private void debounce(String newText) {
        handler.removeCallbacks(debounceRunnable);
        debouncedValue = newText;
        handler.postDelayed(debounceRunnable, 500);
    }

    private final Runnable debounceRunnable = () -> {
        viewModel.setQuery(Query.search(debouncedValue));
    };
}