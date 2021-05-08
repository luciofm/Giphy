# Giphy Browser App
I've implemented the app using a MVVM'ish approach. Since it is a simple app I may have broken some "rules".

## Model/Data Layer

On the Model/Data Layer I've used _Retrofit_ for the *API* calls. These calls returns a **Response** _DTO_ and are accessed via `GiphyRepository` **Repository**. The **Repository** calls returns a `LiveData` of `DataState<T>`that represents 3 different states of the Call, `loading`, `data` (data available) and `error`

## ViewModel
The ViewModel exposes a `UiState` **LiveData**. `UiState` has 4 distinct states: `LOADING`, `LOADED` (with a `List<GiphyUiModel>`), `ERROR` and `COMPLETED` (no more items available)

The ViewModel also have an internal `Query` **LiveData**. A `Query` can represent the `TRENDING` and `SEARCH` feeds.  The *View* calls `setQuery(query)` to request data from the ViewModel. Once a new query value is set, the `Transformations.switchMap()` will run with the new value and will call the appropriate **Repository** call
```
private final MutableLiveData<Query> query = new MutableLiveData<>(Query.trending());  
private final LiveData<UiState<List<GiphyUiModel>>> uiState = createUiStateLiveData();

private LiveData<UiState<List<GiphyUiModel>>> createUiStateLiveData() {  
    return Transformations.switchMap(query, query -> {
	    // Call the repository based on the current query.
    }
}

public LiveData<UiState<List<GiphyUiModel>>> getUiState() {  
    return uiState;  
}
```

## View / Activity
The View layer simply observes the _ViewModel_ `UiState` ** LiveData** (`viewModel.getUiState().observe()`)

The `RecyclerView` has an `EndlessScrollListener` that calls `setQuery` on the ViewModel, so it can download more data.

```
@Override  
public void onLoadMore() {  
    Query currentQuery = viewModel.getQuery();  
    viewModel.setQuery(currentQuery);  
}
```

Also the `SearchView` also calls on the `ViewModel`'s `setQuery` after debouncing the value for 500ms.

```
private final Debouncer<String> debouncer = new Debouncer<>("", value -> {  
    viewModel.setQuery(Query.search(value));  
});
```

I also added some sugar to the `MainActivity`, like implementing Glide's `RecyclerViewPreloader` and hiding the keyboard on scroll when searching...

## Other comments.
If this was a bigger/serious app, I'd probably have used a _Interactor/UseCase_ between the _ViewModel_ and the _Repository_. And if I could, let's say I was implementing a new feature on an existing code base, I'd try to implement the _Interactor/UseCase_ and the _Repository_ and Kotlin, where I could use Coroutines to simplify and avoid callbacks, and just return a _LiveData_ to the old Java code.