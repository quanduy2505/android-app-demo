package com.google.android.gms.common.util;

import android.text.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.tapadoo.android.C0784R;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.wangyuwei.loadingview.C0801R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class zzp {
    private static final Pattern zzaGX;
    private static final Pattern zzaGY;

    static {
        zzaGX = Pattern.compile("\\\\.");
        zzaGY = Pattern.compile("[\\\\\"/\b\f\n\r\t]");
    }

    public static String zzdC(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        Matcher matcher = zzaGY.matcher(str);
        StringBuffer stringBuffer = null;
        while (matcher.find()) {
            if (stringBuffer == null) {
                stringBuffer = new StringBuffer();
            }
            switch (matcher.group().charAt(0)) {
                case ConnectionResult.INTERNAL_ERROR /*8*/:
                    matcher.appendReplacement(stringBuffer, "\\\\b");
                    break;
                case ConnectionResult.SERVICE_INVALID /*9*/:
                    matcher.appendReplacement(stringBuffer, "\\\\t");
                    break;
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    matcher.appendReplacement(stringBuffer, "\\\\n");
                    break;
                case C0801R.styleable.Toolbar_titleTextAppearance /*12*/:
                    matcher.appendReplacement(stringBuffer, "\\\\f");
                    break;
                case ConnectionResult.CANCELED /*13*/:
                    matcher.appendReplacement(stringBuffer, "\\\\r");
                    break;
                case C0801R.styleable.AppCompatTheme_actionModePasteDrawable /*34*/:
                    matcher.appendReplacement(stringBuffer, "\\\\\\\"");
                    break;
                case C0801R.styleable.AppCompatTheme_dropdownListPreferredItemHeight /*47*/:
                    matcher.appendReplacement(stringBuffer, "\\\\/");
                    break;
                case C0784R.styleable.AppCompatTheme_colorBackgroundFloating /*92*/:
                    matcher.appendReplacement(stringBuffer, "\\\\\\\\");
                    break;
                default:
                    break;
            }
        }
        if (stringBuffer == null) {
            return str;
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    public static boolean zzf(Object obj, Object obj2) {
        if (obj == null && obj2 == null) {
            return true;
        }
        if (obj == null || obj2 == null) {
            return false;
        }
        if ((obj instanceof JSONObject) && (obj2 instanceof JSONObject)) {
            JSONObject jSONObject = (JSONObject) obj;
            JSONObject jSONObject2 = (JSONObject) obj2;
            if (jSONObject.length() != jSONObject2.length()) {
                return false;
            }
            Iterator keys = jSONObject.keys();
            while (keys.hasNext()) {
                String str = (String) keys.next();
                if (!jSONObject2.has(str)) {
                    return false;
                }
                try {
                    if (!zzf(jSONObject.get(str), jSONObject2.get(str))) {
                        return false;
                    }
                } catch (JSONException e) {
                    return false;
                }
            }
            return true;
        } else if (!(obj instanceof JSONArray) || !(obj2 instanceof JSONArray)) {
            return obj.equals(obj2);
        } else {
            JSONArray jSONArray = (JSONArray) obj;
            JSONArray jSONArray2 = (JSONArray) obj2;
            if (jSONArray.length() != jSONArray2.length()) {
                return false;
            }
            int i = 0;
            while (i < jSONArray.length()) {
                try {
                    if (!zzf(jSONArray.get(i), jSONArray2.get(i))) {
                        return false;
                    }
                    i++;
                } catch (JSONException e2) {
                    return false;
                }
            }
            return true;
        }
    }
}
