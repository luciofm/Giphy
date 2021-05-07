package mobi.largemind.giphy.data;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TagInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request original = chain.request();
        HttpUrl originalHttpUrl = original.url();
        List<String> path = originalHttpUrl.encodedPathSegments();
        if (!path.isEmpty()) {
            int size = path.size();
            String method = path.get(size -1);

            Request.Builder requestBuilder = original.newBuilder()
                    .tag(method)
                    .url(originalHttpUrl);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        }

        return chain.proceed(original);
    }
}
