package org.apache.http.client.utils;

import java.util.StringTokenizer;
import org.apache.http.annotation.Immutable;

@Immutable
public class Rfc3492Idn implements Idn {
    private static final String ACE_PREFIX = "xn--";
    private static final int base = 36;
    private static final int damp = 700;
    private static final char delimiter = '-';
    private static final int initial_bias = 72;
    private static final int initial_n = 128;
    private static final int skew = 38;
    private static final int tmax = 26;
    private static final int tmin = 1;

    private int adapt(int delta, int numpoints, boolean firsttime) {
        if (firsttime) {
            delta /= damp;
        } else {
            delta /= 2;
        }
        delta += delta / numpoints;
        int k = 0;
        while (delta > 455) {
            delta /= 35;
            k += base;
        }
        return ((delta * base) / (delta + skew)) + k;
    }

    private int digit(char c) {
        if (c >= 'A' && c <= 'Z') {
            return c - 65;
        }
        if (c >= 'a' && c <= 'z') {
            return c - 97;
        }
        if (c >= '0' && c <= '9') {
            return (c - 48) + tmax;
        }
        throw new IllegalArgumentException("illegal digit: " + c);
    }

    public String toUnicode(String punycode) {
        StringBuilder unicode = new StringBuilder(punycode.length());
        StringTokenizer tok = new StringTokenizer(punycode, ".");
        while (tok.hasMoreTokens()) {
            String t = tok.nextToken();
            if (unicode.length() > 0) {
                unicode.append('.');
            }
            if (t.startsWith(ACE_PREFIX)) {
                t = decode(t.substring(4));
            }
            unicode.append(t);
        }
        return unicode.toString();
    }

    protected String decode(String input) {
        int n = initial_n;
        int i = 0;
        int bias = initial_bias;
        StringBuilder output = new StringBuilder(input.length());
        int lastdelim = input.lastIndexOf(45);
        if (lastdelim != -1) {
            output.append(input.subSequence(0, lastdelim));
            input = input.substring(lastdelim + tmin);
        }
        while (input.length() > 0) {
            int oldi = i;
            int w = tmin;
            int k = base;
            while (input.length() != 0) {
                int t;
                char c = input.charAt(0);
                input = input.substring(tmin);
                int digit = digit(c);
                i += digit * w;
                if (k <= bias + tmin) {
                    t = tmin;
                } else if (k >= bias + tmax) {
                    t = tmax;
                } else {
                    t = k - bias;
                }
                if (digit < t) {
                    break;
                }
                w *= 36 - t;
                k += base;
            }
            bias = adapt(i - oldi, output.length() + tmin, oldi == 0);
            n += i / (output.length() + tmin);
            i %= output.length() + tmin;
            output.insert(i, (char) n);
            i += tmin;
        }
        return output.toString();
    }
}
