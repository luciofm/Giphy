package mobi.largemind.giphy.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import mobi.largemind.giphy.data.DataState;
import mobi.largemind.giphy.data.GiphyRepository;
import mobi.largemind.giphy.data.Response;

@HiltViewModel
public class GiphyViewModel extends ViewModel {
    private final GiphyRepository repository;

    private final MutableLiveData<Query> query = new MutableLiveData<>(Query.trending());
    private final LiveData<UiState<List<GiphyUiModel>>> gifs = createGifsLiveData();

    private final List<GiphyUiModel> trending = new ArrayList<>();
    private final List<GiphyUiModel> search = new ArrayList<>();
    private String oldQuery = null;

    @Inject
    public GiphyViewModel(GiphyRepository repository) {
        this.repository = repository;
    }

    public LiveData<UiState<List<GiphyUiModel>>> getGifs() {
        return gifs;
    }

    public LiveData<Query> getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query.setValue(query);
    }

    private LiveData<UiState<List<GiphyUiModel>>> createGifsLiveData() {
        return Transformations.switchMap(query, query -> {
            switch (query.getType()) {
                case TRENDING:
                    if (isNewQuery(query)) {
                        trending.clear();
                    }
                    return Transformations.map(repository.trending(trending.size()), response ->
                            toUiState(response, data -> {
                                trending.addAll(data);
                                return trending;
                            }));
                case SEARCH:
                    if (isNewQuery(query)) {
                        search.clear();
                    }
                    oldQuery = query.getQuery();
                    return Transformations.map(repository.search(query.getQuery(), search.size()), response ->
                            toUiState(response, data -> {
                                search.addAll(data);
                                return search;
                            }));
                default:
                    throw new IllegalStateException("Type must be TRENDING OR SEARCH");
            }
        });
    }

    private boolean isNewQuery(Query query) {
        return (query.isReload() || (oldQuery != null && !oldQuery.equals(query.getQuery())));
    }

    private UiState<List<GiphyUiModel>> toUiState(DataState<Response> state, UpdateData updater) {
        switch (state.getState()) {
            case LOADING:
                return UiState.loading();
            case DATA:
                Response response = state.getData();
                if (response.meta.status != 200 || response.pagination.count == 0) {
                    return UiState.completed();
                }
                List<GiphyUiModel> models = GiphyUiModelMapper.map(response);
                return UiState.loaded(updater.update(models));
            case ERROR:
                return UiState.error();
            default:
                throw new IllegalStateException("State must be LOADING OR DATA OR ERROR");
        }
    }

    private interface UpdateData {
        List<GiphyUiModel> update(List<GiphyUiModel> data);
    }
}
