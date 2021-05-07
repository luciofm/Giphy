package mobi.largemind.giphy.data;

import androidx.annotation.NonNull;

import javax.annotation.Nullable;

import static mobi.largemind.giphy.util.Preconditions.checkNotNull;

public class DataState<T> {

    @NonNull
    private DataState.State state;
    @Nullable
    private T data;
    @Nullable
    private Throwable throwable;

    public DataState(@NonNull State state, @Nullable T data, @Nullable Throwable throwable) {
        checkNotNull(state, () -> "STATE should not be null");
        this.state = state;
        this.data = data;
        this.throwable = throwable;
    }

    public static <T> DataState<T> loading() {
        return new DataState<>(State.LOADING, null, null);
    }

    public static <T> DataState<T> data(T data) {
        return new DataState<>(State.DATA, data, null);
    }

    public static <T> DataState<T> error(Throwable throwable) {
        return new DataState<>(State.ERROR, null, throwable);
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

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }

    public enum State {
        LOADING,
        DATA,
        ERROR
    }
}
