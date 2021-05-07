package mobi.largemind.giphy.viewmodel;

import android.renderscript.RSInvalidStateException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import static mobi.largemind.giphy.util.Preconditions.checkNotNull;

public class UiState<T> {

    @NonNull
    State state;
    @Nullable
    T data;

    private UiState(@NonNull State state, @Nullable T data) {
        if (state != State.LOADED && data != null) {
            throw new RSInvalidStateException("Only LOADED have data");
        }
        this.state = state;
        this.data = data;
    }

    public static <T> UiState<T> loading() {
        return new UiState<>(State.LOADING, null);
    }

    public static <T> UiState<T> loaded(T data) {
        return new UiState<T>(State.LOADED, data);
    }

    public static <T> UiState<T> completed() {
        return new UiState<>(State.COMPLETED, null);
    }

    public static <T> UiState<T> error() {
        return new UiState<>(State.ERROR, null);
    }

    @NonNull
    public State getState() {
        return state;
    }

    @NonNull
    public T getData() {
        checkNotNull(data);
        return data;
    }

    @Override
    public String toString() {
        return "UiState{" +
                "state=" + state +
                '}';
    }

    public enum State {
        LOADING,
        LOADED,
        COMPLETED,
        ERROR
    }
}
