package mobi.largemind.giphy.di;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import mobi.largemind.giphy.data.ApiKeyInterceptor;
import mobi.largemind.giphy.data.GiphyApi;
import mobi.largemind.giphy.data.GiphyRepository;
import mobi.largemind.giphy.data.TagInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return new OkHttpClient.Builder()
                .addInterceptor(new ApiKeyInterceptor())
                .addInterceptor(new TagInterceptor())
                .addInterceptor(logger)
                .build();
    }

    @Provides
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.giphy.com/v1/gifs/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
    }

    @Provides
    GiphyApi provideGiphyApi(Retrofit retrofit) {
        return retrofit.create(GiphyApi.class);
    }

    @Provides
    GiphyRepository provideGiphyRepository(OkHttpClient client, GiphyApi api) {
        return new GiphyRepository(client, api);
    }
}
