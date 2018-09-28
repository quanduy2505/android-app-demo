package com.facebook.internal;

import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicLong;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public final class FileLruCache {
    private static final String HEADER_CACHEKEY_KEY = "key";
    private static final String HEADER_CACHE_CONTENT_TAG_KEY = "tag";
    static final String TAG;
    private static final AtomicLong bufferIndex;
    private final File directory;
    private boolean isTrimInProgress;
    private boolean isTrimPending;
    private AtomicLong lastClearCacheTime;
    private final Limits limits;
    private final Object lock;
    private final String tag;

    /* renamed from: com.facebook.internal.FileLruCache.2 */
    class C04112 implements Runnable {
        final /* synthetic */ File[] val$filesToDelete;

        C04112(File[] fileArr) {
            this.val$filesToDelete = fileArr;
        }

        public void run() {
            for (File file : this.val$filesToDelete) {
                file.delete();
            }
        }
    }

    /* renamed from: com.facebook.internal.FileLruCache.3 */
    class C04123 implements Runnable {
        C04123() {
        }

        public void run() {
            FileLruCache.this.trim();
        }
    }

    private static class BufferFile {
        private static final String FILE_NAME_PREFIX = "buffer";
        private static final FilenameFilter filterExcludeBufferFiles;
        private static final FilenameFilter filterExcludeNonBufferFiles;

        /* renamed from: com.facebook.internal.FileLruCache.BufferFile.1 */
        static class C04131 implements FilenameFilter {
            C04131() {
            }

            public boolean accept(File dir, String filename) {
                return !filename.startsWith(BufferFile.FILE_NAME_PREFIX);
            }
        }

        /* renamed from: com.facebook.internal.FileLruCache.BufferFile.2 */
        static class C04142 implements FilenameFilter {
            C04142() {
            }

            public boolean accept(File dir, String filename) {
                return filename.startsWith(BufferFile.FILE_NAME_PREFIX);
            }
        }

        private BufferFile() {
        }

        static {
            filterExcludeBufferFiles = new C04131();
            filterExcludeNonBufferFiles = new C04142();
        }

        static void deleteAll(File root) {
            File[] filesToDelete = root.listFiles(excludeNonBufferFiles());
            if (filesToDelete != null) {
                for (File file : filesToDelete) {
                    file.delete();
                }
            }
        }

        static FilenameFilter excludeBufferFiles() {
            return filterExcludeBufferFiles;
        }

        static FilenameFilter excludeNonBufferFiles() {
            return filterExcludeNonBufferFiles;
        }

        static File newFile(File root) {
            return new File(root, FILE_NAME_PREFIX + Long.valueOf(FileLruCache.bufferIndex.incrementAndGet()).toString());
        }
    }

    private static class CloseCallbackOutputStream extends OutputStream {
        final StreamCloseCallback callback;
        final OutputStream innerStream;

        CloseCallbackOutputStream(OutputStream innerStream, StreamCloseCallback callback) {
            this.innerStream = innerStream;
            this.callback = callback;
        }

        public void close() throws IOException {
            try {
                this.innerStream.close();
            } finally {
                this.callback.onClose();
            }
        }

        public void flush() throws IOException {
            this.innerStream.flush();
        }

        public void write(byte[] buffer, int offset, int count) throws IOException {
            this.innerStream.write(buffer, offset, count);
        }

        public void write(byte[] buffer) throws IOException {
            this.innerStream.write(buffer);
        }

        public void write(int oneByte) throws IOException {
            this.innerStream.write(oneByte);
        }
    }

    private static final class CopyingInputStream extends InputStream {
        final InputStream input;
        final OutputStream output;

        CopyingInputStream(InputStream input, OutputStream output) {
            this.input = input;
            this.output = output;
        }

        public int available() throws IOException {
            return this.input.available();
        }

        public void close() throws IOException {
            try {
                this.input.close();
            } finally {
                this.output.close();
            }
        }

        public void mark(int readlimit) {
            throw new UnsupportedOperationException();
        }

        public boolean markSupported() {
            return false;
        }

        public int read(byte[] buffer) throws IOException {
            int count = this.input.read(buffer);
            if (count > 0) {
                this.output.write(buffer, 0, count);
            }
            return count;
        }

        public int read() throws IOException {
            int b = this.input.read();
            if (b >= 0) {
                this.output.write(b);
            }
            return b;
        }

        public int read(byte[] buffer, int offset, int length) throws IOException {
            int count = this.input.read(buffer, offset, length);
            if (count > 0) {
                this.output.write(buffer, offset, count);
            }
            return count;
        }

        public synchronized void reset() {
            throw new UnsupportedOperationException();
        }

        public long skip(long byteCount) throws IOException {
            byte[] buffer = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
            long total = 0;
            while (total < byteCount) {
                int count = read(buffer, 0, (int) Math.min(byteCount - total, (long) buffer.length));
                if (count < 0) {
                    break;
                }
                total += (long) count;
            }
            return total;
        }
    }

    public static final class Limits {
        private int byteCount;
        private int fileCount;

        public Limits() {
            this.fileCount = AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT;
            this.byteCount = AccessibilityNodeInfoCompat.ACTION_DISMISS;
        }

        int getByteCount() {
            return this.byteCount;
        }

        int getFileCount() {
            return this.fileCount;
        }

        void setByteCount(int n) {
            if (n < 0) {
                throw new InvalidParameterException("Cache byte-count limit must be >= 0");
            }
            this.byteCount = n;
        }

        void setFileCount(int n) {
            if (n < 0) {
                throw new InvalidParameterException("Cache file count limit must be >= 0");
            }
            this.fileCount = n;
        }
    }

    private static final class ModifiedFile implements Comparable<ModifiedFile> {
        private static final int HASH_MULTIPLIER = 37;
        private static final int HASH_SEED = 29;
        private final File file;
        private final long modified;

        ModifiedFile(File file) {
            this.file = file;
            this.modified = file.lastModified();
        }

        File getFile() {
            return this.file;
        }

        long getModified() {
            return this.modified;
        }

        public int compareTo(ModifiedFile another) {
            if (getModified() < another.getModified()) {
                return -1;
            }
            if (getModified() > another.getModified()) {
                return 1;
            }
            return getFile().compareTo(another.getFile());
        }

        public boolean equals(Object another) {
            return (another instanceof ModifiedFile) && compareTo((ModifiedFile) another) == 0;
        }

        public int hashCode() {
            return ((this.file.hashCode() + 1073) * HASH_MULTIPLIER) + ((int) (this.modified % 2147483647L));
        }
    }

    private interface StreamCloseCallback {
        void onClose();
    }

    private static final class StreamHeader {
        private static final int HEADER_VERSION = 0;

        private StreamHeader() {
        }

        static void writeHeader(OutputStream stream, JSONObject header) throws IOException {
            byte[] headerBytes = header.toString().getBytes();
            stream.write(0);
            stream.write((headerBytes.length >> 16) & MotionEventCompat.ACTION_MASK);
            stream.write((headerBytes.length >> 8) & MotionEventCompat.ACTION_MASK);
            stream.write((headerBytes.length >> 0) & MotionEventCompat.ACTION_MASK);
            stream.write(headerBytes);
        }

        static JSONObject readHeader(InputStream stream) throws IOException {
            if (stream.read() != 0) {
                return null;
            }
            int headerSize = 0;
            for (int i = 0; i < 3; i++) {
                int b = stream.read();
                if (b == -1) {
                    Logger.log(LoggingBehavior.CACHE, FileLruCache.TAG, "readHeader: stream.read returned -1 while reading header size");
                    return null;
                }
                headerSize = (headerSize << 8) + (b & MotionEventCompat.ACTION_MASK);
            }
            byte[] headerBytes = new byte[headerSize];
            int count = 0;
            while (count < headerBytes.length) {
                int readCount = stream.read(headerBytes, count, headerBytes.length - count);
                if (readCount < 1) {
                    Logger.log(LoggingBehavior.CACHE, FileLruCache.TAG, "readHeader: stream.read stopped at " + Integer.valueOf(count) + " when expected " + headerBytes.length);
                    return null;
                }
                count += readCount;
            }
            try {
                Object parsed = new JSONTokener(new String(headerBytes)).nextValue();
                if (parsed instanceof JSONObject) {
                    return (JSONObject) parsed;
                }
                Logger.log(LoggingBehavior.CACHE, FileLruCache.TAG, "readHeader: expected JSONObject, got " + parsed.getClass().getCanonicalName());
                return null;
            } catch (JSONException e) {
                throw new IOException(e.getMessage());
            }
        }
    }

    /* renamed from: com.facebook.internal.FileLruCache.1 */
    class C10011 implements StreamCloseCallback {
        final /* synthetic */ File val$buffer;
        final /* synthetic */ long val$bufferFileCreateTime;
        final /* synthetic */ String val$key;

        C10011(long j, File file, String str) {
            this.val$bufferFileCreateTime = j;
            this.val$buffer = file;
            this.val$key = str;
        }

        public void onClose() {
            if (this.val$bufferFileCreateTime < FileLruCache.this.lastClearCacheTime.get()) {
                this.val$buffer.delete();
            } else {
                FileLruCache.this.renameToTargetAndTrim(this.val$key, this.val$buffer);
            }
        }
    }

    static {
        TAG = FileLruCache.class.getSimpleName();
        bufferIndex = new AtomicLong();
    }

    public FileLruCache(String tag, Limits limits) {
        this.lastClearCacheTime = new AtomicLong(0);
        this.tag = tag;
        this.limits = limits;
        this.directory = new File(FacebookSdk.getCacheDir(), tag);
        this.lock = new Object();
        if (this.directory.mkdirs() || this.directory.isDirectory()) {
            BufferFile.deleteAll(this.directory);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    long sizeInBytesForTest() {
        /*
        r8 = this;
        r5 = r8.lock;
        monitor-enter(r5);
    L_0x0003:
        r4 = r8.isTrimPending;	 Catch:{ all -> 0x002c }
        if (r4 != 0) goto L_0x000b;
    L_0x0007:
        r4 = r8.isTrimInProgress;	 Catch:{ all -> 0x002c }
        if (r4 == 0) goto L_0x0013;
    L_0x000b:
        r4 = r8.lock;	 Catch:{ InterruptedException -> 0x0011 }
        r4.wait();	 Catch:{ InterruptedException -> 0x0011 }
        goto L_0x0003;
    L_0x0011:
        r4 = move-exception;
        goto L_0x0003;
    L_0x0013:
        monitor-exit(r5);	 Catch:{ all -> 0x002c }
        r4 = r8.directory;
        r1 = r4.listFiles();
        r2 = 0;
        if (r1 == 0) goto L_0x002f;
    L_0x001e:
        r5 = r1.length;
        r4 = 0;
    L_0x0020:
        if (r4 >= r5) goto L_0x002f;
    L_0x0022:
        r0 = r1[r4];
        r6 = r0.length();
        r2 = r2 + r6;
        r4 = r4 + 1;
        goto L_0x0020;
    L_0x002c:
        r4 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x002c }
        throw r4;
    L_0x002f:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.internal.FileLruCache.sizeInBytesForTest():long");
    }

    public InputStream get(String key) throws IOException {
        return get(key, null);
    }

    public InputStream get(String key, String contentTag) throws IOException {
        File file = new File(this.directory, Utility.md5hash(key));
        try {
            BufferedInputStream buffered = new BufferedInputStream(new FileInputStream(file), Utility.DEFAULT_STREAM_BUFFER_SIZE);
            try {
                JSONObject header = StreamHeader.readHeader(buffered);
                if (header == null) {
                    return null;
                }
                String foundKey = header.optString(HEADER_CACHEKEY_KEY);
                if (foundKey == null || !foundKey.equals(key)) {
                    if (null == null) {
                        buffered.close();
                    }
                    return null;
                }
                String headerContentTag = header.optString(HEADER_CACHE_CONTENT_TAG_KEY, null);
                if ((contentTag != null || headerContentTag == null) && (contentTag == null || contentTag.equals(headerContentTag))) {
                    long accessTime = new Date().getTime();
                    Logger.log(LoggingBehavior.CACHE, TAG, "Setting lastModified to " + Long.valueOf(accessTime) + " for " + file.getName());
                    file.setLastModified(accessTime);
                    if (true) {
                        return buffered;
                    }
                    buffered.close();
                    return buffered;
                }
                if (null == null) {
                    buffered.close();
                }
                return null;
            } finally {
                if (null == null) {
                    buffered.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

    public OutputStream openPutStream(String key) throws IOException {
        return openPutStream(key, null);
    }

    public OutputStream openPutStream(String key, String contentTag) throws IOException {
        File buffer = BufferFile.newFile(this.directory);
        buffer.delete();
        if (buffer.createNewFile()) {
            try {
                BufferedOutputStream buffered = new BufferedOutputStream(new CloseCallbackOutputStream(new FileOutputStream(buffer), new C10011(System.currentTimeMillis(), buffer, key)), Utility.DEFAULT_STREAM_BUFFER_SIZE);
                try {
                    JSONObject header = new JSONObject();
                    header.put(HEADER_CACHEKEY_KEY, key);
                    if (!Utility.isNullOrEmpty(contentTag)) {
                        header.put(HEADER_CACHE_CONTENT_TAG_KEY, contentTag);
                    }
                    StreamHeader.writeHeader(buffered, header);
                    if (!true) {
                        buffered.close();
                    }
                    return buffered;
                } catch (JSONException e) {
                    Logger.log(LoggingBehavior.CACHE, 5, TAG, "Error creating JSON header for cache file: " + e);
                    throw new IOException(e.getMessage());
                } catch (Throwable th) {
                    if (!false) {
                        buffered.close();
                    }
                }
            } catch (FileNotFoundException e2) {
                Logger.log(LoggingBehavior.CACHE, 5, TAG, "Error creating buffer output stream: " + e2);
                throw new IOException(e2.getMessage());
            }
        }
        throw new IOException("Could not create file at " + buffer.getAbsolutePath());
    }

    public void clearCache() {
        File[] filesToDelete = this.directory.listFiles(BufferFile.excludeBufferFiles());
        this.lastClearCacheTime.set(System.currentTimeMillis());
        if (filesToDelete != null) {
            FacebookSdk.getExecutor().execute(new C04112(filesToDelete));
        }
    }

    public String getLocation() {
        return this.directory.getPath();
    }

    private void renameToTargetAndTrim(String key, File buffer) {
        if (!buffer.renameTo(new File(this.directory, Utility.md5hash(key)))) {
            buffer.delete();
        }
        postTrim();
    }

    public InputStream interceptAndPut(String key, InputStream input) throws IOException {
        return new CopyingInputStream(input, openPutStream(key));
    }

    public String toString() {
        return "{FileLruCache: tag:" + this.tag + " file:" + this.directory.getName() + "}";
    }

    private void postTrim() {
        synchronized (this.lock) {
            if (!this.isTrimPending) {
                this.isTrimPending = true;
                FacebookSdk.getExecutor().execute(new C04123());
            }
        }
    }

    private void trim() {
        synchronized (this.lock) {
            this.isTrimPending = false;
            this.isTrimInProgress = true;
        }
        try {
            File file;
            Logger.log(LoggingBehavior.CACHE, TAG, "trim started");
            PriorityQueue<ModifiedFile> heap = new PriorityQueue();
            long size = 0;
            long count = 0;
            File[] filesToTrim = this.directory.listFiles(BufferFile.excludeBufferFiles());
            if (filesToTrim != null) {
                for (File file2 : filesToTrim) {
                    ModifiedFile modified = new ModifiedFile(file2);
                    heap.add(modified);
                    Logger.log(LoggingBehavior.CACHE, TAG, "  trim considering time=" + Long.valueOf(modified.getModified()) + " name=" + modified.getFile().getName());
                    size += file2.length();
                    count++;
                }
            }
            while (true) {
                if (size <= ((long) this.limits.getByteCount()) && count <= ((long) this.limits.getFileCount())) {
                    break;
                }
                file2 = ((ModifiedFile) heap.remove()).getFile();
                Logger.log(LoggingBehavior.CACHE, TAG, "  trim removing " + file2.getName());
                size -= file2.length();
                count--;
                file2.delete();
            }
            synchronized (this.lock) {
                this.isTrimInProgress = false;
                this.lock.notifyAll();
            }
        } catch (Throwable th) {
            synchronized (this.lock) {
            }
            this.isTrimInProgress = false;
            this.lock.notifyAll();
        }
    }
}
