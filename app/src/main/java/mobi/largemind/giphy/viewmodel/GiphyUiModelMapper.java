package mobi.largemind.giphy.viewmodel;

import java.util.ArrayList;
import java.util.List;

import mobi.largemind.giphy.data.Response;

public class GiphyUiModelMapper {
    public static List<GiphyUiModel> map(Response response) {
        List<GiphyUiModel> list = new ArrayList<>(response.data.size());
        for(Response.Data data : response.data) {
            list.add(map(data));
        }

        return list;
    }

    private static GiphyUiModel map(Response.Data data) {
        String id = data.id;
        String url = data.images.fixed_height_downsampled.url;
        int height = data.images.fixed_height_downsampled.height;
        String urlOriginal = data.images.original.url;

        return new GiphyUiModel(id, url, height, urlOriginal);
    }
}
