package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class DecimalNode extends NumericNode {
    private static final BigDecimal MAX_INTEGER;
    private static final BigDecimal MAX_LONG;
    private static final BigDecimal MIN_INTEGER;
    private static final BigDecimal MIN_LONG;
    public static final DecimalNode ZERO;
    protected final BigDecimal _value;

    static {
        ZERO = new DecimalNode(BigDecimal.ZERO);
        MIN_INTEGER = BigDecimal.valueOf(-2147483648L);
        MAX_INTEGER = BigDecimal.valueOf(2147483647L);
        MIN_LONG = BigDecimal.valueOf(Long.MIN_VALUE);
        MAX_LONG = BigDecimal.valueOf(Long.MAX_VALUE);
    }

    public DecimalNode(BigDecimal bigDecimal) {
        this._value = bigDecimal;
    }

    public static DecimalNode valueOf(BigDecimal bigDecimal) {
        return new DecimalNode(bigDecimal);
    }

    public JsonToken asToken() {
        return JsonToken.VALUE_NUMBER_FLOAT;
    }

    public NumberType numberType() {
        return NumberType.BIG_DECIMAL;
    }

    public boolean isFloatingPointNumber() {
        return true;
    }

    public boolean isBigDecimal() {
        return true;
    }

    public boolean canConvertToInt() {
        return this._value.compareTo(MIN_INTEGER) >= 0 && this._value.compareTo(MAX_INTEGER) <= 0;
    }

    public boolean canConvertToLong() {
        return this._value.compareTo(MIN_LONG) >= 0 && this._value.compareTo(MAX_LONG) <= 0;
    }

    public Number numberValue() {
        return this._value;
    }

    public short shortValue() {
        return this._value.shortValue();
    }

    public int intValue() {
        return this._value.intValue();
    }

    public long longValue() {
        return this._value.longValue();
    }

    public BigInteger bigIntegerValue() {
        return this._value.toBigInteger();
    }

    public float floatValue() {
        return this._value.floatValue();
    }

    public double doubleValue() {
        return this._value.doubleValue();
    }

    public BigDecimal decimalValue() {
        return this._value;
    }

    public String asText() {
        return this._value.toString();
    }

    public final void serialize(JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeNumber(this._value);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        return ((DecimalNode) obj)._value.equals(this._value);
    }

    public int hashCode() {
        return this._value.hashCode();
    }
}
