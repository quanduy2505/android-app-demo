package com.bumptech.glide.disklrucache;

import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import rx.android.BuildConfig;

public final class DiskLruCache implements Closeable {
    static final long ANY_SEQUENCE_NUMBER = -1;
    private static final String CLEAN = "CLEAN";
    private static final String DIRTY = "DIRTY";
    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_BACKUP = "journal.bkp";
    static final String JOURNAL_FILE_TEMP = "journal.tmp";
    static final String MAGIC = "libcore.io.DiskLruCache";
    private static final String READ = "READ";
    private static final String REMOVE = "REMOVE";
    static final String VERSION_1 = "1";
    private final int appVersion;
    private final Callable<Void> cleanupCallable;
    private final File directory;
    final ThreadPoolExecutor executorService;
    private final File journalFile;
    private final File journalFileBackup;
    private final File journalFileTmp;
    private Writer journalWriter;
    private final LinkedHashMap<String, Entry> lruEntries;
    private long maxSize;
    private long nextSequenceNumber;
    private int redundantOpCount;
    private long size;
    private final int valueCount;

    /* renamed from: com.bumptech.glide.disklrucache.DiskLruCache.1 */
    class C03421 implements Callable<Void> {
        C03421() {
        }

        public Void call() throws Exception {
            synchronized (DiskLruCache.this) {
                if (DiskLruCache.this.journalWriter == null) {
                } else {
                    DiskLruCache.this.trimToSize();
                    if (DiskLruCache.this.journalRebuildRequired()) {
                        DiskLruCache.this.rebuildJournal();
                        DiskLruCache.this.redundantOpCount = 0;
                    }
                }
            }
            return null;
        }
    }

    public final class Editor {
        private boolean committed;
        private final Entry entry;
        private final boolean[] written;

        private Editor(Entry entry) {
            this.entry = entry;
            this.written = entry.readable ? null : new boolean[DiskLruCache.this.valueCount];
        }

