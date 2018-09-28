package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import com.google.android.gms.common.ConnectionResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import rx.internal.operators.OnSubscribeConcatMap;

@JacksonStdImpl
public class UntypedObjectDeserializer extends StdDeserializer<Object> {
    private static final Object[] NO_OBJECTS;
    public static final UntypedObjectDeserializer instance;
    private static final long serialVersionUID = 1;

    /* renamed from: com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer.1 */
    static /* synthetic */ class C05001 {
        static final /* synthetic */ int[] $SwitchMap$com$fasterxml$jackson$core$JsonToken;

        static {
            $SwitchMap$com$fasterxml$jackson$core$JsonToken = new int[JsonToken.values().length];
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.START_OBJECT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.START_ARRAY.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.FIELD_NAME.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_EMBEDDED_OBJECT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_STRING.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_NUMBER_INT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_NUMBER_FLOAT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_TRUE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_FALSE.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.VALUE_NULL.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.END_ARRAY.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$fasterxml$jackson$core$JsonToken[JsonToken.END_OBJECT.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    static {
        NO_OBJECTS = new Object[0];
        instance = new UntypedObjectDeserializer();
    }

    public UntypedObjectDeserializer() {
        super(Object.class);
    }

    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        switch (C05001.$SwitchMap$com$fasterxml$jackson$core$JsonToken[jsonParser.getCurrentToken().ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                return mapObject(jsonParser, deserializationContext);
            case OnSubscribeConcatMap.END /*2*/:
                return mapArray(jsonParser, deserializationContext);
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                return mapObject(jsonParser, deserializationContext);
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                return jsonParser.getEmbeddedObject();
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                return jsonParser.getText();
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                if (deserializationContext.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                    return jsonParser.getBigIntegerValue();
                }
                return jsonParser.getNumberValue();
            case ConnectionResult.NETWORK_ERROR /*7*/:
                if (deserializationContext.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return jsonParser.getDecimalValue();
                }
                return Double.valueOf(jsonParser.getDoubleValue());
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                return Boolean.TRUE;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                return Boolean.FALSE;
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                return null;
            default:
                throw deserializationContext.mappingException(Object.class);
        }
    }

    public Object deserializeWithType(JsonParser jsonParser, DeserializationContext deserializationContext, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        switch (C05001.$SwitchMap$com$fasterxml$jackson$core$JsonToken[jsonParser.getCurrentToken().ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
            case OnSubscribeConcatMap.END /*2*/:
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                return typeDeserializer.deserializeTypedFromAny(jsonParser, deserializationContext);
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                return jsonParser.getEmbeddedObject();
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                return jsonParser.getText();
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                if (deserializationContext.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                    return jsonParser.getBigIntegerValue();
                }
                return jsonParser.getNumberValue();
            case ConnectionResult.NETWORK_ERROR /*7*/:
                if (deserializationContext.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                    return jsonParser.getDecimalValue();
                }
                return Double.valueOf(jsonParser.getDoubleValue());
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                return Boolean.TRUE;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                return Boolean.FALSE;
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                return null;
            default:
                throw deserializationContext.mappingException(Object.class);
        }
    }

    protected Object mapArray(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        if (deserializationContext.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
            return mapArrayToArray(jsonParser, deserializationContext);
        }
        if (jsonParser.nextToken() == JsonToken.END_ARRAY) {
            return new ArrayList(4);
        }
        ObjectBuffer leaseObjectBuffer = deserializationContext.leaseObjectBuffer();
        int i = 0;
        Object[] resetAndStart = leaseObjectBuffer.resetAndStart();
        int i2 = 0;
        do {
            int i3;
            Object deserialize = deserialize(jsonParser, deserializationContext);
            i2++;
            if (i >= resetAndStart.length) {
                resetAndStart = leaseObjectBuffer.appendCompletedChunk(resetAndStart);
                i3 = 0;
            } else {
                i3 = i;
            }
            i = i3 + 1;
            resetAndStart[i3] = deserialize;
        } while (jsonParser.nextToken() != JsonToken.END_ARRAY);
        List arrayList = new ArrayList((i2 + (i2 >> 3)) + 1);
        leaseObjectBuffer.completeAndClearBuffer(resetAndStart, i, arrayList);
        return arrayList;
    }

    protected Object mapObject(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonToken currentToken = jsonParser.getCurrentToken();
        if (currentToken == JsonToken.START_OBJECT) {
            currentToken = jsonParser.nextToken();
        }
        if (currentToken != JsonToken.FIELD_NAME) {
            return new LinkedHashMap(4);
        }
        String text = jsonParser.getText();
        jsonParser.nextToken();
        Object deserialize = deserialize(jsonParser, deserializationContext);
        if (jsonParser.nextToken() != JsonToken.FIELD_NAME) {
            Object linkedHashMap = new LinkedHashMap(4);
            linkedHashMap.put(text, deserialize);
            return linkedHashMap;
        }
        String text2 = jsonParser.getText();
        jsonParser.nextToken();
        Object deserialize2 = deserialize(jsonParser, deserializationContext);
        if (jsonParser.nextToken() != JsonToken.FIELD_NAME) {
            linkedHashMap = new LinkedHashMap(4);
            linkedHashMap.put(text, deserialize);
            linkedHashMap.put(text2, deserialize2);
            return linkedHashMap;
        }
        linkedHashMap = new LinkedHashMap();
        linkedHashMap.put(text, deserialize);
        linkedHashMap.put(text2, deserialize2);
        do {
            text = jsonParser.getText();
            jsonParser.nextToken();
            linkedHashMap.put(text, deserialize(jsonParser, deserializationContext));
        } while (jsonParser.nextToken() != JsonToken.END_OBJECT);
        return linkedHashMap;
    }

    protected Object[] mapArrayToArray(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        if (jsonParser.nextToken() == JsonToken.END_ARRAY) {
            return NO_OBJECTS;
        }
        ObjectBuffer leaseObjectBuffer = deserializationContext.leaseObjectBuffer();
        Object[] resetAndStart = leaseObjectBuffer.resetAndStart();
        int i = 0;
        do {
            int i2;
            Object deserialize = deserialize(jsonParser, deserializationContext);
            if (i >= resetAndStart.length) {
                resetAndStart = leaseObjectBuffer.appendCompletedChunk(resetAndStart);
                i2 = 0;
            } else {
                i2 = i;
            }
            i = i2 + 1;
            resetAndStart[i2] = deserialize;
        } while (jsonParser.nextToken() != JsonToken.END_ARRAY);
        return leaseObjectBuffer.completeAndClearBuffer(resetAndStart, i);
    }
}
