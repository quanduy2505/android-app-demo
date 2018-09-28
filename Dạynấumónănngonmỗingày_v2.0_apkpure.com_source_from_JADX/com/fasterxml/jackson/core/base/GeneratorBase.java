package com.fasterxml.jackson.core.base;

import android.support.v4.media.TransportMediator;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.google.android.gms.common.ConnectionResult;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import me.wangyuwei.loadingview.C0801R;
import rx.internal.operators.OnSubscribeConcatMap;

public abstract class GeneratorBase extends JsonGenerator {
    protected boolean _cfgNumbersAsStrings;
    protected boolean _closed;
    protected int _features;
    protected ObjectCodec _objectCodec;
    protected JsonWriteContext _writeContext;

    /* renamed from: com.fasterxml.jackson.core.base.GeneratorBase.1 */
    static /* synthetic */ class C04861 {
        static final /* synthetic */ int[] $SwitchMap$com$fasterxml$jackson$core$JsonParser$NumberType;
        static final /* synthetic */ int[] $SwitchMap$com$fasterxml$jackson$core$JsonToken;

        static {
            $SwitchMap$com$fasterxml$jackson$core$JsonToken = new int[JsonToken.values().length];
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.START_OBJECT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.END_OBJECT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.START_ARRAY.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.END_ARRAY.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.FIELD_NAME.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_STRING.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_NUMBER_INT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_NUMBER_FLOAT.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_TRUE.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_FALSE.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_NULL.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_EMBEDDED_OBJECT.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            $SwitchMap$com$fasterxml$jackson$core$JsonParser$NumberType = new int[NumberType.values().length];
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonParser$NumberType[NumberType.INT.ordinal()] = 1;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonParser$NumberType[NumberType.BIG_INTEGER.ordinal()] = 2;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonParser$NumberType[NumberType.BIG_DECIMAL.ordinal()] = 3;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonParser$NumberType[NumberType.FLOAT.ordinal()] = 4;
            } catch (NoSuchFieldError e16) {
            }
        }
    }

    protected abstract void _releaseBuffers();

    protected abstract void _verifyValueWrite(String str) throws IOException, JsonGenerationException;

    public abstract void flush() throws IOException;

    protected GeneratorBase(int i, ObjectCodec objectCodec) {
        this._features = i;
        this._writeContext = JsonWriteContext.createRootContext();
        this._objectCodec = objectCodec;
        this._cfgNumbersAsStrings = isEnabled(Feature.WRITE_NUMBERS_AS_STRINGS);
    }

    public Version version() {
        return VersionUtil.versionFor(getClass());
    }

    public JsonGenerator enable(Feature feature) {
        this._features |= feature.getMask();
        if (feature == Feature.WRITE_NUMBERS_AS_STRINGS) {
            this._cfgNumbersAsStrings = true;
        } else if (feature == Feature.ESCAPE_NON_ASCII) {
            setHighestNonEscapedChar(TransportMediator.KEYCODE_MEDIA_PAUSE);
        }
        return this;
    }

    public JsonGenerator disable(Feature feature) {
        this._features &= feature.getMask() ^ -1;
        if (feature == Feature.WRITE_NUMBERS_AS_STRINGS) {
            this._cfgNumbersAsStrings = false;
        } else if (feature == Feature.ESCAPE_NON_ASCII) {
            setHighestNonEscapedChar(0);
        }
        return this;
    }

    public final boolean isEnabled(Feature feature) {
        return (this._features & feature.getMask()) != 0;
    }

    public JsonGenerator useDefaultPrettyPrinter() {
        return getPrettyPrinter() != null ? this : setPrettyPrinter(new DefaultPrettyPrinter());
    }

    public JsonGenerator setCodec(ObjectCodec objectCodec) {
        this._objectCodec = objectCodec;
        return this;
    }

    public final ObjectCodec getCodec() {
        return this._objectCodec;
    }

    public final JsonWriteContext getOutputContext() {
        return this._writeContext;
    }

    public void writeFieldName(SerializableString serializableString) throws IOException, JsonGenerationException {
        writeFieldName(serializableString.getValue());
    }

    public void writeString(SerializableString serializableString) throws IOException, JsonGenerationException {
        writeString(serializableString.getValue());
    }

    public void writeRawValue(String str) throws IOException, JsonGenerationException {
        _verifyValueWrite("write raw value");
        writeRaw(str);
    }

    public void writeRawValue(String str, int i, int i2) throws IOException, JsonGenerationException {
        _verifyValueWrite("write raw value");
        writeRaw(str, i, i2);
    }

    public void writeRawValue(char[] cArr, int i, int i2) throws IOException, JsonGenerationException {
        _verifyValueWrite("write raw value");
        writeRaw(cArr, i, i2);
    }

    public int writeBinary(Base64Variant base64Variant, InputStream inputStream, int i) throws IOException, JsonGenerationException {
        _reportUnsupportedOperation();
        return 0;
    }

    public void writeObject(Object obj) throws IOException, JsonProcessingException {
        if (obj == null) {
            writeNull();
        } else if (this._objectCodec != null) {
            this._objectCodec.writeValue(this, obj);
        } else {
            _writeSimpleObject(obj);
        }
    }

    public void writeTree(TreeNode treeNode) throws IOException, JsonProcessingException {
        if (treeNode == null) {
            writeNull();
        } else if (this._objectCodec == null) {
            throw new IllegalStateException("No ObjectCodec defined");
        } else {
            this._objectCodec.writeValue(this, treeNode);
        }
    }

    public void close() throws IOException {
        this._closed = true;
    }

    public boolean isClosed() {
        return this._closed;
    }

    public final void copyCurrentEvent(JsonParser jsonParser) throws IOException, JsonProcessingException {
        JsonToken currentToken = jsonParser.getCurrentToken();
        if (currentToken == null) {
            _reportError("No current event to copy");
        }
        switch (C04861.$SwitchMap$com$fasterxml$jackson$core$JsonToken[currentToken.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                writeStartObject();
            case OnSubscribeConcatMap.END /*2*/:
                writeEndObject();
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                writeStartArray();
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                writeEndArray();
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                writeFieldName(jsonParser.getCurrentName());
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                if (jsonParser.hasTextCharacters()) {
                    writeString(jsonParser.getTextCharacters(), jsonParser.getTextOffset(), jsonParser.getTextLength());
                } else {
                    writeString(jsonParser.getText());
                }
            case ConnectionResult.NETWORK_ERROR /*7*/:
                switch (C04861.$SwitchMap$com$fasterxml$jackson$core$JsonParser$NumberType[jsonParser.getNumberType().ordinal()]) {
                    case OnSubscribeConcatMap.BOUNDARY /*1*/:
                        writeNumber(jsonParser.getIntValue());
                    case OnSubscribeConcatMap.END /*2*/:
                        writeNumber(jsonParser.getBigIntegerValue());
                    default:
                        writeNumber(jsonParser.getLongValue());
                }
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                switch (C04861.$SwitchMap$com$fasterxml$jackson$core$JsonParser$NumberType[jsonParser.getNumberType().ordinal()]) {
                    case ConnectionResult.SERVICE_DISABLED /*3*/:
                        writeNumber(jsonParser.getDecimalValue());
                    case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                        writeNumber(jsonParser.getFloatValue());
                    default:
                        writeNumber(jsonParser.getDoubleValue());
                }
            case ConnectionResult.SERVICE_INVALID /*9*/:
                writeBoolean(true);
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                writeBoolean(false);
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                writeNull();
            case C0801R.styleable.Toolbar_titleTextAppearance /*12*/:
                writeObject(jsonParser.getEmbeddedObject());
            default:
                _throwInternal();
        }
    }

    public final void copyCurrentStructure(JsonParser jsonParser) throws IOException, JsonProcessingException {
        JsonToken currentToken = jsonParser.getCurrentToken();
        if (currentToken == JsonToken.FIELD_NAME) {
            writeFieldName(jsonParser.getCurrentName());
            currentToken = jsonParser.nextToken();
        }
        switch (C04861.$SwitchMap$com$fasterxml$jackson$core$JsonToken[currentToken.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                writeStartObject();
                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                    copyCurrentStructure(jsonParser);
                }
                writeEndObject();
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                writeStartArray();
                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                    copyCurrentStructure(jsonParser);
                }
                writeEndArray();
            default:
                copyCurrentEvent(jsonParser);
        }
    }

    protected void _reportError(String str) throws JsonGenerationException {
        throw new JsonGenerationException(str);
    }

    protected void _writeSimpleObject(Object obj) throws IOException, JsonGenerationException {
        if (obj == null) {
            writeNull();
        } else if (obj instanceof String) {
            writeString((String) obj);
        } else {
            if (obj instanceof Number) {
                Number number = (Number) obj;
                if (number instanceof Integer) {
                    writeNumber(number.intValue());
                    return;
                } else if (number instanceof Long) {
                    writeNumber(number.longValue());
                    return;
                } else if (number instanceof Double) {
                    writeNumber(number.doubleValue());
                    return;
                } else if (number instanceof Float) {
                    writeNumber(number.floatValue());
                    return;
                } else if (number instanceof Short) {
                    writeNumber(number.shortValue());
                    return;
                } else if (number instanceof Byte) {
                    writeNumber((short) number.byteValue());
                    return;
                } else if (number instanceof BigInteger) {
                    writeNumber((BigInteger) number);
                    return;
                } else if (number instanceof BigDecimal) {
                    writeNumber((BigDecimal) number);
                    return;
                } else if (number instanceof AtomicInteger) {
                    writeNumber(((AtomicInteger) number).get());
                    return;
                } else if (number instanceof AtomicLong) {
                    writeNumber(((AtomicLong) number).get());
                    return;
                }
            } else if (obj instanceof byte[]) {
                writeBinary((byte[]) obj);
                return;
            } else if (obj instanceof Boolean) {
                writeBoolean(((Boolean) obj).booleanValue());
                return;
            } else if (obj instanceof AtomicBoolean) {
                writeBoolean(((AtomicBoolean) obj).get());
                return;
            }
            throw new IllegalStateException("No ObjectCodec defined for the generator, can only serialize simple wrapper types (type passed " + obj.getClass().getName() + ")");
        }
    }

    protected final void _throwInternal() {
        VersionUtil.throwInternal();
    }

    protected void _reportUnsupportedOperation() {
        throw new UnsupportedOperationException("Operation not supported by generator of type " + getClass().getName());
    }
}
