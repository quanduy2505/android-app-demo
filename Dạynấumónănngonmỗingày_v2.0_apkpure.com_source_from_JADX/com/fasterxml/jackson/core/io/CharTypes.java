package com.fasterxml.jackson.core.io;

import android.support.v4.media.TransportMediator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.tapadoo.android.C0784R;
import java.util.Arrays;
import me.wangyuwei.loadingview.C0801R;
import org.apache.http.HttpStatus;

public final class CharTypes {
    private static final byte[] HEX_BYTES;
    private static final char[] HEX_CHARS;
    static final int[] sHexValues;
    static final int[] sInputCodes;
    static final int[] sInputCodesComment;
    static final int[] sInputCodesJsNames;
    static final int[] sInputCodesUtf8;
    static final int[] sInputCodesUtf8JsNames;
    static final int[] sOutputEscapes128;

    static {
        int i;
        HEX_CHARS = "0123456789ABCDEF".toCharArray();
        int length = HEX_CHARS.length;
        HEX_BYTES = new byte[length];
        for (i = 0; i < length; i++) {
            HEX_BYTES[i] = (byte) HEX_CHARS[i];
        }
        int[] iArr = new int[AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY];
        for (i = 0; i < 32; i++) {
            iArr[i] = -1;
        }
        iArr[34] = 1;
        iArr[92] = 1;
        sInputCodes = iArr;
        Object obj = new int[sInputCodes.length];
        System.arraycopy(sInputCodes, 0, obj, 0, sInputCodes.length);
        for (length = TransportMediator.FLAG_KEY_MEDIA_NEXT; length < AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY; length++) {
            i = (length & 224) == 192 ? 2 : (length & 240) == 224 ? 3 : (length & 248) == 240 ? 4 : -1;
            obj[length] = i;
        }
        sInputCodesUtf8 = obj;
        iArr = new int[AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY];
        Arrays.fill(iArr, -1);
        for (i = 33; i < AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY; i++) {
            if (Character.isJavaIdentifierPart((char) i)) {
                iArr[i] = 0;
            }
        }
        iArr[64] = 0;
        iArr[35] = 0;
        iArr[42] = 0;
        iArr[45] = 0;
        iArr[43] = 0;
        sInputCodesJsNames = iArr;
        Object obj2 = new int[AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY];
        System.arraycopy(sInputCodesJsNames, 0, obj2, 0, sInputCodesJsNames.length);
        Arrays.fill(obj2, TransportMediator.FLAG_KEY_MEDIA_NEXT, TransportMediator.FLAG_KEY_MEDIA_NEXT, 0);
        sInputCodesUtf8JsNames = obj2;
        sInputCodesComment = new int[AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY];
        System.arraycopy(sInputCodesUtf8, TransportMediator.FLAG_KEY_MEDIA_NEXT, sInputCodesComment, TransportMediator.FLAG_KEY_MEDIA_NEXT, TransportMediator.FLAG_KEY_MEDIA_NEXT);
        Arrays.fill(sInputCodesComment, 0, 32, -1);
        sInputCodesComment[9] = 0;
        sInputCodesComment[10] = 10;
        sInputCodesComment[13] = 13;
        sInputCodesComment[42] = 42;
        iArr = new int[TransportMediator.FLAG_KEY_MEDIA_NEXT];
        for (i = 0; i < 32; i++) {
            iArr[i] = -1;
        }
        iArr[34] = 34;
        iArr[92] = 92;
        iArr[8] = 98;
        iArr[9] = 116;
        iArr[12] = HttpStatus.SC_PROCESSING;
        iArr[10] = C0801R.styleable.AppCompatTheme_ratingBarStyleSmall;
        iArr[13] = C0784R.styleable.AppCompatTheme_listMenuViewStyle;
        sOutputEscapes128 = iArr;
        sHexValues = new int[TransportMediator.FLAG_KEY_MEDIA_NEXT];
        Arrays.fill(sHexValues, -1);
        for (i = 0; i < 10; i++) {
            sHexValues[i + 48] = i;
        }
        for (i = 0; i < 6; i++) {
            sHexValues[i + 97] = i + 10;
            sHexValues[i + 65] = i + 10;
        }
    }

    public static int[] getInputCodeLatin1() {
        return sInputCodes;
    }

    public static int[] getInputCodeUtf8() {
        return sInputCodesUtf8;
    }

    public static int[] getInputCodeLatin1JsNames() {
        return sInputCodesJsNames;
    }

    public static int[] getInputCodeUtf8JsNames() {
        return sInputCodesUtf8JsNames;
    }

    public static int[] getInputCodeComment() {
        return sInputCodesComment;
    }

    public static int[] get7BitOutputEscapes() {
        return sOutputEscapes128;
    }

    public static int charToHex(int i) {
        return i > TransportMediator.KEYCODE_MEDIA_PAUSE ? -1 : sHexValues[i];
    }

    public static void appendQuoted(StringBuilder stringBuilder, String str) {
        int[] iArr = sOutputEscapes128;
        char length = iArr.length;
        int length2 = str.length();
        for (int i = 0; i < length2; i++) {
            char charAt = str.charAt(i);
            if (charAt >= length || iArr[charAt] == 0) {
                stringBuilder.append(charAt);
            } else {
                stringBuilder.append('\\');
                int i2 = iArr[charAt];
                if (i2 < 0) {
                    stringBuilder.append('u');
                    stringBuilder.append('0');
                    stringBuilder.append('0');
                    i2 = -(i2 + 1);
                    stringBuilder.append(HEX_CHARS[i2 >> 4]);
                    stringBuilder.append(HEX_CHARS[i2 & 15]);
                } else {
                    stringBuilder.append((char) i2);
                }
            }
        }
    }

    public static char[] copyHexChars() {
        return (char[]) HEX_CHARS.clone();
    }

    public static byte[] copyHexBytes() {
        return (byte[]) HEX_BYTES.clone();
    }
}
