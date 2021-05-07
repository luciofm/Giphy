package mobi.largemind.giphy.data;

import com.squareup.moshi.Json;

import java.util.List;

public class Response {

    public List<Data> data;
    public Pagination pagination;
    public Meta meta;

    public static Response error() {
        Response response = new Response();
        response.meta.status = 400;
        return response;
    }

    public static class Data {
        public String id;
        public Images images;
    }

    public static class Images {
        public Image original;
        public Image fixed_width_downsampled;
        public Image fixed_height_downsampled;
    }

    public static class Image {
        public String url;
        public int width;
        public int height;
        public int frames;
        public int size;
    }

    public static class Pagination {
        @Json(name = "total_count")
        public int totalCount;
        public int count;
        public int offset;
    }

    public static class Meta {
        public int status;
        public String msg;
        @Json(name = "response_id")
        public String responseId;
    }
}