        private InputStream newInputStream(int index) throws IOException {
            synchronized (DiskLruCache.this) {
                if (this.entry.currentEditor != this) {
                    throw new IllegalStateException();
                } else if (this.entry.readable) {
                    try {
                        InputStream fileInputStream = new FileInputStream(this.entry.getCleanFile(index));
                        return fileInputStream;
                    } catch (FileNotFoundException e) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }

        public String getString(int index) throws IOException {
            InputStream in = newInputStream(index);
            return in != null ? DiskLruCache.inputStreamToString(in) : null;
        }

        public File getFile(int index) throws IOException {
            File dirtyFile;
            synchronized (DiskLruCache.this) {
                if (this.entry.currentEditor != this) {
                    throw new IllegalStateException();
                }
                if (!this.entry.readable) {
                    this.written[index] = true;
                }
                dirtyFile = this.entry.getDirtyFile(index);
                if (!DiskLruCache.this.directory.exists()) {
                    DiskLruCache.this.directory.mkdirs();
                }
            }
            return dirtyFile;
        }

        public void set(int index, String value) throws IOException {
            Throwable th;
            Writer writer = null;
            try {
                Writer writer2 = new OutputStreamWriter(new FileOutputStream(getFile(index)), Util.UTF_8);
                try {
                    writer2.write(value);
                    Util.closeQuietly(writer2);
                } catch (Throwable th2) {
                    th = th2;
                    writer = writer2;
                    Util.closeQuietly(writer);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                Util.closeQuietly(writer);
                throw th;
            }
        }

        public void commit() throws IOException {
            DiskLruCache.this.completeEdit(this, true);
            this.committed = true;
        }

        public void abort() throws IOException {
            DiskLruCache.this.completeEdit(this, false);
        }

        public void abortUnlessCommitted() {
            if (!this.committed) {
                try {
                    abort();
                } catch (IOException e) {
                }
            }
        }
    }

    private final class Entry {
        File[] cleanFiles;
        private Editor currentEditor;
        File[] dirtyFiles;
        private final String key;
        private final long[] lengths;
        private boolean readable;
        private long sequenceNumber;

        private Entry(String key) {
            this.key = key;
            this.lengths = new long[DiskLruCache.this.valueCount];
            this.cleanFiles = new File[DiskLruCache.this.valueCount];
            this.dirtyFiles = new File[DiskLruCache.this.valueCount];
            StringBuilder fileBuilder = new StringBuilder(key).append('.');
            int truncateTo = fileBuilder.length();
            for (int i = 0; i < DiskLruCache.this.valueCount; i++) {
                fileBuilder.append(i);
                this.cleanFiles[i] = new File(DiskLruCache.this.directory, fileBuilder.toString());
                fileBuilder.append(".tmp");
                this.dirtyFiles[i] = new File(DiskLruCache.this.directory, fileBuilder.toString());
                fileBuilder.setLength(truncateTo);
            }
        }

        public String getLengths() throws IOException {
            StringBuilder result = new StringBuilder();
            for (long size : this.lengths) {
                result.append(' ').append(size);
            }
            return result.toString();
        }

        private void setLengths(String[] strings) throws IOException {
            if (strings.length != DiskLruCache.this.valueCount) {
                throw invalidLengths(strings);
            }
            int i = 0;
            while (i < strings.length) {
                try {
                    this.lengths[i] = Long.parseLong(strings[i]);
                    i++;
                } catch (NumberFormatException e) {
                    throw invalidLengths(strings);
                }
            }
        }

        private IOException invalidLengths(String[] strings) throws IOException {
            throw new IOException("unexpected journal line: " + Arrays.toString(strings));
        }

        public File getCleanFile(int i) {
            return this.cleanFiles[i];
        }

        public File getDirtyFile(int i) {
            return this.dirtyFiles[i];
        }
    }

    public final class Value {
        private final File[] files;
        private final String key;
        private final long[] lengths;
        private final long sequenceNumber;

        private Value(String key, long sequenceNumber, File[] files, long[] lengths) {
            this.key = key;
            this.sequenceNumber = sequenceNumber;
            this.files = files;
            this.lengths = lengths;
        }

        public Editor edit() throws IOException {
            return DiskLruCache.this.edit(this.key, this.sequenceNumber);
        }

        public File getFile(int index) {
            return this.files[index];
        }

        public String getString(int index) throws IOException {
            return DiskLruCache.inputStreamToString(new FileInputStream(this.files[index]));
        }

        public long getLength(int index) {
            return this.lengths[index];
        }
    }

    private DiskLruCache(File directory, int appVersion, int valueCount, long maxSize) {
        this.size = 0;
        this.lruEntries = new LinkedHashMap(0, 0.75f, true);
        this.nextSequenceNumber = 0;
        this.executorService = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
        this.cleanupCallable = new C03421();
        this.directory = directory;
        this.appVersion = appVersion;
        this.journalFile = new File(directory, JOURNAL_FILE);
        this.journalFileTmp = new File(directory, JOURNAL_FILE_TEMP);
        this.journalFileBackup = new File(directory, JOURNAL_FILE_BACKUP);
        this.valueCount = valueCount;
        this.maxSize = maxSize;
    }

    public static DiskLruCache open(File directory, int appVersion, int valueCount, long maxSize) throws IOException {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        } else if (valueCount <= 0) {
            throw new IllegalArgumentException("valueCount <= 0");
        } else {
            File backupFile = new File(directory, JOURNAL_FILE_BACKUP);
            if (backupFile.exists()) {
                File journalFile = new File(directory, JOURNAL_FILE);
                if (journalFile.exists()) {
                    backupFile.delete();
                } else {
                    renameTo(backupFile, journalFile, false);
                }
            }
            DiskLruCache cache = new DiskLruCache(directory, appVersion, valueCount, maxSize);
            if (cache.journalFile.exists()) {
                try {
                    cache.readJournal();
                    cache.processJournal();
                    return cache;
                } catch (IOException journalIsCorrupt) {
                    System.out.println("DiskLruCache " + directory + " is corrupt: " + journalIsCorrupt.getMessage() + ", removing");
                    cache.delete();
                }
            }
            directory.mkdirs();
            cache = new DiskLruCache(directory, appVersion, valueCount, maxSize);
            cache.rebuildJournal();
            return cache;
        }
    }

    private void readJournal() throws IOException {
        StrictLineReader reader = new StrictLineReader(new FileInputStream(this.journalFile), Util.US_ASCII);
        int lineCount;
        try {
            String magic = reader.readLine();
            String version = reader.readLine();
            String appVersionString = reader.readLine();
            String valueCountString = reader.readLine();
            String blank = reader.readLine();
            if (MAGIC.equals(magic) && VERSION_1.equals(version) && Integer.toString(this.appVersion).equals(appVersionString) && Integer.toString(this.valueCount).equals(valueCountString) && BuildConfig.VERSION_NAME.equals(blank)) {
                lineCount = 0;
                while (true) {
                    readJournalLine(reader.readLine());
                    lineCount++;
                }
            } else {
                throw new IOException("unexpected journal header: [" + magic + ", " + version + ", " + valueCountString + ", " + blank + "]");
            }
        } catch (EOFException e) {
            this.redundantOpCount = lineCount - this.lruEntries.size();
            if (reader.hasUnterminatedLine()) {
                rebuildJournal();
            } else {
                this.journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFile, true), Util.US_ASCII));
            }
            Util.closeQuietly(reader);
        } catch (Throwable th) {
            Util.closeQuietly(reader);
        }
    }

    private void readJournalLine(String line) throws IOException {
        int firstSpace = line.indexOf(32);
        if (firstSpace == -1) {
            throw new IOException("unexpected journal line: " + line);
        }
        String key;
        int keyBegin = firstSpace + 1;
        int secondSpace = line.indexOf(32, keyBegin);
        if (secondSpace == -1) {
            key = line.substring(keyBegin);
            if (firstSpace == REMOVE.length() && line.startsWith(REMOVE)) {
                this.lruEntries.remove(key);
                return;
            }
        }
        key = line.substring(keyBegin, secondSpace);
        Entry entry = (Entry) this.lruEntries.get(key);
        if (entry == null) {
            entry = new Entry(key, null);
            this.lruEntries.put(key, entry);
        }
        if (secondSpace != -1 && firstSpace == CLEAN.length() && line.startsWith(CLEAN)) {
            String[] parts = line.substring(secondSpace + 1).split(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR);
            entry.readable = true;
            entry.currentEditor = null;
            entry.setLengths(parts);
        } else if (secondSpace == -1 && firstSpace == DIRTY.length() && line.startsWith(DIRTY)) {
            entry.currentEditor = new Editor(entry, null);
        } else if (secondSpace != -1 || firstSpace != READ.length() || !line.startsWith(READ)) {
            throw new IOException("unexpected journal line: " + line);
        }
    }

    private void processJournal() throws IOException {
        deleteIfExists(this.journalFileTmp);
        Iterator<Entry> i = this.lruEntries.values().iterator();
        while (i.hasNext()) {
            Entry entry = (Entry) i.next();
            int t;
            if (entry.currentEditor == null) {
                for (t = 0; t < this.valueCount; t++) {
                    this.size += entry.lengths[t];
                }
            } else {
                entry.currentEditor = null;
                for (t = 0; t < this.valueCount; t++) {
                    deleteIfExists(entry.getCleanFile(t));
                    deleteIfExists(entry.getDirtyFile(t));
                }
                i.remove();
            }
        }
    }

    private synchronized void rebuildJournal() throws IOException {
        if (this.journalWriter != null) {
            this.journalWriter.close();
        }
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFileTmp), Util.US_ASCII));
        try {
            writer.write(MAGIC);
            writer.write("\n");
            writer.write(VERSION_1);
            writer.write("\n");
            writer.write(Integer.toString(this.appVersion));
            writer.write("\n");
            writer.write(Integer.toString(this.valueCount));
            writer.write("\n");
            writer.write("\n");
            for (Entry entry : this.lruEntries.values()) {
                if (entry.currentEditor != null) {
                    writer.write("DIRTY " + entry.key + '\n');
                } else {
                    writer.write("CLEAN " + entry.key + entry.getLengths() + '\n');
                }
            }
            writer.close();
            if (this.journalFile.exists()) {
                renameTo(this.journalFile, this.journalFileBackup, true);
            }
            renameTo(this.journalFileTmp, this.journalFile, false);
            this.journalFileBackup.delete();
            this.journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFile, true), Util.US_ASCII));
        } catch (Throwable th) {
            writer.close();
        }
    }

    private static void deleteIfExists(File file) throws IOException {
        if (file.exists() && !file.delete()) {
            throw new IOException();
        }
    }

    private static void renameTo(File from, File to, boolean deleteDestination) throws IOException {
        if (deleteDestination) {
            deleteIfExists(to);
        }
        if (!from.renameTo(to)) {
            throw new IOException();
        }
    }

    public synchronized Value get(String key) throws IOException {
        Value value = null;
        synchronized (this) {
            checkNotClosed();
            Entry entry = (Entry) this.lruEntries.get(key);
            if (entry != null) {
                if (entry.readable) {
                    for (File file : entry.cleanFiles) {
                        if (!file.exists()) {
                            break;
                        }
                    }
                    this.redundantOpCount++;
                    this.journalWriter.append(READ);
                    this.journalWriter.append(' ');
                    this.journalWriter.append(key);
                    this.journalWriter.append('\n');
                    if (journalRebuildRequired()) {
                        this.executorService.submit(this.cleanupCallable);
                    }
                    value = new Value(key, entry.sequenceNumber, entry.cleanFiles, entry.lengths, null);
                }
            }
        }
        return value;
    }

    public Editor edit(String key) throws IOException {
        return edit(key, ANY_SEQUENCE_NUMBER);
    }

    private synchronized Editor edit(String key, long expectedSequenceNumber) throws IOException {
        Editor editor = null;
        synchronized (this) {
            checkNotClosed();
            Entry entry = (Entry) this.lruEntries.get(key);
            if (expectedSequenceNumber == ANY_SEQUENCE_NUMBER || (entry != null && entry.sequenceNumber == expectedSequenceNumber)) {
                if (entry == null) {
                    entry = new Entry(key, null);
                    this.lruEntries.put(key, entry);
                } else if (entry.currentEditor != null) {
                }
                editor = new Editor(entry, null);
                entry.currentEditor = editor;
                this.journalWriter.append(DIRTY);
                this.journalWriter.append(' ');
                this.journalWriter.append(key);
                this.journalWriter.append('\n');
                this.journalWriter.flush();
            }
        }
        return editor;
    }

    public File getDirectory() {
        return this.directory;
    }

    public synchronized long getMaxSize() {
        return this.maxSize;
    }

    public synchronized void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
        this.executorService.submit(this.cleanupCallable);
    }

    public synchronized long size() {
        return this.size;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void completeEdit(com.bumptech.glide.disklrucache.DiskLruCache.Editor r13, boolean r14) throws java.io.IOException {
        /*
        r12 = this;
        monitor-enter(r12);
        r2 = r13.entry;	 Catch:{ all -> 0x0011 }
        r8 = r2.currentEditor;	 Catch:{ all -> 0x0011 }
        if (r8 == r13) goto L_0x0014;
    L_0x000b:
        r8 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0011 }
        r8.<init>();	 Catch:{ all -> 0x0011 }
        throw r8;	 Catch:{ all -> 0x0011 }
    L_0x0011:
        r8 = move-exception;
        monitor-exit(r12);
        throw r8;
    L_0x0014:
        if (r14 == 0) goto L_0x0057;
    L_0x0016:
        r8 = r2.readable;	 Catch:{ all -> 0x0011 }
        if (r8 != 0) goto L_0x0057;
    L_0x001c:
        r3 = 0;
    L_0x001d:
        r8 = r12.valueCount;	 Catch:{ all -> 0x0011 }
        if (r3 >= r8) goto L_0x0057;
    L_0x0021:
        r8 = r13.written;	 Catch:{ all -> 0x0011 }
        r8 = r8[r3];	 Catch:{ all -> 0x0011 }
        if (r8 != 0) goto L_0x0045;
    L_0x0029:
        r13.abort();	 Catch:{ all -> 0x0011 }
        r8 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0011 }
        r9 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0011 }
        r9.<init>();	 Catch:{ all -> 0x0011 }
        r10 = "Newly created entry didn't create value for index ";
        r9 = r9.append(r10);	 Catch:{ all -> 0x0011 }
        r9 = r9.append(r3);	 Catch:{ all -> 0x0011 }
        r9 = r9.toString();	 Catch:{ all -> 0x0011 }
        r8.<init>(r9);	 Catch:{ all -> 0x0011 }
        throw r8;	 Catch:{ all -> 0x0011 }
    L_0x0045:
        r8 = r2.getDirtyFile(r3);	 Catch:{ all -> 0x0011 }
        r8 = r8.exists();	 Catch:{ all -> 0x0011 }
        if (r8 != 0) goto L_0x0054;
    L_0x004f:
        r13.abort();	 Catch:{ all -> 0x0011 }
    L_0x0052:
        monitor-exit(r12);
        return;
    L_0x0054:
        r3 = r3 + 1;
        goto L_0x001d;
    L_0x0057:
        r3 = 0;
    L_0x0058:
        r8 = r12.valueCount;	 Catch:{ all -> 0x0011 }
        if (r3 >= r8) goto L_0x008c;
    L_0x005c:
        r1 = r2.getDirtyFile(r3);	 Catch:{ all -> 0x0011 }
        if (r14 == 0) goto L_0x0088;
    L_0x0062:
        r8 = r1.exists();	 Catch:{ all -> 0x0011 }
        if (r8 == 0) goto L_0x0085;
    L_0x0068:
        r0 = r2.getCleanFile(r3);	 Catch:{ all -> 0x0011 }
        r1.renameTo(r0);	 Catch:{ all -> 0x0011 }
        r8 = r2.lengths;	 Catch:{ all -> 0x0011 }
        r6 = r8[r3];	 Catch:{ all -> 0x0011 }
        r4 = r0.length();	 Catch:{ all -> 0x0011 }
        r8 = r2.lengths;	 Catch:{ all -> 0x0011 }
        r8[r3] = r4;	 Catch:{ all -> 0x0011 }
        r8 = r12.size;	 Catch:{ all -> 0x0011 }
        r8 = r8 - r6;
        r8 = r8 + r4;
        r12.size = r8;	 Catch:{ all -> 0x0011 }
    L_0x0085:
        r3 = r3 + 1;
        goto L_0x0058;
    L_0x0088:
        deleteIfExists(r1);	 Catch:{ all -> 0x0011 }
        goto L_0x0085;
    L_0x008c:
        r8 = r12.redundantOpCount;	 Catch:{ all -> 0x0011 }
        r8 = r8 + 1;
        r12.redundantOpCount = r8;	 Catch:{ all -> 0x0011 }
        r8 = 0;
        r2.currentEditor = r8;	 Catch:{ all -> 0x0011 }
        r8 = r2.readable;	 Catch:{ all -> 0x0011 }
        r8 = r8 | r14;
        if (r8 == 0) goto L_0x00f0;
    L_0x009d:
        r8 = 1;
        r2.readable = r8;	 Catch:{ all -> 0x0011 }
        r8 = r12.journalWriter;	 Catch:{ all -> 0x0011 }
        r9 = "CLEAN";
        r8.append(r9);	 Catch:{ all -> 0x0011 }
        r8 = r12.journalWriter;	 Catch:{ all -> 0x0011 }
        r9 = 32;
        r8.append(r9);	 Catch:{ all -> 0x0011 }
        r8 = r12.journalWriter;	 Catch:{ all -> 0x0011 }
        r9 = r2.key;	 Catch:{ all -> 0x0011 }
        r8.append(r9);	 Catch:{ all -> 0x0011 }
        r8 = r12.journalWriter;	 Catch:{ all -> 0x0011 }
        r9 = r2.getLengths();	 Catch:{ all -> 0x0011 }
        r8.append(r9);	 Catch:{ all -> 0x0011 }
        r8 = r12.journalWriter;	 Catch:{ all -> 0x0011 }
        r9 = 10;
        r8.append(r9);	 Catch:{ all -> 0x0011 }
        if (r14 == 0) goto L_0x00d4;
    L_0x00ca:
        r8 = r12.nextSequenceNumber;	 Catch:{ all -> 0x0011 }
        r10 = 1;
        r10 = r10 + r8;
        r12.nextSequenceNumber = r10;	 Catch:{ all -> 0x0011 }
        r2.sequenceNumber = r8;	 Catch:{ all -> 0x0011 }
    L_0x00d4:
        r8 = r12.journalWriter;	 Catch:{ all -> 0x0011 }
        r8.flush();	 Catch:{ all -> 0x0011 }
        r8 = r12.size;	 Catch:{ all -> 0x0011 }
        r10 = r12.maxSize;	 Catch:{ all -> 0x0011 }
        r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r8 > 0) goto L_0x00e7;
    L_0x00e1:
        r8 = r12.journalRebuildRequired();	 Catch:{ all -> 0x0011 }
        if (r8 == 0) goto L_0x0052;
    L_0x00e7:
        r8 = r12.executorService;	 Catch:{ all -> 0x0011 }
        r9 = r12.cleanupCallable;	 Catch:{ all -> 0x0011 }
        r8.submit(r9);	 Catch:{ all -> 0x0011 }
        goto L_0x0052;
    L_0x00f0:
        r8 = r12.lruEntries;	 Catch:{ all -> 0x0011 }
        r9 = r2.key;	 Catch:{ all -> 0x0011 }
        r8.remove(r9);	 Catch:{ all -> 0x0011 }
        r8 = r12.journalWriter;	 Catch:{ all -> 0x0011 }
        r9 = "REMOVE";
        r8.append(r9);	 Catch:{ all -> 0x0011 }
        r8 = r12.journalWriter;	 Catch:{ all -> 0x0011 }
        r9 = 32;
        r8.append(r9);	 Catch:{ all -> 0x0011 }
        r8 = r12.journalWriter;	 Catch:{ all -> 0x0011 }
        r9 = r2.key;	 Catch:{ all -> 0x0011 }
        r8.append(r9);	 Catch:{ all -> 0x0011 }
        r8 = r12.journalWriter;	 Catch:{ all -> 0x0011 }
        r9 = 10;
        r8.append(r9);	 Catch:{ all -> 0x0011 }
        goto L_0x00d4;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.DiskLruCache.completeEdit(com.bumptech.glide.disklrucache.DiskLruCache$Editor, boolean):void");
    }

    private boolean journalRebuildRequired() {
        return this.redundantOpCount >= BufferRecycler.DEFAULT_WRITE_CONCAT_BUFFER_LEN && this.redundantOpCount >= this.lruEntries.size();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean remove(java.lang.String r9) throws java.io.IOException {
        /*
        r8 = this;
        monitor-enter(r8);
        r8.checkNotClosed();	 Catch:{ all -> 0x0045 }
        r3 = r8.lruEntries;	 Catch:{ all -> 0x0045 }
        r0 = r3.get(r9);	 Catch:{ all -> 0x0045 }
        r0 = (com.bumptech.glide.disklrucache.DiskLruCache.Entry) r0;	 Catch:{ all -> 0x0045 }
        if (r0 == 0) goto L_0x0014;
    L_0x000e:
        r3 = r0.currentEditor;	 Catch:{ all -> 0x0045 }
        if (r3 == 0) goto L_0x0017;
    L_0x0014:
        r3 = 0;
    L_0x0015:
        monitor-exit(r8);
        return r3;
    L_0x0017:
        r2 = 0;
    L_0x0018:
        r3 = r8.valueCount;	 Catch:{ all -> 0x0045 }
        if (r2 >= r3) goto L_0x005e;
    L_0x001c:
        r1 = r0.getCleanFile(r2);	 Catch:{ all -> 0x0045 }
        r3 = r1.exists();	 Catch:{ all -> 0x0045 }
        if (r3 == 0) goto L_0x0048;
    L_0x0026:
        r3 = r1.delete();	 Catch:{ all -> 0x0045 }
        if (r3 != 0) goto L_0x0048;
    L_0x002c:
        r3 = new java.io.IOException;	 Catch:{ all -> 0x0045 }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0045 }
        r4.<init>();	 Catch:{ all -> 0x0045 }
        r5 = "failed to delete ";
        r4 = r4.append(r5);	 Catch:{ all -> 0x0045 }
        r4 = r4.append(r1);	 Catch:{ all -> 0x0045 }
        r4 = r4.toString();	 Catch:{ all -> 0x0045 }
        r3.<init>(r4);	 Catch:{ all -> 0x0045 }
        throw r3;	 Catch:{ all -> 0x0045 }
    L_0x0045:
        r3 = move-exception;
        monitor-exit(r8);
        throw r3;
    L_0x0048:
        r4 = r8.size;	 Catch:{ all -> 0x0045 }
        r3 = r0.lengths;	 Catch:{ all -> 0x0045 }
        r6 = r3[r2];	 Catch:{ all -> 0x0045 }
        r4 = r4 - r6;
        r8.size = r4;	 Catch:{ all -> 0x0045 }
        r3 = r0.lengths;	 Catch:{ all -> 0x0045 }
        r4 = 0;
        r3[r2] = r4;	 Catch:{ all -> 0x0045 }
        r2 = r2 + 1;
        goto L_0x0018;
    L_0x005e:
        r3 = r8.redundantOpCount;	 Catch:{ all -> 0x0045 }
        r3 = r3 + 1;
        r8.redundantOpCount = r3;	 Catch:{ all -> 0x0045 }
        r3 = r8.journalWriter;	 Catch:{ all -> 0x0045 }
        r4 = "REMOVE";
        r3.append(r4);	 Catch:{ all -> 0x0045 }
        r3 = r8.journalWriter;	 Catch:{ all -> 0x0045 }
        r4 = 32;
        r3.append(r4);	 Catch:{ all -> 0x0045 }
        r3 = r8.journalWriter;	 Catch:{ all -> 0x0045 }
        r3.append(r9);	 Catch:{ all -> 0x0045 }
        r3 = r8.journalWriter;	 Catch:{ all -> 0x0045 }
        r4 = 10;
        r3.append(r4);	 Catch:{ all -> 0x0045 }
        r3 = r8.lruEntries;	 Catch:{ all -> 0x0045 }
        r3.remove(r9);	 Catch:{ all -> 0x0045 }
        r3 = r8.journalRebuildRequired();	 Catch:{ all -> 0x0045 }
        if (r3 == 0) goto L_0x0090;
    L_0x0089:
        r3 = r8.executorService;	 Catch:{ all -> 0x0045 }
        r4 = r8.cleanupCallable;	 Catch:{ all -> 0x0045 }
        r3.submit(r4);	 Catch:{ all -> 0x0045 }
    L_0x0090:
        r3 = 1;
        goto L_0x0015;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.disklrucache.DiskLruCache.remove(java.lang.String):boolean");
    }

    public synchronized boolean isClosed() {
        return this.journalWriter == null;
    }

    private void checkNotClosed() {
        if (this.journalWriter == null) {
            throw new IllegalStateException("cache is closed");
        }
    }

    public synchronized void flush() throws IOException {
        checkNotClosed();
        trimToSize();
        this.journalWriter.flush();
    }

    public synchronized void close() throws IOException {
        if (this.journalWriter != null) {
            Iterator i$ = new ArrayList(this.lruEntries.values()).iterator();
            while (i$.hasNext()) {
                Entry entry = (Entry) i$.next();
                if (entry.currentEditor != null) {
                    entry.currentEditor.abort();
                }
            }
            trimToSize();
            this.journalWriter.close();
            this.journalWriter = null;
        }
    }

    private void trimToSize() throws IOException {
        while (this.size > this.maxSize) {
            remove((String) ((java.util.Map.Entry) this.lruEntries.entrySet().iterator().next()).getKey());
        }
    }

    public void delete() throws IOException {
        close();
        Util.deleteContents(this.directory);
    }

    private static String inputStreamToString(InputStream in) throws IOException {
        return Util.readFully(new InputStreamReader(in, Util.UTF_8));
    }
}
