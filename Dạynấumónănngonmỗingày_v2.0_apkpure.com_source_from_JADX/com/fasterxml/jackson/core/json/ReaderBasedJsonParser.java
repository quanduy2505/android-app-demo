package com.fasterxml.jackson.core.json;

import com.facebook.internal.ServerProtocol;
import com.facebook.share.internal.ShareConstants;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.TextBuffer;
import com.google.android.gms.common.ConnectionResult;
import com.tapadoo.android.C0784R;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import me.wangyuwei.loadingview.C0801R;
import org.apache.http.HttpStatus;
import rx.internal.operators.OnSubscribeConcatMap;

public final class ReaderBasedJsonParser extends ParserBase {
    protected final int _hashSeed;
    protected char[] _inputBuffer;
    protected ObjectCodec _objectCodec;
    protected Reader _reader;
    protected final CharsToNameCanonicalizer _symbols;
    protected boolean _tokenIncomplete;

    /* renamed from: com.fasterxml.jackson.core.json.ReaderBasedJsonParser.1 */
    static /* synthetic */ class C04891 {
        static final /* synthetic */ int[] $SwitchMap$com$fasterxml$jackson$core$JsonToken;

        static {
            $SwitchMap$com$fasterxml$jackson$core$JsonToken = new int[JsonToken.values().length];
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.FIELD_NAME.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_STRING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_NUMBER_INT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_NUMBER_FLOAT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_TRUE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_FALSE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    public ReaderBasedJsonParser(IOContext iOContext, int i, Reader reader, ObjectCodec objectCodec, CharsToNameCanonicalizer charsToNameCanonicalizer) {
        super(iOContext, i);
        this._tokenIncomplete = false;
        this._reader = reader;
        this._inputBuffer = iOContext.allocTokenBuffer();
        this._objectCodec = objectCodec;
        this._symbols = charsToNameCanonicalizer;
        this._hashSeed = charsToNameCanonicalizer.hashSeed();
    }

    public ObjectCodec getCodec() {
        return this._objectCodec;
    }

    public void setCodec(ObjectCodec objectCodec) {
        this._objectCodec = objectCodec;
    }

    public int releaseBuffered(Writer writer) throws IOException {
        int i = this._inputEnd - this._inputPtr;
        if (i < 1) {
            return 0;
        }
        writer.write(this._inputBuffer, this._inputPtr, i);
        return i;
    }

    public Object getInputSource() {
        return this._reader;
    }

    protected boolean loadMore() throws IOException {
        this._currInputProcessed += (long) this._inputEnd;
        this._currInputRowStart -= this._inputEnd;
        if (this._reader == null) {
            return false;
        }
        int read = this._reader.read(this._inputBuffer, 0, this._inputBuffer.length);
        if (read > 0) {
            this._inputPtr = 0;
            this._inputEnd = read;
            return true;
        }
        _closeInput();
        if (read != 0) {
            return false;
        }
        throw new IOException("Reader returned 0 characters when trying to read " + this._inputEnd);
    }

    protected char getNextChar(String str) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(str);
        }
        char[] cArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        return cArr[i];
    }

    protected void _closeInput() throws IOException {
        if (this._reader != null) {
            if (this._ioContext.isResourceManaged() || isEnabled(Feature.AUTO_CLOSE_SOURCE)) {
                this._reader.close();
            }
            this._reader = null;
        }
    }

    protected void _releaseBuffers() throws IOException {
        super._releaseBuffers();
        char[] cArr = this._inputBuffer;
        if (cArr != null) {
            this._inputBuffer = null;
            this._ioContext.releaseTokenBuffer(cArr);
        }
    }

    public String getText() throws IOException, JsonParseException {
        JsonToken jsonToken = this._currToken;
        if (jsonToken != JsonToken.VALUE_STRING) {
            return _getText2(jsonToken);
        }
        if (this._tokenIncomplete) {
            this._tokenIncomplete = false;
            _finishString();
        }
        return this._textBuffer.contentsAsString();
    }

    public String getValueAsString() throws IOException, JsonParseException {
        if (this._currToken != JsonToken.VALUE_STRING) {
            return super.getValueAsString(null);
        }
        if (this._tokenIncomplete) {
            this._tokenIncomplete = false;
            _finishString();
        }
        return this._textBuffer.contentsAsString();
    }

    public String getValueAsString(String str) throws IOException, JsonParseException {
        if (this._currToken != JsonToken.VALUE_STRING) {
            return super.getValueAsString(str);
        }
        if (this._tokenIncomplete) {
            this._tokenIncomplete = false;
            _finishString();
        }
        return this._textBuffer.contentsAsString();
    }

