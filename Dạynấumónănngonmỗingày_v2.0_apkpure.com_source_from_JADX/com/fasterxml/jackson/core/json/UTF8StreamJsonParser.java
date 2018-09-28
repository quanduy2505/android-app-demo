package com.fasterxml.jackson.core.json;

import android.support.v4.media.TransportMediator;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.facebook.internal.NativeProtocol;
import com.facebook.internal.ServerProtocol;
import com.facebook.share.internal.ShareConstants;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.BytesToNameCanonicalizer;
import com.fasterxml.jackson.core.sym.Name;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.tapadoo.android.C0784R;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import me.wangyuwei.loadingview.C0801R;
import org.apache.http.HttpStatus;
import rx.internal.operators.OnSubscribeConcatMap;

public final class UTF8StreamJsonParser extends ParserBase {
    static final byte BYTE_LF = (byte) 10;
    private static final int[] sInputCodesLatin1;
    private static final int[] sInputCodesUtf8;
    protected boolean _bufferRecyclable;
    protected byte[] _inputBuffer;
    protected InputStream _inputStream;
    protected ObjectCodec _objectCodec;
    private int _quad1;
    protected int[] _quadBuffer;
    protected final BytesToNameCanonicalizer _symbols;
    protected boolean _tokenIncomplete;

