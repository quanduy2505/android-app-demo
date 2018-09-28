package org.apache.http.impl.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import rx.internal.schedulers.NewThreadWorker;

public abstract class AbstractMessageParser implements HttpMessageParser {
    private static final int HEADERS = 1;
    private static final int HEAD_LINE = 0;
    private final List headerLines;
    protected final LineParser lineParser;
    private final int maxHeaderCount;
    private final int maxLineLen;
    private HttpMessage message;
    private final SessionInputBuffer sessionBuffer;
    private int state;

    protected abstract HttpMessage parseHead(SessionInputBuffer sessionInputBuffer) throws IOException, HttpException, ParseException;

    public AbstractMessageParser(SessionInputBuffer buffer, LineParser parser, HttpParams params) {
        if (buffer == null) {
            throw new IllegalArgumentException("Session input buffer may not be null");
        } else if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            this.sessionBuffer = buffer;
            this.maxHeaderCount = params.getIntParameter(CoreConnectionPNames.MAX_HEADER_COUNT, -1);
            this.maxLineLen = params.getIntParameter(CoreConnectionPNames.MAX_LINE_LENGTH, -1);
            if (parser == null) {
                parser = BasicLineParser.DEFAULT;
            }
            this.lineParser = parser;
            this.headerLines = new ArrayList();
            this.state = 0;
        }
    }

    public static Header[] parseHeaders(SessionInputBuffer inbuffer, int maxHeaderCount, int maxLineLen, LineParser parser) throws HttpException, IOException {
        if (parser == null) {
            parser = BasicLineParser.DEFAULT;
        }
        return parseHeaders(inbuffer, maxHeaderCount, maxLineLen, parser, new ArrayList());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.apache.http.Header[] parseHeaders(org.apache.http.io.SessionInputBuffer r10, int r11, int r12, org.apache.http.message.LineParser r13, java.util.List r14) throws org.apache.http.HttpException, java.io.IOException {
        /*
        if (r10 != 0) goto L_0x000a;
    L_0x0002:
        r8 = new java.lang.IllegalArgumentException;
        r9 = "Session input buffer may not be null";
        r8.<init>(r9);
        throw r8;
    L_0x000a:
        if (r13 != 0) goto L_0x0014;
    L_0x000c:
        r8 = new java.lang.IllegalArgumentException;
        r9 = "Line parser may not be null";
        r8.<init>(r9);
        throw r8;
    L_0x0014:
        if (r14 != 0) goto L_0x001e;
    L_0x0016:
        r8 = new java.lang.IllegalArgumentException;
        r9 = "Header line list may not be null";
        r8.<init>(r9);
        throw r8;
    L_0x001e:
        r2 = 0;
        r7 = 0;
    L_0x0020:
        if (r2 != 0) goto L_0x0053;
    L_0x0022:
        r2 = new org.apache.http.util.CharArrayBuffer;
        r8 = 64;
        r2.<init>(r8);
    L_0x0029:
        r6 = r10.readLine(r2);
        r8 = -1;
        if (r6 == r8) goto L_0x0037;
    L_0x0030:
        r8 = r2.length();
        r9 = 1;
        if (r8 >= r9) goto L_0x0057;
    L_0x0037:
        r8 = r14.size();
        r4 = new org.apache.http.Header[r8];
        r5 = 0;
    L_0x003e:
        r8 = r14.size();
        if (r5 >= r8) goto L_0x00c7;
    L_0x0044:
        r0 = r14.get(r5);
        r0 = (org.apache.http.util.CharArrayBuffer) r0;
        r8 = r13.parseHeader(r0);	 Catch:{ ParseException -> 0x00bc }
        r4[r5] = r8;	 Catch:{ ParseException -> 0x00bc }
        r5 = r5 + 1;
        goto L_0x003e;
    L_0x0053:
        r2.clear();
        goto L_0x0029;
    L_0x0057:
        r8 = 0;
        r8 = r2.charAt(r8);
        r9 = 32;
        if (r8 == r9) goto L_0x0069;
    L_0x0060:
        r8 = 0;
        r8 = r2.charAt(r8);
        r9 = 9;
        if (r8 != r9) goto L_0x00b6;
    L_0x0069:
        if (r7 == 0) goto L_0x00b6;
    L_0x006b:
        r5 = 0;
    L_0x006c:
        r8 = r2.length();
        if (r5 >= r8) goto L_0x007e;
    L_0x0072:
        r1 = r2.charAt(r5);
        r8 = 32;
        if (r1 == r8) goto L_0x0096;
    L_0x007a:
        r8 = 9;
        if (r1 == r8) goto L_0x0096;
    L_0x007e:
        if (r12 <= 0) goto L_0x0099;
    L_0x0080:
        r8 = r7.length();
        r8 = r8 + 1;
        r9 = r2.length();
        r8 = r8 + r9;
        r8 = r8 - r5;
        if (r8 <= r12) goto L_0x0099;
    L_0x008e:
        r8 = new java.io.IOException;
        r9 = "Maximum line length limit exceeded";
        r8.<init>(r9);
        throw r8;
    L_0x0096:
        r5 = r5 + 1;
        goto L_0x006c;
    L_0x0099:
        r8 = 32;
        r7.append(r8);
        r8 = r2.length();
        r8 = r8 - r5;
        r7.append(r2, r5, r8);
    L_0x00a6:
        if (r11 <= 0) goto L_0x0020;
    L_0x00a8:
        r8 = r14.size();
        if (r8 < r11) goto L_0x0020;
    L_0x00ae:
        r8 = new java.io.IOException;
        r9 = "Maximum header count exceeded";
        r8.<init>(r9);
        throw r8;
    L_0x00b6:
        r14.add(r2);
        r7 = r2;
        r2 = 0;
        goto L_0x00a6;
    L_0x00bc:
        r3 = move-exception;
        r8 = new org.apache.http.ProtocolException;
        r9 = r3.getMessage();
        r8.<init>(r9);
        throw r8;
    L_0x00c7:
        return r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.io.AbstractMessageParser.parseHeaders(org.apache.http.io.SessionInputBuffer, int, int, org.apache.http.message.LineParser, java.util.List):org.apache.http.Header[]");
    }

    public HttpMessage parse() throws IOException, HttpException {
        switch (this.state) {
            case NewThreadWorker.PURGE_FREQUENCY /*0*/:
                try {
                    this.message = parseHead(this.sessionBuffer);
                    this.state = HEADERS;
                    break;
                } catch (ParseException px) {
                    throw new ProtocolException(px.getMessage(), px);
                }
            case HEADERS /*1*/:
                break;
            default:
                throw new IllegalStateException("Inconsistent parser state");
        }
        this.message.setHeaders(parseHeaders(this.sessionBuffer, this.maxHeaderCount, this.maxLineLen, this.lineParser, this.headerLines));
        HttpMessage result = this.message;
        this.message = null;
        this.headerLines.clear();
        this.state = 0;
        return result;
    }
}
