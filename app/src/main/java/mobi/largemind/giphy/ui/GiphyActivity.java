package mobi.largemind.giphy.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.resource.gif.GifDrawable;

import mobi.largemind.giphy.databinding.ActivityGiphyBinding;
import mobi.largemind.giphy.glide.GlideApp;
import mobi.largemind.giphy.viewmodel.GiphyUiModel;

public class GiphyActivity extends AppCompatActivity {

    public static final String KEY_URL = "KEY_URL";
    public static final String KEY_ORIGINAL_URL = "KEY_ORIGINAL_URL";
    public static final String KEY_HEIGHT = "KEY_HEIGHT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityGiphyBinding binding = ActivityGiphyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GiphyUiModel model = toUiModel(getIntent());

        GlideApp.with(this).load(model).into(binding.gifView).clearOnDetach();

        binding.gifView.setOnClickListener(v -> {
            Drawable drawable = binding.gifView.getDrawable();
            if (drawable instanceof GifDrawable) {
                GifDrawable gifDrawable = (GifDrawable) drawable;
                if (gifDrawable.isRunning()) {
                    gifDrawable.stop();
                } else {
                    gifDrawable.start();
                }
            }
        });
    }

    public static void launch(Context context, GiphyUiModel uiModel) {
        Intent intent = new Intent(context, GiphyActivity.class);
        intent.putExtra(KEY_URL, uiModel.getFixedHeightUrl());
        intent.putExtra(KEY_HEIGHT, uiModel.getFixedHeight());
        intent.putExtra(KEY_ORIGINAL_URL, uiModel.getUrlOriginal());
        context.startActivity(intent);
    }

    private static GiphyUiModel toUiModel(Intent intent) {
        return new GiphyUiModel(null, intent.getStringExtra(KEY_URL),
                intent.getIntExtra(KEY_HEIGHT, 0),
                intent.getStringExtra(KEY_ORIGINAL_URL));
    }
}