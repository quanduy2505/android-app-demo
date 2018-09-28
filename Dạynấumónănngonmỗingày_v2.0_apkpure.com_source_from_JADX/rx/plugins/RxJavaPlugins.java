package rx.plugins;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import rx.annotations.Experimental;

public class RxJavaPlugins {
    static final RxJavaErrorHandler DEFAULT_ERROR_HANDLER;
    private static final RxJavaPlugins INSTANCE;
    private final AtomicReference<RxJavaErrorHandler> errorHandler;
    private final AtomicReference<RxJavaObservableExecutionHook> observableExecutionHook;
    private final AtomicReference<RxJavaSchedulersHook> schedulersHook;
    private final AtomicReference<RxJavaSingleExecutionHook> singleExecutionHook;

    /* renamed from: rx.plugins.RxJavaPlugins.1 */
    static class C12861 extends RxJavaErrorHandler {
        C12861() {
        }
    }

    static {
        INSTANCE = new RxJavaPlugins();
        DEFAULT_ERROR_HANDLER = new C12861();
    }

    public static RxJavaPlugins getInstance() {
        return INSTANCE;
    }

    RxJavaPlugins() {
        this.errorHandler = new AtomicReference();
        this.observableExecutionHook = new AtomicReference();
        this.singleExecutionHook = new AtomicReference();
        this.schedulersHook = new AtomicReference();
    }

    @Experimental
    public void reset() {
        INSTANCE.errorHandler.set(null);
        INSTANCE.observableExecutionHook.set(null);
        INSTANCE.singleExecutionHook.set(null);
        INSTANCE.schedulersHook.set(null);
    }

    public RxJavaErrorHandler getErrorHandler() {
        if (this.errorHandler.get() == null) {
            Object impl = getPluginImplementationViaProperty(RxJavaErrorHandler.class, System.getProperties());
            if (impl == null) {
                this.errorHandler.compareAndSet(null, DEFAULT_ERROR_HANDLER);
            } else {
                this.errorHandler.compareAndSet(null, (RxJavaErrorHandler) impl);
            }
        }
        return (RxJavaErrorHandler) this.errorHandler.get();
    }

    public void registerErrorHandler(RxJavaErrorHandler impl) {
        if (!this.errorHandler.compareAndSet(null, impl)) {
            throw new IllegalStateException("Another strategy was already registered: " + this.errorHandler.get());
        }
    }

    public RxJavaObservableExecutionHook getObservableExecutionHook() {
        if (this.observableExecutionHook.get() == null) {
            Object impl = getPluginImplementationViaProperty(RxJavaObservableExecutionHook.class, System.getProperties());
            if (impl == null) {
                this.observableExecutionHook.compareAndSet(null, RxJavaObservableExecutionHookDefault.getInstance());
            } else {
                this.observableExecutionHook.compareAndSet(null, (RxJavaObservableExecutionHook) impl);
            }
        }
        return (RxJavaObservableExecutionHook) this.observableExecutionHook.get();
    }

    public void registerObservableExecutionHook(RxJavaObservableExecutionHook impl) {
        if (!this.observableExecutionHook.compareAndSet(null, impl)) {
            throw new IllegalStateException("Another strategy was already registered: " + this.observableExecutionHook.get());
        }
    }

    public RxJavaSingleExecutionHook getSingleExecutionHook() {
        if (this.singleExecutionHook.get() == null) {
            Object impl = getPluginImplementationViaProperty(RxJavaSingleExecutionHook.class, System.getProperties());
            if (impl == null) {
                this.singleExecutionHook.compareAndSet(null, RxJavaSingleExecutionHookDefault.getInstance());
            } else {
                this.singleExecutionHook.compareAndSet(null, (RxJavaSingleExecutionHook) impl);
            }
        }
        return (RxJavaSingleExecutionHook) this.singleExecutionHook.get();
    }

    public void registerSingleExecutionHook(RxJavaSingleExecutionHook impl) {
        if (!this.singleExecutionHook.compareAndSet(null, impl)) {
            throw new IllegalStateException("Another strategy was already registered: " + this.singleExecutionHook.get());
        }
    }

    static Object getPluginImplementationViaProperty(Class<?> pluginClass, Properties propsIn) {
        Properties props = (Properties) propsIn.clone();
        String classSimpleName = pluginClass.getSimpleName();
        String pluginPrefix = "rxjava.plugin.";
        String implementingClass = props.getProperty("rxjava.plugin." + classSimpleName + ".implementation");
        if (implementingClass == null) {
            String classSuffix = ".class";
            String implSuffix = ".impl";
            for (Entry<Object, Object> e : props.entrySet()) {
                String key = e.getKey().toString();
                if (key.startsWith("rxjava.plugin.")) {
                    if (key.endsWith(".class")) {
                        if (classSimpleName.equals(e.getValue().toString())) {
                            String index = key.substring(0, key.length() - ".class".length()).substring("rxjava.plugin.".length());
                            String implKey = "rxjava.plugin." + index + ".impl";
                            implementingClass = props.getProperty(implKey);
                            if (implementingClass == null) {
                                throw new RuntimeException("Implementing class declaration for " + classSimpleName + " missing: " + implKey);
                            }
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        if (implementingClass == null) {
            return null;
        }
        try {
            return Class.forName(implementingClass).asSubclass(pluginClass).newInstance();
        } catch (ClassCastException e2) {
            throw new RuntimeException(classSimpleName + " implementation is not an instance of " + classSimpleName + ": " + implementingClass);
        } catch (ClassNotFoundException e3) {
            throw new RuntimeException(classSimpleName + " implementation class not found: " + implementingClass, e3);
        } catch (InstantiationException e4) {
            throw new RuntimeException(classSimpleName + " implementation not able to be instantiated: " + implementingClass, e4);
        } catch (IllegalAccessException e5) {
            throw new RuntimeException(classSimpleName + " implementation not able to be accessed: " + implementingClass, e5);
        }
    }

    public RxJavaSchedulersHook getSchedulersHook() {
        if (this.schedulersHook.get() == null) {
            Object impl = getPluginImplementationViaProperty(RxJavaSchedulersHook.class, System.getProperties());
            if (impl == null) {
                this.schedulersHook.compareAndSet(null, RxJavaSchedulersHook.getDefaultInstance());
            } else {
                this.schedulersHook.compareAndSet(null, (RxJavaSchedulersHook) impl);
            }
        }
        return (RxJavaSchedulersHook) this.schedulersHook.get();
    }

    public void registerSchedulersHook(RxJavaSchedulersHook impl) {
        if (!this.schedulersHook.compareAndSet(null, impl)) {
            throw new IllegalStateException("Another strategy was already registered: " + this.schedulersHook.get());
        }
    }
}
