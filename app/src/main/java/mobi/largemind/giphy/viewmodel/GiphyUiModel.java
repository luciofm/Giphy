package mobi.largemind.giphy.viewmodel;

public class GiphyUiModel {
    private String id;
    private String fixedHeightUrl;
    private int fixedHeight;
    private String urlOriginal;


    public GiphyUiModel(String id, String fixedHeightUrl, int fixedHeight, String urlOriginal) {
        this.id = id;
        this.fixedHeightUrl = fixedHeightUrl;
        this.fixedHeight = fixedHeight;
        this.urlOriginal = urlOriginal;
    }

    public String getId() {
        return id;
    }

    public String getFixedHeightUrl() {
        return fixedHeightUrl;
    }

    public int getFixedHeight() {
        return fixedHeight;
    }

    public String getUrlOriginal() {
        return urlOriginal;
    }
}
