package rx;

import rx.annotations.Experimental;
import rx.exceptions.MissingBackpressureException;

@Experimental
public final class BackpressureOverflow {
    public static final Strategy ON_OVERFLOW_DEFAULT;
    public static final Strategy ON_OVERFLOW_DROP_LATEST;
    public static final Strategy ON_OVERFLOW_DROP_OLDEST;
    public static final Strategy ON_OVERFLOW_ERROR;

    public interface Strategy {
        boolean mayAttemptDrop() throws MissingBackpressureException;
    }

    static class DropLatest implements Strategy {
        static final DropLatest INSTANCE;

        static {
            INSTANCE = new DropLatest();
        }

        private DropLatest() {
        }

        public boolean mayAttemptDrop() {
            return false;
        }
    }

    static class DropOldest implements Strategy {
        static final DropOldest INSTANCE;

        static {
            INSTANCE = new DropOldest();
        }

        private DropOldest() {
        }

        public boolean mayAttemptDrop() {
            return true;
        }
    }

    static class Error implements Strategy {
        static final Error INSTANCE;

        static {
            INSTANCE = new Error();
        }

        private Error() {
        }

        public boolean mayAttemptDrop() throws MissingBackpressureException {
            throw new MissingBackpressureException("Overflowed buffer");
        }
    }

    static {
        ON_OVERFLOW_DEFAULT = Error.INSTANCE;
        ON_OVERFLOW_ERROR = Error.INSTANCE;
        ON_OVERFLOW_DROP_OLDEST = DropOldest.INSTANCE;
        ON_OVERFLOW_DROP_LATEST = DropLatest.INSTANCE;
    }
}
