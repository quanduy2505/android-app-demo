package rx.plugins;

class RxJavaSingleExecutionHookDefault extends RxJavaSingleExecutionHook {
    private static final RxJavaSingleExecutionHookDefault INSTANCE;

    RxJavaSingleExecutionHookDefault() {
    }

    static {
        INSTANCE = new RxJavaSingleExecutionHookDefault();
    }

    public static RxJavaSingleExecutionHook getInstance() {
        return INSTANCE;
    }
}
