package mobi.largemind.giphy.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GiphyApi {
    @GET("trending")
    Call<Response> trending(@Query("offset") int offset);

    @GET("search")
    Call<Response> search(@Query("q") String query, @Query("offset") int offset);
}
