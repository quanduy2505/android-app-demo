package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Typing;
import com.fasterxml.jackson.databind.annotation.NoClass;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.impl.IndexedStringListSerializer;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import com.fasterxml.jackson.databind.ser.impl.StringCollectionSerializer;
import com.fasterxml.jackson.databind.ser.std.BooleanSerializer;
import com.fasterxml.jackson.databind.ser.std.CalendarSerializer;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.EnumMapSerializer;
import com.fasterxml.jackson.databind.ser.std.EnumSerializer;
import com.fasterxml.jackson.databind.ser.std.InetAddressSerializer;
import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers.NumberSerializer;
import com.fasterxml.jackson.databind.ser.std.ObjectArraySerializer;
import com.fasterxml.jackson.databind.ser.std.SerializableSerializer;
import com.fasterxml.jackson.databind.ser.std.SqlDateSerializer;
import com.fasterxml.jackson.databind.ser.std.SqlTimeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdArraySerializers;
import com.fasterxml.jackson.databind.ser.std.StdContainerSerializers;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.ser.std.StdJdkSerializers;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.fasterxml.jackson.databind.ser.std.TimeZoneSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.ser.std.TokenBufferSerializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.EnumValues;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.RandomAccess;
import java.util.TimeZone;

public abstract class BasicSerializerFactory extends SerializerFactory implements Serializable {
    protected static final HashMap<String, JsonSerializer<?>> _concrete;
    protected static final HashMap<String, Class<? extends JsonSerializer<?>>> _concreteLazy;
    protected final SerializerFactoryConfig _factoryConfig;

    public abstract JsonSerializer<Object> createSerializer(SerializerProvider serializerProvider, JavaType javaType) throws JsonMappingException;

    protected abstract Iterable<Serializers> customSerializers();

    public abstract SerializerFactory withConfig(SerializerFactoryConfig serializerFactoryConfig);

    static {
        _concrete = new HashMap();
        _concreteLazy = new HashMap();
        _concrete.put(String.class.getName(), new StringSerializer());
        ToStringSerializer toStringSerializer = ToStringSerializer.instance;
        _concrete.put(StringBuffer.class.getName(), toStringSerializer);
        _concrete.put(StringBuilder.class.getName(), toStringSerializer);
        _concrete.put(Character.class.getName(), toStringSerializer);
        _concrete.put(Character.TYPE.getName(), toStringSerializer);
        NumberSerializers.addAll(_concrete);
        _concrete.put(Boolean.TYPE.getName(), new BooleanSerializer(true));
        _concrete.put(Boolean.class.getName(), new BooleanSerializer(false));
        NumberSerializer numberSerializer = new NumberSerializer();
        _concrete.put(BigInteger.class.getName(), numberSerializer);
        _concrete.put(BigDecimal.class.getName(), numberSerializer);
        _concrete.put(Calendar.class.getName(), CalendarSerializer.instance);
        DateSerializer dateSerializer = DateSerializer.instance;
        _concrete.put(Date.class.getName(), dateSerializer);
        _concrete.put(Timestamp.class.getName(), dateSerializer);
        _concreteLazy.put(java.sql.Date.class.getName(), SqlDateSerializer.class);
        _concreteLazy.put(Time.class.getName(), SqlTimeSerializer.class);
        for (Entry entry : StdJdkSerializers.all()) {
            Object value = entry.getValue();
            if (value instanceof JsonSerializer) {
                _concrete.put(((Class) entry.getKey()).getName(), (JsonSerializer) value);
            } else if (value instanceof Class) {
                _concreteLazy.put(((Class) entry.getKey()).getName(), (Class) value);
            } else {
                throw new IllegalStateException("Internal error: unrecognized value of type " + entry.getClass().getName());
            }
        }
        _concreteLazy.put(TokenBuffer.class.getName(), TokenBufferSerializer.class);
    }

    protected BasicSerializerFactory(SerializerFactoryConfig serializerFactoryConfig) {
        if (serializerFactoryConfig == null) {
            serializerFactoryConfig = new SerializerFactoryConfig();
        }
        this._factoryConfig = serializerFactoryConfig;
    }

