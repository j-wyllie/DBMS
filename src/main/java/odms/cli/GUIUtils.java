package odms.cli;

import javafx.application.Platform;

import java.util.Objects;

public final class GUIUtils {
    private GUIUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Runs function when ready
     *
     * @param runnable
     */
    public static void runSafe(final Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable");
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        }
        else {
            Platform.runLater(runnable);
        }
    }
}