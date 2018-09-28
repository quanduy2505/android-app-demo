package org.apache.http.client.entity;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class DeflateDecompressingEntity extends DecompressingEntity {
    public DeflateDecompressingEntity(HttpEntity entity) {
        super(entity);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    java.io.InputStream getDecompressingInputStream(java.io.InputStream r12) throws java.io.IOException {
        /*
        r11 = this;
        r10 = 1;
        r9 = 0;
        r8 = -1;
        r7 = 6;
        r5 = new byte[r7];
        r6 = new java.io.PushbackInputStream;
        r7 = r5.length;
        r6.<init>(r12, r7);
        r2 = r6.read(r5);
        if (r2 != r8) goto L_0x001a;
    L_0x0012:
        r7 = new java.io.IOException;
        r8 = "Unable to read the response";
        r7.<init>(r8);
        throw r7;
    L_0x001a:
        r0 = new byte[r10];
        r3 = new java.util.zip.Inflater;
        r3.<init>();
    L_0x0021:
        r4 = r3.inflate(r0);	 Catch:{ DataFormatException -> 0x0035 }
        if (r4 != 0) goto L_0x004a;
    L_0x0027:
        r7 = r3.finished();	 Catch:{ DataFormatException -> 0x0035 }
        if (r7 == 0) goto L_0x0044;
    L_0x002d:
        r7 = new java.io.IOException;	 Catch:{ DataFormatException -> 0x0035 }
        r8 = "Unable to read the response";
        r7.<init>(r8);	 Catch:{ DataFormatException -> 0x0035 }
        throw r7;	 Catch:{ DataFormatException -> 0x0035 }
    L_0x0035:
        r1 = move-exception;
        r6.unread(r5, r9, r2);
        r7 = new java.util.zip.InflaterInputStream;
        r8 = new java.util.zip.Inflater;
        r8.<init>(r10);
        r7.<init>(r6, r8);
    L_0x0043:
        return r7;
    L_0x0044:
        r7 = r3.needsDictionary();	 Catch:{ DataFormatException -> 0x0035 }
        if (r7 == 0) goto L_0x0054;
    L_0x004a:
        if (r4 != r8) goto L_0x005e;
    L_0x004c:
        r7 = new java.io.IOException;	 Catch:{ DataFormatException -> 0x0035 }
        r8 = "Unable to read the response";
        r7.<init>(r8);	 Catch:{ DataFormatException -> 0x0035 }
        throw r7;	 Catch:{ DataFormatException -> 0x0035 }
    L_0x0054:
        r7 = r3.needsInput();	 Catch:{ DataFormatException -> 0x0035 }
        if (r7 == 0) goto L_0x0021;
    L_0x005a:
        r3.setInput(r5);	 Catch:{ DataFormatException -> 0x0035 }
        goto L_0x0021;
    L_0x005e:
        r7 = 0;
        r6.unread(r5, r7, r2);	 Catch:{ DataFormatException -> 0x0035 }
        r7 = new java.util.zip.InflaterInputStream;	 Catch:{ DataFormatException -> 0x0035 }
        r7.<init>(r6);	 Catch:{ DataFormatException -> 0x0035 }
        goto L_0x0043;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.client.entity.DeflateDecompressingEntity.getDecompressingInputStream(java.io.InputStream):java.io.InputStream");
    }

    public Header getContentEncoding() {
        return null;
    }

    public long getContentLength() {
        return -1;
    }
}