    protected String _getText2(JsonToken jsonToken) {
        if (jsonToken == null) {
            return null;
        }
        switch (C04891.$SwitchMap$com$fasterxml$jackson$core$JsonToken[jsonToken.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                return this._parsingContext.getCurrentName();
            case OnSubscribeConcatMap.END /*2*/:
            case ConnectionResult.SERVICE_DISABLED /*3*/:
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                return this._textBuffer.contentsAsString();
            default:
                return jsonToken.asString();
        }
    }

    public char[] getTextCharacters() throws IOException, JsonParseException {
        if (this._currToken == null) {
            return null;
        }
        switch (C04891.$SwitchMap$com$fasterxml$jackson$core$JsonToken[this._currToken.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                if (!this._nameCopied) {
                    String currentName = this._parsingContext.getCurrentName();
                    int length = currentName.length();
                    if (this._nameCopyBuffer == null) {
                        this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(length);
                    } else if (this._nameCopyBuffer.length < length) {
                        this._nameCopyBuffer = new char[length];
                    }
                    currentName.getChars(0, length, this._nameCopyBuffer, 0);
                    this._nameCopied = true;
                }
                return this._nameCopyBuffer;
            case OnSubscribeConcatMap.END /*2*/:
                if (this._tokenIncomplete) {
                    this._tokenIncomplete = false;
                    _finishString();
                    break;
                }
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                break;
            default:
                return this._currToken.asCharArray();
        }
        return this._textBuffer.getTextBuffer();
    }

    public int getTextLength() throws IOException, JsonParseException {
        if (this._currToken == null) {
            return 0;
        }
        switch (C04891.$SwitchMap$com$fasterxml$jackson$core$JsonToken[this._currToken.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                return this._parsingContext.getCurrentName().length();
            case OnSubscribeConcatMap.END /*2*/:
                if (this._tokenIncomplete) {
                    this._tokenIncomplete = false;
                    _finishString();
                    break;
                }
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                break;
            default:
                return this._currToken.asCharArray().length;
        }
        return this._textBuffer.size();
    }

    public int getTextOffset() throws IOException, JsonParseException {
        if (this._currToken == null) {
            return 0;
        }
        switch (C04891.$SwitchMap$com$fasterxml$jackson$core$JsonToken[this._currToken.ordinal()]) {
            case OnSubscribeConcatMap.END /*2*/:
                if (this._tokenIncomplete) {
                    this._tokenIncomplete = false;
                    _finishString();
                    break;
                }
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                break;
            default:
                return 0;
        }
        return this._textBuffer.getTextOffset();
    }

    public byte[] getBinaryValue(Base64Variant base64Variant) throws IOException, JsonParseException {
        if (this._currToken != JsonToken.VALUE_STRING && (this._currToken != JsonToken.VALUE_EMBEDDED_OBJECT || this._binaryValue == null)) {
            _reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
        }
        if (this._tokenIncomplete) {
            try {
                this._binaryValue = _decodeBase64(base64Variant);
                this._tokenIncomplete = false;
            } catch (IllegalArgumentException e) {
                throw _constructError("Failed to decode VALUE_STRING as base64 (" + base64Variant + "): " + e.getMessage());
            }
        } else if (this._binaryValue == null) {
            ByteArrayBuilder _getByteArrayBuilder = _getByteArrayBuilder();
            _decodeBase64(getText(), _getByteArrayBuilder, base64Variant);
            this._binaryValue = _getByteArrayBuilder.toByteArray();
        }
        return this._binaryValue;
    }

    public int readBinaryValue(Base64Variant base64Variant, OutputStream outputStream) throws IOException, JsonParseException {
        if (this._tokenIncomplete && this._currToken == JsonToken.VALUE_STRING) {
            byte[] allocBase64Buffer = this._ioContext.allocBase64Buffer();
            try {
                int _readBinary = _readBinary(base64Variant, outputStream, allocBase64Buffer);
                return _readBinary;
            } finally {
                this._ioContext.releaseBase64Buffer(allocBase64Buffer);
            }
        } else {
            byte[] binaryValue = getBinaryValue(base64Variant);
            outputStream.write(binaryValue);
            return binaryValue.length;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected int _readBinary(com.fasterxml.jackson.core.Base64Variant r12, java.io.OutputStream r13, byte[] r14) throws java.io.IOException, com.fasterxml.jackson.core.JsonParseException {
        /*
        r11 = this;
        r10 = 3;
        r9 = 34;
        r8 = -2;
        r1 = 0;
        r0 = r14.length;
        r5 = r0 + -3;
        r0 = r1;
        r2 = r1;
    L_0x000a:
        r3 = r11._inputPtr;
        r4 = r11._inputEnd;
        if (r3 < r4) goto L_0x0013;
    L_0x0010:
        r11.loadMoreGuaranteed();
    L_0x0013:
        r3 = r11._inputBuffer;
        r4 = r11._inputPtr;
        r6 = r4 + 1;
        r11._inputPtr = r6;
        r4 = r3[r4];
        r3 = 32;
        if (r4 <= r3) goto L_0x000a;
    L_0x0021:
        r3 = r12.decodeBase64Char(r4);
        if (r3 >= 0) goto L_0x0038;
    L_0x0027:
        if (r4 != r9) goto L_0x0032;
    L_0x0029:
        r11._tokenIncomplete = r1;
        if (r2 <= 0) goto L_0x0031;
    L_0x002d:
        r0 = r0 + r2;
        r13.write(r14, r1, r2);
    L_0x0031:
        return r0;
    L_0x0032:
        r3 = r11._decodeBase64Escape(r12, r4, r1);
        if (r3 < 0) goto L_0x000a;
    L_0x0038:
        r4 = r3;
        if (r2 <= r5) goto L_0x013b;
    L_0x003b:
        r0 = r0 + r2;
        r13.write(r14, r1, r2);
        r3 = r1;
    L_0x0040:
        r2 = r11._inputPtr;
        r6 = r11._inputEnd;
        if (r2 < r6) goto L_0x0049;
    L_0x0046:
        r11.loadMoreGuaranteed();
    L_0x0049:
        r2 = r11._inputBuffer;
        r6 = r11._inputPtr;
        r7 = r6 + 1;
        r11._inputPtr = r7;
        r6 = r2[r6];
        r2 = r12.decodeBase64Char(r6);
        if (r2 >= 0) goto L_0x005e;
    L_0x0059:
        r2 = 1;
        r2 = r11._decodeBase64Escape(r12, r6, r2);
    L_0x005e:
        r4 = r4 << 6;
        r4 = r4 | r2;
        r2 = r11._inputPtr;
        r6 = r11._inputEnd;
        if (r2 < r6) goto L_0x006a;
    L_0x0067:
        r11.loadMoreGuaranteed();
    L_0x006a:
        r2 = r11._inputBuffer;
        r6 = r11._inputPtr;
        r7 = r6 + 1;
        r11._inputPtr = r7;
        r6 = r2[r6];
        r2 = r12.decodeBase64Char(r6);
        if (r2 >= 0) goto L_0x00d7;
    L_0x007a:
        if (r2 == r8) goto L_0x0091;
    L_0x007c:
        if (r6 != r9) goto L_0x008c;
    L_0x007e:
        r2 = r12.usesPadding();
        if (r2 != 0) goto L_0x008c;
    L_0x0084:
        r4 = r4 >> 4;
        r2 = r3 + 1;
        r4 = (byte) r4;
        r14[r3] = r4;
        goto L_0x0029;
    L_0x008c:
        r2 = 2;
        r2 = r11._decodeBase64Escape(r12, r6, r2);
    L_0x0091:
        if (r2 != r8) goto L_0x00d7;
    L_0x0093:
        r2 = r11._inputPtr;
        r6 = r11._inputEnd;
        if (r2 < r6) goto L_0x009c;
    L_0x0099:
        r11.loadMoreGuaranteed();
    L_0x009c:
        r2 = r11._inputBuffer;
        r6 = r11._inputPtr;
        r7 = r6 + 1;
        r11._inputPtr = r7;
        r2 = r2[r6];
        r6 = r12.usesPaddingChar(r2);
        if (r6 != 0) goto L_0x00ce;
    L_0x00ac:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "expected padding character '";
        r0 = r0.append(r1);
        r1 = r12.getPaddingChar();
        r0 = r0.append(r1);
        r1 = "'";
        r0 = r0.append(r1);
        r0 = r0.toString();
        r0 = r11.reportInvalidBase64Char(r12, r2, r10, r0);
        throw r0;
    L_0x00ce:
        r4 = r4 >> 4;
        r2 = r3 + 1;
        r4 = (byte) r4;
        r14[r3] = r4;
        goto L_0x000a;
    L_0x00d7:
        r4 = r4 << 6;
        r4 = r4 | r2;
        r2 = r11._inputPtr;
        r6 = r11._inputEnd;
        if (r2 < r6) goto L_0x00e3;
    L_0x00e0:
        r11.loadMoreGuaranteed();
    L_0x00e3:
        r2 = r11._inputBuffer;
        r6 = r11._inputPtr;
        r7 = r6 + 1;
        r11._inputPtr = r7;
        r6 = r2[r6];
        r2 = r12.decodeBase64Char(r6);
        if (r2 >= 0) goto L_0x0123;
    L_0x00f3:
        if (r2 == r8) goto L_0x0111;
    L_0x00f5:
        if (r6 != r9) goto L_0x010d;
    L_0x00f7:
        r2 = r12.usesPadding();
        if (r2 != 0) goto L_0x010d;
    L_0x00fd:
        r4 = r4 >> 2;
        r5 = r3 + 1;
        r2 = r4 >> 8;
        r2 = (byte) r2;
        r14[r3] = r2;
        r2 = r5 + 1;
        r3 = (byte) r4;
        r14[r5] = r3;
        goto L_0x0029;
    L_0x010d:
        r2 = r11._decodeBase64Escape(r12, r6, r10);
    L_0x0111:
        if (r2 != r8) goto L_0x0123;
    L_0x0113:
        r4 = r4 >> 2;
        r6 = r3 + 1;
        r2 = r4 >> 8;
        r2 = (byte) r2;
        r14[r3] = r2;
        r2 = r6 + 1;
        r3 = (byte) r4;
        r14[r6] = r3;
        goto L_0x000a;
    L_0x0123:
        r4 = r4 << 6;
        r4 = r4 | r2;
        r2 = r3 + 1;
        r6 = r4 >> 16;
        r6 = (byte) r6;
        r14[r3] = r6;
        r3 = r2 + 1;
        r6 = r4 >> 8;
        r6 = (byte) r6;
        r14[r2] = r6;
        r2 = r3 + 1;
        r4 = (byte) r4;
        r14[r3] = r4;
        goto L_0x000a;
    L_0x013b:
        r3 = r2;
        goto L_0x0040;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.ReaderBasedJsonParser._readBinary(com.fasterxml.jackson.core.Base64Variant, java.io.OutputStream, byte[]):int");
    }

    public JsonToken nextToken() throws IOException, JsonParseException {
        this._numTypesValid = 0;
        if (this._currToken == JsonToken.FIELD_NAME) {
            return _nextAfterName();
        }
        if (this._tokenIncomplete) {
            _skipString();
        }
        int _skipWSOrEnd = _skipWSOrEnd();
        if (_skipWSOrEnd < 0) {
            close();
            this._currToken = null;
            return null;
        }
        this._tokenInputTotal = (this._currInputProcessed + ((long) this._inputPtr)) - 1;
        this._tokenInputRow = this._currInputRow;
        this._tokenInputCol = (this._inputPtr - this._currInputRowStart) - 1;
        this._binaryValue = null;
        JsonToken jsonToken;
        if (_skipWSOrEnd == 93) {
            if (!this._parsingContext.inArray()) {
                _reportMismatchedEndMarker(_skipWSOrEnd, '}');
            }
            this._parsingContext = this._parsingContext.getParent();
            jsonToken = JsonToken.END_ARRAY;
            this._currToken = jsonToken;
            return jsonToken;
        } else if (_skipWSOrEnd == 125) {
            if (!this._parsingContext.inObject()) {
                _reportMismatchedEndMarker(_skipWSOrEnd, ']');
            }
            this._parsingContext = this._parsingContext.getParent();
            jsonToken = JsonToken.END_OBJECT;
            this._currToken = jsonToken;
            return jsonToken;
        } else {
            if (this._parsingContext.expectComma()) {
                if (_skipWSOrEnd != 44) {
                    _reportUnexpectedChar(_skipWSOrEnd, "was expecting comma to separate " + this._parsingContext.getTypeDesc() + " entries");
                }
                _skipWSOrEnd = _skipWS();
            }
            boolean inObject = this._parsingContext.inObject();
            if (inObject) {
                this._parsingContext.setCurrentName(_parseFieldName(_skipWSOrEnd));
                this._currToken = JsonToken.FIELD_NAME;
                _skipWSOrEnd = _skipWS();
                if (_skipWSOrEnd != 58) {
                    _reportUnexpectedChar(_skipWSOrEnd, "was expecting a colon to separate field name and value");
                }
                _skipWSOrEnd = _skipWS();
            }
            switch (_skipWSOrEnd) {
                case C0801R.styleable.AppCompatTheme_actionModePasteDrawable /*34*/:
                    this._tokenIncomplete = true;
                    jsonToken = JsonToken.VALUE_STRING;
                    break;
                case C0801R.styleable.AppCompatTheme_listDividerAlertDialog /*45*/:
                case C0801R.styleable.AppCompatTheme_spinnerDropDownItemStyle /*48*/:
                case C0801R.styleable.AppCompatTheme_homeAsUpIndicator /*49*/:
                case C0801R.styleable.AppCompatTheme_actionButtonStyle /*50*/:
                case C0801R.styleable.AppCompatTheme_buttonBarStyle /*51*/:
                case C0801R.styleable.AppCompatTheme_buttonBarButtonStyle /*52*/:
                case C0801R.styleable.AppCompatTheme_selectableItemBackground /*53*/:
                case C0801R.styleable.AppCompatTheme_selectableItemBackgroundBorderless /*54*/:
                case C0801R.styleable.AppCompatTheme_borderlessButtonStyle /*55*/:
                case C0801R.styleable.AppCompatTheme_dividerVertical /*56*/:
                case C0801R.styleable.AppCompatTheme_dividerHorizontal /*57*/:
                    jsonToken = parseNumberText(_skipWSOrEnd);
                    break;
                case C0801R.styleable.AppCompatTheme_controlBackground /*91*/:
                    if (!inObject) {
                        this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                    }
                    jsonToken = JsonToken.START_ARRAY;
                    break;
                case C0801R.styleable.AppCompatTheme_alertDialogStyle /*93*/:
                case 125:
                    _reportUnexpectedChar(_skipWSOrEnd, "expected a value");
                    break;
                case HttpStatus.SC_PROCESSING /*102*/:
                    _matchToken("false", 1);
                    jsonToken = JsonToken.VALUE_FALSE;
                    break;
                case C0801R.styleable.AppCompatTheme_ratingBarStyleSmall /*110*/:
                    _matchToken("null", 1);
                    jsonToken = JsonToken.VALUE_NULL;
                    break;
                case 116:
                    break;
                case 123:
                    if (!inObject) {
                        this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                    }
                    jsonToken = JsonToken.START_OBJECT;
                    break;
                default:
                    jsonToken = _handleUnexpectedValue(_skipWSOrEnd);
                    break;
            }
            _matchToken(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE, 1);
            jsonToken = JsonToken.VALUE_TRUE;
            if (inObject) {
                this._nextToken = jsonToken;
                return this._currToken;
            }
            this._currToken = jsonToken;
            return jsonToken;
        }
    }

    private JsonToken _nextAfterName() {
        this._nameCopied = false;
        JsonToken jsonToken = this._nextToken;
        this._nextToken = null;
        if (jsonToken == JsonToken.START_ARRAY) {
            this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
        } else if (jsonToken == JsonToken.START_OBJECT) {
            this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
        }
        this._currToken = jsonToken;
        return jsonToken;
    }

    public String nextTextValue() throws IOException, JsonParseException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            this._nameCopied = false;
            JsonToken jsonToken = this._nextToken;
            this._nextToken = null;
            this._currToken = jsonToken;
            if (jsonToken == JsonToken.VALUE_STRING) {
                if (this._tokenIncomplete) {
                    this._tokenIncomplete = false;
                    _finishString();
                }
                return this._textBuffer.contentsAsString();
            } else if (jsonToken == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                return null;
            } else if (jsonToken != JsonToken.START_OBJECT) {
                return null;
            } else {
                this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                return null;
            }
        } else if (nextToken() == JsonToken.VALUE_STRING) {
            return getText();
        } else {
            return null;
        }
    }

    public int nextIntValue(int i) throws IOException, JsonParseException {
        if (this._currToken != JsonToken.FIELD_NAME) {
            return nextToken() == JsonToken.VALUE_NUMBER_INT ? getIntValue() : i;
        } else {
            this._nameCopied = false;
            JsonToken jsonToken = this._nextToken;
            this._nextToken = null;
            this._currToken = jsonToken;
            if (jsonToken == JsonToken.VALUE_NUMBER_INT) {
                return getIntValue();
            }
            if (jsonToken == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                return i;
            } else if (jsonToken != JsonToken.START_OBJECT) {
                return i;
            } else {
                this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                return i;
            }
        }
    }

    public long nextLongValue(long j) throws IOException, JsonParseException {
        if (this._currToken != JsonToken.FIELD_NAME) {
            return nextToken() == JsonToken.VALUE_NUMBER_INT ? getLongValue() : j;
        } else {
            this._nameCopied = false;
            JsonToken jsonToken = this._nextToken;
            this._nextToken = null;
            this._currToken = jsonToken;
            if (jsonToken == JsonToken.VALUE_NUMBER_INT) {
                return getLongValue();
            }
            if (jsonToken == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                return j;
            } else if (jsonToken != JsonToken.START_OBJECT) {
                return j;
            } else {
                this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                return j;
            }
        }
    }

    public Boolean nextBooleanValue() throws IOException, JsonParseException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            this._nameCopied = false;
            JsonToken jsonToken = this._nextToken;
            this._nextToken = null;
            this._currToken = jsonToken;
            if (jsonToken == JsonToken.VALUE_TRUE) {
                return Boolean.TRUE;
            }
            if (jsonToken == JsonToken.VALUE_FALSE) {
                return Boolean.FALSE;
            }
            if (jsonToken == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                return null;
            } else if (jsonToken != JsonToken.START_OBJECT) {
                return null;
            } else {
                this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                return null;
            }
        }
        switch (C04891.$SwitchMap$com$fasterxml$jackson$core$JsonToken[nextToken().ordinal()]) {
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                return Boolean.TRUE;
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                return Boolean.FALSE;
            default:
                return null;
        }
    }

    public void close() throws IOException {
        super.close();
        this._symbols.release();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected com.fasterxml.jackson.core.JsonToken parseNumberText(int r14) throws java.io.IOException, com.fasterxml.jackson.core.JsonParseException {
        /*
        r13 = this;
        r11 = 45;
        r1 = 1;
        r2 = 0;
        r10 = 57;
        r9 = 48;
        if (r14 != r11) goto L_0x0022;
    L_0x000a:
        r0 = r1;
    L_0x000b:
        r4 = r13._inputPtr;
        r5 = r4 + -1;
        r7 = r13._inputEnd;
        if (r0 == 0) goto L_0x0035;
    L_0x0013:
        r3 = r13._inputEnd;
        if (r4 < r3) goto L_0x0024;
    L_0x0017:
        if (r0 == 0) goto L_0x00b4;
    L_0x0019:
        r1 = r5 + 1;
    L_0x001b:
        r13._inputPtr = r1;
        r0 = r13.parseNumberText2(r0);
    L_0x0021:
        return r0;
    L_0x0022:
        r0 = r2;
        goto L_0x000b;
    L_0x0024:
        r6 = r13._inputBuffer;
        r3 = r4 + 1;
        r14 = r6[r4];
        if (r14 > r10) goto L_0x002e;
    L_0x002c:
        if (r14 >= r9) goto L_0x0036;
    L_0x002e:
        r13._inputPtr = r3;
        r0 = r13._handleInvalidNumberStart(r14, r1);
        goto L_0x0021;
    L_0x0035:
        r3 = r4;
    L_0x0036:
        if (r14 == r9) goto L_0x0017;
    L_0x0038:
        r4 = r13._inputEnd;
        if (r3 >= r4) goto L_0x0017;
    L_0x003c:
        r6 = r13._inputBuffer;
        r4 = r3 + 1;
        r3 = r6[r3];
        if (r3 < r9) goto L_0x0046;
    L_0x0044:
        if (r3 <= r10) goto L_0x0093;
    L_0x0046:
        r6 = 46;
        if (r3 != r6) goto L_0x00bb;
    L_0x004a:
        r3 = r2;
        r6 = r4;
    L_0x004c:
        if (r6 >= r7) goto L_0x0017;
    L_0x004e:
        r8 = r13._inputBuffer;
        r4 = r6 + 1;
        r6 = r8[r6];
        if (r6 < r9) goto L_0x0058;
    L_0x0056:
        if (r6 <= r10) goto L_0x0097;
    L_0x0058:
        if (r3 != 0) goto L_0x005f;
    L_0x005a:
        r8 = "Decimal point not followed by a digit";
        r13.reportUnexpectedNumberChar(r6, r8);
    L_0x005f:
        r12 = r3;
        r3 = r4;
        r4 = r6;
        r6 = r12;
    L_0x0063:
        r8 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r4 == r8) goto L_0x006b;
    L_0x0067:
        r8 = 69;
        if (r4 != r8) goto L_0x00a2;
    L_0x006b:
        if (r3 >= r7) goto L_0x0017;
    L_0x006d:
        r8 = r13._inputBuffer;
        r4 = r3 + 1;
        r3 = r8[r3];
        if (r3 == r11) goto L_0x0079;
    L_0x0075:
        r8 = 43;
        if (r3 != r8) goto L_0x00b7;
    L_0x0079:
        if (r4 >= r7) goto L_0x0017;
    L_0x007b:
        r8 = r13._inputBuffer;
        r3 = r4 + 1;
        r4 = r8[r4];
    L_0x0081:
        if (r4 > r10) goto L_0x009b;
    L_0x0083:
        if (r4 < r9) goto L_0x009b;
    L_0x0085:
        r2 = r2 + 1;
        if (r3 >= r7) goto L_0x0017;
    L_0x0089:
        r8 = r13._inputBuffer;
        r4 = r3 + 1;
        r3 = r8[r3];
        r12 = r4;
        r4 = r3;
        r3 = r12;
        goto L_0x0081;
    L_0x0093:
        r1 = r1 + 1;
        r3 = r4;
        goto L_0x0038;
    L_0x0097:
        r3 = r3 + 1;
        r6 = r4;
        goto L_0x004c;
    L_0x009b:
        if (r2 != 0) goto L_0x00a2;
    L_0x009d:
        r7 = "Exponent indicator not followed by a digit";
        r13.reportUnexpectedNumberChar(r4, r7);
    L_0x00a2:
        r3 = r3 + -1;
        r13._inputPtr = r3;
        r3 = r3 - r5;
        r4 = r13._textBuffer;
        r7 = r13._inputBuffer;
        r4.resetWithShared(r7, r5, r3);
        r0 = r13.reset(r0, r1, r6, r2);
        goto L_0x0021;
    L_0x00b4:
        r1 = r5;
        goto L_0x001b;
    L_0x00b7:
        r12 = r4;
        r4 = r3;
        r3 = r12;
        goto L_0x0081;
    L_0x00bb:
        r6 = r2;
        r12 = r4;
        r4 = r3;
        r3 = r12;
        goto L_0x0063;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.ReaderBasedJsonParser.parseNumberText(int):com.fasterxml.jackson.core.JsonToken");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.fasterxml.jackson.core.JsonToken parseNumberText2(boolean r15) throws java.io.IOException, com.fasterxml.jackson.core.JsonParseException {
        /*
        r14 = this;
        r10 = 45;
        r12 = 57;
        r11 = 48;
        r1 = 1;
        r2 = 0;
        r0 = r14._textBuffer;
        r4 = r0.emptyAndGetCurrentSegment();
        if (r15 == 0) goto L_0x01a0;
    L_0x0010:
        r4[r2] = r10;
        r0 = r1;
    L_0x0013:
        r3 = r14._inputPtr;
        r5 = r14._inputEnd;
        if (r3 >= r5) goto L_0x0126;
    L_0x0019:
        r3 = r14._inputBuffer;
        r5 = r14._inputPtr;
        r6 = r5 + 1;
        r14._inputPtr = r6;
        r3 = r3[r5];
    L_0x0023:
        if (r3 != r11) goto L_0x0029;
    L_0x0025:
        r3 = r14._verifyNoLeadingZeroes();
    L_0x0029:
        r5 = r2;
        r13 = r3;
        r3 = r4;
        r4 = r13;
    L_0x002d:
        if (r4 < r11) goto L_0x0199;
    L_0x002f:
        if (r4 > r12) goto L_0x0199;
    L_0x0031:
        r5 = r5 + 1;
        r6 = r3.length;
        if (r0 < r6) goto L_0x003e;
    L_0x0036:
        r0 = r14._textBuffer;
        r0 = r0.finishCurrentSegment();
        r3 = r0;
        r0 = r2;
    L_0x003e:
        r6 = r0 + 1;
        r3[r0] = r4;
        r0 = r14._inputPtr;
        r4 = r14._inputEnd;
        if (r0 < r4) goto L_0x012e;
    L_0x0048:
        r0 = r14.loadMore();
        if (r0 != 0) goto L_0x012e;
    L_0x004e:
        r7 = r1;
        r0 = r2;
        r9 = r5;
        r4 = r3;
        r5 = r6;
    L_0x0053:
        if (r9 != 0) goto L_0x0075;
    L_0x0055:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r6 = "Missing integer part (next char ";
        r3 = r3.append(r6);
        r6 = com.fasterxml.jackson.core.base.ParserMinimalBase._getCharDesc(r0);
        r3 = r3.append(r6);
        r6 = ")";
        r3 = r3.append(r6);
        r3 = r3.toString();
        r14.reportInvalidNumber(r3);
    L_0x0075:
        r3 = 46;
        if (r0 != r3) goto L_0x0192;
    L_0x0079:
        r3 = r5 + 1;
        r4[r5] = r0;
        r5 = r4;
        r4 = r3;
        r3 = r0;
        r0 = r2;
    L_0x0081:
        r6 = r14._inputPtr;
        r8 = r14._inputEnd;
        if (r6 < r8) goto L_0x013b;
    L_0x0087:
        r6 = r14.loadMore();
        if (r6 != 0) goto L_0x013b;
    L_0x008d:
        r6 = r3;
        r3 = r1;
    L_0x008f:
        if (r0 != 0) goto L_0x0096;
    L_0x0091:
        r7 = "Decimal point not followed by a digit";
        r14.reportUnexpectedNumberChar(r6, r7);
    L_0x0096:
        r8 = r0;
        r0 = r4;
        r13 = r3;
        r3 = r5;
        r5 = r13;
    L_0x009b:
        r4 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r6 == r4) goto L_0x00a3;
    L_0x009f:
        r4 = 69;
        if (r6 != r4) goto L_0x0189;
    L_0x00a3:
        r4 = r3.length;
        if (r0 < r4) goto L_0x00ae;
    L_0x00a6:
        r0 = r14._textBuffer;
        r0 = r0.finishCurrentSegment();
        r3 = r0;
        r0 = r2;
    L_0x00ae:
        r4 = r0 + 1;
        r3[r0] = r6;
        r0 = r14._inputPtr;
        r6 = r14._inputEnd;
        if (r0 >= r6) goto L_0x015f;
    L_0x00b8:
        r0 = r14._inputBuffer;
        r6 = r14._inputPtr;
        r7 = r6 + 1;
        r14._inputPtr = r7;
        r6 = r0[r6];
    L_0x00c2:
        if (r6 == r10) goto L_0x00c8;
    L_0x00c4:
        r0 = 43;
        if (r6 != r0) goto L_0x0184;
    L_0x00c8:
        r0 = r3.length;
        if (r4 < r0) goto L_0x0181;
    L_0x00cb:
        r0 = r14._textBuffer;
        r3 = r0.finishCurrentSegment();
        r0 = r2;
    L_0x00d2:
        r4 = r0 + 1;
        r3[r0] = r6;
        r0 = r14._inputPtr;
        r6 = r14._inputEnd;
        if (r0 >= r6) goto L_0x0167;
    L_0x00dc:
        r0 = r14._inputBuffer;
        r6 = r14._inputPtr;
        r7 = r6 + 1;
        r14._inputPtr = r7;
        r0 = r0[r6];
    L_0x00e6:
        r7 = r0;
        r0 = r4;
        r4 = r2;
    L_0x00e9:
        if (r7 > r12) goto L_0x017d;
    L_0x00eb:
        if (r7 < r11) goto L_0x017d;
    L_0x00ed:
        r4 = r4 + 1;
        r6 = r3.length;
        if (r0 < r6) goto L_0x00fa;
    L_0x00f2:
        r0 = r14._textBuffer;
        r0 = r0.finishCurrentSegment();
        r3 = r0;
        r0 = r2;
    L_0x00fa:
        r6 = r0 + 1;
        r3[r0] = r7;
        r0 = r14._inputPtr;
        r10 = r14._inputEnd;
        if (r0 < r10) goto L_0x016f;
    L_0x0104:
        r0 = r14.loadMore();
        if (r0 != 0) goto L_0x016f;
    L_0x010a:
        r2 = r4;
        r0 = r1;
        r1 = r6;
    L_0x010d:
        if (r2 != 0) goto L_0x0114;
    L_0x010f:
        r3 = "Exponent indicator not followed by a digit";
        r14.reportUnexpectedNumberChar(r7, r3);
    L_0x0114:
        if (r0 != 0) goto L_0x011c;
    L_0x0116:
        r0 = r14._inputPtr;
        r0 = r0 + -1;
        r14._inputPtr = r0;
    L_0x011c:
        r0 = r14._textBuffer;
        r0.setCurrentLength(r1);
        r0 = r14.reset(r15, r9, r8, r2);
        return r0;
    L_0x0126:
        r3 = "No digit following minus sign";
        r3 = r14.getNextChar(r3);
        goto L_0x0023;
    L_0x012e:
        r0 = r14._inputBuffer;
        r4 = r14._inputPtr;
        r7 = r4 + 1;
        r14._inputPtr = r7;
        r4 = r0[r4];
        r0 = r6;
        goto L_0x002d;
    L_0x013b:
        r3 = r14._inputBuffer;
        r6 = r14._inputPtr;
        r8 = r6 + 1;
        r14._inputPtr = r8;
        r3 = r3[r6];
        if (r3 < r11) goto L_0x018e;
    L_0x0147:
        if (r3 <= r12) goto L_0x014d;
    L_0x0149:
        r6 = r3;
        r3 = r7;
        goto L_0x008f;
    L_0x014d:
        r0 = r0 + 1;
        r6 = r5.length;
        if (r4 < r6) goto L_0x018c;
    L_0x0152:
        r4 = r14._textBuffer;
        r5 = r4.finishCurrentSegment();
        r6 = r2;
    L_0x0159:
        r4 = r6 + 1;
        r5[r6] = r3;
        goto L_0x0081;
    L_0x015f:
        r0 = "expected a digit for number exponent";
        r6 = r14.getNextChar(r0);
        goto L_0x00c2;
    L_0x0167:
        r0 = "expected a digit for number exponent";
        r0 = r14.getNextChar(r0);
        goto L_0x00e6;
    L_0x016f:
        r0 = r14._inputBuffer;
        r7 = r14._inputPtr;
        r10 = r7 + 1;
        r14._inputPtr = r10;
        r0 = r0[r7];
        r7 = r0;
        r0 = r6;
        goto L_0x00e9;
    L_0x017d:
        r2 = r4;
        r1 = r0;
        r0 = r5;
        goto L_0x010d;
    L_0x0181:
        r0 = r4;
        goto L_0x00d2;
    L_0x0184:
        r7 = r6;
        r0 = r4;
        r4 = r2;
        goto L_0x00e9;
    L_0x0189:
        r1 = r0;
        r0 = r5;
        goto L_0x0114;
    L_0x018c:
        r6 = r4;
        goto L_0x0159;
    L_0x018e:
        r6 = r3;
        r3 = r7;
        goto L_0x008f;
    L_0x0192:
        r8 = r2;
        r6 = r0;
        r3 = r4;
        r0 = r5;
        r5 = r7;
        goto L_0x009b;
    L_0x0199:
        r7 = r2;
        r9 = r5;
        r5 = r0;
        r0 = r4;
        r4 = r3;
        goto L_0x0053;
    L_0x01a0:
        r0 = r2;
        goto L_0x0013;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.ReaderBasedJsonParser.parseNumberText2(boolean):com.fasterxml.jackson.core.JsonToken");
    }

    private char _verifyNoLeadingZeroes() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            return '0';
        }
        char c = this._inputBuffer[this._inputPtr];
        if (c < '0' || c > '9') {
            return '0';
        }
        if (!isEnabled(Feature.ALLOW_NUMERIC_LEADING_ZEROS)) {
            reportInvalidNumber("Leading zeroes not allowed");
        }
        this._inputPtr++;
        if (c != '0') {
            return c;
        }
        do {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                return c;
            }
            c = this._inputBuffer[this._inputPtr];
            if (c < '0' || c > '9') {
                return '0';
            }
            this._inputPtr++;
        } while (c == '0');
        return c;
    }

    protected JsonToken _handleInvalidNumberStart(int i, boolean z) throws IOException, JsonParseException {
        double d = Double.NEGATIVE_INFINITY;
        if (i == 73) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOFInValue();
            }
            char[] cArr = this._inputBuffer;
            int i2 = this._inputPtr;
            this._inputPtr = i2 + 1;
            i = cArr[i2];
            String str;
            if (i == 78) {
                str = z ? "-INF" : "+INF";
                _matchToken(str, 3);
                if (isEnabled(Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
                    if (!z) {
                        d = Double.POSITIVE_INFINITY;
                    }
                    return resetAsNaN(str, d);
                }
                _reportError("Non-standard token '" + str + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
            } else if (i == C0801R.styleable.AppCompatTheme_ratingBarStyleSmall) {
                str = z ? "-Infinity" : "+Infinity";
                _matchToken(str, 3);
                if (isEnabled(Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
                    if (!z) {
                        d = Double.POSITIVE_INFINITY;
                    }
                    return resetAsNaN(str, d);
                }
                _reportError("Non-standard token '" + str + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
            }
        }
        reportUnexpectedNumberChar(i, "expected digit (0-9) to follow minus sign, for valid numeric value");
        return null;
    }

    protected String _parseFieldName(int i) throws IOException, JsonParseException {
        if (i != 34) {
            return _handleUnusualFieldName(i);
        }
        int i2 = this._inputPtr;
        int i3 = this._hashSeed;
        int i4 = this._inputEnd;
        if (i2 < i4) {
            int[] inputCodeLatin1 = CharTypes.getInputCodeLatin1();
            char length = inputCodeLatin1.length;
            do {
                char c = this._inputBuffer[i2];
                if (c >= length || inputCodeLatin1[c] == 0) {
                    i3 = (i3 * 33) + c;
                    i2++;
                } else if (c == '\"') {
                    i4 = this._inputPtr;
                    this._inputPtr = i2 + 1;
                    return this._symbols.findSymbol(this._inputBuffer, i4, i2 - i4, i3);
                }
            } while (i2 < i4);
        }
        i4 = this._inputPtr;
        this._inputPtr = i2;
        return _parseFieldName2(i4, i3, 34);
    }

    private String _parseFieldName2(int i, int i2, int i3) throws IOException, JsonParseException {
        this._textBuffer.resetWithShared(this._inputBuffer, i, this._inputPtr - i);
        char[] currentSegment = this._textBuffer.getCurrentSegment();
        int currentSegmentSize = this._textBuffer.getCurrentSegmentSize();
        while (true) {
            char _decodeEscaped;
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(": was expecting closing '" + ((char) i3) + "' for name");
            }
            char[] cArr = this._inputBuffer;
            int i4 = this._inputPtr;
            this._inputPtr = i4 + 1;
            char c = cArr[i4];
            if (c <= '\\') {
                if (c == '\\') {
                    _decodeEscaped = _decodeEscaped();
                    i2 = (i2 * 33) + c;
                    i4 = currentSegmentSize + 1;
                    currentSegment[currentSegmentSize] = _decodeEscaped;
                    if (i4 < currentSegment.length) {
                        currentSegment = this._textBuffer.finishCurrentSegment();
                        currentSegmentSize = 0;
                    } else {
                        currentSegmentSize = i4;
                    }
                } else if (c <= i3) {
                    if (c == i3) {
                        this._textBuffer.setCurrentLength(currentSegmentSize);
                        TextBuffer textBuffer = this._textBuffer;
                        return this._symbols.findSymbol(textBuffer.getTextBuffer(), textBuffer.getTextOffset(), textBuffer.size(), i2);
                    } else if (c < ' ') {
                        _throwUnquotedSpace(c, ShareConstants.WEB_DIALOG_PARAM_NAME);
                    }
                }
            }
            _decodeEscaped = c;
            i2 = (i2 * 33) + c;
            i4 = currentSegmentSize + 1;
            currentSegment[currentSegmentSize] = _decodeEscaped;
            if (i4 < currentSegment.length) {
                currentSegmentSize = i4;
            } else {
                currentSegment = this._textBuffer.finishCurrentSegment();
                currentSegmentSize = 0;
            }
        }
    }

    protected String _handleUnusualFieldName(int i) throws IOException, JsonParseException {
        if (i == 39 && isEnabled(Feature.ALLOW_SINGLE_QUOTES)) {
            return _parseApostropheFieldName();
        }
        if (!isEnabled(Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
            _reportUnexpectedChar(i, "was expecting double-quote to start field name");
        }
        int[] inputCodeLatin1JsNames = CharTypes.getInputCodeLatin1JsNames();
        char length = inputCodeLatin1JsNames.length;
        boolean isJavaIdentifierPart = i < length ? inputCodeLatin1JsNames[i] == 0 && (i < 48 || i > 57) : Character.isJavaIdentifierPart((char) i);
        if (!isJavaIdentifierPart) {
            _reportUnexpectedChar(i, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
        }
        int i2 = this._inputPtr;
        int i3 = this._hashSeed;
        int i4 = this._inputEnd;
        if (i2 < i4) {
            do {
                char c = this._inputBuffer[i2];
                int i5;
                if (c < length) {
                    if (inputCodeLatin1JsNames[c] != 0) {
                        i5 = this._inputPtr - 1;
                        this._inputPtr = i2;
                        return this._symbols.findSymbol(this._inputBuffer, i5, i2 - i5, i3);
                    }
                } else if (!Character.isJavaIdentifierPart((char) c)) {
                    i5 = this._inputPtr - 1;
                    this._inputPtr = i2;
                    return this._symbols.findSymbol(this._inputBuffer, i5, i2 - i5, i3);
                }
                i3 = (i3 * 33) + c;
                i2++;
            } while (i2 < i4);
        }
        int i6 = this._inputPtr - 1;
        this._inputPtr = i2;
        return _parseUnusualFieldName2(i6, i3, inputCodeLatin1JsNames);
    }

    protected String _parseApostropheFieldName() throws IOException, JsonParseException {
        int i = this._inputPtr;
        int i2 = this._hashSeed;
        int i3 = this._inputEnd;
        if (i < i3) {
            int[] inputCodeLatin1 = CharTypes.getInputCodeLatin1();
            char length = inputCodeLatin1.length;
            do {
                char c = this._inputBuffer[i];
                if (c != '\'') {
                    if (c < length && inputCodeLatin1[c] != 0) {
                        break;
                    }
                    i2 = (i2 * 33) + c;
                    i++;
                } else {
                    i3 = this._inputPtr;
                    this._inputPtr = i + 1;
                    return this._symbols.findSymbol(this._inputBuffer, i3, i - i3, i2);
                }
            } while (i < i3);
        }
        i3 = this._inputPtr;
        this._inputPtr = i;
        return _parseFieldName2(i3, i2, 39);
    }

    protected JsonToken _handleUnexpectedValue(int i) throws IOException, JsonParseException {
        switch (i) {
            case C0801R.styleable.AppCompatTheme_actionModePopupWindowStyle /*39*/:
                if (isEnabled(Feature.ALLOW_SINGLE_QUOTES)) {
                    return _handleApostropheValue();
                }
                break;
            case C0801R.styleable.AppCompatTheme_dialogTheme /*43*/:
                if (this._inputPtr >= this._inputEnd && !loadMore()) {
                    _reportInvalidEOFInValue();
                }
                char[] cArr = this._inputBuffer;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                return _handleInvalidNumberStart(cArr[i2], false);
            case C0801R.styleable.AppCompatTheme_textAppearanceListItemSmall /*78*/:
                _matchToken("NaN", 1);
                if (!isEnabled(Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
                    _reportError("Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
                    break;
                }
                return resetAsNaN("NaN", Double.NaN);
        }
        _reportUnexpectedChar(i, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
        return null;
    }

    protected JsonToken _handleApostropheValue() throws IOException, JsonParseException {
        char[] emptyAndGetCurrentSegment = this._textBuffer.emptyAndGetCurrentSegment();
        int currentSegmentSize = this._textBuffer.getCurrentSegmentSize();
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(": was expecting closing quote for a string value");
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char c = cArr[i];
            if (c <= '\\') {
                if (c == '\\') {
                    c = _decodeEscaped();
                } else if (c <= '\'') {
                    if (c == '\'') {
                        this._textBuffer.setCurrentLength(currentSegmentSize);
                        return JsonToken.VALUE_STRING;
                    } else if (c < ' ') {
                        _throwUnquotedSpace(c, "string value");
                    }
                }
            }
            if (currentSegmentSize >= emptyAndGetCurrentSegment.length) {
                emptyAndGetCurrentSegment = this._textBuffer.finishCurrentSegment();
                i = 0;
            } else {
                i = currentSegmentSize;
            }
            currentSegmentSize = i + 1;
            emptyAndGetCurrentSegment[i] = c;
        }
    }

    private String _parseUnusualFieldName2(int i, int i2, int[] iArr) throws IOException, JsonParseException {
        this._textBuffer.resetWithShared(this._inputBuffer, i, this._inputPtr - i);
        char[] currentSegment = this._textBuffer.getCurrentSegment();
        int currentSegmentSize = this._textBuffer.getCurrentSegmentSize();
        char length = iArr.length;
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                break;
            }
            char c = this._inputBuffer[this._inputPtr];
            if (c > length) {
                if (!Character.isJavaIdentifierPart(c)) {
                    break;
                }
            } else if (iArr[c] != 0) {
                break;
            }
            this._inputPtr++;
            i2 = (i2 * 33) + c;
            int i3 = currentSegmentSize + 1;
            currentSegment[currentSegmentSize] = c;
            if (i3 >= currentSegment.length) {
                currentSegment = this._textBuffer.finishCurrentSegment();
                currentSegmentSize = 0;
            } else {
                currentSegmentSize = i3;
            }
        }
        this._textBuffer.setCurrentLength(currentSegmentSize);
        TextBuffer textBuffer = this._textBuffer;
        return this._symbols.findSymbol(textBuffer.getTextBuffer(), textBuffer.getTextOffset(), textBuffer.size(), i2);
    }

    protected void _finishString() throws IOException, JsonParseException {
        int i = this._inputPtr;
        int i2 = this._inputEnd;
        if (i < i2) {
            int[] inputCodeLatin1 = CharTypes.getInputCodeLatin1();
            char length = inputCodeLatin1.length;
            do {
                char c = this._inputBuffer[i];
                if (c >= length || inputCodeLatin1[c] == 0) {
                    i++;
                } else if (c == '\"') {
                    this._textBuffer.resetWithShared(this._inputBuffer, this._inputPtr, i - this._inputPtr);
                    this._inputPtr = i + 1;
                    return;
                }
            } while (i < i2);
        }
        this._textBuffer.resetWithCopy(this._inputBuffer, this._inputPtr, i - this._inputPtr);
        this._inputPtr = i;
        _finishString2();
    }

    protected void _finishString2() throws IOException, JsonParseException {
        char[] currentSegment = this._textBuffer.getCurrentSegment();
        int currentSegmentSize = this._textBuffer.getCurrentSegmentSize();
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(": was expecting closing quote for a string value");
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char c = cArr[i];
            if (c <= '\\') {
                if (c == '\\') {
                    c = _decodeEscaped();
                } else if (c <= '\"') {
                    if (c == '\"') {
                        this._textBuffer.setCurrentLength(currentSegmentSize);
                        return;
                    } else if (c < ' ') {
                        _throwUnquotedSpace(c, "string value");
                    }
                }
            }
            if (currentSegmentSize >= currentSegment.length) {
                currentSegment = this._textBuffer.finishCurrentSegment();
                i = 0;
            } else {
                i = currentSegmentSize;
            }
            currentSegmentSize = i + 1;
            currentSegment[i] = c;
        }
    }

    protected void _skipString() throws IOException, JsonParseException {
        this._tokenIncomplete = false;
        int i = this._inputPtr;
        int i2 = this._inputEnd;
        char[] cArr = this._inputBuffer;
        while (true) {
            if (i >= i2) {
                this._inputPtr = i;
                if (!loadMore()) {
                    _reportInvalidEOF(": was expecting closing quote for a string value");
                }
                i = this._inputPtr;
                i2 = this._inputEnd;
            }
            int i3 = i + 1;
            char c = cArr[i];
            if (c <= '\\') {
                if (c == '\\') {
                    this._inputPtr = i3;
                    _decodeEscaped();
                    i = this._inputPtr;
                    i2 = this._inputEnd;
                } else if (c <= '\"') {
                    if (c == '\"') {
                        this._inputPtr = i3;
                        return;
                    } else if (c < ' ') {
                        this._inputPtr = i3;
                        _throwUnquotedSpace(c, "string value");
                    }
                }
            }
            i = i3;
        }
    }

    protected void _skipCR() throws IOException {
        if ((this._inputPtr < this._inputEnd || loadMore()) && this._inputBuffer[this._inputPtr] == '\n') {
            this._inputPtr++;
        }
        this._currInputRow++;
        this._currInputRowStart = this._inputPtr;
    }

    protected void _skipLF() throws IOException {
        this._currInputRow++;
        this._currInputRowStart = this._inputPtr;
    }

    private int _skipWS() throws IOException, JsonParseException {
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                break;
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char c = cArr[i];
            if (c > ' ') {
                if (c != '/') {
                    return c;
                }
                _skipComment();
            } else if (c != ' ') {
                if (c == '\n') {
                    _skipLF();
                } else if (c == '\r') {
                    _skipCR();
                } else if (c != '\t') {
                    _throwInvalidSpace(c);
                }
            }
        }
        throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.getTypeDesc() + " entries");
    }

    private int _skipWSOrEnd() throws IOException, JsonParseException {
        while (true) {
            if (this._inputPtr < this._inputEnd || loadMore()) {
                char[] cArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int i2 = cArr[i];
                if (i2 > 32) {
                    if (i2 != 47) {
                        return i2;
                    }
                    _skipComment();
                } else if (i2 != 32) {
                    if (i2 == 10) {
                        _skipLF();
                    } else if (i2 == 13) {
                        _skipCR();
                    } else if (i2 != 9) {
                        _throwInvalidSpace(i2);
                    }
                }
            } else {
                _handleEOF();
                return -1;
            }
        }
    }

    private void _skipComment() throws IOException, JsonParseException {
        if (!isEnabled(Feature.ALLOW_COMMENTS)) {
            _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
        }
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(" in a comment");
        }
        char[] cArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        char c = cArr[i];
        if (c == '/') {
            _skipCppComment();
        } else if (c == '*') {
            _skipCComment();
        } else {
            _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
        }
    }

    private void _skipCComment() throws IOException, JsonParseException {
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                break;
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char c = cArr[i];
            if (c <= '*') {
                if (c == '*') {
                    if (this._inputPtr >= this._inputEnd && !loadMore()) {
                        break;
                    } else if (this._inputBuffer[this._inputPtr] == '/') {
                        this._inputPtr++;
                        return;
                    }
                } else if (c < ' ') {
                    if (c == '\n') {
                        _skipLF();
                    } else if (c == '\r') {
                        _skipCR();
                    } else if (c != '\t') {
                        _throwInvalidSpace(c);
                    }
                }
            }
        }
        _reportInvalidEOF(" in a comment");
    }

    private void _skipCppComment() throws IOException, JsonParseException {
        while (true) {
            if (this._inputPtr < this._inputEnd || loadMore()) {
                char[] cArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                char c = cArr[i];
                if (c < ' ') {
                    if (c == '\n') {
                        _skipLF();
                        return;
                    } else if (c == '\r') {
                        _skipCR();
                        return;
                    } else if (c != '\t') {
                        _throwInvalidSpace(c);
                    }
                }
            } else {
                return;
            }
        }
    }

    protected char _decodeEscaped() throws IOException, JsonParseException {
        int i = 0;
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(" in character escape sequence");
        }
        char[] cArr = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        char c = cArr[i2];
        switch (c) {
            case C0801R.styleable.AppCompatTheme_actionModePasteDrawable /*34*/:
            case C0801R.styleable.AppCompatTheme_dropdownListPreferredItemHeight /*47*/:
            case C0784R.styleable.AppCompatTheme_colorBackgroundFloating /*92*/:
                return c;
            case C0801R.styleable.AppCompatTheme_buttonBarPositiveButtonStyle /*98*/:
                return '\b';
            case HttpStatus.SC_PROCESSING /*102*/:
                return '\f';
            case C0801R.styleable.AppCompatTheme_ratingBarStyleSmall /*110*/:
                return '\n';
            case C0784R.styleable.AppCompatTheme_listMenuViewStyle /*114*/:
                return '\r';
            case 't':
                return '\t';
            case 'u':
                for (int i3 = 0; i3 < 4; i3++) {
                    if (this._inputPtr >= this._inputEnd && !loadMore()) {
                        _reportInvalidEOF(" in character escape sequence");
                    }
                    char[] cArr2 = this._inputBuffer;
                    int i4 = this._inputPtr;
                    this._inputPtr = i4 + 1;
                    char c2 = cArr2[i4];
                    i4 = CharTypes.charToHex(c2);
                    if (i4 < 0) {
                        _reportUnexpectedChar(c2, "expected a hex-digit for character escape sequence");
                    }
                    i = (i << 4) | i4;
                }
                return (char) i;
            default:
                return _handleUnrecognizedCharacterEscape(c);
        }
    }

    protected void _matchToken(String str, int i) throws IOException, JsonParseException {
        int length = str.length();
        do {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidToken(str.substring(0, i));
            }
            if (this._inputBuffer[this._inputPtr] != str.charAt(i)) {
                _reportInvalidToken(str.substring(0, i));
            }
            this._inputPtr++;
            i++;
        } while (i < length);
        if (this._inputPtr < this._inputEnd || loadMore()) {
            char c = this._inputBuffer[this._inputPtr];
            if (c >= '0' && c != ']' && c != '}' && Character.isJavaIdentifierPart(c)) {
                _reportInvalidToken(str.substring(0, i));
            }
        }
    }

    protected byte[] _decodeBase64(Base64Variant base64Variant) throws IOException, JsonParseException {
        char c;
        ByteArrayBuilder _getByteArrayBuilder = _getByteArrayBuilder();
        while (true) {
            if (this._inputPtr >= this._inputEnd) {
                loadMoreGuaranteed();
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char c2 = cArr[i];
            if (c2 > ' ') {
                int decodeBase64Char = base64Variant.decodeBase64Char(c2);
                if (decodeBase64Char < 0) {
                    if (c2 == '\"') {
                        return _getByteArrayBuilder.toByteArray();
                    }
                    decodeBase64Char = _decodeBase64Escape(base64Variant, c2, 0);
                    if (decodeBase64Char < 0) {
                        continue;
                    }
                }
                if (this._inputPtr >= this._inputEnd) {
                    loadMoreGuaranteed();
                }
                char[] cArr2 = this._inputBuffer;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                char c3 = cArr2[i2];
                i = base64Variant.decodeBase64Char(c3);
                if (i < 0) {
                    i = _decodeBase64Escape(base64Variant, c3, 1);
                }
                i |= decodeBase64Char << 6;
                if (this._inputPtr >= this._inputEnd) {
                    loadMoreGuaranteed();
                }
                cArr = this._inputBuffer;
                i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                c3 = cArr[i2];
                decodeBase64Char = base64Variant.decodeBase64Char(c3);
                if (decodeBase64Char < 0) {
                    if (decodeBase64Char != -2) {
                        if (c3 != '\"' || base64Variant.usesPadding()) {
                            decodeBase64Char = _decodeBase64Escape(base64Variant, c3, 2);
                        } else {
                            _getByteArrayBuilder.append(i >> 4);
                            return _getByteArrayBuilder.toByteArray();
                        }
                    }
                    if (decodeBase64Char == -2) {
                        if (this._inputPtr >= this._inputEnd) {
                            loadMoreGuaranteed();
                        }
                        cArr = this._inputBuffer;
                        i2 = this._inputPtr;
                        this._inputPtr = i2 + 1;
                        c = cArr[i2];
                        if (!base64Variant.usesPaddingChar(c)) {
                            break;
                        }
                        _getByteArrayBuilder.append(i >> 4);
                    }
                }
                i = (i << 6) | decodeBase64Char;
                if (this._inputPtr >= this._inputEnd) {
                    loadMoreGuaranteed();
                }
                cArr = this._inputBuffer;
                i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                c3 = cArr[i2];
                decodeBase64Char = base64Variant.decodeBase64Char(c3);
                if (decodeBase64Char < 0) {
                    if (decodeBase64Char != -2) {
                        if (c3 != '\"' || base64Variant.usesPadding()) {
                            decodeBase64Char = _decodeBase64Escape(base64Variant, c3, 3);
                        } else {
                            _getByteArrayBuilder.appendTwoBytes(i >> 2);
                            return _getByteArrayBuilder.toByteArray();
                        }
                    }
                    if (decodeBase64Char == -2) {
                        _getByteArrayBuilder.appendTwoBytes(i >> 2);
                    }
                }
                _getByteArrayBuilder.appendThreeBytes(decodeBase64Char | (i << 6));
            }
        }
        throw reportInvalidBase64Char(base64Variant, c, 3, "expected padding character '" + base64Variant.getPaddingChar() + "'");
    }

    protected void _reportInvalidToken(String str) throws IOException, JsonParseException {
        _reportInvalidToken(str, "'null', 'true', 'false' or NaN");
    }

    protected void _reportInvalidToken(String str, String str2) throws IOException, JsonParseException {
        StringBuilder stringBuilder = new StringBuilder(str);
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                break;
            }
            char c = this._inputBuffer[this._inputPtr];
            if (!Character.isJavaIdentifierPart(c)) {
                break;
            }
            this._inputPtr++;
            stringBuilder.append(c);
        }
        _reportError("Unrecognized token '" + stringBuilder.toString() + "': was expecting ");
    }
}
