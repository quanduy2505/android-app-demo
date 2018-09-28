package com.fasterxml.jackson.databind.deser;

import com.facebook.share.internal.ShareConstants;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.BeanProperty.Std;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder.Value;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.impl.FieldProperty;
import com.fasterxml.jackson.databind.deser.impl.MethodProperty;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.databind.deser.impl.SetterlessProperty;
import com.fasterxml.jackson.databind.deser.std.JdkDeserializers.AtomicReferenceDeserializer;
import com.fasterxml.jackson.databind.deser.std.ThrowableDeserializer;
import com.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class BeanDeserializerFactory extends BasicDeserializerFactory implements Serializable {
    private static final Class<?>[] INIT_CAUSE_PARAMS;
    private static final Class<?>[] NO_VIEWS;
    public static final BeanDeserializerFactory instance;
    private static final long serialVersionUID = 1;

    static {
        INIT_CAUSE_PARAMS = new Class[]{Throwable.class};
        NO_VIEWS = new Class[0];
        instance = new BeanDeserializerFactory(new DeserializerFactoryConfig());
    }

    public BeanDeserializerFactory(DeserializerFactoryConfig deserializerFactoryConfig) {
        super(deserializerFactoryConfig);
    }

    public DeserializerFactory withConfig(DeserializerFactoryConfig deserializerFactoryConfig) {
        if (this._factoryConfig == deserializerFactoryConfig) {
            return this;
        }
        if (getClass() != BeanDeserializerFactory.class) {
            throw new IllegalStateException("Subtype of BeanDeserializerFactory (" + getClass().getName() + ") has not properly overridden method 'withAdditionalDeserializers': can not instantiate subtype with " + "additional deserializer definitions");
        }
        this(deserializerFactoryConfig);
        return this;
    }

    protected JsonDeserializer<Object> _findCustomBeanDeserializer(JavaType javaType, DeserializationConfig deserializationConfig, BeanDescription beanDescription) throws JsonMappingException {
        for (Deserializers findBeanDeserializer : this._factoryConfig.deserializers()) {
            JsonDeserializer<Object> findBeanDeserializer2 = findBeanDeserializer.findBeanDeserializer(javaType, deserializationConfig, beanDescription);
            if (findBeanDeserializer2 != null) {
                return findBeanDeserializer2;
            }
        }
        return null;
    }

    public JsonDeserializer<Object> createBeanDeserializer(DeserializationContext deserializationContext, JavaType javaType, BeanDescription beanDescription) throws JsonMappingException {
        DeserializationConfig config = deserializationContext.getConfig();
        JsonDeserializer<Object> _findCustomBeanDeserializer = _findCustomBeanDeserializer(javaType, config, beanDescription);
        if (_findCustomBeanDeserializer != null) {
            return _findCustomBeanDeserializer;
        }
        if (javaType.isThrowable()) {
            return buildThrowableDeserializer(deserializationContext, javaType, beanDescription);
        }
        if (javaType.isAbstract()) {
            JavaType materializeAbstractType = materializeAbstractType(deserializationContext, javaType, beanDescription);
            if (materializeAbstractType != null) {
                return buildBeanDeserializer(deserializationContext, materializeAbstractType, config.introspect(materializeAbstractType));
            }
        }
        _findCustomBeanDeserializer = findStdDeserializer(deserializationContext, javaType, beanDescription);
        if (_findCustomBeanDeserializer != null) {
            return _findCustomBeanDeserializer;
        }
        if (isPotentialBeanType(javaType.getRawClass())) {
            return buildBeanDeserializer(deserializationContext, javaType, beanDescription);
        }
        return null;
    }

    public JsonDeserializer<Object> createBuilderBasedDeserializer(DeserializationContext deserializationContext, JavaType javaType, BeanDescription beanDescription, Class<?> cls) throws JsonMappingException {
        return buildBuilderBasedDeserializer(deserializationContext, javaType, deserializationContext.getConfig().introspectForBuilder(deserializationContext.constructType(cls)));
    }

    protected JsonDeserializer<?> findStdDeserializer(DeserializationContext deserializationContext, JavaType javaType, BeanDescription beanDescription) throws JsonMappingException {
        JsonDeserializer<?> findDefaultDeserializer = findDefaultDeserializer(deserializationContext, javaType, beanDescription);
        if (findDefaultDeserializer != null) {
            return findDefaultDeserializer;
        }
        if (!AtomicReference.class.isAssignableFrom(javaType.getRawClass())) {
            return findOptionalStdDeserializer(deserializationContext, javaType, beanDescription);
        }
        JavaType unknownType;
        JavaType[] findTypeParameters = deserializationContext.getTypeFactory().findTypeParameters(javaType, AtomicReference.class);
        if (findTypeParameters == null || findTypeParameters.length < 1) {
            unknownType = TypeFactory.unknownType();
        } else {
            unknownType = findTypeParameters[0];
        }
        return new AtomicReferenceDeserializer(unknownType);
    }

    protected JsonDeserializer<?> findOptionalStdDeserializer(DeserializationContext deserializationContext, JavaType javaType, BeanDescription beanDescription) throws JsonMappingException {
        return OptionalHandlerFactory.instance.findDeserializer(javaType, deserializationContext.getConfig(), beanDescription);
    }

    protected JavaType materializeAbstractType(DeserializationContext deserializationContext, JavaType javaType, BeanDescription beanDescription) throws JsonMappingException {
        JavaType type = beanDescription.getType();
        for (AbstractTypeResolver resolveAbstractType : this._factoryConfig.abstractTypeResolvers()) {
            JavaType resolveAbstractType2 = resolveAbstractType.resolveAbstractType(deserializationContext.getConfig(), type);
            if (resolveAbstractType2 != null) {
                return resolveAbstractType2;
            }
        }
        return null;
    }

    public JsonDeserializer<Object> buildBeanDeserializer(DeserializationContext deserializationContext, JavaType javaType, BeanDescription beanDescription) throws JsonMappingException {
        BeanDeserializerBuilder beanDeserializerBuilder;
        JsonDeserializer<Object> build;
        ValueInstantiator findValueInstantiator = findValueInstantiator(deserializationContext, beanDescription);
        BeanDeserializerBuilder constructBeanDeserializerBuilder = constructBeanDeserializerBuilder(deserializationContext, beanDescription);
        constructBeanDeserializerBuilder.setValueInstantiator(findValueInstantiator);
        addBeanProps(deserializationContext, beanDescription, constructBeanDeserializerBuilder);
        addObjectIdReader(deserializationContext, beanDescription, constructBeanDeserializerBuilder);
        addReferenceProperties(deserializationContext, beanDescription, constructBeanDeserializerBuilder);
        addInjectables(deserializationContext, beanDescription, constructBeanDeserializerBuilder);
        DeserializationConfig config = deserializationContext.getConfig();
        if (this._factoryConfig.hasDeserializerModifiers()) {
            beanDeserializerBuilder = constructBeanDeserializerBuilder;
            for (BeanDeserializerModifier updateBuilder : this._factoryConfig.deserializerModifiers()) {
                beanDeserializerBuilder = updateBuilder.updateBuilder(config, beanDescription, beanDeserializerBuilder);
            }
        } else {
            beanDeserializerBuilder = constructBeanDeserializerBuilder;
        }
        if (!javaType.isAbstract() || findValueInstantiator.canInstantiate()) {
            build = beanDeserializerBuilder.build();
        } else {
            build = beanDeserializerBuilder.buildAbstract();
        }
        if (!this._factoryConfig.hasDeserializerModifiers()) {
            return build;
        }
        JsonDeserializer<Object> jsonDeserializer = build;
        for (BeanDeserializerModifier updateBuilder2 : this._factoryConfig.deserializerModifiers()) {
            jsonDeserializer = updateBuilder2.modifyDeserializer(config, beanDescription, jsonDeserializer);
        }
        return jsonDeserializer;
    }

    protected JsonDeserializer<Object> buildBuilderBasedDeserializer(DeserializationContext deserializationContext, JavaType javaType, BeanDescription beanDescription) throws JsonMappingException {
        ValueInstantiator findValueInstantiator = findValueInstantiator(deserializationContext, beanDescription);
        DeserializationConfig config = deserializationContext.getConfig();
        BeanDeserializerBuilder constructBeanDeserializerBuilder = constructBeanDeserializerBuilder(deserializationContext, beanDescription);
        constructBeanDeserializerBuilder.setValueInstantiator(findValueInstantiator);
        addBeanProps(deserializationContext, beanDescription, constructBeanDeserializerBuilder);
        addObjectIdReader(deserializationContext, beanDescription, constructBeanDeserializerBuilder);
        addReferenceProperties(deserializationContext, beanDescription, constructBeanDeserializerBuilder);
        addInjectables(deserializationContext, beanDescription, constructBeanDeserializerBuilder);
        Value findPOJOBuilderConfig = beanDescription.findPOJOBuilderConfig();
        String str = findPOJOBuilderConfig == null ? "build" : findPOJOBuilderConfig.buildMethodName;
        AnnotatedMethod findMethod = beanDescription.findMethod(str, null);
        if (findMethod != null && config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(findMethod.getMember());
        }
        constructBeanDeserializerBuilder.setPOJOBuilder(findMethod, findPOJOBuilderConfig);
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier updateBuilder : this._factoryConfig.deserializerModifiers()) {
                constructBeanDeserializerBuilder = updateBuilder.updateBuilder(config, beanDescription, constructBeanDeserializerBuilder);
            }
        }
        JsonDeserializer<Object> buildBuilderBased = constructBeanDeserializerBuilder.buildBuilderBased(javaType, str);
        if (!this._factoryConfig.hasDeserializerModifiers()) {
            return buildBuilderBased;
        }
        JsonDeserializer<Object> jsonDeserializer = buildBuilderBased;
        for (BeanDeserializerModifier updateBuilder2 : this._factoryConfig.deserializerModifiers()) {
            jsonDeserializer = updateBuilder2.modifyDeserializer(config, beanDescription, jsonDeserializer);
        }
        return jsonDeserializer;
    }

    protected void addObjectIdReader(DeserializationContext deserializationContext, BeanDescription beanDescription, BeanDeserializerBuilder beanDeserializerBuilder) throws JsonMappingException {
        ObjectIdInfo objectIdInfo = beanDescription.getObjectIdInfo();
        if (objectIdInfo != null) {
            SettableBeanProperty findProperty;
            JavaType type;
            ObjectIdGenerator propertyBasedObjectIdGenerator;
            Class generatorType = objectIdInfo.getGeneratorType();
            if (generatorType == PropertyGenerator.class) {
                String propertyName = objectIdInfo.getPropertyName();
                findProperty = beanDeserializerBuilder.findProperty(propertyName);
                if (findProperty == null) {
                    throw new IllegalArgumentException("Invalid Object Id definition for " + beanDescription.getBeanClass().getName() + ": can not find property with name '" + propertyName + "'");
                }
                type = findProperty.getType();
                propertyBasedObjectIdGenerator = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
            } else {
                type = deserializationContext.getTypeFactory().findTypeParameters(deserializationContext.constructType(generatorType), ObjectIdGenerator.class)[0];
                findProperty = null;
                propertyBasedObjectIdGenerator = deserializationContext.objectIdGeneratorInstance(beanDescription.getClassInfo(), objectIdInfo);
            }
            beanDeserializerBuilder.setObjectIdReader(ObjectIdReader.construct(type, objectIdInfo.getPropertyName(), propertyBasedObjectIdGenerator, deserializationContext.findRootValueDeserializer(type), findProperty));
        }
    }

    public JsonDeserializer<Object> buildThrowableDeserializer(DeserializationContext deserializationContext, JavaType javaType, BeanDescription beanDescription) throws JsonMappingException {
        BeanDeserializerBuilder beanDeserializerBuilder;
        DeserializationConfig config = deserializationContext.getConfig();
        BeanDeserializerBuilder constructBeanDeserializerBuilder = constructBeanDeserializerBuilder(deserializationContext, beanDescription);
        constructBeanDeserializerBuilder.setValueInstantiator(findValueInstantiator(deserializationContext, beanDescription));
        addBeanProps(deserializationContext, beanDescription, constructBeanDeserializerBuilder);
        AnnotatedMember findMethod = beanDescription.findMethod("initCause", INIT_CAUSE_PARAMS);
        if (findMethod != null) {
            SettableBeanProperty constructSettableProperty = constructSettableProperty(deserializationContext, beanDescription, SimpleBeanPropertyDefinition.construct(deserializationContext.getConfig(), findMethod, "cause"), findMethod.getGenericParameterType(0));
            if (constructSettableProperty != null) {
                constructBeanDeserializerBuilder.addOrReplaceProperty(constructSettableProperty, true);
            }
        }
        constructBeanDeserializerBuilder.addIgnorable("localizedMessage");
        constructBeanDeserializerBuilder.addIgnorable("suppressed");
        constructBeanDeserializerBuilder.addIgnorable(ShareConstants.WEB_DIALOG_PARAM_MESSAGE);
        if (this._factoryConfig.hasDeserializerModifiers()) {
            beanDeserializerBuilder = constructBeanDeserializerBuilder;
            for (BeanDeserializerModifier updateBuilder : this._factoryConfig.deserializerModifiers()) {
                beanDeserializerBuilder = updateBuilder.updateBuilder(config, beanDescription, beanDeserializerBuilder);
            }
        } else {
            beanDeserializerBuilder = constructBeanDeserializerBuilder;
        }
        JsonDeserializer<Object> build = beanDeserializerBuilder.build();
        if (build instanceof BeanDeserializer) {
            build = new ThrowableDeserializer((BeanDeserializer) build);
        }
        if (!this._factoryConfig.hasDeserializerModifiers()) {
            return build;
        }
        JsonDeserializer<Object> jsonDeserializer = build;
        for (BeanDeserializerModifier updateBuilder2 : this._factoryConfig.deserializerModifiers()) {
            jsonDeserializer = updateBuilder2.modifyDeserializer(config, beanDescription, jsonDeserializer);
        }
        return jsonDeserializer;
    }

    protected BeanDeserializerBuilder constructBeanDeserializerBuilder(DeserializationContext deserializationContext, BeanDescription beanDescription) {
        return new BeanDeserializerBuilder(beanDescription, deserializationContext.getConfig());
    }

    protected void addBeanProps(DeserializationContext deserializationContext, BeanDescription beanDescription, BeanDeserializerBuilder beanDeserializerBuilder) throws JsonMappingException {
        SettableBeanProperty[] fromObjectArguments = beanDeserializerBuilder.getValueInstantiator().getFromObjectArguments(deserializationContext.getConfig());
        AnnotationIntrospector annotationIntrospector = deserializationContext.getAnnotationIntrospector();
        Boolean findIgnoreUnknownProperties = annotationIntrospector.findIgnoreUnknownProperties(beanDescription.getClassInfo());
        if (findIgnoreUnknownProperties != null) {
            beanDeserializerBuilder.setIgnoreUnknownProperties(findIgnoreUnknownProperties.booleanValue());
        }
        Set<String> arrayToSet = ArrayBuilders.arrayToSet(annotationIntrospector.findPropertiesToIgnore(beanDescription.getClassInfo()));
        for (String addIgnorable : arrayToSet) {
            beanDeserializerBuilder.addIgnorable(addIgnorable);
        }
        AnnotatedMethod findAnySetter = beanDescription.findAnySetter();
        if (findAnySetter != null) {
            beanDeserializerBuilder.setAnySetter(constructAnySetter(deserializationContext, beanDescription, findAnySetter));
        }
        if (findAnySetter == null) {
            Collection<String> ignoredPropertyNames = beanDescription.getIgnoredPropertyNames();
            if (ignoredPropertyNames != null) {
                for (String addIgnorable2 : ignoredPropertyNames) {
                    beanDeserializerBuilder.addIgnorable(addIgnorable2);
                }
            }
        }
        int i = (deserializationContext.isEnabled(MapperFeature.USE_GETTERS_AS_SETTERS) && deserializationContext.isEnabled(MapperFeature.AUTO_DETECT_GETTERS)) ? 1 : 0;
        List filterBeanProps = filterBeanProps(deserializationContext, beanDescription, beanDeserializerBuilder, beanDescription.findProperties(), arrayToSet);
        List list;
        if (this._factoryConfig.hasDeserializerModifiers()) {
            list = filterBeanProps;
            for (BeanDeserializerModifier updateProperties : this._factoryConfig.deserializerModifiers()) {
                list = updateProperties.updateProperties(deserializationContext.getConfig(), beanDescription, list);
            }
        } else {
            list = filterBeanProps;
        }
        for (BeanPropertyDefinition beanPropertyDefinition : r1) {
            SettableBeanProperty settableBeanProperty = null;
            if (beanPropertyDefinition.hasConstructorParameter()) {
                String name = beanPropertyDefinition.getName();
                if (fromObjectArguments != null) {
                    for (SettableBeanProperty settableBeanProperty2 : fromObjectArguments) {
                        if (name.equals(settableBeanProperty2.getName())) {
                            settableBeanProperty = settableBeanProperty2;
                            break;
                        }
                    }
                }
                if (settableBeanProperty == null) {
                    throw deserializationContext.mappingException("Could not find creator property with name '" + name + "' (in class " + beanDescription.getBeanClass().getName() + ")");
                }
                beanDeserializerBuilder.addCreatorProperty(settableBeanProperty);
            } else {
                if (beanPropertyDefinition.hasSetter()) {
                    settableBeanProperty = constructSettableProperty(deserializationContext, beanDescription, beanPropertyDefinition, beanPropertyDefinition.getSetter().getGenericParameterType(0));
                } else if (beanPropertyDefinition.hasField()) {
                    settableBeanProperty = constructSettableProperty(deserializationContext, beanDescription, beanPropertyDefinition, beanPropertyDefinition.getField().getGenericType());
                } else if (i != 0 && beanPropertyDefinition.hasGetter()) {
                    Class rawType = beanPropertyDefinition.getGetter().getRawType();
                    if (Collection.class.isAssignableFrom(rawType) || Map.class.isAssignableFrom(rawType)) {
                        settableBeanProperty = constructSetterlessProperty(deserializationContext, beanDescription, beanPropertyDefinition);
                    }
                }
                if (settableBeanProperty != null) {
                    Class[] findViews = beanPropertyDefinition.findViews();
                    if (findViews == null && !deserializationContext.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION)) {
                        findViews = NO_VIEWS;
                    }
                    settableBeanProperty.setViews(findViews);
                    beanDeserializerBuilder.addProperty(settableBeanProperty);
                }
            }
        }
    }

    protected List<BeanPropertyDefinition> filterBeanProps(DeserializationContext deserializationContext, BeanDescription beanDescription, BeanDeserializerBuilder beanDeserializerBuilder, List<BeanPropertyDefinition> list, Set<String> set) throws JsonMappingException {
        List arrayList = new ArrayList(Math.max(4, list.size()));
        Map hashMap = new HashMap();
        for (BeanPropertyDefinition beanPropertyDefinition : list) {
            String name = beanPropertyDefinition.getName();
            if (!set.contains(name)) {
                if (!beanPropertyDefinition.hasConstructorParameter()) {
                    Class cls = null;
                    if (beanPropertyDefinition.hasSetter()) {
                        cls = beanPropertyDefinition.getSetter().getRawParameterType(0);
                    } else if (beanPropertyDefinition.hasField()) {
                        cls = beanPropertyDefinition.getField().getRawType();
                    }
                    if (cls != null && isIgnorableType(deserializationContext.getConfig(), beanDescription, cls, hashMap)) {
                        beanDeserializerBuilder.addIgnorable(name);
                    }
                }
                arrayList.add(beanPropertyDefinition);
            }
        }
        return arrayList;
    }

    protected void addReferenceProperties(DeserializationContext deserializationContext, BeanDescription beanDescription, BeanDeserializerBuilder beanDeserializerBuilder) throws JsonMappingException {
        Map findBackReferenceProperties = beanDescription.findBackReferenceProperties();
        if (findBackReferenceProperties != null) {
            for (Entry entry : findBackReferenceProperties.entrySet()) {
                Type genericParameterType;
                String str = (String) entry.getKey();
                AnnotatedMember annotatedMember = (AnnotatedMember) entry.getValue();
                if (annotatedMember instanceof AnnotatedMethod) {
                    genericParameterType = ((AnnotatedMethod) annotatedMember).getGenericParameterType(0);
                } else {
                    genericParameterType = annotatedMember.getRawType();
                }
                beanDeserializerBuilder.addBackReferenceProperty(str, constructSettableProperty(deserializationContext, beanDescription, SimpleBeanPropertyDefinition.construct(deserializationContext.getConfig(), annotatedMember), genericParameterType));
            }
        }
    }

    protected void addInjectables(DeserializationContext deserializationContext, BeanDescription beanDescription, BeanDeserializerBuilder beanDeserializerBuilder) throws JsonMappingException {
        Map findInjectables = beanDescription.findInjectables();
        if (findInjectables != null) {
            boolean canOverrideAccessModifiers = deserializationContext.canOverrideAccessModifiers();
            for (Entry entry : findInjectables.entrySet()) {
                AnnotatedMember annotatedMember = (AnnotatedMember) entry.getValue();
                if (canOverrideAccessModifiers) {
                    annotatedMember.fixAccess();
                }
                beanDeserializerBuilder.addInjectable(annotatedMember.getName(), beanDescription.resolveType(annotatedMember.getGenericType()), beanDescription.getClassAnnotations(), annotatedMember, entry.getKey());
            }
        }
    }

    protected SettableAnyProperty constructAnySetter(DeserializationContext deserializationContext, BeanDescription beanDescription, AnnotatedMethod annotatedMethod) throws JsonMappingException {
        if (deserializationContext.canOverrideAccessModifiers()) {
            annotatedMethod.fixAccess();
        }
        JavaType resolveType = beanDescription.bindingsForBeanType().resolveType(annotatedMethod.getGenericParameterType(1));
        BeanProperty std = new Std(annotatedMethod.getName(), resolveType, null, beanDescription.getClassAnnotations(), annotatedMethod, false);
        resolveType = resolveType(deserializationContext, beanDescription, resolveType, annotatedMethod);
        JsonDeserializer findDeserializerFromAnnotation = findDeserializerFromAnnotation(deserializationContext, annotatedMethod);
        if (findDeserializerFromAnnotation != null) {
            return new SettableAnyProperty(std, annotatedMethod, resolveType, findDeserializerFromAnnotation);
        }
        return new SettableAnyProperty(std, annotatedMethod, modifyTypeByAnnotation(deserializationContext, annotatedMethod, resolveType), null);
    }

    protected SettableBeanProperty constructSettableProperty(DeserializationContext deserializationContext, BeanDescription beanDescription, BeanPropertyDefinition beanPropertyDefinition, Type type) throws JsonMappingException {
        SettableBeanProperty methodProperty;
        Annotated mutator = beanPropertyDefinition.getMutator();
        if (deserializationContext.canOverrideAccessModifiers()) {
            mutator.fixAccess();
        }
        JavaType resolveType = beanDescription.resolveType(type);
        Std std = new Std(beanPropertyDefinition.getName(), resolveType, beanPropertyDefinition.getWrapperName(), beanDescription.getClassAnnotations(), mutator, beanPropertyDefinition.isRequired());
        JavaType resolveType2 = resolveType(deserializationContext, beanDescription, resolveType, mutator);
        if (resolveType2 != resolveType) {
            std.withType(resolveType2);
        }
        JsonDeserializer findDeserializerFromAnnotation = findDeserializerFromAnnotation(deserializationContext, mutator);
        resolveType = modifyTypeByAnnotation(deserializationContext, mutator, resolveType2);
        TypeDeserializer typeDeserializer = (TypeDeserializer) resolveType.getTypeHandler();
        if (mutator instanceof AnnotatedMethod) {
            methodProperty = new MethodProperty(beanPropertyDefinition, resolveType, typeDeserializer, beanDescription.getClassAnnotations(), (AnnotatedMethod) mutator);
        } else {
            methodProperty = new FieldProperty(beanPropertyDefinition, resolveType, typeDeserializer, beanDescription.getClassAnnotations(), (AnnotatedField) mutator);
        }
        if (findDeserializerFromAnnotation != null) {
            methodProperty = methodProperty.withValueDeserializer(findDeserializerFromAnnotation);
        }
        ReferenceProperty findReferenceType = beanPropertyDefinition.findReferenceType();
        if (findReferenceType != null && findReferenceType.isManagedReference()) {
            methodProperty.setManagedReferenceName(findReferenceType.getName());
        }
        return methodProperty;
    }

    protected SettableBeanProperty constructSetterlessProperty(DeserializationContext deserializationContext, BeanDescription beanDescription, BeanPropertyDefinition beanPropertyDefinition) throws JsonMappingException {
        Annotated getter = beanPropertyDefinition.getGetter();
        if (deserializationContext.canOverrideAccessModifiers()) {
            getter.fixAccess();
        }
        JavaType type = getter.getType(beanDescription.bindingsForBeanType());
        JsonDeserializer findDeserializerFromAnnotation = findDeserializerFromAnnotation(deserializationContext, getter);
        JavaType modifyTypeByAnnotation = modifyTypeByAnnotation(deserializationContext, getter, type);
        SettableBeanProperty setterlessProperty = new SetterlessProperty(beanPropertyDefinition, modifyTypeByAnnotation, (TypeDeserializer) modifyTypeByAnnotation.getTypeHandler(), beanDescription.getClassAnnotations(), getter);
        if (findDeserializerFromAnnotation != null) {
            return setterlessProperty.withValueDeserializer(findDeserializerFromAnnotation);
        }
        return setterlessProperty;
    }

    protected boolean isPotentialBeanType(Class<?> cls) {
        String canBeABeanType = ClassUtil.canBeABeanType(cls);
        if (canBeABeanType != null) {
            throw new IllegalArgumentException("Can not deserialize Class " + cls.getName() + " (of type " + canBeABeanType + ") as a Bean");
        } else if (ClassUtil.isProxyType(cls)) {
            throw new IllegalArgumentException("Can not deserialize Proxy class " + cls.getName() + " as a Bean");
        } else {
            canBeABeanType = ClassUtil.isLocalType(cls, true);
            if (canBeABeanType == null) {
                return true;
            }
            throw new IllegalArgumentException("Can not deserialize Class " + cls.getName() + " (of type " + canBeABeanType + ") as a Bean");
        }
    }

    protected boolean isIgnorableType(DeserializationConfig deserializationConfig, BeanDescription beanDescription, Class<?> cls, Map<Class<?>, Boolean> map) {
        Boolean bool = (Boolean) map.get(cls);
        if (bool == null) {
            bool = deserializationConfig.getAnnotationIntrospector().isIgnorableType(deserializationConfig.introspectClassAnnotations((Class) cls).getClassInfo());
            if (bool == null) {
                bool = Boolean.FALSE;
            }
        }
        return bool.booleanValue();
    }
}
