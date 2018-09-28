package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import java.io.IOException;

public class AsArrayTypeSerializer extends TypeSerializerBase {
    public AsArrayTypeSerializer(TypeIdResolver typeIdResolver, BeanProperty beanProperty) {
        super(typeIdResolver, beanProperty);
    }

    public AsArrayTypeSerializer forProperty(BeanProperty beanProperty) {
        return this._property == beanProperty ? this : new AsArrayTypeSerializer(this._idResolver, beanProperty);
    }

    public As getTypeInclusion() {
        return As.WRAPPER_ARRAY;
    }

    public void writeTypePrefixForObject(Object obj, JsonGenerator jsonGenerator) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartArray();
        jsonGenerator.writeString(idFromValue(obj));
        jsonGenerator.writeStartObject();
    }

    public void writeTypePrefixForObject(Object obj, JsonGenerator jsonGenerator, Class<?> cls) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartArray();
        jsonGenerator.writeString(idFromValueAndType(obj, cls));
        jsonGenerator.writeStartObject();
    }

    public void writeTypePrefixForArray(Object obj, JsonGenerator jsonGenerator) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartArray();
        jsonGenerator.writeString(idFromValue(obj));
        jsonGenerator.writeStartArray();
    }

    public void writeTypePrefixForArray(Object obj, JsonGenerator jsonGenerator, Class<?> cls) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartArray();
        jsonGenerator.writeString(idFromValueAndType(obj, cls));
        jsonGenerator.writeStartArray();
    }

    public void writeTypePrefixForScalar(Object obj, JsonGenerator jsonGenerator) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartArray();
        jsonGenerator.writeString(idFromValue(obj));
    }

    public void writeTypePrefixForScalar(Object obj, JsonGenerator jsonGenerator, Class<?> cls) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartArray();
        jsonGenerator.writeString(idFromValueAndType(obj, cls));
    }

    public void writeTypeSuffixForObject(Object obj, JsonGenerator jsonGenerator) throws IOException, JsonProcessingException {
        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndArray();
    }

    public void writeTypeSuffixForArray(Object obj, JsonGenerator jsonGenerator) throws IOException, JsonProcessingException {
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndArray();
    }

    public void writeTypeSuffixForScalar(Object obj, JsonGenerator jsonGenerator) throws IOException, JsonProcessingException {
        jsonGenerator.writeEndArray();
    }

    public void writeCustomTypePrefixForObject(Object obj, JsonGenerator jsonGenerator, String str) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartArray();
        jsonGenerator.writeString(str);
        jsonGenerator.writeStartObject();
    }

    public void writeCustomTypePrefixForArray(Object obj, JsonGenerator jsonGenerator, String str) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartArray();
        jsonGenerator.writeString(str);
        jsonGenerator.writeStartArray();
    }

    public void writeCustomTypePrefixForScalar(Object obj, JsonGenerator jsonGenerator, String str) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartArray();
        jsonGenerator.writeString(str);
    }

    public void writeCustomTypeSuffixForObject(Object obj, JsonGenerator jsonGenerator, String str) throws IOException, JsonProcessingException {
        writeTypeSuffixForObject(obj, jsonGenerator);
    }

    public void writeCustomTypeSuffixForArray(Object obj, JsonGenerator jsonGenerator, String str) throws IOException, JsonProcessingException {
        writeTypeSuffixForArray(obj, jsonGenerator);
    }

    public void writeCustomTypeSuffixForScalar(Object obj, JsonGenerator jsonGenerator, String str) throws IOException, JsonProcessingException {
        writeTypeSuffixForScalar(obj, jsonGenerator);
    }
}
