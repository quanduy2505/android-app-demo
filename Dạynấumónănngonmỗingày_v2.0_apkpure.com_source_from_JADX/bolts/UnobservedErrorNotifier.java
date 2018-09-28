package bolts;

import bolts.Task.UnobservedExceptionHandler;

class UnobservedErrorNotifier {
    private Task<?> task;

    public UnobservedErrorNotifier(Task<?> task) {
        this.task = task;
    }

    protected void finalize() throws Throwable {
        try {
            Task faultedTask = this.task;
            if (faultedTask != null) {
                UnobservedExceptionHandler ueh = Task.getUnobservedExceptionHandler();
                if (ueh != null) {
                    ueh.unobservedException(faultedTask, new UnobservedTaskException(faultedTask.getError()));
                }
            }
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
        }
    }

    public void setObserved() {
        this.task = null;
    }
}