    public SerializerFactoryConfig getFactoryConfig() {
        return this._factoryConfig;
    }

    public final SerializerFactory withAdditionalSerializers(Serializers serializers) {
        return withConfig(this._factoryConfig.withAdditionalSerializers(serializers));
    }

    public final SerializerFactory withAdditionalKeySerializers(Serializers serializers) {
        return withConfig(this._factoryConfig.withAdditionalKeySerializers(serializers));
    }

    public final SerializerFactory withSerializerModifier(BeanSerializerModifier beanSerializerModifier) {
        return withConfig(this._factoryConfig.withSerializerModifier(beanSerializerModifier));
    }

    public JsonSerializer<Object> createKeySerializer(SerializationConfig serializationConfig, JavaType javaType, JsonSerializer<Object> jsonSerializer) {
        BeanDescription introspectClassAnnotations = serializationConfig.introspectClassAnnotations(javaType.getRawClass());
        JsonSerializer<Object> jsonSerializer2 = null;
        if (this._factoryConfig.hasKeySerializers()) {
            for (Serializers findSerializer : this._factoryConfig.keySerializers()) {
                jsonSerializer2 = findSerializer.findSerializer(serializationConfig, javaType, introspectClassAnnotations);
                if (jsonSerializer2 != null) {
                    break;
                }
            }
        }
        if (jsonSerializer2 != null) {
            jsonSerializer = jsonSerializer2;
        } else if (jsonSerializer == null) {
            jsonSerializer = StdKeySerializers.getStdKeySerializer(javaType);
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier modifyKeySerializer : this._factoryConfig.serializerModifiers()) {
                jsonSerializer = modifyKeySerializer.modifyKeySerializer(serializationConfig, javaType, introspectClassAnnotations, jsonSerializer);
            }
        }
        return jsonSerializer;
    }

    public TypeSerializer createTypeSerializer(SerializationConfig serializationConfig, JavaType javaType) {
        Collection collection;
        AnnotatedClass classInfo = serializationConfig.introspectClassAnnotations(javaType.getRawClass()).getClassInfo();
        AnnotationIntrospector annotationIntrospector = serializationConfig.getAnnotationIntrospector();
        TypeResolverBuilder findTypeResolver = annotationIntrospector.findTypeResolver(serializationConfig, classInfo, javaType);
        if (findTypeResolver == null) {
            findTypeResolver = serializationConfig.getDefaultTyper(javaType);
            collection = null;
        } else {
            collection = serializationConfig.getSubtypeResolver().collectAndResolveSubtypes(classInfo, (MapperConfig) serializationConfig, annotationIntrospector);
        }
        if (findTypeResolver == null) {
            return null;
        }
        return findTypeResolver.buildTypeSerializer(serializationConfig, javaType, collection);
    }

    public final JsonSerializer<?> getNullSerializer() {
        return NullSerializer.instance;
    }

    protected final JsonSerializer<?> findSerializerByLookup(JavaType javaType, SerializationConfig serializationConfig, BeanDescription beanDescription, boolean z) {
        String name = javaType.getRawClass().getName();
        JsonSerializer<?> jsonSerializer = (JsonSerializer) _concrete.get(name);
        if (jsonSerializer != null) {
            return jsonSerializer;
        }
        Class cls = (Class) _concreteLazy.get(name);
        if (cls == null) {
            return jsonSerializer;
        }
        try {
            return (JsonSerializer) cls.newInstance();
        } catch (Throwable e) {
            throw new IllegalStateException("Failed to instantiate standard serializer (of type " + cls.getName() + "): " + e.getMessage(), e);
        }
    }

    protected final JsonSerializer<?> findSerializerByAnnotations(SerializerProvider serializerProvider, JavaType javaType, BeanDescription beanDescription) throws JsonMappingException {
        if (JsonSerializable.class.isAssignableFrom(javaType.getRawClass())) {
            return SerializableSerializer.instance;
        }
        Annotated findJsonValueMethod = beanDescription.findJsonValueMethod();
        if (findJsonValueMethod == null) {
            return null;
        }
        Object annotated = findJsonValueMethod.getAnnotated();
        if (serializerProvider.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(annotated);
        }
        return new JsonValueSerializer(annotated, findSerializerFromAnnotation(serializerProvider, findJsonValueMethod));
    }

    protected final JsonSerializer<?> findSerializerByPrimaryType(SerializerProvider serializerProvider, JavaType javaType, BeanDescription beanDescription, boolean z) throws JsonMappingException {
        Class rawClass = javaType.getRawClass();
        if (InetAddress.class.isAssignableFrom(rawClass)) {
            return InetAddressSerializer.instance;
        }
        if (TimeZone.class.isAssignableFrom(rawClass)) {
            return TimeZoneSerializer.instance;
        }
        if (Charset.class.isAssignableFrom(rawClass)) {
            return ToStringSerializer.instance;
        }
        JsonSerializer<?> findOptionalStdSerializer = findOptionalStdSerializer(serializerProvider, javaType, beanDescription, z);
        if (findOptionalStdSerializer != null) {
            return findOptionalStdSerializer;
        }
        if (Number.class.isAssignableFrom(rawClass)) {
            return NumberSerializer.instance;
        }
        if (Enum.class.isAssignableFrom(rawClass)) {
            return buildEnumSerializer(serializerProvider.getConfig(), javaType, beanDescription);
        }
        if (Calendar.class.isAssignableFrom(rawClass)) {
            return CalendarSerializer.instance;
        }
        if (Date.class.isAssignableFrom(rawClass)) {
            return DateSerializer.instance;
        }
        return null;
    }

    protected JsonSerializer<?> findOptionalStdSerializer(SerializerProvider serializerProvider, JavaType javaType, BeanDescription beanDescription, boolean z) throws JsonMappingException {
        return OptionalHandlerFactory.instance.findSerializer(serializerProvider.getConfig(), javaType, beanDescription);
    }

    protected final JsonSerializer<?> findSerializerByAddonType(SerializationConfig serializationConfig, JavaType javaType, BeanDescription beanDescription, boolean z) throws JsonMappingException {
        Class rawClass = javaType.getRawClass();
        if (Iterator.class.isAssignableFrom(rawClass)) {
            return buildIteratorSerializer(serializationConfig, javaType, beanDescription, z);
        }
        if (Iterable.class.isAssignableFrom(rawClass)) {
            return buildIterableSerializer(serializationConfig, javaType, beanDescription, z);
        }
        if (CharSequence.class.isAssignableFrom(rawClass)) {
            return ToStringSerializer.instance;
        }
        return null;
    }

    protected JsonSerializer<Object> findSerializerFromAnnotation(SerializerProvider serializerProvider, Annotated annotated) throws JsonMappingException {
        Object findSerializer = serializerProvider.getAnnotationIntrospector().findSerializer(annotated);
        if (findSerializer == null) {
            return null;
        }
        return findConvertingSerializer(serializerProvider, annotated, serializerProvider.serializerInstance(annotated, findSerializer));
    }

    protected JsonSerializer<?> findConvertingSerializer(SerializerProvider serializerProvider, Annotated annotated, JsonSerializer<?> jsonSerializer) throws JsonMappingException {
        Converter findConverter = findConverter(serializerProvider, annotated);
        return findConverter == null ? jsonSerializer : new StdDelegatingSerializer(findConverter, findConverter.getOutputType(serializerProvider.getTypeFactory()), jsonSerializer);
    }

    protected Converter<Object, Object> findConverter(SerializerProvider serializerProvider, Annotated annotated) throws JsonMappingException {
        Object findSerializationConverter = serializerProvider.getAnnotationIntrospector().findSerializationConverter(annotated);
        if (findSerializationConverter == null) {
            return null;
        }
        return serializerProvider.converterInstance(annotated, findSerializationConverter);
    }

    @Deprecated
    protected final JsonSerializer<?> buildContainerSerializer(SerializerProvider serializerProvider, JavaType javaType, BeanDescription beanDescription, BeanProperty beanProperty, boolean z) throws JsonMappingException {
        return buildContainerSerializer(serializerProvider, javaType, beanDescription, z);
    }

    protected JsonSerializer<?> buildContainerSerializer(SerializerProvider serializerProvider, JavaType javaType, BeanDescription beanDescription, boolean z) throws JsonMappingException {
        boolean z2;
        JsonSerializer<?> findMapLikeSerializer;
        SerializationConfig config = serializerProvider.getConfig();
        if (!(z || !javaType.useStaticType() || (javaType.isContainerType() && javaType.getContentType().getRawClass() == Object.class))) {
            z = true;
        }
        TypeSerializer createTypeSerializer = createTypeSerializer(config, javaType.getContentType());
        if (createTypeSerializer != null) {
            z2 = false;
        } else {
            z2 = z;
        }
        JsonSerializer _findContentSerializer = _findContentSerializer(serializerProvider, beanDescription.getClassInfo());
        JsonSerializer<?> jsonSerializer;
        if (javaType.isMapLikeType()) {
            MapLikeType mapLikeType = (MapLikeType) javaType;
            JsonSerializer _findKeySerializer = _findKeySerializer(serializerProvider, beanDescription.getClassInfo());
            if (mapLikeType.isTrueMapType()) {
                return buildMapSerializer(config, (MapType) mapLikeType, beanDescription, z2, _findKeySerializer, createTypeSerializer, _findContentSerializer);
            }
            for (Serializers findMapLikeSerializer2 : customSerializers()) {
                MapLikeType mapLikeType2 = (MapLikeType) javaType;
                findMapLikeSerializer = findMapLikeSerializer2.findMapLikeSerializer(config, mapLikeType2, beanDescription, _findKeySerializer, createTypeSerializer, _findContentSerializer);
                if (findMapLikeSerializer != null) {
                    if (this._factoryConfig.hasSerializerModifiers()) {
                        jsonSerializer = findMapLikeSerializer;
                        for (BeanSerializerModifier modifyMapLikeSerializer : this._factoryConfig.serializerModifiers()) {
                            jsonSerializer = modifyMapLikeSerializer.modifyMapLikeSerializer(config, mapLikeType2, beanDescription, jsonSerializer);
                        }
                        return jsonSerializer;
                    }
                }
            }
            return null;
        } else if (javaType.isCollectionLikeType()) {
            CollectionLikeType collectionLikeType = (CollectionLikeType) javaType;
            if (collectionLikeType.isTrueCollectionType()) {
                return buildCollectionSerializer(config, (CollectionType) collectionLikeType, beanDescription, z2, createTypeSerializer, _findContentSerializer);
            }
            CollectionLikeType collectionLikeType2 = (CollectionLikeType) javaType;
            for (Serializers findCollectionLikeSerializer : customSerializers()) {
                findMapLikeSerializer = findCollectionLikeSerializer.findCollectionLikeSerializer(config, collectionLikeType2, beanDescription, createTypeSerializer, _findContentSerializer);
                if (findMapLikeSerializer != null) {
                    if (this._factoryConfig.hasSerializerModifiers()) {
                        jsonSerializer = findMapLikeSerializer;
                        for (BeanSerializerModifier modifyMapLikeSerializer2 : this._factoryConfig.serializerModifiers()) {
                            jsonSerializer = modifyMapLikeSerializer2.modifyCollectionLikeSerializer(config, collectionLikeType2, beanDescription, jsonSerializer);
                        }
                        return jsonSerializer;
                    }
                }
            }
            return null;
        } else if (!javaType.isArrayType()) {
            return null;
        } else {
            return buildArraySerializer(config, (ArrayType) javaType, beanDescription, z2, createTypeSerializer, _findContentSerializer);
        }
        return findMapLikeSerializer;
    }

    @Deprecated
    protected final JsonSerializer<?> buildCollectionSerializer(SerializationConfig serializationConfig, CollectionType collectionType, BeanDescription beanDescription, BeanProperty beanProperty, boolean z, TypeSerializer typeSerializer, JsonSerializer<Object> jsonSerializer) throws JsonMappingException {
        return buildCollectionSerializer(serializationConfig, collectionType, beanDescription, z, typeSerializer, jsonSerializer);
    }

    protected JsonSerializer<?> buildCollectionSerializer(SerializationConfig serializationConfig, CollectionType collectionType, BeanDescription beanDescription, boolean z, TypeSerializer typeSerializer, JsonSerializer<Object> jsonSerializer) throws JsonMappingException {
        JsonSerializer<?> jsonSerializer2;
        JavaType javaType = null;
        JsonSerializer<?> jsonSerializer3 = null;
        for (Serializers findCollectionSerializer : customSerializers()) {
            jsonSerializer3 = findCollectionSerializer.findCollectionSerializer(serializationConfig, collectionType, beanDescription, typeSerializer, jsonSerializer);
            if (jsonSerializer3 != null) {
                break;
            }
        }
        if (jsonSerializer3 == null) {
            Value findExpectedFormat = beanDescription.findExpectedFormat(null);
            if (findExpectedFormat != null && findExpectedFormat.getShape() == Shape.OBJECT) {
                return null;
            }
            Class rawClass = collectionType.getRawClass();
            if (EnumSet.class.isAssignableFrom(rawClass)) {
                JavaType contentType = collectionType.getContentType();
                if (contentType.isEnumType()) {
                    javaType = contentType;
                }
                jsonSerializer3 = StdContainerSerializers.enumSetSerializer(javaType);
            } else {
                Class rawClass2 = collectionType.getContentType().getRawClass();
                if (isIndexedList(rawClass)) {
                    if (rawClass2 != String.class) {
                        jsonSerializer3 = StdContainerSerializers.indexedListSerializer(collectionType.getContentType(), z, typeSerializer, jsonSerializer);
                    } else if (jsonSerializer == null || ClassUtil.isJacksonStdImpl((Object) jsonSerializer)) {
                        jsonSerializer3 = IndexedStringListSerializer.instance;
                    }
                } else if (rawClass2 == String.class && (jsonSerializer == null || ClassUtil.isJacksonStdImpl((Object) jsonSerializer))) {
                    jsonSerializer3 = StringCollectionSerializer.instance;
                }
                if (jsonSerializer3 == null) {
                    jsonSerializer3 = StdContainerSerializers.collectionSerializer(collectionType.getContentType(), z, typeSerializer, jsonSerializer);
                }
            }
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            jsonSerializer2 = jsonSerializer3;
            for (BeanSerializerModifier modifyCollectionSerializer : this._factoryConfig.serializerModifiers()) {
                jsonSerializer2 = modifyCollectionSerializer.modifyCollectionSerializer(serializationConfig, collectionType, beanDescription, jsonSerializer2);
            }
        } else {
            jsonSerializer2 = jsonSerializer3;
        }
        return jsonSerializer2;
    }

    protected boolean isIndexedList(Class<?> cls) {
        return RandomAccess.class.isAssignableFrom(cls);
    }

    protected JsonSerializer<?> buildMapSerializer(SerializationConfig serializationConfig, MapType mapType, BeanDescription beanDescription, boolean z, JsonSerializer<Object> jsonSerializer, TypeSerializer typeSerializer, JsonSerializer<Object> jsonSerializer2) throws JsonMappingException {
        JsonSerializer<?> jsonSerializer3 = null;
        for (Serializers findMapSerializer : customSerializers()) {
            jsonSerializer3 = findMapSerializer.findMapSerializer(serializationConfig, mapType, beanDescription, jsonSerializer, typeSerializer, jsonSerializer2);
            if (jsonSerializer3 != null) {
                break;
            }
        }
        if (jsonSerializer3 == null) {
            if (EnumMap.class.isAssignableFrom(mapType.getRawClass())) {
                JavaType keyType = mapType.getKeyType();
                EnumValues enumValues = null;
                if (keyType.isEnumType()) {
                    enumValues = EnumValues.construct(keyType.getRawClass(), serializationConfig.getAnnotationIntrospector());
                }
                jsonSerializer3 = new EnumMapSerializer(mapType.getContentType(), z, enumValues, typeSerializer, jsonSerializer2);
            } else {
                jsonSerializer3 = MapSerializer.construct(serializationConfig.getAnnotationIntrospector().findPropertiesToIgnore(beanDescription.getClassInfo()), mapType, z, typeSerializer, jsonSerializer, jsonSerializer2);
            }
        }
        if (!this._factoryConfig.hasSerializerModifiers()) {
            return jsonSerializer3;
        }
        JsonSerializer<?> jsonSerializer4 = jsonSerializer3;
        for (BeanSerializerModifier modifyMapSerializer : this._factoryConfig.serializerModifiers()) {
            jsonSerializer4 = modifyMapSerializer.modifyMapSerializer(serializationConfig, mapType, beanDescription, jsonSerializer4);
        }
        return jsonSerializer4;
    }

    protected JsonSerializer<?> buildArraySerializer(SerializationConfig serializationConfig, ArrayType arrayType, BeanDescription beanDescription, boolean z, TypeSerializer typeSerializer, JsonSerializer<Object> jsonSerializer) throws JsonMappingException {
        JsonSerializer<?> jsonSerializer2 = null;
        for (Serializers findArraySerializer : customSerializers()) {
            jsonSerializer2 = findArraySerializer.findArraySerializer(serializationConfig, arrayType, beanDescription, typeSerializer, jsonSerializer);
            if (jsonSerializer2 != null) {
                break;
            }
        }
        if (jsonSerializer2 == null) {
            Class rawClass = arrayType.getRawClass();
            if (jsonSerializer == null || ClassUtil.isJacksonStdImpl((Object) jsonSerializer)) {
                if (String[].class == rawClass) {
                    jsonSerializer2 = StringArraySerializer.instance;
                } else {
                    jsonSerializer2 = StdArraySerializers.findStandardImpl(rawClass);
                }
            }
            if (jsonSerializer2 == null) {
                jsonSerializer2 = new ObjectArraySerializer(arrayType.getContentType(), z, typeSerializer, (JsonSerializer) jsonSerializer);
            }
        }
        if (!this._factoryConfig.hasSerializerModifiers()) {
            return jsonSerializer2;
        }
        JsonSerializer<?> jsonSerializer3 = jsonSerializer2;
        for (BeanSerializerModifier modifyArraySerializer : this._factoryConfig.serializerModifiers()) {
            jsonSerializer3 = modifyArraySerializer.modifyArraySerializer(serializationConfig, arrayType, beanDescription, jsonSerializer3);
        }
        return jsonSerializer3;
    }

    protected JsonSerializer<?> buildIteratorSerializer(SerializationConfig serializationConfig, JavaType javaType, BeanDescription beanDescription, boolean z) throws JsonMappingException {
        JavaType containedType = javaType.containedType(0);
        if (containedType == null) {
            containedType = TypeFactory.unknownType();
        }
        TypeSerializer createTypeSerializer = createTypeSerializer(serializationConfig, containedType);
        return StdContainerSerializers.iteratorSerializer(containedType, usesStaticTyping(serializationConfig, beanDescription, createTypeSerializer), createTypeSerializer);
    }

    protected JsonSerializer<?> buildIterableSerializer(SerializationConfig serializationConfig, JavaType javaType, BeanDescription beanDescription, boolean z) throws JsonMappingException {
        JavaType containedType = javaType.containedType(0);
        if (containedType == null) {
            containedType = TypeFactory.unknownType();
        }
        TypeSerializer createTypeSerializer = createTypeSerializer(serializationConfig, containedType);
        return StdContainerSerializers.iterableSerializer(containedType, usesStaticTyping(serializationConfig, beanDescription, createTypeSerializer), createTypeSerializer);
    }

    protected JsonSerializer<?> buildEnumSerializer(SerializationConfig serializationConfig, JavaType javaType, BeanDescription beanDescription) throws JsonMappingException {
        Value findExpectedFormat = beanDescription.findExpectedFormat(null);
        if (findExpectedFormat == null || findExpectedFormat.getShape() != Shape.OBJECT) {
            JsonSerializer<?> construct = EnumSerializer.construct(javaType.getRawClass(), serializationConfig, beanDescription, findExpectedFormat);
            if (!this._factoryConfig.hasSerializerModifiers()) {
                return construct;
            }
            JsonSerializer<?> jsonSerializer = construct;
            for (BeanSerializerModifier modifyEnumSerializer : this._factoryConfig.serializerModifiers()) {
                jsonSerializer = modifyEnumSerializer.modifyEnumSerializer(serializationConfig, javaType, beanDescription, jsonSerializer);
            }
            return jsonSerializer;
        }
        ((BasicBeanDescription) beanDescription).removeProperty("declaringClass");
        return null;
    }

    protected <T extends JavaType> T modifyTypeByAnnotation(SerializationConfig serializationConfig, Annotated annotated, T t) {
        Class findSerializationType = serializationConfig.getAnnotationIntrospector().findSerializationType(annotated);
        if (findSerializationType != null) {
            try {
                t = t.widenBy(findSerializationType);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Failed to widen type " + t + " with concrete-type annotation (value " + findSerializationType.getName() + "), method '" + annotated.getName() + "': " + e.getMessage());
            }
        }
        return modifySecondaryTypesByAnnotation(serializationConfig, annotated, t);
    }

    protected static <T extends JavaType> T modifySecondaryTypesByAnnotation(SerializationConfig serializationConfig, Annotated annotated, T t) {
        AnnotationIntrospector annotationIntrospector = serializationConfig.getAnnotationIntrospector();
        if (t.isContainerType()) {
            Class findSerializationKeyType = annotationIntrospector.findSerializationKeyType(annotated, t.getKeyType());
            if (findSerializationKeyType != null) {
                if (t instanceof MapType) {
                    try {
                        t = ((MapType) t).widenKey(findSerializationKeyType);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Failed to narrow key type " + t + " with key-type annotation (" + findSerializationKeyType.getName() + "): " + e.getMessage());
                    }
                }
                throw new IllegalArgumentException("Illegal key-type annotation: type " + t + " is not a Map type");
            }
            Class findSerializationContentType = annotationIntrospector.findSerializationContentType(annotated, t.getContentType());
            if (findSerializationContentType != null) {
                try {
                    t = t.widenContentsBy(findSerializationContentType);
                } catch (IllegalArgumentException e2) {
                    throw new IllegalArgumentException("Failed to narrow content type " + t + " with content-type annotation (" + findSerializationContentType.getName() + "): " + e2.getMessage());
                }
            }
        }
        return t;
    }

    protected JsonSerializer<Object> _findKeySerializer(SerializerProvider serializerProvider, Annotated annotated) throws JsonMappingException {
        Object findKeySerializer = serializerProvider.getAnnotationIntrospector().findKeySerializer(annotated);
        if (findKeySerializer != null) {
            return serializerProvider.serializerInstance(annotated, findKeySerializer);
        }
        return null;
    }

    protected JsonSerializer<Object> _findContentSerializer(SerializerProvider serializerProvider, Annotated annotated) throws JsonMappingException {
        Object findContentSerializer = serializerProvider.getAnnotationIntrospector().findContentSerializer(annotated);
        if (findContentSerializer != null) {
            return serializerProvider.serializerInstance(annotated, findContentSerializer);
        }
        return null;
    }

    @Deprecated
    protected final boolean usesStaticTyping(SerializationConfig serializationConfig, BeanDescription beanDescription, TypeSerializer typeSerializer, BeanProperty beanProperty) {
        return usesStaticTyping(serializationConfig, beanDescription, typeSerializer);
    }

    protected boolean usesStaticTyping(SerializationConfig serializationConfig, BeanDescription beanDescription, TypeSerializer typeSerializer) {
        if (typeSerializer != null) {
            return false;
        }
        Typing findSerializationTyping = serializationConfig.getAnnotationIntrospector().findSerializationTyping(beanDescription.getClassInfo());
        if (findSerializationTyping == null) {
            return serializationConfig.isEnabled(MapperFeature.USE_STATIC_TYPING);
        }
        if (findSerializationTyping == Typing.STATIC) {
            return true;
        }
        return false;
    }

    protected Class<?> _verifyAsClass(Object obj, String str, Class<?> cls) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Class) {
            Class<?> cls2 = (Class) obj;
            if (cls2 == cls || cls2 == NoClass.class) {
                return null;
            }
            return cls2;
        }
        throw new IllegalStateException("AnnotationIntrospector." + str + "() returned value of type " + obj.getClass().getName() + ": expected type JsonSerializer or Class<JsonSerializer> instead");
    }
}
