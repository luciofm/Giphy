package mobi.largemind.giphy.glide;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

import mobi.largemind.giphy.viewmodel.GiphyUiModel;

public class GiphyUiModelLoader extends BaseGlideUrlLoader<GiphyUiModel> {

    protected GiphyUiModelLoader(ModelLoader<GlideUrl, InputStream> concreteLoader) {
        super(concreteLoader);
    }

    @Override
    protected String getUrl(GiphyUiModel model, int width, int height, Options options) {
        if (height > model.getFixedHeight() && model.getUrlOriginal() != null) {
            return model.getUrlOriginal();
        }
        return model.getFixedHeightUrl();
    }

    @Override
    public boolean handles(@NonNull @NotNull GiphyUiModel giphyUiModel) {
        return true;
    }

    public static final class Factory implements ModelLoaderFactory<GiphyUiModel, InputStream> {
        @NonNull
        @Override
        public ModelLoader<GiphyUiModel, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new GiphyUiModelLoader(multiFactory.build(GlideUrl.class, InputStream.class));
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }
}
