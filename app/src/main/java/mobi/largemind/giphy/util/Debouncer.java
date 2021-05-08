package mobi.largemind.giphy.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

public class Debouncer<T> {
    private final T initialValue;
    private T debouncedValue;
    @NonNull private Callback<T> callback;

    private final Handler handler = new Handler(Looper.getMainLooper());

    public Debouncer(T initialValue, @NonNull Callback<T> callback) {
        this.initialValue = debouncedValue = initialValue;
        this.callback = callback;
    }

    public void debounce(T value) {
        handler.removeCallbacks(debounceRunnable);
        debouncedValue = value;
        handler.postDelayed(debounceRunnable, 500);
    }

    public void stop() {
        debouncedValue = initialValue;
        handler.removeCallbacks(debounceRunnable);
    }

    private final Runnable debounceRunnable = () -> {
        callback.onValue(debouncedValue);
    };

    public interface Callback<T> {
        void onValue(T value);
    }
}
