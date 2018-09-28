package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import rx.internal.operators.OnSubscribeConcatMap;

public class JsonNodeDeserializer extends BaseNodeDeserializer {
    private static final JsonNodeDeserializer instance;

    /* renamed from: com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer.1 */
    static /* synthetic */ class C04971 {
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
        }
    }

    static final class ArrayDeserializer extends BaseNodeDeserializer {
        protected static final ArrayDeserializer _instance;
        private static final long serialVersionUID = 1;

        static {
            _instance = new ArrayDeserializer();
        }

        protected ArrayDeserializer() {
        }

        public static ArrayDeserializer getInstance() {
            return _instance;
        }

        public ArrayNode deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            if (jsonParser.isExpectedStartArrayToken()) {
                return deserializeArray(jsonParser, deserializationContext, deserializationContext.getNodeFactory());
            }
            throw deserializationContext.mappingException(ArrayNode.class);
        }
    }

    static final class ObjectDeserializer extends BaseNodeDeserializer {
        protected static final ObjectDeserializer _instance;
        private static final long serialVersionUID = 1;

        static {
            _instance = new ObjectDeserializer();
        }

        protected ObjectDeserializer() {
        }

        public static ObjectDeserializer getInstance() {
            return _instance;
        }

        public ObjectNode deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                jsonParser.nextToken();
                return deserializeObject(jsonParser, deserializationContext, deserializationContext.getNodeFactory());
            } else if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
                return deserializeObject(jsonParser, deserializationContext, deserializationContext.getNodeFactory());
            } else {
                throw deserializationContext.mappingException(ObjectNode.class);
            }
        }
    }

    public /* bridge */ /* synthetic */ Object deserializeWithType(JsonParser jsonParser, DeserializationContext deserializationContext, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return super.deserializeWithType(jsonParser, deserializationContext, typeDeserializer);
    }

    public /* bridge */ /* synthetic */ JsonNode getNullValue() {
        return super.getNullValue();
    }

    static {
        instance = new JsonNodeDeserializer();
    }

    protected JsonNodeDeserializer() {
    }

    public static JsonDeserializer<? extends JsonNode> getDeserializer(Class<?> cls) {
        if (cls == ObjectNode.class) {
            return ObjectDeserializer.getInstance();
        }
        if (cls == ArrayNode.class) {
            return ArrayDeserializer.getInstance();
        }
        return instance;
    }

    public JsonNode deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        switch (C04971.$SwitchMap$com$fasterxml$jackson$core$JsonToken[jsonParser.getCurrentToken().ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                return deserializeObject(jsonParser, deserializationContext, deserializationContext.getNodeFactory());
            case OnSubscribeConcatMap.END /*2*/:
                return deserializeArray(jsonParser, deserializationContext, deserializationContext.getNodeFactory());
            default:
                return deserializeAny(jsonParser, deserializationContext, deserializationContext.getNodeFactory());
        }
    }
}