    /* renamed from: com.fasterxml.jackson.core.json.UTF8StreamJsonParser.1 */
    static /* synthetic */ class C04901 {
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

    static {
        sInputCodesUtf8 = CharTypes.getInputCodeUtf8();
        sInputCodesLatin1 = CharTypes.getInputCodeLatin1();
    }

    public UTF8StreamJsonParser(IOContext iOContext, int i, InputStream inputStream, ObjectCodec objectCodec, BytesToNameCanonicalizer bytesToNameCanonicalizer, byte[] bArr, int i2, int i3, boolean z) {
        super(iOContext, i);
        this._quadBuffer = new int[16];
        this._tokenIncomplete = false;
        this._inputStream = inputStream;
        this._objectCodec = objectCodec;
        this._symbols = bytesToNameCanonicalizer;
        this._inputBuffer = bArr;
        this._inputPtr = i2;
        this._inputEnd = i3;
        this._bufferRecyclable = z;
    }

    public ObjectCodec getCodec() {
        return this._objectCodec;
    }

    public void setCodec(ObjectCodec objectCodec) {
        this._objectCodec = objectCodec;
    }

    public int releaseBuffered(OutputStream outputStream) throws IOException {
        int i = this._inputEnd - this._inputPtr;
        if (i < 1) {
            return 0;
        }
        outputStream.write(this._inputBuffer, this._inputPtr, i);
        return i;
    }

    public Object getInputSource() {
        return this._inputStream;
    }

    protected boolean loadMore() throws IOException {
        this._currInputProcessed += (long) this._inputEnd;
        this._currInputRowStart -= this._inputEnd;
        if (this._inputStream == null) {
            return false;
        }
        int read = this._inputStream.read(this._inputBuffer, 0, this._inputBuffer.length);
        if (read > 0) {
            this._inputPtr = 0;
            this._inputEnd = read;
            return true;
        }
        _closeInput();
        if (read != 0) {
            return false;
        }
        throw new IOException("InputStream.read() returned 0 characters when trying to read " + this._inputBuffer.length + " bytes");
    }

    protected boolean _loadToHaveAtLeast(int i) throws IOException {
        if (this._inputStream == null) {
            return false;
        }
        int i2 = this._inputEnd - this._inputPtr;
        if (i2 <= 0 || this._inputPtr <= 0) {
            this._inputEnd = 0;
        } else {
            this._currInputProcessed += (long) this._inputPtr;
            this._currInputRowStart -= this._inputPtr;
            System.arraycopy(this._inputBuffer, this._inputPtr, this._inputBuffer, 0, i2);
            this._inputEnd = i2;
        }
        this._inputPtr = 0;
        while (this._inputEnd < i) {
            int read = this._inputStream.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
            if (read < 1) {
                _closeInput();
                if (read != 0) {
                    return false;
                }
                throw new IOException("InputStream.read() returned 0 characters when trying to read " + i2 + " bytes");
            }
            this._inputEnd = read + this._inputEnd;
        }
        return true;
    }

    protected void _closeInput() throws IOException {
        if (this._inputStream != null) {
            if (this._ioContext.isResourceManaged() || isEnabled(Feature.AUTO_CLOSE_SOURCE)) {
                this._inputStream.close();
            }
            this._inputStream = null;
        }
    }

    protected void _releaseBuffers() throws IOException {
        super._releaseBuffers();
        if (this._bufferRecyclable) {
            byte[] bArr = this._inputBuffer;
            if (bArr != null) {
                this._inputBuffer = null;
                this._ioContext.releaseReadIOBuffer(bArr);
            }
        }
    }

    public String getText() throws IOException, JsonParseException {
        if (this._currToken != JsonToken.VALUE_STRING) {
            return _getText2(this._currToken);
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
        switch (C04901.$SwitchMap$com$fasterxml$jackson$core$JsonToken[jsonToken.ordinal()]) {
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
        switch (C04901.$SwitchMap$com$fasterxml$jackson$core$JsonToken[this._currToken.ordinal()]) {
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
        switch (C04901.$SwitchMap$com$fasterxml$jackson$core$JsonToken[this._currToken.ordinal()]) {
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
        switch (C04901.$SwitchMap$com$fasterxml$jackson$core$JsonToken[this._currToken.ordinal()]) {
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
        r3 = r3[r4];
        r4 = r3 & 255;
        r3 = 32;
        if (r4 <= r3) goto L_0x000a;
    L_0x0023:
        r3 = r12.decodeBase64Char(r4);
        if (r3 >= 0) goto L_0x003a;
    L_0x0029:
        if (r4 != r9) goto L_0x0034;
    L_0x002b:
        r11._tokenIncomplete = r1;
        if (r2 <= 0) goto L_0x0033;
    L_0x002f:
        r0 = r0 + r2;
        r13.write(r14, r1, r2);
    L_0x0033:
        return r0;
    L_0x0034:
        r3 = r11._decodeBase64Escape(r12, r4, r1);
        if (r3 < 0) goto L_0x000a;
    L_0x003a:
        r4 = r3;
        if (r2 <= r5) goto L_0x0145;
    L_0x003d:
        r0 = r0 + r2;
        r13.write(r14, r1, r2);
        r3 = r1;
    L_0x0042:
        r2 = r11._inputPtr;
        r6 = r11._inputEnd;
        if (r2 < r6) goto L_0x004b;
    L_0x0048:
        r11.loadMoreGuaranteed();
    L_0x004b:
        r2 = r11._inputBuffer;
        r6 = r11._inputPtr;
        r7 = r6 + 1;
        r11._inputPtr = r7;
        r2 = r2[r6];
        r6 = r2 & 255;
        r2 = r12.decodeBase64Char(r6);
        if (r2 >= 0) goto L_0x0062;
    L_0x005d:
        r2 = 1;
        r2 = r11._decodeBase64Escape(r12, r6, r2);
    L_0x0062:
        r4 = r4 << 6;
        r4 = r4 | r2;
        r2 = r11._inputPtr;
        r6 = r11._inputEnd;
        if (r2 < r6) goto L_0x006e;
    L_0x006b:
        r11.loadMoreGuaranteed();
    L_0x006e:
        r2 = r11._inputBuffer;
        r6 = r11._inputPtr;
        r7 = r6 + 1;
        r11._inputPtr = r7;
        r2 = r2[r6];
        r6 = r2 & 255;
        r2 = r12.decodeBase64Char(r6);
        if (r2 >= 0) goto L_0x00df;
    L_0x0080:
        if (r2 == r8) goto L_0x0097;
    L_0x0082:
        if (r6 != r9) goto L_0x0092;
    L_0x0084:
        r2 = r12.usesPadding();
        if (r2 != 0) goto L_0x0092;
    L_0x008a:
        r4 = r4 >> 4;
        r2 = r3 + 1;
        r4 = (byte) r4;
        r14[r3] = r4;
        goto L_0x002b;
    L_0x0092:
        r2 = 2;
        r2 = r11._decodeBase64Escape(r12, r6, r2);
    L_0x0097:
        if (r2 != r8) goto L_0x00df;
    L_0x0099:
        r2 = r11._inputPtr;
        r6 = r11._inputEnd;
        if (r2 < r6) goto L_0x00a2;
    L_0x009f:
        r11.loadMoreGuaranteed();
    L_0x00a2:
        r2 = r11._inputBuffer;
        r6 = r11._inputPtr;
        r7 = r6 + 1;
        r11._inputPtr = r7;
        r2 = r2[r6];
        r2 = r2 & 255;
        r6 = r12.usesPaddingChar(r2);
        if (r6 != 0) goto L_0x00d6;
    L_0x00b4:
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
    L_0x00d6:
        r4 = r4 >> 4;
        r2 = r3 + 1;
        r4 = (byte) r4;
        r14[r3] = r4;
        goto L_0x000a;
    L_0x00df:
        r4 = r4 << 6;
        r4 = r4 | r2;
        r2 = r11._inputPtr;
        r6 = r11._inputEnd;
        if (r2 < r6) goto L_0x00eb;
    L_0x00e8:
        r11.loadMoreGuaranteed();
    L_0x00eb:
        r2 = r11._inputBuffer;
        r6 = r11._inputPtr;
        r7 = r6 + 1;
        r11._inputPtr = r7;
        r2 = r2[r6];
        r6 = r2 & 255;
        r2 = r12.decodeBase64Char(r6);
        if (r2 >= 0) goto L_0x012d;
    L_0x00fd:
        if (r2 == r8) goto L_0x011b;
    L_0x00ff:
        if (r6 != r9) goto L_0x0117;
    L_0x0101:
        r2 = r12.usesPadding();
        if (r2 != 0) goto L_0x0117;
    L_0x0107:
        r4 = r4 >> 2;
        r5 = r3 + 1;
        r2 = r4 >> 8;
        r2 = (byte) r2;
        r14[r3] = r2;
        r2 = r5 + 1;
        r3 = (byte) r4;
        r14[r5] = r3;
        goto L_0x002b;
    L_0x0117:
        r2 = r11._decodeBase64Escape(r12, r6, r10);
    L_0x011b:
        if (r2 != r8) goto L_0x012d;
    L_0x011d:
        r4 = r4 >> 2;
        r6 = r3 + 1;
        r2 = r4 >> 8;
        r2 = (byte) r2;
        r14[r3] = r2;
        r2 = r6 + 1;
        r3 = (byte) r4;
        r14[r6] = r3;
        goto L_0x000a;
    L_0x012d:
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
    L_0x0145:
        r3 = r2;
        goto L_0x0042;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.UTF8StreamJsonParser._readBinary(com.fasterxml.jackson.core.Base64Variant, java.io.OutputStream, byte[]):int");
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
            if (!this._parsingContext.inObject()) {
                return _nextTokenNotInObject(_skipWSOrEnd);
            }
            this._parsingContext.setCurrentName(_parseFieldName(_skipWSOrEnd).getName());
            this._currToken = JsonToken.FIELD_NAME;
            _skipWSOrEnd = _skipWS();
            if (_skipWSOrEnd != 58) {
                _reportUnexpectedChar(_skipWSOrEnd, "was expecting a colon to separate field name and value");
            }
            _skipWSOrEnd = _skipWS();
            if (_skipWSOrEnd == 34) {
                this._tokenIncomplete = true;
                this._nextToken = JsonToken.VALUE_STRING;
                return this._currToken;
            }
            switch (_skipWSOrEnd) {
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
                    jsonToken = JsonToken.START_OBJECT;
                    break;
                default:
                    jsonToken = _handleUnexpectedValue(_skipWSOrEnd);
                    break;
            }
            _matchToken(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE, 1);
            jsonToken = JsonToken.VALUE_TRUE;
            this._nextToken = jsonToken;
            return this._currToken;
        }
    }

    private JsonToken _nextTokenNotInObject(int i) throws IOException, JsonParseException {
        if (i == 34) {
            this._tokenIncomplete = true;
            JsonToken jsonToken = JsonToken.VALUE_STRING;
            this._currToken = jsonToken;
            return jsonToken;
        }
        switch (i) {
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
                jsonToken = parseNumberText(i);
                this._currToken = jsonToken;
                return jsonToken;
            case C0801R.styleable.AppCompatTheme_controlBackground /*91*/:
                this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                jsonToken = JsonToken.START_ARRAY;
                this._currToken = jsonToken;
                return jsonToken;
            case C0801R.styleable.AppCompatTheme_alertDialogStyle /*93*/:
            case 125:
                _reportUnexpectedChar(i, "expected a value");
                break;
            case HttpStatus.SC_PROCESSING /*102*/:
                _matchToken("false", 1);
                jsonToken = JsonToken.VALUE_FALSE;
                this._currToken = jsonToken;
                return jsonToken;
            case C0801R.styleable.AppCompatTheme_ratingBarStyleSmall /*110*/:
                _matchToken("null", 1);
                jsonToken = JsonToken.VALUE_NULL;
                this._currToken = jsonToken;
                return jsonToken;
            case 116:
                break;
            case 123:
                this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                jsonToken = JsonToken.START_OBJECT;
                this._currToken = jsonToken;
                return jsonToken;
            default:
                jsonToken = _handleUnexpectedValue(i);
                this._currToken = jsonToken;
                return jsonToken;
        }
        _matchToken(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE, 1);
        jsonToken = JsonToken.VALUE_TRUE;
        this._currToken = jsonToken;
        return jsonToken;
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

    public void close() throws IOException {
        super.close();
        this._symbols.release();
    }

    public boolean nextFieldName(SerializableString serializableString) throws IOException, JsonParseException {
        int i = 0;
        this._numTypesValid = 0;
        if (this._currToken == JsonToken.FIELD_NAME) {
            _nextAfterName();
            return false;
        }
        if (this._tokenIncomplete) {
            _skipString();
        }
        int _skipWSOrEnd = _skipWSOrEnd();
        if (_skipWSOrEnd < 0) {
            close();
            this._currToken = null;
            return false;
        }
        this._tokenInputTotal = (this._currInputProcessed + ((long) this._inputPtr)) - 1;
        this._tokenInputRow = this._currInputRow;
        this._tokenInputCol = (this._inputPtr - this._currInputRowStart) - 1;
        this._binaryValue = null;
        if (_skipWSOrEnd == 93) {
            if (!this._parsingContext.inArray()) {
                _reportMismatchedEndMarker(_skipWSOrEnd, '}');
            }
            this._parsingContext = this._parsingContext.getParent();
            this._currToken = JsonToken.END_ARRAY;
            return false;
        } else if (_skipWSOrEnd == 125) {
            if (!this._parsingContext.inObject()) {
                _reportMismatchedEndMarker(_skipWSOrEnd, ']');
            }
            this._parsingContext = this._parsingContext.getParent();
            this._currToken = JsonToken.END_OBJECT;
            return false;
        } else {
            if (this._parsingContext.expectComma()) {
                if (_skipWSOrEnd != 44) {
                    _reportUnexpectedChar(_skipWSOrEnd, "was expecting comma to separate " + this._parsingContext.getTypeDesc() + " entries");
                }
                _skipWSOrEnd = _skipWS();
            }
            if (this._parsingContext.inObject()) {
                if (_skipWSOrEnd == 34) {
                    byte[] asQuotedUTF8 = serializableString.asQuotedUTF8();
                    int length = asQuotedUTF8.length;
                    if (this._inputPtr + length < this._inputEnd) {
                        int i2 = this._inputPtr + length;
                        if (this._inputBuffer[i2] == (byte) 34) {
                            int i3 = this._inputPtr;
                            while (i != length) {
                                if (asQuotedUTF8[i] == this._inputBuffer[i3 + i]) {
                                    i++;
                                }
                            }
                            this._inputPtr = i2 + 1;
                            this._parsingContext.setCurrentName(serializableString.getValue());
                            this._currToken = JsonToken.FIELD_NAME;
                            _isNextTokenNameYes();
                            return true;
                        }
                    }
                }
                return _isNextTokenNameMaybe(_skipWSOrEnd, serializableString);
            }
            _nextTokenNotInObject(_skipWSOrEnd);
            return false;
        }
    }

    private void _isNextTokenNameYes() throws IOException, JsonParseException {
        int _skipColon;
        if (this._inputPtr >= this._inputEnd - 1 || this._inputBuffer[this._inputPtr] != 58) {
            _skipColon = _skipColon();
        } else {
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr + 1;
            this._inputPtr = i;
            byte b = bArr[i];
            this._inputPtr++;
            if (b == 34) {
                this._tokenIncomplete = true;
                this._nextToken = JsonToken.VALUE_STRING;
                return;
            } else if (b == 123) {
                this._nextToken = JsonToken.START_OBJECT;
                return;
            } else if (b == 91) {
                this._nextToken = JsonToken.START_ARRAY;
                return;
            } else {
                _skipColon = b & MotionEventCompat.ACTION_MASK;
                if (_skipColon <= 32 || _skipColon == 47) {
                    this._inputPtr--;
                    _skipColon = _skipWS();
                }
            }
        }
        switch (_skipColon) {
            case C0801R.styleable.AppCompatTheme_actionModePasteDrawable /*34*/:
                this._tokenIncomplete = true;
                this._nextToken = JsonToken.VALUE_STRING;
                return;
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
                this._nextToken = parseNumberText(_skipColon);
                return;
            case C0801R.styleable.AppCompatTheme_controlBackground /*91*/:
                this._nextToken = JsonToken.START_ARRAY;
                return;
            case C0801R.styleable.AppCompatTheme_alertDialogStyle /*93*/:
            case 125:
                _reportUnexpectedChar(_skipColon, "expected a value");
                break;
            case HttpStatus.SC_PROCESSING /*102*/:
                _matchToken("false", 1);
                this._nextToken = JsonToken.VALUE_FALSE;
                return;
            case C0801R.styleable.AppCompatTheme_ratingBarStyleSmall /*110*/:
                _matchToken("null", 1);
                this._nextToken = JsonToken.VALUE_NULL;
                return;
            case 116:
                break;
            case 123:
                this._nextToken = JsonToken.START_OBJECT;
                return;
            default:
                this._nextToken = _handleUnexpectedValue(_skipColon);
                return;
        }
        _matchToken(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE, 1);
        this._nextToken = JsonToken.VALUE_TRUE;
    }

    private boolean _isNextTokenNameMaybe(int i, SerializableString serializableString) throws IOException, JsonParseException {
        String name = _parseFieldName(i).getName();
        this._parsingContext.setCurrentName(name);
        boolean equals = name.equals(serializableString.getValue());
        this._currToken = JsonToken.FIELD_NAME;
        int _skipWS = _skipWS();
        if (_skipWS != 58) {
            _reportUnexpectedChar(_skipWS, "was expecting a colon to separate field name and value");
        }
        _skipWS = _skipWS();
        if (_skipWS == 34) {
            this._tokenIncomplete = true;
            this._nextToken = JsonToken.VALUE_STRING;
            return equals;
        }
        JsonToken parseNumberText;
        switch (_skipWS) {
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
                parseNumberText = parseNumberText(_skipWS);
                break;
            case C0801R.styleable.AppCompatTheme_controlBackground /*91*/:
                parseNumberText = JsonToken.START_ARRAY;
                break;
            case C0801R.styleable.AppCompatTheme_alertDialogStyle /*93*/:
            case 125:
                _reportUnexpectedChar(_skipWS, "expected a value");
                break;
            case HttpStatus.SC_PROCESSING /*102*/:
                _matchToken("false", 1);
                parseNumberText = JsonToken.VALUE_FALSE;
                break;
            case C0801R.styleable.AppCompatTheme_ratingBarStyleSmall /*110*/:
                _matchToken("null", 1);
                parseNumberText = JsonToken.VALUE_NULL;
                break;
            case 116:
                break;
            case 123:
                parseNumberText = JsonToken.START_OBJECT;
                break;
            default:
                parseNumberText = _handleUnexpectedValue(_skipWS);
                break;
        }
        _matchToken(ServerProtocol.DIALOG_RETURN_SCOPES_TRUE, 1);
        parseNumberText = JsonToken.VALUE_TRUE;
        this._nextToken = parseNumberText;
        return equals;
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
        switch (C04901.$SwitchMap$com$fasterxml$jackson$core$JsonToken[nextToken().ordinal()]) {
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                return Boolean.TRUE;
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                return Boolean.FALSE;
            default:
                return null;
        }
    }

    protected JsonToken parseNumberText(int i) throws IOException, JsonParseException {
        int i2;
        int i3;
        int i4;
        int i5 = 1;
        char[] emptyAndGetCurrentSegment = this._textBuffer.emptyAndGetCurrentSegment();
        boolean z = i == 45;
        if (z) {
            emptyAndGetCurrentSegment[0] = '-';
            if (this._inputPtr >= this._inputEnd) {
                loadMoreGuaranteed();
            }
            byte[] bArr = this._inputBuffer;
            i2 = this._inputPtr;
            this._inputPtr = i2 + 1;
            i3 = bArr[i2] & MotionEventCompat.ACTION_MASK;
            if (i3 < 48 || i3 > 57) {
                return _handleInvalidNumberStart(i3, true);
            }
            i4 = 1;
        } else {
            i4 = 0;
            i3 = i;
        }
        if (i3 == 48) {
            i3 = _verifyNoLeadingZeroes();
        }
        i2 = i4 + 1;
        emptyAndGetCurrentSegment[i4] = (char) i3;
        i3 = this._inputPtr + emptyAndGetCurrentSegment.length;
        if (i3 > this._inputEnd) {
            i3 = this._inputEnd;
        }
        while (this._inputPtr < i3) {
            byte[] bArr2 = this._inputBuffer;
            int i6 = this._inputPtr;
            this._inputPtr = i6 + 1;
            i4 = bArr2[i6] & MotionEventCompat.ACTION_MASK;
            if (i4 >= 48 && i4 <= 57) {
                i5++;
                i6 = i2 + 1;
                emptyAndGetCurrentSegment[i2] = (char) i4;
                i2 = i6;
            } else if (i4 == 46 || i4 == HttpStatus.SC_SWITCHING_PROTOCOLS || i4 == 69) {
                return _parseFloatText(emptyAndGetCurrentSegment, i2, i4, z, i5);
            } else {
                this._inputPtr--;
                this._textBuffer.setCurrentLength(i2);
                return resetInt(z, i5);
            }
        }
        return _parserNumber2(emptyAndGetCurrentSegment, i2, z, i5);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.fasterxml.jackson.core.JsonToken _parserNumber2(char[] r7, int r8, boolean r9, int r10) throws java.io.IOException, com.fasterxml.jackson.core.JsonParseException {
        /*
        r6 = this;
        r5 = r10;
        r2 = r8;
        r1 = r7;
    L_0x0003:
        r0 = r6._inputPtr;
        r3 = r6._inputEnd;
        if (r0 < r3) goto L_0x0019;
    L_0x0009:
        r0 = r6.loadMore();
        if (r0 != 0) goto L_0x0019;
    L_0x000f:
        r0 = r6._textBuffer;
        r0.setCurrentLength(r2);
        r0 = r6.resetInt(r9, r5);
    L_0x0018:
        return r0;
    L_0x0019:
        r0 = r6._inputBuffer;
        r3 = r6._inputPtr;
        r4 = r3 + 1;
        r6._inputPtr = r4;
        r0 = r0[r3];
        r3 = r0 & 255;
        r0 = 57;
        if (r3 > r0) goto L_0x002d;
    L_0x0029:
        r0 = 48;
        if (r3 >= r0) goto L_0x0040;
    L_0x002d:
        r0 = 46;
        if (r3 == r0) goto L_0x0039;
    L_0x0031:
        r0 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r3 == r0) goto L_0x0039;
    L_0x0035:
        r0 = 69;
        if (r3 != r0) goto L_0x0053;
    L_0x0039:
        r0 = r6;
        r4 = r9;
        r0 = r0._parseFloatText(r1, r2, r3, r4, r5);
        goto L_0x0018;
    L_0x0040:
        r0 = r1.length;
        if (r2 < r0) goto L_0x0063;
    L_0x0043:
        r0 = r6._textBuffer;
        r1 = r0.finishCurrentSegment();
        r2 = 0;
        r0 = r2;
    L_0x004b:
        r2 = r0 + 1;
        r3 = (char) r3;
        r1[r0] = r3;
        r5 = r5 + 1;
        goto L_0x0003;
    L_0x0053:
        r0 = r6._inputPtr;
        r0 = r0 + -1;
        r6._inputPtr = r0;
        r0 = r6._textBuffer;
        r0.setCurrentLength(r2);
        r0 = r6.resetInt(r9, r5);
        goto L_0x0018;
    L_0x0063:
        r0 = r2;
        goto L_0x004b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.UTF8StreamJsonParser._parserNumber2(char[], int, boolean, int):com.fasterxml.jackson.core.JsonToken");
    }

    private int _verifyNoLeadingZeroes() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            return 48;
        }
        int i = this._inputBuffer[this._inputPtr] & MotionEventCompat.ACTION_MASK;
        if (i < 48 || i > 57) {
            return 48;
        }
        if (!isEnabled(Feature.ALLOW_NUMERIC_LEADING_ZEROS)) {
            reportInvalidNumber("Leading zeroes not allowed");
        }
        this._inputPtr++;
        if (i != 48) {
            return i;
        }
        do {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                return i;
            }
            i = this._inputBuffer[this._inputPtr] & MotionEventCompat.ACTION_MASK;
            if (i < 48 || i > 57) {
                return 48;
            }
            this._inputPtr++;
        } while (i == 48);
        return i;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.fasterxml.jackson.core.JsonToken _parseFloatText(char[] r10, int r11, int r12, boolean r13, int r14) throws java.io.IOException, com.fasterxml.jackson.core.JsonParseException {
        /*
        r9 = this;
        r0 = 0;
        r4 = 0;
        r1 = 46;
        if (r12 != r1) goto L_0x0109;
    L_0x0006:
        r1 = r11 + 1;
        r2 = (char) r12;
        r10[r11] = r2;
    L_0x000b:
        r2 = r9._inputPtr;
        r3 = r9._inputEnd;
        if (r2 < r3) goto L_0x00c0;
    L_0x0011:
        r2 = r9.loadMore();
        if (r2 != 0) goto L_0x00c0;
    L_0x0017:
        r4 = 1;
    L_0x0018:
        if (r0 != 0) goto L_0x001f;
    L_0x001a:
        r2 = "Decimal point not followed by a digit";
        r9.reportUnexpectedNumberChar(r12, r2);
    L_0x001f:
        r6 = r0;
        r0 = r1;
        r1 = r10;
    L_0x0022:
        r3 = 0;
        r2 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r12 == r2) goto L_0x002b;
    L_0x0027:
        r2 = 69;
        if (r12 != r2) goto L_0x0103;
    L_0x002b:
        r2 = r1.length;
        if (r0 < r2) goto L_0x0035;
    L_0x002e:
        r0 = r9._textBuffer;
        r1 = r0.finishCurrentSegment();
        r0 = 0;
    L_0x0035:
        r2 = r0 + 1;
        r5 = (char) r12;
        r1[r0] = r5;
        r0 = r9._inputPtr;
        r5 = r9._inputEnd;
        if (r0 < r5) goto L_0x0043;
    L_0x0040:
        r9.loadMoreGuaranteed();
    L_0x0043:
        r0 = r9._inputBuffer;
        r5 = r9._inputPtr;
        r7 = r5 + 1;
        r9._inputPtr = r7;
        r0 = r0[r5];
        r5 = r0 & 255;
        r0 = 45;
        if (r5 == r0) goto L_0x0057;
    L_0x0053:
        r0 = 43;
        if (r5 != r0) goto L_0x00ff;
    L_0x0057:
        r0 = r1.length;
        if (r2 < r0) goto L_0x00fc;
    L_0x005a:
        r0 = r9._textBuffer;
        r1 = r0.finishCurrentSegment();
        r0 = 0;
    L_0x0061:
        r2 = r0 + 1;
        r5 = (char) r5;
        r1[r0] = r5;
        r0 = r9._inputPtr;
        r5 = r9._inputEnd;
        if (r0 < r5) goto L_0x006f;
    L_0x006c:
        r9.loadMoreGuaranteed();
    L_0x006f:
        r0 = r9._inputBuffer;
        r5 = r9._inputPtr;
        r7 = r5 + 1;
        r9._inputPtr = r7;
        r0 = r0[r5];
        r0 = r0 & 255;
        r5 = r0;
        r0 = r2;
        r2 = r3;
    L_0x007e:
        r3 = 57;
        if (r5 > r3) goto L_0x00f7;
    L_0x0082:
        r3 = 48;
        if (r5 < r3) goto L_0x00f7;
    L_0x0086:
        r2 = r2 + 1;
        r3 = r1.length;
        if (r0 < r3) goto L_0x0092;
    L_0x008b:
        r0 = r9._textBuffer;
        r1 = r0.finishCurrentSegment();
        r0 = 0;
    L_0x0092:
        r3 = r0 + 1;
        r7 = (char) r5;
        r1[r0] = r7;
        r0 = r9._inputPtr;
        r7 = r9._inputEnd;
        if (r0 < r7) goto L_0x00e8;
    L_0x009d:
        r0 = r9.loadMore();
        if (r0 != 0) goto L_0x00e8;
    L_0x00a3:
        r4 = 1;
        r0 = r2;
        r1 = r4;
        r2 = r3;
    L_0x00a7:
        if (r0 != 0) goto L_0x00ae;
    L_0x00a9:
        r3 = "Exponent indicator not followed by a digit";
        r9.reportUnexpectedNumberChar(r5, r3);
    L_0x00ae:
        if (r1 != 0) goto L_0x00b6;
    L_0x00b0:
        r1 = r9._inputPtr;
        r1 = r1 + -1;
        r9._inputPtr = r1;
    L_0x00b6:
        r1 = r9._textBuffer;
        r1.setCurrentLength(r2);
        r0 = r9.resetFloat(r13, r14, r6, r0);
        return r0;
    L_0x00c0:
        r2 = r9._inputBuffer;
        r3 = r9._inputPtr;
        r5 = r3 + 1;
        r9._inputPtr = r5;
        r2 = r2[r3];
        r12 = r2 & 255;
        r2 = 48;
        if (r12 < r2) goto L_0x0018;
    L_0x00d0:
        r2 = 57;
        if (r12 > r2) goto L_0x0018;
    L_0x00d4:
        r0 = r0 + 1;
        r2 = r10.length;
        if (r1 < r2) goto L_0x0107;
    L_0x00d9:
        r1 = r9._textBuffer;
        r10 = r1.finishCurrentSegment();
        r1 = 0;
        r2 = r1;
    L_0x00e1:
        r1 = r2 + 1;
        r3 = (char) r12;
        r10[r2] = r3;
        goto L_0x000b;
    L_0x00e8:
        r0 = r9._inputBuffer;
        r5 = r9._inputPtr;
        r7 = r5 + 1;
        r9._inputPtr = r7;
        r0 = r0[r5];
        r0 = r0 & 255;
        r5 = r0;
        r0 = r3;
        goto L_0x007e;
    L_0x00f7:
        r1 = r4;
        r8 = r2;
        r2 = r0;
        r0 = r8;
        goto L_0x00a7;
    L_0x00fc:
        r0 = r2;
        goto L_0x0061;
    L_0x00ff:
        r0 = r2;
        r2 = r3;
        goto L_0x007e;
    L_0x0103:
        r1 = r4;
        r2 = r0;
        r0 = r3;
        goto L_0x00ae;
    L_0x0107:
        r2 = r1;
        goto L_0x00e1;
    L_0x0109:
        r6 = r0;
        r1 = r10;
        r0 = r11;
        goto L_0x0022;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.UTF8StreamJsonParser._parseFloatText(char[], int, int, boolean, int):com.fasterxml.jackson.core.JsonToken");
    }

    protected Name _parseFieldName(int i) throws IOException, JsonParseException {
        if (i != 34) {
            return _handleUnusualFieldName(i);
        }
        if (this._inputPtr + 9 > this._inputEnd) {
            return slowParseFieldName();
        }
        byte[] bArr = this._inputBuffer;
        int[] iArr = sInputCodesLatin1;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        i2 = bArr[i2] & MotionEventCompat.ACTION_MASK;
        if (iArr[i2] == 0) {
            int i3 = this._inputPtr;
            this._inputPtr = i3 + 1;
            i3 = bArr[i3] & MotionEventCompat.ACTION_MASK;
            if (iArr[i3] == 0) {
                i2 = (i2 << 8) | i3;
                i3 = this._inputPtr;
                this._inputPtr = i3 + 1;
                i3 = bArr[i3] & MotionEventCompat.ACTION_MASK;
                if (iArr[i3] == 0) {
                    i2 = (i2 << 8) | i3;
                    i3 = this._inputPtr;
                    this._inputPtr = i3 + 1;
                    i3 = bArr[i3] & MotionEventCompat.ACTION_MASK;
                    if (iArr[i3] == 0) {
                        i2 = (i2 << 8) | i3;
                        i3 = this._inputPtr;
                        this._inputPtr = i3 + 1;
                        int i4 = bArr[i3] & MotionEventCompat.ACTION_MASK;
                        if (iArr[i4] == 0) {
                            this._quad1 = i2;
                            return parseMediumFieldName(i4, iArr);
                        } else if (i4 == 34) {
                            return findName(i2, 4);
                        } else {
                            return parseFieldName(i2, i4, 4);
                        }
                    } else if (i3 == 34) {
                        return findName(i2, 3);
                    } else {
                        return parseFieldName(i2, i3, 3);
                    }
                } else if (i3 == 34) {
                    return findName(i2, 2);
                } else {
                    return parseFieldName(i2, i3, 2);
                }
            } else if (i3 == 34) {
                return findName(i2, 1);
            } else {
                return parseFieldName(i2, i3, 1);
            }
        } else if (i2 == 34) {
            return BytesToNameCanonicalizer.getEmptyName();
        } else {
            return parseFieldName(0, i2, 0);
        }
    }

    protected Name parseMediumFieldName(int i, int[] iArr) throws IOException, JsonParseException {
        byte[] bArr = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        int i3 = bArr[i2] & MotionEventCompat.ACTION_MASK;
        if (iArr[i3] == 0) {
            i3 |= i << 8;
            byte[] bArr2 = this._inputBuffer;
            int i4 = this._inputPtr;
            this._inputPtr = i4 + 1;
            i2 = bArr2[i4] & MotionEventCompat.ACTION_MASK;
            if (iArr[i2] == 0) {
                i3 = (i3 << 8) | i2;
                bArr2 = this._inputBuffer;
                i4 = this._inputPtr;
                this._inputPtr = i4 + 1;
                i2 = bArr2[i4] & MotionEventCompat.ACTION_MASK;
                if (iArr[i2] == 0) {
                    i3 = (i3 << 8) | i2;
                    bArr2 = this._inputBuffer;
                    i4 = this._inputPtr;
                    this._inputPtr = i4 + 1;
                    i2 = bArr2[i4] & MotionEventCompat.ACTION_MASK;
                    if (iArr[i2] == 0) {
                        this._quadBuffer[0] = this._quad1;
                        this._quadBuffer[1] = i3;
                        return parseLongFieldName(i2);
                    } else if (i2 == 34) {
                        return findName(this._quad1, i3, 4);
                    } else {
                        return parseFieldName(this._quad1, i3, i2, 4);
                    }
                } else if (i2 == 34) {
                    return findName(this._quad1, i3, 3);
                } else {
                    return parseFieldName(this._quad1, i3, i2, 3);
                }
            } else if (i2 == 34) {
                return findName(this._quad1, i3, 2);
            } else {
                return parseFieldName(this._quad1, i3, i2, 2);
            }
        } else if (i3 == 34) {
            return findName(this._quad1, i, 1);
        } else {
            return parseFieldName(this._quad1, i, i3, 1);
        }
    }

    protected Name parseLongFieldName(int i) throws IOException, JsonParseException {
        int[] iArr = sInputCodesLatin1;
        int i2 = 2;
        int i3 = i;
        while (this._inputEnd - this._inputPtr >= 4) {
            byte[] bArr = this._inputBuffer;
            int i4 = this._inputPtr;
            this._inputPtr = i4 + 1;
            int i5 = bArr[i4] & MotionEventCompat.ACTION_MASK;
            if (iArr[i5] == 0) {
                i4 = (i3 << 8) | i5;
                bArr = this._inputBuffer;
                i3 = this._inputPtr;
                this._inputPtr = i3 + 1;
                i3 = bArr[i3] & MotionEventCompat.ACTION_MASK;
                if (iArr[i3] == 0) {
                    i4 = (i4 << 8) | i3;
                    bArr = this._inputBuffer;
                    i3 = this._inputPtr;
                    this._inputPtr = i3 + 1;
                    i3 = bArr[i3] & MotionEventCompat.ACTION_MASK;
                    if (iArr[i3] == 0) {
                        i4 = (i4 << 8) | i3;
                        bArr = this._inputBuffer;
                        i3 = this._inputPtr;
                        this._inputPtr = i3 + 1;
                        i3 = bArr[i3] & MotionEventCompat.ACTION_MASK;
                        if (iArr[i3] == 0) {
                            if (i2 >= this._quadBuffer.length) {
                                this._quadBuffer = growArrayBy(this._quadBuffer, i2);
                            }
                            int i6 = i2 + 1;
                            this._quadBuffer[i2] = i4;
                            i2 = i6;
                        } else if (i3 == 34) {
                            return findName(this._quadBuffer, i2, i4, 4);
                        } else {
                            return parseEscapedFieldName(this._quadBuffer, i2, i4, i3, 4);
                        }
                    } else if (i3 == 34) {
                        return findName(this._quadBuffer, i2, i4, 3);
                    } else {
                        return parseEscapedFieldName(this._quadBuffer, i2, i4, i3, 3);
                    }
                } else if (i3 == 34) {
                    return findName(this._quadBuffer, i2, i4, 2);
                } else {
                    return parseEscapedFieldName(this._quadBuffer, i2, i4, i3, 2);
                }
            } else if (i5 == 34) {
                return findName(this._quadBuffer, i2, i3, 1);
            } else {
                return parseEscapedFieldName(this._quadBuffer, i2, i3, i5, 1);
            }
        }
        return parseEscapedFieldName(this._quadBuffer, i2, 0, i3, 0);
    }

    protected Name slowParseFieldName() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(": was expecting closing '\"' for name");
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int i2 = bArr[i] & MotionEventCompat.ACTION_MASK;
        if (i2 == 34) {
            return BytesToNameCanonicalizer.getEmptyName();
        }
        return parseEscapedFieldName(this._quadBuffer, 0, 0, i2, 0);
    }

    private Name parseFieldName(int i, int i2, int i3) throws IOException, JsonParseException {
        return parseEscapedFieldName(this._quadBuffer, 0, i, i2, i3);
    }

    private Name parseFieldName(int i, int i2, int i3, int i4) throws IOException, JsonParseException {
        this._quadBuffer[0] = i;
        return parseEscapedFieldName(this._quadBuffer, 1, i2, i3, i4);
    }

    protected Name parseEscapedFieldName(int[] iArr, int i, int i2, int i3, int i4) throws IOException, JsonParseException {
        int[] iArr2 = sInputCodesLatin1;
        while (true) {
            int[] iArr3;
            int i5;
            int i6;
            int i7;
            byte[] bArr;
            if (iArr2[i3] != 0) {
                if (i3 == 34) {
                    break;
                }
                if (i3 != 92) {
                    _throwUnquotedSpace(i3, ShareConstants.WEB_DIALOG_PARAM_NAME);
                } else {
                    i3 = _decodeEscaped();
                }
                if (i3 > TransportMediator.KEYCODE_MEDIA_PAUSE) {
                    int i8;
                    int[] iArr4;
                    if (i4 >= 4) {
                        if (i >= iArr.length) {
                            iArr = growArrayBy(iArr, iArr.length);
                            this._quadBuffer = iArr;
                        }
                        i8 = i + 1;
                        iArr[i] = i2;
                        i4 = 0;
                        i2 = 0;
                        iArr3 = iArr;
                    } else {
                        i8 = i;
                        iArr3 = iArr;
                    }
                    if (i3 < ItemAnimator.FLAG_MOVED) {
                        i5 = ((i3 >> 6) | 192) | (i2 << 8);
                        iArr4 = iArr3;
                        i6 = i4 + 1;
                    } else {
                        int[] iArr5;
                        int i9;
                        i7 = ((i3 >> 12) | 224) | (i2 << 8);
                        i5 = i4 + 1;
                        if (i5 >= 4) {
                            if (i8 >= iArr3.length) {
                                iArr3 = growArrayBy(iArr3, iArr3.length);
                                this._quadBuffer = iArr3;
                            }
                            i5 = i8 + 1;
                            iArr3[i8] = i7;
                            i7 = i5;
                            iArr5 = iArr3;
                            i6 = 0;
                            i5 = 0;
                        } else {
                            i9 = i5;
                            i5 = i7;
                            i7 = i8;
                            iArr5 = iArr3;
                            i6 = i9;
                        }
                        i5 = (i5 << 8) | (((i3 >> 6) & 63) | TransportMediator.FLAG_KEY_MEDIA_NEXT);
                        i6++;
                        i9 = i7;
                        iArr4 = iArr5;
                        i8 = i9;
                    }
                    i2 = (i3 & 63) | TransportMediator.FLAG_KEY_MEDIA_NEXT;
                    i4 = i6;
                    i = i8;
                    iArr3 = iArr4;
                    i7 = i5;
                    if (i4 >= 4) {
                        i4++;
                        i2 |= i7 << 8;
                        iArr = iArr3;
                    } else {
                        if (i >= iArr3.length) {
                            iArr3 = growArrayBy(iArr3, iArr3.length);
                            this._quadBuffer = iArr3;
                        }
                        i5 = i + 1;
                        iArr3[i] = i7;
                        i4 = 1;
                        i = i5;
                        iArr = iArr3;
                    }
                    if (this._inputPtr >= this._inputEnd && !loadMore()) {
                        _reportInvalidEOF(" in field name");
                    }
                    bArr = this._inputBuffer;
                    i5 = this._inputPtr;
                    this._inputPtr = i5 + 1;
                    i3 = bArr[i5] & MotionEventCompat.ACTION_MASK;
                }
            }
            i7 = i2;
            iArr3 = iArr;
            i2 = i3;
            if (i4 >= 4) {
                if (i >= iArr3.length) {
                    iArr3 = growArrayBy(iArr3, iArr3.length);
                    this._quadBuffer = iArr3;
                }
                i5 = i + 1;
                iArr3[i] = i7;
                i4 = 1;
                i = i5;
                iArr = iArr3;
            } else {
                i4++;
                i2 |= i7 << 8;
                iArr = iArr3;
            }
            _reportInvalidEOF(" in field name");
            bArr = this._inputBuffer;
            i5 = this._inputPtr;
            this._inputPtr = i5 + 1;
            i3 = bArr[i5] & MotionEventCompat.ACTION_MASK;
        }
        if (i4 > 0) {
            if (i >= iArr.length) {
                iArr = growArrayBy(iArr, iArr.length);
                this._quadBuffer = iArr;
            }
            i6 = i + 1;
            iArr[i] = i2;
            i = i6;
        }
        Name findName = this._symbols.findName(iArr, i);
        if (findName == null) {
            return addName(iArr, i, i4);
        }
        return findName;
    }

    protected Name _handleUnusualFieldName(int i) throws IOException, JsonParseException {
        if (i == 39 && isEnabled(Feature.ALLOW_SINGLE_QUOTES)) {
            return _parseApostropheFieldName();
        }
        int[] iArr;
        int i2;
        if (!isEnabled(Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
            _reportUnexpectedChar(i, "was expecting double-quote to start field name");
        }
        int[] inputCodeUtf8JsNames = CharTypes.getInputCodeUtf8JsNames();
        if (inputCodeUtf8JsNames[i] != 0) {
            _reportUnexpectedChar(i, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
        }
        int i3 = 0;
        int i4 = 0;
        int i5 = i;
        int i6 = 0;
        int[] iArr2 = this._quadBuffer;
        while (true) {
            if (i3 < 4) {
                int i7 = i3 + 1;
                i3 = i5 | (i4 << 8);
                i5 = i6;
                iArr = iArr2;
                i2 = i7;
            } else {
                if (i6 >= iArr2.length) {
                    iArr2 = growArrayBy(iArr2, iArr2.length);
                    this._quadBuffer = iArr2;
                }
                int i8 = i6 + 1;
                iArr2[i6] = i4;
                iArr = iArr2;
                i2 = 1;
                i3 = i5;
                i5 = i8;
            }
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOF(" in field name");
            }
            i = this._inputBuffer[this._inputPtr] & MotionEventCompat.ACTION_MASK;
            if (inputCodeUtf8JsNames[i] != 0) {
                break;
            }
            this._inputPtr++;
            i4 = i3;
            i3 = i2;
            iArr2 = iArr;
            i6 = i5;
            i5 = i;
        }
        if (i2 > 0) {
            if (i5 >= iArr.length) {
                iArr = growArrayBy(iArr, iArr.length);
                this._quadBuffer = iArr;
            }
            i8 = i5 + 1;
            iArr[i5] = i3;
            i5 = i8;
        }
        Name findName = this._symbols.findName(iArr, i5);
        if (findName == null) {
            return addName(iArr, i5, i2);
        }
        return findName;
    }

    protected Name _parseApostropheFieldName() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(": was expecting closing ''' for name");
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int i2 = bArr[i] & MotionEventCompat.ACTION_MASK;
        if (i2 == 39) {
            return BytesToNameCanonicalizer.getEmptyName();
        }
        int i3;
        int[] iArr;
        int[] iArr2 = this._quadBuffer;
        int[] iArr3 = sInputCodesLatin1;
        int i4 = 0;
        int i5 = 0;
        i = 0;
        while (i2 != 39) {
            int i6;
            int[] iArr4;
            int i7;
            byte[] bArr2;
            if (!(i2 == 34 || iArr3[i2] == 0)) {
                if (i2 != 92) {
                    _throwUnquotedSpace(i2, ShareConstants.WEB_DIALOG_PARAM_NAME);
                } else {
                    i2 = _decodeEscaped();
                }
                if (i2 > TransportMediator.KEYCODE_MEDIA_PAUSE) {
                    int[] iArr5;
                    if (i4 >= 4) {
                        if (i >= iArr2.length) {
                            iArr2 = growArrayBy(iArr2, iArr2.length);
                            this._quadBuffer = iArr2;
                        }
                        i4 = i + 1;
                        iArr2[i] = i5;
                        i = 0;
                        i5 = i4;
                        i4 = 0;
                    } else {
                        i6 = i4;
                        i4 = i5;
                        i5 = i;
                        i = i6;
                    }
                    if (i2 < ItemAnimator.FLAG_MOVED) {
                        i6 = i + 1;
                        i = (i4 << 8) | ((i2 >> 6) | 192);
                        iArr5 = iArr2;
                        i3 = i6;
                    } else {
                        i4 = (i4 << 8) | ((i2 >> 12) | 224);
                        i++;
                        if (i >= 4) {
                            if (i5 >= iArr2.length) {
                                iArr2 = growArrayBy(iArr2, iArr2.length);
                                this._quadBuffer = iArr2;
                            }
                            i = i5 + 1;
                            iArr2[i5] = i4;
                            i4 = i;
                            iArr4 = iArr2;
                            i3 = 0;
                            i = 0;
                        } else {
                            i6 = i;
                            i = i4;
                            i4 = i5;
                            iArr4 = iArr2;
                            i3 = i6;
                        }
                        i = (i << 8) | (((i2 >> 6) & 63) | TransportMediator.FLAG_KEY_MEDIA_NEXT);
                        i3++;
                        i6 = i4;
                        iArr5 = iArr4;
                        i5 = i6;
                    }
                    i7 = i;
                    i = i3;
                    iArr2 = iArr5;
                    i4 = (i2 & 63) | TransportMediator.FLAG_KEY_MEDIA_NEXT;
                    if (i >= 4) {
                        i6 = i + 1;
                        i = i4 | (i7 << 8);
                        i4 = i5;
                        iArr4 = iArr2;
                        i3 = i6;
                    } else {
                        if (i5 >= iArr2.length) {
                            iArr2 = growArrayBy(iArr2, iArr2.length);
                            this._quadBuffer = iArr2;
                        }
                        i2 = i5 + 1;
                        iArr2[i5] = i7;
                        iArr4 = iArr2;
                        i3 = 1;
                        i = i4;
                        i4 = i2;
                    }
                    if (this._inputPtr >= this._inputEnd && !loadMore()) {
                        _reportInvalidEOF(" in field name");
                    }
                    bArr2 = this._inputBuffer;
                    i7 = this._inputPtr;
                    this._inputPtr = i7 + 1;
                    i2 = bArr2[i7] & MotionEventCompat.ACTION_MASK;
                    i6 = i3;
                    iArr2 = iArr4;
                    i5 = i;
                    i = i4;
                    i4 = i6;
                }
            }
            i7 = i5;
            i5 = i;
            i = i4;
            i4 = i2;
            if (i >= 4) {
                if (i5 >= iArr2.length) {
                    iArr2 = growArrayBy(iArr2, iArr2.length);
                    this._quadBuffer = iArr2;
                }
                i2 = i5 + 1;
                iArr2[i5] = i7;
                iArr4 = iArr2;
                i3 = 1;
                i = i4;
                i4 = i2;
            } else {
                i6 = i + 1;
                i = i4 | (i7 << 8);
                i4 = i5;
                iArr4 = iArr2;
                i3 = i6;
            }
            _reportInvalidEOF(" in field name");
            bArr2 = this._inputBuffer;
            i7 = this._inputPtr;
            this._inputPtr = i7 + 1;
            i2 = bArr2[i7] & MotionEventCompat.ACTION_MASK;
            i6 = i3;
            iArr2 = iArr4;
            i5 = i;
            i = i4;
            i4 = i6;
        }
        if (i4 > 0) {
            if (i >= iArr2.length) {
                iArr2 = growArrayBy(iArr2, iArr2.length);
                this._quadBuffer = iArr2;
            }
            int i8 = i + 1;
            iArr2[i] = i5;
            i6 = i8;
            iArr = iArr2;
            i3 = i6;
        } else {
            iArr = iArr2;
            i3 = i;
        }
        Name findName = this._symbols.findName(iArr, i3);
        if (findName == null) {
            return addName(iArr, i3, i4);
        }
        return findName;
    }

    private Name findName(int i, int i2) throws JsonParseException {
        Name findName = this._symbols.findName(i);
        if (findName != null) {
            return findName;
        }
        this._quadBuffer[0] = i;
        return addName(this._quadBuffer, 1, i2);
    }

    private Name findName(int i, int i2, int i3) throws JsonParseException {
        Name findName = this._symbols.findName(i, i2);
        if (findName != null) {
            return findName;
        }
        this._quadBuffer[0] = i;
        this._quadBuffer[1] = i2;
        return addName(this._quadBuffer, 2, i3);
    }

    private Name findName(int[] iArr, int i, int i2, int i3) throws JsonParseException {
        if (i >= iArr.length) {
            iArr = growArrayBy(iArr, iArr.length);
            this._quadBuffer = iArr;
        }
        int i4 = i + 1;
        iArr[i] = i2;
        Name findName = this._symbols.findName(iArr, i4);
        if (findName == null) {
            return addName(iArr, i4, i3);
        }
        return findName;
    }

    private Name addName(int[] iArr, int i, int i2) throws JsonParseException {
        int i3;
        int i4 = ((i << 2) - 4) + i2;
        if (i2 < 4) {
            i3 = iArr[i - 1];
            iArr[i - 1] = i3 << ((4 - i2) << 3);
        } else {
            i3 = 0;
        }
        char[] emptyAndGetCurrentSegment = this._textBuffer.emptyAndGetCurrentSegment();
        int i5 = 0;
        int i6 = 0;
        while (i6 < i4) {
            char[] cArr;
            int i7;
            int i8 = (iArr[i6 >> 2] >> ((3 - (i6 & 3)) << 3)) & MotionEventCompat.ACTION_MASK;
            i6++;
            if (i8 > TransportMediator.KEYCODE_MEDIA_PAUSE) {
                int i9;
                if ((i8 & 224) == 192) {
                    i8 &= 31;
                    i9 = 1;
                } else if ((i8 & 240) == 224) {
                    i8 &= 15;
                    i9 = 2;
                } else if ((i8 & 248) == 240) {
                    i8 &= 7;
                    i9 = 3;
                } else {
                    _reportInvalidInitial(i8);
                    i8 = 1;
                    i9 = 1;
                }
                if (i6 + i9 > i4) {
                    _reportInvalidEOF(" in field name");
                }
                int i10 = iArr[i6 >> 2] >> ((3 - (i6 & 3)) << 3);
                i6++;
                if ((i10 & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
                    _reportInvalidOther(i10);
                }
                i8 = (i8 << 6) | (i10 & 63);
                if (i9 > 1) {
                    i10 = iArr[i6 >> 2] >> ((3 - (i6 & 3)) << 3);
                    i6++;
                    if ((i10 & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
                        _reportInvalidOther(i10);
                    }
                    i8 = (i8 << 6) | (i10 & 63);
                    if (i9 > 2) {
                        i10 = iArr[i6 >> 2] >> ((3 - (i6 & 3)) << 3);
                        i6++;
                        if ((i10 & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
                            _reportInvalidOther(i10 & MotionEventCompat.ACTION_MASK);
                        }
                        i8 = (i8 << 6) | (i10 & 63);
                    }
                }
                if (i9 > 2) {
                    i8 -= NativeProtocol.MESSAGE_GET_ACCESS_TOKEN_REQUEST;
                    if (i5 >= emptyAndGetCurrentSegment.length) {
                        emptyAndGetCurrentSegment = this._textBuffer.expandCurrentSegment();
                    }
                    i9 = i5 + 1;
                    emptyAndGetCurrentSegment[i5] = (char) (55296 + (i8 >> 10));
                    int i11 = (i8 & 1023) | 56320;
                    i8 = i6;
                    i6 = i9;
                    cArr = emptyAndGetCurrentSegment;
                    i7 = i11;
                    if (i6 >= cArr.length) {
                        cArr = this._textBuffer.expandCurrentSegment();
                    }
                    i5 = i6 + 1;
                    cArr[i6] = (char) i7;
                    i6 = i8;
                    emptyAndGetCurrentSegment = cArr;
                }
            }
            cArr = emptyAndGetCurrentSegment;
            i7 = i8;
            i8 = i6;
            i6 = i5;
            if (i6 >= cArr.length) {
                cArr = this._textBuffer.expandCurrentSegment();
            }
            i5 = i6 + 1;
            cArr[i6] = (char) i7;
            i6 = i8;
            emptyAndGetCurrentSegment = cArr;
        }
        String str = new String(emptyAndGetCurrentSegment, 0, i5);
        if (i2 < 4) {
            iArr[i - 1] = i3;
        }
        return this._symbols.addName(str, iArr, i);
    }

    protected void _finishString() throws IOException, JsonParseException {
        int i = this._inputPtr;
        if (i >= this._inputEnd) {
            loadMoreGuaranteed();
            i = this._inputPtr;
        }
        char[] emptyAndGetCurrentSegment = this._textBuffer.emptyAndGetCurrentSegment();
        int[] iArr = sInputCodesUtf8;
        int min = Math.min(this._inputEnd, emptyAndGetCurrentSegment.length + i);
        byte[] bArr = this._inputBuffer;
        int i2 = i;
        i = 0;
        while (i2 < min) {
            int i3 = bArr[i2] & MotionEventCompat.ACTION_MASK;
            if (iArr[i3] != 0) {
                if (i3 == 34) {
                    this._inputPtr = i2 + 1;
                    this._textBuffer.setCurrentLength(i);
                    return;
                }
                this._inputPtr = i2;
                _finishString2(emptyAndGetCurrentSegment, i);
            }
            int i4 = i2 + 1;
            i2 = i + 1;
            emptyAndGetCurrentSegment[i] = (char) i3;
            i = i2;
            i2 = i4;
        }
        this._inputPtr = i2;
        _finishString2(emptyAndGetCurrentSegment, i);
    }

    private void _finishString2(char[] cArr, int i) throws IOException, JsonParseException {
        int[] iArr = sInputCodesUtf8;
        byte[] bArr = this._inputBuffer;
        while (true) {
            int i2 = this._inputPtr;
            if (i2 >= this._inputEnd) {
                loadMoreGuaranteed();
                i2 = this._inputPtr;
            }
            if (i >= cArr.length) {
                cArr = this._textBuffer.finishCurrentSegment();
                i = 0;
            }
            int min = Math.min(this._inputEnd, (cArr.length - i) + i2);
            while (i2 < min) {
                int i3 = i2 + 1;
                i2 = bArr[i2] & MotionEventCompat.ACTION_MASK;
                if (iArr[i2] != 0) {
                    this._inputPtr = i3;
                    if (i2 == 34) {
                        this._textBuffer.setCurrentLength(i);
                        return;
                    }
                    switch (iArr[i2]) {
                        case OnSubscribeConcatMap.BOUNDARY /*1*/:
                            i2 = _decodeEscaped();
                            break;
                        case OnSubscribeConcatMap.END /*2*/:
                            i2 = _decodeUtf8_2(i2);
                            break;
                        case ConnectionResult.SERVICE_DISABLED /*3*/:
                            if (this._inputEnd - this._inputPtr < 2) {
                                i2 = _decodeUtf8_3(i2);
                                break;
                            } else {
                                i2 = _decodeUtf8_3fast(i2);
                                break;
                            }
                        case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                            i3 = _decodeUtf8_4(i2);
                            i2 = i + 1;
                            cArr[i] = (char) (55296 | (i3 >> 10));
                            if (i2 >= cArr.length) {
                                cArr = this._textBuffer.finishCurrentSegment();
                                i2 = 0;
                            }
                            i = i2;
                            i2 = (i3 & 1023) | 56320;
                            break;
                        default:
                            if (i2 >= 32) {
                                _reportInvalidChar(i2);
                                break;
                            } else {
                                _throwUnquotedSpace(i2, "string value");
                                break;
                            }
                    }
                    if (i >= cArr.length) {
                        cArr = this._textBuffer.finishCurrentSegment();
                        i3 = 0;
                    } else {
                        i3 = i;
                    }
                    i = i3 + 1;
                    cArr[i3] = (char) i2;
                } else {
                    int i4 = i + 1;
                    cArr[i] = (char) i2;
                    i2 = i3;
                    i = i4;
                }
            }
            this._inputPtr = i2;
        }
    }

    protected void _skipString() throws IOException, JsonParseException {
        this._tokenIncomplete = false;
        int[] iArr = sInputCodesUtf8;
        byte[] bArr = this._inputBuffer;
        while (true) {
            int i = this._inputPtr;
            int i2 = this._inputEnd;
            if (i >= i2) {
                loadMoreGuaranteed();
                i = this._inputPtr;
                i2 = this._inputEnd;
            }
            while (i < i2) {
                int i3 = i + 1;
                i = bArr[i] & MotionEventCompat.ACTION_MASK;
                if (iArr[i] != 0) {
                    this._inputPtr = i3;
                    if (i != 34) {
                        switch (iArr[i]) {
                            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                                _decodeEscaped();
                                break;
                            case OnSubscribeConcatMap.END /*2*/:
                                _skipUtf8_2(i);
                                break;
                            case ConnectionResult.SERVICE_DISABLED /*3*/:
                                _skipUtf8_3(i);
                                break;
                            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                                _skipUtf8_4(i);
                                break;
                            default:
                                if (i >= 32) {
                                    _reportInvalidChar(i);
                                    break;
                                } else {
                                    _throwUnquotedSpace(i, "string value");
                                    break;
                                }
                        }
                    }
                    return;
                }
                i = i3;
            }
            this._inputPtr = i;
        }
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
                byte[] bArr = this._inputBuffer;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                return _handleInvalidNumberStart(bArr[i2] & MotionEventCompat.ACTION_MASK, false);
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected com.fasterxml.jackson.core.JsonToken _handleApostropheValue() throws java.io.IOException, com.fasterxml.jackson.core.JsonParseException {
        /*
        r10 = this;
        r9 = 39;
        r2 = 0;
        r0 = r10._textBuffer;
        r0 = r0.emptyAndGetCurrentSegment();
        r6 = sInputCodesUtf8;
        r7 = r10._inputBuffer;
        r1 = r2;
    L_0x000e:
        r3 = r10._inputPtr;
        r4 = r10._inputEnd;
        if (r3 < r4) goto L_0x0017;
    L_0x0014:
        r10.loadMoreGuaranteed();
    L_0x0017:
        r3 = r0.length;
        if (r1 < r3) goto L_0x0021;
    L_0x001a:
        r0 = r10._textBuffer;
        r0 = r0.finishCurrentSegment();
        r1 = r2;
    L_0x0021:
        r4 = r10._inputEnd;
        r3 = r10._inputPtr;
        r5 = r0.length;
        r5 = r5 - r1;
        r3 = r3 + r5;
        if (r3 >= r4) goto L_0x00b5;
    L_0x002a:
        r4 = r10._inputPtr;
        if (r4 >= r3) goto L_0x000e;
    L_0x002e:
        r4 = r10._inputPtr;
        r5 = r4 + 1;
        r10._inputPtr = r5;
        r4 = r7[r4];
        r5 = r4 & 255;
        if (r5 == r9) goto L_0x003e;
    L_0x003a:
        r4 = r6[r5];
        if (r4 == 0) goto L_0x0048;
    L_0x003e:
        if (r5 != r9) goto L_0x004f;
    L_0x0040:
        r0 = r10._textBuffer;
        r0.setCurrentLength(r1);
        r0 = com.fasterxml.jackson.core.JsonToken.VALUE_STRING;
        return r0;
    L_0x0048:
        r4 = r1 + 1;
        r5 = (char) r5;
        r0[r1] = r5;
        r1 = r4;
        goto L_0x002a;
    L_0x004f:
        r3 = r6[r5];
        switch(r3) {
            case 1: goto L_0x0071;
            case 2: goto L_0x007a;
            case 3: goto L_0x007f;
            case 4: goto L_0x0091;
            default: goto L_0x0054;
        };
    L_0x0054:
        r3 = 32;
        if (r5 >= r3) goto L_0x005d;
    L_0x0058:
        r3 = "string value";
        r10._throwUnquotedSpace(r5, r3);
    L_0x005d:
        r10._reportInvalidChar(r5);
    L_0x0060:
        r3 = r5;
    L_0x0061:
        r4 = r0.length;
        if (r1 < r4) goto L_0x00b1;
    L_0x0064:
        r0 = r10._textBuffer;
        r0 = r0.finishCurrentSegment();
        r4 = r2;
    L_0x006b:
        r1 = r4 + 1;
        r3 = (char) r3;
        r0[r4] = r3;
        goto L_0x000e;
    L_0x0071:
        r3 = 34;
        if (r5 == r3) goto L_0x0060;
    L_0x0075:
        r3 = r10._decodeEscaped();
        goto L_0x0061;
    L_0x007a:
        r3 = r10._decodeUtf8_2(r5);
        goto L_0x0061;
    L_0x007f:
        r3 = r10._inputEnd;
        r4 = r10._inputPtr;
        r3 = r3 - r4;
        r4 = 2;
        if (r3 < r4) goto L_0x008c;
    L_0x0087:
        r3 = r10._decodeUtf8_3fast(r5);
        goto L_0x0061;
    L_0x008c:
        r3 = r10._decodeUtf8_3(r5);
        goto L_0x0061;
    L_0x0091:
        r4 = r10._decodeUtf8_4(r5);
        r3 = r1 + 1;
        r5 = 55296; // 0xd800 float:7.7486E-41 double:2.732E-319;
        r8 = r4 >> 10;
        r5 = r5 | r8;
        r5 = (char) r5;
        r0[r1] = r5;
        r1 = r0.length;
        if (r3 < r1) goto L_0x00b3;
    L_0x00a3:
        r0 = r10._textBuffer;
        r0 = r0.finishCurrentSegment();
        r1 = r2;
    L_0x00aa:
        r3 = 56320; // 0xdc00 float:7.8921E-41 double:2.7826E-319;
        r4 = r4 & 1023;
        r3 = r3 | r4;
        goto L_0x0061;
    L_0x00b1:
        r4 = r1;
        goto L_0x006b;
    L_0x00b3:
        r1 = r3;
        goto L_0x00aa;
    L_0x00b5:
        r3 = r4;
        goto L_0x002a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.UTF8StreamJsonParser._handleApostropheValue():com.fasterxml.jackson.core.JsonToken");
    }

    protected JsonToken _handleInvalidNumberStart(int i, boolean z) throws IOException, JsonParseException {
        int i2 = i;
        while (i2 == 73) {
            String str;
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                _reportInvalidEOFInValue();
            }
            byte[] bArr = this._inputBuffer;
            int i3 = this._inputPtr;
            this._inputPtr = i3 + 1;
            byte b = bArr[i3];
            if (b != 78) {
                if (b != 110) {
                    i2 = b;
                    break;
                }
                str = z ? "-Infinity" : "+Infinity";
            } else {
                str = z ? "-INF" : "+INF";
            }
            _matchToken(str, 3);
            if (isEnabled(Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
                return resetAsNaN(str, z ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
            }
            _reportError("Non-standard token '" + str + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
            byte b2 = b;
        }
        reportUnexpectedNumberChar(i2, "expected digit (0-9) to follow minus sign, for valid numeric value");
        return null;
    }

    protected void _matchToken(String str, int i) throws IOException, JsonParseException {
        int length = str.length();
        do {
            if ((this._inputPtr >= this._inputEnd && !loadMore()) || this._inputBuffer[this._inputPtr] != str.charAt(i)) {
                _reportInvalidToken(str.substring(0, i));
            }
            this._inputPtr++;
            i++;
        } while (i < length);
        if (this._inputPtr < this._inputEnd || loadMore()) {
            length = this._inputBuffer[this._inputPtr] & MotionEventCompat.ACTION_MASK;
            if (length >= 48 && length != 93 && length != 125 && Character.isJavaIdentifierPart((char) _decodeCharForError(length))) {
                _reportInvalidToken(str.substring(0, i));
            }
        }
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
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char _decodeCharForError = (char) _decodeCharForError(bArr[i]);
            if (!Character.isJavaIdentifierPart(_decodeCharForError)) {
                break;
            }
            stringBuilder.append(_decodeCharForError);
        }
        _reportError("Unrecognized token '" + stringBuilder.toString() + "': was expecting " + str2);
    }

    private int _skipWS() throws IOException, JsonParseException {
        while (true) {
            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                break;
            }
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            int i2 = bArr[i] & MotionEventCompat.ACTION_MASK;
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
        }
        throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.getTypeDesc() + " entries");
    }

    private int _skipWSOrEnd() throws IOException, JsonParseException {
        while (true) {
            if (this._inputPtr < this._inputEnd || loadMore()) {
                byte[] bArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int i2 = bArr[i] & MotionEventCompat.ACTION_MASK;
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

    private int _skipColon() throws IOException, JsonParseException {
        int i;
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        byte b = bArr[i2];
        if (b == (byte) 58) {
            if (this._inputPtr < this._inputEnd) {
                i = this._inputBuffer[this._inputPtr] & MotionEventCompat.ACTION_MASK;
                if (i > 32 && i != 47) {
                    this._inputPtr++;
                }
            }
            while (true) {
                if (this._inputPtr < this._inputEnd || loadMore()) {
                    bArr = this._inputBuffer;
                    i2 = this._inputPtr;
                    this._inputPtr = i2 + 1;
                    i = bArr[i2] & MotionEventCompat.ACTION_MASK;
                    if (i > 32) {
                        if (i == 47) {
                            _skipComment();
                        }
                    } else if (i != 32) {
                        if (i == 10) {
                            _skipLF();
                        } else if (i == 13) {
                            _skipCR();
                        } else if (i != 9) {
                            _throwInvalidSpace(i);
                        }
                    }
                } else {
                    throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.getTypeDesc() + " entries");
                }
            }
        }
        i = b & MotionEventCompat.ACTION_MASK;
        while (true) {
            switch (i) {
                case ConnectionResult.SERVICE_INVALID /*9*/:
                case ItemTouchHelper.END /*32*/:
                    break;
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    _skipLF();
                    break;
                case ConnectionResult.CANCELED /*13*/:
                    _skipCR();
                    break;
                case C0801R.styleable.AppCompatTheme_dropdownListPreferredItemHeight /*47*/:
                    _skipComment();
                    break;
                default:
                    if (i < 32) {
                        _throwInvalidSpace(i);
                    }
                    if (i != 58) {
                        _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
                        break;
                    }
                    break;
            }
            if (this._inputPtr >= this._inputEnd) {
                loadMoreGuaranteed();
            }
            bArr = this._inputBuffer;
            i2 = this._inputPtr;
            this._inputPtr = i2 + 1;
            i = bArr[i2] & MotionEventCompat.ACTION_MASK;
        }
        return i;
    }

    private void _skipComment() throws IOException, JsonParseException {
        if (!isEnabled(Feature.ALLOW_COMMENTS)) {
            _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
        }
        if (this._inputPtr >= this._inputEnd && !loadMore()) {
            _reportInvalidEOF(" in a comment");
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int i2 = bArr[i] & MotionEventCompat.ACTION_MASK;
        if (i2 == 47) {
            _skipCppComment();
        } else if (i2 == 42) {
            _skipCComment();
        } else {
            _reportUnexpectedChar(i2, "was expecting either '*' or '/' for a comment");
        }
    }

    private void _skipCComment() throws IOException, JsonParseException {
        int[] inputCodeComment = CharTypes.getInputCodeComment();
        while (true) {
            if (this._inputPtr < this._inputEnd || loadMore()) {
                byte[] bArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int i2 = bArr[i] & MotionEventCompat.ACTION_MASK;
                i = inputCodeComment[i2];
                if (i != 0) {
                    switch (i) {
                        case OnSubscribeConcatMap.END /*2*/:
                            _skipUtf8_2(i2);
                            continue;
                        case ConnectionResult.SERVICE_DISABLED /*3*/:
                            _skipUtf8_3(i2);
                            continue;
                        case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                            _skipUtf8_4(i2);
                            continue;
                        case ConnectionResult.DEVELOPER_ERROR /*10*/:
                            _skipLF();
                            continue;
                        case ConnectionResult.CANCELED /*13*/:
                            _skipCR();
                            continue;
                        case C0784R.styleable.AppCompatTheme_textAppearancePopupMenuHeader /*42*/:
                            if (this._inputPtr >= this._inputEnd && !loadMore()) {
                                break;
                            } else if (this._inputBuffer[this._inputPtr] == 47) {
                                this._inputPtr++;
                                return;
                            } else {
                                continue;
                            }
                        default:
                            _reportInvalidChar(i2);
                            continue;
                    }
                }
            }
            _reportInvalidEOF(" in a comment");
            return;
        }
    }

    private void _skipCppComment() throws IOException, JsonParseException {
        int[] inputCodeComment = CharTypes.getInputCodeComment();
        while (true) {
            if (this._inputPtr < this._inputEnd || loadMore()) {
                byte[] bArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int i2 = bArr[i] & MotionEventCompat.ACTION_MASK;
                i = inputCodeComment[i2];
                if (i != 0) {
                    switch (i) {
                        case OnSubscribeConcatMap.END /*2*/:
                            _skipUtf8_2(i2);
                            break;
                        case ConnectionResult.SERVICE_DISABLED /*3*/:
                            _skipUtf8_3(i2);
                            break;
                        case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                            _skipUtf8_4(i2);
                            break;
                        case ConnectionResult.DEVELOPER_ERROR /*10*/:
                            _skipLF();
                            return;
                        case ConnectionResult.CANCELED /*13*/:
                            _skipCR();
                            return;
                        case C0784R.styleable.AppCompatTheme_textAppearancePopupMenuHeader /*42*/:
                            break;
                        default:
                            _reportInvalidChar(i2);
                            break;
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
        byte[] bArr = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        byte b = bArr[i2];
        switch (b) {
            case C0801R.styleable.AppCompatTheme_actionModePasteDrawable /*34*/:
            case C0801R.styleable.AppCompatTheme_dropdownListPreferredItemHeight /*47*/:
            case C0784R.styleable.AppCompatTheme_colorBackgroundFloating /*92*/:
                return (char) b;
            case C0801R.styleable.AppCompatTheme_buttonBarPositiveButtonStyle /*98*/:
                return '\b';
            case HttpStatus.SC_PROCESSING /*102*/:
                return '\f';
            case C0801R.styleable.AppCompatTheme_ratingBarStyleSmall /*110*/:
                return '\n';
            case C0784R.styleable.AppCompatTheme_listMenuViewStyle /*114*/:
                return '\r';
            case (byte) 116:
                return '\t';
            case (byte) 117:
                int i3 = 0;
                while (i < 4) {
                    if (this._inputPtr >= this._inputEnd && !loadMore()) {
                        _reportInvalidEOF(" in character escape sequence");
                    }
                    byte[] bArr2 = this._inputBuffer;
                    int i4 = this._inputPtr;
                    this._inputPtr = i4 + 1;
                    byte b2 = bArr2[i4];
                    i4 = CharTypes.charToHex(b2);
                    if (i4 < 0) {
                        _reportUnexpectedChar(b2, "expected a hex-digit for character escape sequence");
                    }
                    i3 = (i3 << 4) | i4;
                    i++;
                }
                return (char) i3;
            default:
                return _handleUnrecognizedCharacterEscape((char) _decodeCharForError(b));
        }
    }

    protected int _decodeCharForError(int i) throws IOException, JsonParseException {
        if (i >= 0) {
            return i;
        }
        Object obj;
        int i2;
        if ((i & 224) == 192) {
            i &= 31;
            obj = 1;
        } else if ((i & 240) == 224) {
            i &= 15;
            i2 = 2;
        } else if ((i & 248) == 240) {
            i &= 7;
            obj = 3;
        } else {
            _reportInvalidInitial(i & MotionEventCompat.ACTION_MASK);
            i2 = 1;
        }
        int nextByte = nextByte();
        if ((nextByte & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(nextByte & MotionEventCompat.ACTION_MASK);
        }
        i = (i << 6) | (nextByte & 63);
        if (obj <= 1) {
            return i;
        }
        int nextByte2 = nextByte();
        if ((nextByte2 & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(nextByte2 & MotionEventCompat.ACTION_MASK);
        }
        i = (i << 6) | (nextByte2 & 63);
        if (obj <= 2) {
            return i;
        }
        i2 = nextByte();
        if ((i2 & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(i2 & MotionEventCompat.ACTION_MASK);
        }
        return (i << 6) | (i2 & 63);
    }

    private int _decodeUtf8_2(int i) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        byte b = bArr[i2];
        if ((b & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(b & MotionEventCompat.ACTION_MASK, this._inputPtr);
        }
        return (b & 63) | ((i & 31) << 6);
    }

    private int _decodeUtf8_3(int i) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        int i2 = i & 15;
        byte[] bArr = this._inputBuffer;
        int i3 = this._inputPtr;
        this._inputPtr = i3 + 1;
        byte b = bArr[i3];
        if ((b & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(b & MotionEventCompat.ACTION_MASK, this._inputPtr);
        }
        i2 = (i2 << 6) | (b & 63);
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        bArr = this._inputBuffer;
        i3 = this._inputPtr;
        this._inputPtr = i3 + 1;
        b = bArr[i3];
        if ((b & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(b & MotionEventCompat.ACTION_MASK, this._inputPtr);
        }
        return (i2 << 6) | (b & 63);
    }

    private int _decodeUtf8_3fast(int i) throws IOException, JsonParseException {
        int i2 = i & 15;
        byte[] bArr = this._inputBuffer;
        int i3 = this._inputPtr;
        this._inputPtr = i3 + 1;
        byte b = bArr[i3];
        if ((b & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(b & MotionEventCompat.ACTION_MASK, this._inputPtr);
        }
        i2 = (i2 << 6) | (b & 63);
        bArr = this._inputBuffer;
        i3 = this._inputPtr;
        this._inputPtr = i3 + 1;
        b = bArr[i3];
        if ((b & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(b & MotionEventCompat.ACTION_MASK, this._inputPtr);
        }
        return (i2 << 6) | (b & 63);
    }

    private int _decodeUtf8_4(int i) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        byte b = bArr[i2];
        if ((b & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(b & MotionEventCompat.ACTION_MASK, this._inputPtr);
        }
        int i3 = (b & 63) | ((i & 7) << 6);
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr2 = this._inputBuffer;
        int i4 = this._inputPtr;
        this._inputPtr = i4 + 1;
        byte b2 = bArr2[i4];
        if ((b2 & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(b2 & MotionEventCompat.ACTION_MASK, this._inputPtr);
        }
        i3 = (i3 << 6) | (b2 & 63);
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        bArr2 = this._inputBuffer;
        i4 = this._inputPtr;
        this._inputPtr = i4 + 1;
        b2 = bArr2[i4];
        if ((b2 & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(b2 & MotionEventCompat.ACTION_MASK, this._inputPtr);
        }
        return ((i3 << 6) | (b2 & 63)) - NativeProtocol.MESSAGE_GET_ACCESS_TOKEN_REQUEST;
    }

    private void _skipUtf8_2(int i) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        byte b = bArr[i2];
        if ((b & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(b & MotionEventCompat.ACTION_MASK, this._inputPtr);
        }
    }

    private void _skipUtf8_3(int i) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        byte b = bArr[i2];
        if ((b & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(b & MotionEventCompat.ACTION_MASK, this._inputPtr);
        }
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        bArr = this._inputBuffer;
        i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        b = bArr[i2];
        if ((b & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(b & MotionEventCompat.ACTION_MASK, this._inputPtr);
        }
    }

    private void _skipUtf8_4(int i) throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        byte b = bArr[i2];
        if ((b & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(b & MotionEventCompat.ACTION_MASK, this._inputPtr);
        }
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        bArr = this._inputBuffer;
        i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        b = bArr[i2];
        if ((b & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(b & MotionEventCompat.ACTION_MASK, this._inputPtr);
        }
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        bArr = this._inputBuffer;
        i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        b = bArr[i2];
        if ((b & 192) != TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            _reportInvalidOther(b & MotionEventCompat.ACTION_MASK, this._inputPtr);
        }
    }

    protected void _skipCR() throws IOException {
        if ((this._inputPtr < this._inputEnd || loadMore()) && this._inputBuffer[this._inputPtr] == 10) {
            this._inputPtr++;
        }
        this._currInputRow++;
        this._currInputRowStart = this._inputPtr;
    }

    protected void _skipLF() throws IOException {
        this._currInputRow++;
        this._currInputRowStart = this._inputPtr;
    }

    private int nextByte() throws IOException, JsonParseException {
        if (this._inputPtr >= this._inputEnd) {
            loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        return bArr[i] & MotionEventCompat.ACTION_MASK;
    }

    protected void _reportInvalidChar(int i) throws JsonParseException {
        if (i < 32) {
            _throwInvalidSpace(i);
        }
        _reportInvalidInitial(i);
    }

    protected void _reportInvalidInitial(int i) throws JsonParseException {
        _reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(i));
    }

    protected void _reportInvalidOther(int i) throws JsonParseException {
        _reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(i));
    }

    protected void _reportInvalidOther(int i, int i2) throws JsonParseException {
        this._inputPtr = i2;
        _reportInvalidOther(i);
    }

    public static int[] growArrayBy(int[] iArr, int i) {
        if (iArr == null) {
            return new int[i];
        }
        int length = iArr.length;
        int[] iArr2 = new int[(length + i)];
        System.arraycopy(iArr, 0, iArr2, 0, length);
        return iArr2;
    }

    protected byte[] _decodeBase64(Base64Variant base64Variant) throws IOException, JsonParseException {
        int decodeBase64Char;
        ByteArrayBuilder _getByteArrayBuilder = _getByteArrayBuilder();
        while (true) {
            if (this._inputPtr >= this._inputEnd) {
                loadMoreGuaranteed();
            }
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            i = bArr[i] & MotionEventCompat.ACTION_MASK;
            if (i > 32) {
                decodeBase64Char = base64Variant.decodeBase64Char(i);
                if (decodeBase64Char < 0) {
                    if (i == 34) {
                        return _getByteArrayBuilder.toByteArray();
                    }
                    decodeBase64Char = _decodeBase64Escape(base64Variant, i, 0);
                    if (decodeBase64Char < 0) {
                        continue;
                    }
                }
                if (this._inputPtr >= this._inputEnd) {
                    loadMoreGuaranteed();
                }
                byte[] bArr2 = this._inputBuffer;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                i2 = bArr2[i2] & MotionEventCompat.ACTION_MASK;
                i = base64Variant.decodeBase64Char(i2);
                if (i < 0) {
                    i = _decodeBase64Escape(base64Variant, i2, 1);
                }
                i |= decodeBase64Char << 6;
                if (this._inputPtr >= this._inputEnd) {
                    loadMoreGuaranteed();
                }
                bArr = this._inputBuffer;
                i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                i2 = bArr[i2] & MotionEventCompat.ACTION_MASK;
                decodeBase64Char = base64Variant.decodeBase64Char(i2);
                if (decodeBase64Char < 0) {
                    if (decodeBase64Char != -2) {
                        if (i2 != 34 || base64Variant.usesPadding()) {
                            decodeBase64Char = _decodeBase64Escape(base64Variant, i2, 2);
                        } else {
                            _getByteArrayBuilder.append(i >> 4);
                            return _getByteArrayBuilder.toByteArray();
                        }
                    }
                    if (decodeBase64Char == -2) {
                        if (this._inputPtr >= this._inputEnd) {
                            loadMoreGuaranteed();
                        }
                        bArr = this._inputBuffer;
                        i2 = this._inputPtr;
                        this._inputPtr = i2 + 1;
                        decodeBase64Char = bArr[i2] & MotionEventCompat.ACTION_MASK;
                        if (!base64Variant.usesPaddingChar(decodeBase64Char)) {
                            break;
                        }
                        _getByteArrayBuilder.append(i >> 4);
                    }
                }
                i = (i << 6) | decodeBase64Char;
                if (this._inputPtr >= this._inputEnd) {
                    loadMoreGuaranteed();
                }
                bArr = this._inputBuffer;
                i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                i2 = bArr[i2] & MotionEventCompat.ACTION_MASK;
                decodeBase64Char = base64Variant.decodeBase64Char(i2);
                if (decodeBase64Char < 0) {
                    if (decodeBase64Char != -2) {
                        if (i2 != 34 || base64Variant.usesPadding()) {
                            decodeBase64Char = _decodeBase64Escape(base64Variant, i2, 3);
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
        throw reportInvalidBase64Char(base64Variant, decodeBase64Char, 3, "expected padding character '" + base64Variant.getPaddingChar() + "'");
    }
}
