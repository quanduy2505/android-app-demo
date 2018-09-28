package com.firebase.client.utilities;

import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.firebase.client.Logger;
import com.firebase.client.Logger.Level;
import com.google.android.gms.common.ConnectionResult;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import rx.internal.operators.OnSubscribeConcatMap;

public class DefaultLogger implements Logger {
    private final Set<String> enabledComponents;
    private final Level minLevel;

    /* renamed from: com.firebase.client.utilities.DefaultLogger.1 */
    static /* synthetic */ class C05861 {
        static final /* synthetic */ int[] $SwitchMap$com$firebase$client$Logger$Level;

        static {
            $SwitchMap$com$firebase$client$Logger$Level = new int[Level.values().length];
            try {
                $SwitchMap$com$firebase$client$Logger$Level[Level.ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$firebase$client$Logger$Level[Level.WARN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$firebase$client$Logger$Level[Level.INFO.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$firebase$client$Logger$Level[Level.DEBUG.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public DefaultLogger(Level level, List<String> enabledComponents) {
        if (enabledComponents != null) {
            this.enabledComponents = new HashSet(enabledComponents);
        } else {
            this.enabledComponents = null;
        }
        this.minLevel = level;
    }

    public Level getLogLevel() {
        return this.minLevel;
    }

    public void onLogMessage(Level level, String tag, String message, long msTimestamp) {
        if (shouldLog(level, tag)) {
            String toLog = buildLogMessage(level, tag, message, msTimestamp);
            switch (C05861.$SwitchMap$com$firebase$client$Logger$Level[level.ordinal()]) {
                case OnSubscribeConcatMap.BOUNDARY /*1*/:
                    error(tag, toLog);
                case OnSubscribeConcatMap.END /*2*/:
                    warn(tag, toLog);
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    info(tag, toLog);
                case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                    debug(tag, toLog);
                default:
                    throw new RuntimeException("Should not reach here!");
            }
        }
    }

    protected String buildLogMessage(Level level, String tag, String message, long msTimestamp) {
        return new Date(msTimestamp).toString() + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + "[" + level + "] " + tag + ": " + message;
    }

    protected void error(String tag, String toLog) {
        System.err.println(toLog);
    }

    protected void warn(String tag, String toLog) {
        System.out.println(toLog);
    }

    protected void info(String tag, String toLog) {
        System.out.println(toLog);
    }

    protected void debug(String tag, String toLog) {
        System.out.println(toLog);
    }

    protected boolean shouldLog(Level level, String tag) {
        return level.ordinal() >= this.minLevel.ordinal() && (this.enabledComponents == null || level.ordinal() > Level.DEBUG.ordinal() || this.enabledComponents.contains(tag));
    }
}
