package mobi.largemind.giphy.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;

public class GiphyRepository {
    private final GiphyApi api;
    private final OkHttpClient client;

    public GiphyRepository(OkHttpClient client, GiphyApi api) {
        this.client = client;
        this.api = api;
    }

    public LiveData<DataState<Response>> trending(int offset) {
        MutableLiveData<DataState<Response>> liveData = new MutableLiveData<>();
        liveData.setValue(DataState.loading());
        cancelRequests("trending");
        api.trending(offset).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(@NotNull Call<Response> call, @NotNull retrofit2.Response<Response> response) {
                liveData.setValue(DataState.data(response.body()));
            }

            @Override
            public void onFailure(@NotNull Call<Response> call, @NotNull Throwable t) {
                // ignore canceled calls
                if (!call.isCanceled()) {
                    liveData.setValue(DataState.error(t));
                }
            }
        });
        return liveData;
    }

    public LiveData<DataState<Response>> search(String query, int offset) {
        MutableLiveData<DataState<Response>> liveData = new MutableLiveData<>();
        liveData.setValue(DataState.loading());
        cancelRequests("search");
        api.search(query, offset).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(@NotNull Call<Response> call, @NotNull retrofit2.Response<Response> response) {
                liveData.setValue(DataState.data(response.body()));
            }

            @Override
            public void onFailure(@NotNull Call<Response> call, @NotNull Throwable t) {
                // ignore canceled calls
                if (!call.isCanceled()) {
                    liveData.setValue(DataState.error(t));
                }
            }
        });
        return liveData;
    }

    private void cancelRequests(String tag) {
        for (okhttp3.Call call : client.dispatcher().queuedCalls()) {
            if (Objects.equals(call.request().tag(), tag))
                call.cancel();
        }
        for (okhttp3.Call call : client.dispatcher().runningCalls()) {
            if (Objects.equals(call.request().tag(), tag))
                call.cancel();
        }
    }
}
