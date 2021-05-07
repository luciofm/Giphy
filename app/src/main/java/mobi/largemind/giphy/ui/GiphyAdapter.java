package mobi.largemind.giphy.ui;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mobi.largemind.giphy.R;
import mobi.largemind.giphy.databinding.GifItemBinding;
import mobi.largemind.giphy.viewmodel.GiphyUiModel;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class GiphyAdapter extends RecyclerView.Adapter<GiphyAdapter.GifViewHolder>
        implements ListPreloader.PreloadModelProvider<GiphyUiModel> {

    private final RequestBuilder<Drawable> requestBuilder;
    private final ViewPreloadSizeProvider<GiphyUiModel> provider;
    private List<GiphyUiModel> data = new ArrayList<>();

    public GiphyAdapter(RequestBuilder<Drawable> requestBuilder,
                        ViewPreloadSizeProvider<GiphyUiModel> provider) {
        this.requestBuilder = requestBuilder;
        this.provider = provider;
    }

    @NonNull
    @Override
    public GifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        GifItemBinding binding = GifItemBinding.inflate(inflater, parent, false);
        return new GifViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GifViewHolder holder, int position) {
        holder.bind(data.get(position), requestBuilder);
        provider.setView(holder.gifView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(List<GiphyUiModel> models) {
        data.clear();
        data.addAll(models);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public List<GiphyUiModel> getPreloadItems(int position) {
        return Collections.singletonList(data.get(position));
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull @NotNull GiphyUiModel item) {
        return requestBuilder.load(item);
    }

    public static class GifViewHolder extends RecyclerView.ViewHolder {
        private final ImageView gifView;
        private GiphyUiModel model;

        DrawableCrossFadeFactory factory =
                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

        public GifViewHolder(GifItemBinding binding) {
            super(binding.getRoot());
            gifView = binding.gifView;
            gifView.setOnClickListener(v -> {
                GiphyActivity.launch(v.getContext(), model);
            });
        }

        public void bind(GiphyUiModel model, RequestBuilder<Drawable> requestBuilder) {
            this.model = model;
            requestBuilder
                    .load(model)
                    .placeholder(R.drawable.solid_color)
                    .transition(withCrossFade(factory))
                    .into(gifView)
                    .clearOnDetach();
        }

        public ImageView getGifView() {
            return gifView;
        }
    }
}
