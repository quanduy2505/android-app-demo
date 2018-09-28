package org.apache.http.message;

import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
import rx.android.BuildConfig;

public class BasicLineParser implements LineParser {
    public static final BasicLineParser DEFAULT;
    protected final ProtocolVersion protocol;

    static {
        DEFAULT = new BasicLineParser();
    }

    public BasicLineParser(ProtocolVersion proto) {
        if (proto == null) {
            proto = HttpVersion.HTTP_1_1;
        }
        this.protocol = proto;
    }

    public BasicLineParser() {
        this(null);
    }

    public static final ProtocolVersion parseProtocolVersion(String value, LineParser parser) throws ParseException {
        if (value == null) {
            throw new IllegalArgumentException("Value to parse may not be null.");
        }
        if (parser == null) {
            parser = DEFAULT;
        }
        CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        return parser.parseProtocolVersion(buffer, new ParserCursor(0, value.length()));
    }

    public ProtocolVersion parseProtocolVersion(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        } else if (cursor == null) {
            throw new IllegalArgumentException("Parser cursor may not be null");
        } else {
            String protoname = this.protocol.getProtocol();
            int protolength = protoname.length();
            int indexFrom = cursor.getPos();
            int indexTo = cursor.getUpperBound();
            skipWhitespace(buffer, cursor);
            int i = cursor.getPos();
            if ((i + protolength) + 4 > indexTo) {
                throw new ParseException(new StringBuffer().append("Not a valid protocol version: ").append(buffer.substring(indexFrom, indexTo)).toString());
            }
            boolean ok = true;
            int j = 0;
            while (ok && j < protolength) {
                ok = buffer.charAt(i + j) == protoname.charAt(j);
                j++;
            }
            if (ok) {
                ok = buffer.charAt(i + protolength) == '/';
            }
            if (ok) {
                i += protolength + 1;
                int period = buffer.indexOf(46, i, indexTo);
                if (period == -1) {
                    throw new ParseException(new StringBuffer().append("Invalid protocol version number: ").append(buffer.substring(indexFrom, indexTo)).toString());
                }
                try {
                    int major = Integer.parseInt(buffer.substringTrimmed(i, period));
                    i = period + 1;
                    int blank = buffer.indexOf(32, i, indexTo);
                    if (blank == -1) {
                        blank = indexTo;
                    }
                    try {
                        int minor = Integer.parseInt(buffer.substringTrimmed(i, blank));
                        cursor.updatePos(blank);
                        return createProtocolVersion(major, minor);
                    } catch (NumberFormatException e) {
                        throw new ParseException(new StringBuffer().append("Invalid protocol minor version number: ").append(buffer.substring(indexFrom, indexTo)).toString());
                    }
                } catch (NumberFormatException e2) {
                    throw new ParseException(new StringBuffer().append("Invalid protocol major version number: ").append(buffer.substring(indexFrom, indexTo)).toString());
                }
            }
            throw new ParseException(new StringBuffer().append("Not a valid protocol version: ").append(buffer.substring(indexFrom, indexTo)).toString());
        }
    }

    protected ProtocolVersion createProtocolVersion(int major, int minor) {
        return this.protocol.forVersion(major, minor);
    }

    public boolean hasProtocolVersion(CharArrayBuffer buffer, ParserCursor cursor) {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        } else if (cursor == null) {
            throw new IllegalArgumentException("Parser cursor may not be null");
        } else {
            int index = cursor.getPos();
            String protoname = this.protocol.getProtocol();
            int protolength = protoname.length();
            if (buffer.length() < protolength + 4) {
                return false;
            }
            if (index < 0) {
                index = (buffer.length() - 4) - protolength;
            } else if (index == 0) {
                while (index < buffer.length() && HTTP.isWhitespace(buffer.charAt(index))) {
                    index++;
                }
            }
            if ((index + protolength) + 4 > buffer.length()) {
                return false;
            }
            boolean ok = true;
            int j = 0;
            while (ok && j < protolength) {
                if (buffer.charAt(index + j) == protoname.charAt(j)) {
                    ok = true;
                } else {
                    ok = false;
                }
                j++;
            }
            if (ok) {
                if (buffer.charAt(index + protolength) == '/') {
                    ok = true;
                } else {
                    ok = false;
                }
            }
            return ok;
        }
    }

    public static final RequestLine parseRequestLine(String value, LineParser parser) throws ParseException {
        if (value == null) {
            throw new IllegalArgumentException("Value to parse may not be null.");
        }
        if (parser == null) {
            parser = DEFAULT;
        }
        CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        return parser.parseRequestLine(buffer, new ParserCursor(0, value.length()));
    }

    public RequestLine parseRequestLine(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        } else if (cursor == null) {
            throw new IllegalArgumentException("Parser cursor may not be null");
        } else {
            int indexFrom = cursor.getPos();
            int indexTo = cursor.getUpperBound();
            try {
                skipWhitespace(buffer, cursor);
                int i = cursor.getPos();
                int blank = buffer.indexOf(32, i, indexTo);
                if (blank < 0) {
                    throw new ParseException(new StringBuffer().append("Invalid request line: ").append(buffer.substring(indexFrom, indexTo)).toString());
                }
                String method = buffer.substringTrimmed(i, blank);
                cursor.updatePos(blank);
                skipWhitespace(buffer, cursor);
                i = cursor.getPos();
                blank = buffer.indexOf(32, i, indexTo);
                if (blank < 0) {
                    throw new ParseException(new StringBuffer().append("Invalid request line: ").append(buffer.substring(indexFrom, indexTo)).toString());
                }
                String uri = buffer.substringTrimmed(i, blank);
                cursor.updatePos(blank);
                ProtocolVersion ver = parseProtocolVersion(buffer, cursor);
                skipWhitespace(buffer, cursor);
                if (cursor.atEnd()) {
                    return createRequestLine(method, uri, ver);
                }
                throw new ParseException(new StringBuffer().append("Invalid request line: ").append(buffer.substring(indexFrom, indexTo)).toString());
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException(new StringBuffer().append("Invalid request line: ").append(buffer.substring(indexFrom, indexTo)).toString());
            }
        }
    }

    protected RequestLine createRequestLine(String method, String uri, ProtocolVersion ver) {
        return new BasicRequestLine(method, uri, ver);
    }

    public static final StatusLine parseStatusLine(String value, LineParser parser) throws ParseException {
        if (value == null) {
            throw new IllegalArgumentException("Value to parse may not be null.");
        }
        if (parser == null) {
            parser = DEFAULT;
        }
        CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        return parser.parseStatusLine(buffer, new ParserCursor(0, value.length()));
    }

    public StatusLine parseStatusLine(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        } else if (cursor == null) {
            throw new IllegalArgumentException("Parser cursor may not be null");
        } else {
            int indexFrom = cursor.getPos();
            int indexTo = cursor.getUpperBound();
            try {
                String reasonPhrase;
                ProtocolVersion ver = parseProtocolVersion(buffer, cursor);
                skipWhitespace(buffer, cursor);
                int i = cursor.getPos();
                int blank = buffer.indexOf(32, i, indexTo);
                if (blank < 0) {
                    blank = indexTo;
                }
                String s = buffer.substringTrimmed(i, blank);
                int j = 0;
                while (j < s.length()) {
                    if (Character.isDigit(s.charAt(j))) {
                        j++;
                    } else {
                        throw new ParseException(new StringBuffer().append("Status line contains invalid status code: ").append(buffer.substring(indexFrom, indexTo)).toString());
                    }
                }
                int statusCode = Integer.parseInt(s);
                i = blank;
                if (i < indexTo) {
                    reasonPhrase = buffer.substringTrimmed(i, indexTo);
                } else {
                    reasonPhrase = BuildConfig.VERSION_NAME;
                }
                return createStatusLine(ver, statusCode, reasonPhrase);
            } catch (NumberFormatException e) {
                throw new ParseException(new StringBuffer().append("Status line contains invalid status code: ").append(buffer.substring(indexFrom, indexTo)).toString());
            } catch (IndexOutOfBoundsException e2) {
                throw new ParseException(new StringBuffer().append("Invalid status line: ").append(buffer.substring(indexFrom, indexTo)).toString());
            }
        }
    }

    protected StatusLine createStatusLine(ProtocolVersion ver, int status, String reason) {
        return new BasicStatusLine(ver, status, reason);
    }

    public static final Header parseHeader(String value, LineParser parser) throws ParseException {
        if (value == null) {
            throw new IllegalArgumentException("Value to parse may not be null");
        }
        if (parser == null) {
            parser = DEFAULT;
        }
        CharArrayBuffer buffer = new CharArrayBuffer(value.length());
        buffer.append(value);
        return parser.parseHeader(buffer);
    }

    public Header parseHeader(CharArrayBuffer buffer) throws ParseException {
        return new BufferedHeader(buffer);
    }

    protected void skipWhitespace(CharArrayBuffer buffer, ParserCursor cursor) {
        int pos = cursor.getPos();
        int indexTo = cursor.getUpperBound();
        while (pos < indexTo && HTTP.isWhitespace(buffer.charAt(pos))) {
            pos++;
        }
        cursor.updatePos(pos);
    }
}
