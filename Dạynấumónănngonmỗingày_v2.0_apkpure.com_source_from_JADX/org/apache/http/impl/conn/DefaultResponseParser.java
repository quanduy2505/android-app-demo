package org.apache.http.impl.conn;

import android.support.v4.media.TransportMediator;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponseFactory;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.conn.params.ConnConnectionPNames;
import org.apache.http.impl.io.AbstractMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.LineParser;
import org.apache.http.params.HttpParams;
import org.apache.http.util.CharArrayBuffer;

@ThreadSafe
public class DefaultResponseParser extends AbstractMessageParser {
    private final CharArrayBuffer lineBuf;
    private final Log log;
    private final int maxGarbageLines;
    private final HttpResponseFactory responseFactory;

    public DefaultResponseParser(SessionInputBuffer buffer, LineParser parser, HttpResponseFactory responseFactory, HttpParams params) {
        super(buffer, parser, params);
        this.log = LogFactory.getLog(getClass());
        if (responseFactory == null) {
            throw new IllegalArgumentException("Response factory may not be null");
        }
        this.responseFactory = responseFactory;
        this.lineBuf = new CharArrayBuffer(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        this.maxGarbageLines = params.getIntParameter(ConnConnectionPNames.MAX_STATUS_LINE_GARBAGE, UrlImageViewHelper.CACHE_DURATION_INFINITE);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected org.apache.http.HttpMessage parseHead(org.apache.http.io.SessionInputBuffer r9) throws java.io.IOException, org.apache.http.HttpException {
        /*
        r8 = this;
        r7 = -1;
        r0 = 0;
        r1 = 0;
    L_0x0003:
        r4 = r8.lineBuf;
        r4.clear();
        r4 = r8.lineBuf;
        r2 = r9.readLine(r4);
        if (r2 != r7) goto L_0x001a;
    L_0x0010:
        if (r0 != 0) goto L_0x001a;
    L_0x0012:
        r4 = new org.apache.http.NoHttpResponseException;
        r5 = "The target server failed to respond";
        r4.<init>(r5);
        throw r4;
    L_0x001a:
        r1 = new org.apache.http.message.ParserCursor;
        r4 = 0;
        r5 = r8.lineBuf;
        r5 = r5.length();
        r1.<init>(r4, r5);
        r4 = r8.lineParser;
        r5 = r8.lineBuf;
        r4 = r4.hasProtocolVersion(r5, r1);
        if (r4 == 0) goto L_0x0040;
    L_0x0030:
        r4 = r8.lineParser;
        r5 = r8.lineBuf;
        r3 = r4.parseStatusLine(r5, r1);
        r4 = r8.responseFactory;
        r5 = 0;
        r4 = r4.newHttpResponse(r3, r5);
        return r4;
    L_0x0040:
        if (r2 == r7) goto L_0x0046;
    L_0x0042:
        r4 = r8.maxGarbageLines;
        if (r0 < r4) goto L_0x004e;
    L_0x0046:
        r4 = new org.apache.http.ProtocolException;
        r5 = "The server failed to respond with a valid HTTP response";
        r4.<init>(r5);
        throw r4;
    L_0x004e:
        r4 = r8.log;
        r4 = r4.isDebugEnabled();
        if (r4 == 0) goto L_0x0074;
    L_0x0056:
        r4 = r8.log;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Garbage in response: ";
        r5 = r5.append(r6);
        r6 = r8.lineBuf;
        r6 = r6.toString();
        r5 = r5.append(r6);
        r5 = r5.toString();
        r4.debug(r5);
    L_0x0074:
        r0 = r0 + 1;
        goto L_0x0003;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.conn.DefaultResponseParser.parseHead(org.apache.http.io.SessionInputBuffer):org.apache.http.HttpMessage");
    }
}
