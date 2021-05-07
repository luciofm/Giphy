package mobi.largemind.giphy.util;

import androidx.annotation.NonNull;

public class Preconditions {
    public static void checkNotNull(Object object) {
        checkNotNull(object, null);
    }

    public static void checkNotNull(Object object, ErrorMessage errorMessage) {
        if (object == null) {
            String message = errorMessage != null ? errorMessage.errorMessage() : "Value could not be null";
            throw new NullPointerException(message);
        }
    }

    public interface ErrorMessage {
        @NonNull String errorMessage();
    }
}
