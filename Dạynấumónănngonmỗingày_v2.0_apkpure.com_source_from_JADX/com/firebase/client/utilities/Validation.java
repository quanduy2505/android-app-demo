package com.firebase.client.utilities;

import com.firebase.client.FirebaseException;
import com.firebase.client.core.Path;
import com.firebase.client.core.ServerValues;
import com.firebase.client.core.ValidationPath;
import com.firebase.client.snapshot.ChildKey;
import com.firebase.client.snapshot.Node;
import com.firebase.client.snapshot.NodeUtilities;
import com.firebase.client.snapshot.PriorityUtilities;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import rx.android.BuildConfig;

public class Validation {
    private static final Pattern INVALID_KEY_REGEX;
    private static final Pattern INVALID_PATH_REGEX;

    static {
        INVALID_PATH_REGEX = Pattern.compile("[\\[\\]\\.#$]");
        INVALID_KEY_REGEX = Pattern.compile("[\\[\\]\\.#\\$\\/\\u0000-\\u001F\\u007F]");
    }

    private static boolean isValidPathString(String pathString) {
        return !INVALID_PATH_REGEX.matcher(pathString).find();
    }

    public static void validatePathString(String pathString) throws FirebaseException {
        if (!isValidPathString(pathString)) {
            throw new FirebaseException("Invalid Firebase path: " + pathString + ". Firebase paths must not contain '.', '#', '$', '[', or ']'");
        }
    }

    public static void validateRootPathString(String pathString) throws FirebaseException {
        if (pathString.startsWith(".info")) {
            validatePathString(pathString.substring(5));
        } else if (pathString.startsWith("/.info")) {
            validatePathString(pathString.substring(6));
        } else {
            validatePathString(pathString);
        }
    }

    private static boolean isWritableKey(String key) {
        return key != null && key.length() > 0 && (key.equals(".value") || key.equals(".priority") || !(key.startsWith(".") || INVALID_KEY_REGEX.matcher(key).find()));
    }

    private static boolean isValidKey(String key) {
        return key.equals(".info") || !INVALID_KEY_REGEX.matcher(key).find();
    }

    public static void validateNullableKey(String key) throws FirebaseException {
        if (key != null && !isValidKey(key)) {
            throw new FirebaseException("Invalid key: " + key + ". Keys must not contain '/', '.', '#', '$', '[', or ']'");
        }
    }

    private static boolean isWritablePath(Path path) {
        ChildKey front = path.getFront();
        return front == null || !front.asString().startsWith(".");
    }

    public static void validateWritableObject(Object object) {
        if (object instanceof Map) {
            Map<String, Object> map = (Map) object;
            if (!map.containsKey(ServerValues.NAME_SUBKEY_SERVERVALUE)) {
                for (Entry<String, Object> entry : map.entrySet()) {
                    validateWritableKey((String) entry.getKey());
                    validateWritableObject(entry.getValue());
                }
            }
        } else if (object instanceof List) {
            for (Object child : (List) object) {
                validateWritableObject(child);
            }
        }
    }

    public static void validateWritableKey(String key) throws FirebaseException {
        if (!isWritableKey(key)) {
            throw new FirebaseException("Invalid key: " + key + ". Keys must not contain '/', '.', '#', '$', '[', or ']'");
        }
    }

    public static void validateWritablePath(Path path) throws FirebaseException {
        if (!isWritablePath(path)) {
            throw new FirebaseException("Invalid write location: " + path.toString());
        }
    }

    public static Map<Path, Node> parseAndValidateUpdate(Path path, Map<String, Object> update) throws FirebaseException {
        SortedMap<Path, Node> parsedUpdate = new TreeMap();
        for (Entry<String, Object> entry : update.entrySet()) {
            Path updatePath = new Path((String) entry.getKey());
            Object newValue = entry.getValue();
            ValidationPath.validateWithObject(path.child(updatePath), newValue);
            String childName = !updatePath.isEmpty() ? updatePath.getBack().asString() : BuildConfig.VERSION_NAME;
            if (childName.equals(ServerValues.NAME_SUBKEY_SERVERVALUE) || childName.equals(".value")) {
                throw new FirebaseException("Path '" + updatePath + "' contains disallowed child name: " + childName);
            } else if (!childName.equals(".priority") || PriorityUtilities.isValidPriority(NodeUtilities.NodeFromJSON(newValue))) {
                validateWritableObject(newValue);
                parsedUpdate.put(updatePath, NodeUtilities.NodeFromJSON(newValue));
            } else {
                throw new FirebaseException("Path '" + updatePath + "' contains invalid priority " + "(must be a string, double, ServerValue, or null).");
            }
        }
        Path prevPath = null;
        for (Path curPath : parsedUpdate.keySet()) {
            boolean z = prevPath == null || prevPath.compareTo(curPath) < 0;
            Utilities.hardAssert(z);
            if (prevPath == null || !prevPath.contains(curPath)) {
                prevPath = curPath;
            } else {
                throw new FirebaseException("Path '" + prevPath + "' is an ancestor of '" + curPath + "' in an update.");
            }
        }
        return parsedUpdate;
    }
}
