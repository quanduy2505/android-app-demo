package org.apache.http.impl.cookie;

import com.fasterxml.jackson.core.util.BufferRecycler;
import java.lang.ref.SoftReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.http.annotation.Immutable;

@Immutable
public final class DateUtils {
    private static final String[] DEFAULT_PATTERNS;
    private static final Date DEFAULT_TWO_DIGIT_YEAR_START;
    public static final TimeZone GMT;
    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
    public static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

    static final class DateFormatHolder {
        private static final ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>> THREADLOCAL_FORMATS;

        /* renamed from: org.apache.http.impl.cookie.DateUtils.DateFormatHolder.1 */
        static class C08031 extends ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>> {
            C08031() {
            }

            protected SoftReference<Map<String, SimpleDateFormat>> initialValue() {
                return new SoftReference(new HashMap());
            }
        }

        DateFormatHolder() {
        }

        static {
            THREADLOCAL_FORMATS = new C08031();
        }

        public static SimpleDateFormat formatFor(String pattern) {
            Map<String, SimpleDateFormat> formats = (Map) ((SoftReference) THREADLOCAL_FORMATS.get()).get();
            if (formats == null) {
                formats = new HashMap();
                THREADLOCAL_FORMATS.set(new SoftReference(formats));
            }
            SimpleDateFormat format = (SimpleDateFormat) formats.get(pattern);
            if (format != null) {
                return format;
            }
            format = new SimpleDateFormat(pattern, Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            formats.put(pattern, format);
            return format;
        }
    }

    static {
        DEFAULT_PATTERNS = new String[]{PATTERN_RFC1036, PATTERN_RFC1123, PATTERN_ASCTIME};
        GMT = TimeZone.getTimeZone("GMT");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(GMT);
        calendar.set(BufferRecycler.DEFAULT_WRITE_CONCAT_BUFFER_LEN, 0, 1, 0, 0, 0);
        calendar.set(14, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
    }

    public static Date parseDate(String dateValue) throws DateParseException {
        return parseDate(dateValue, null, null);
    }

    public static Date parseDate(String dateValue, String[] dateFormats) throws DateParseException {
        return parseDate(dateValue, dateFormats, null);
    }

    public static Date parseDate(String dateValue, String[] dateFormats, Date startDate) throws DateParseException {
        if (dateValue == null) {
            throw new IllegalArgumentException("dateValue is null");
        }
        if (dateFormats == null) {
            dateFormats = DEFAULT_PATTERNS;
        }
        if (startDate == null) {
            startDate = DEFAULT_TWO_DIGIT_YEAR_START;
        }
        if (dateValue.length() > 1 && dateValue.startsWith("'") && dateValue.endsWith("'")) {
            dateValue = dateValue.substring(1, dateValue.length() - 1);
        }
        String[] arr$ = dateFormats;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            SimpleDateFormat dateParser = DateFormatHolder.formatFor(arr$[i$]);
            dateParser.set2DigitYearStart(startDate);
            try {
                return dateParser.parse(dateValue);
            } catch (ParseException e) {
                i$++;
            }
        }
        throw new DateParseException("Unable to parse the date " + dateValue);
    }

    public static String formatDate(Date date) {
        return formatDate(date, PATTERN_RFC1123);
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("date is null");
        } else if (pattern != null) {
            return DateFormatHolder.formatFor(pattern).format(date);
        } else {
            throw new IllegalArgumentException("pattern is null");
        }
    }

    private DateUtils() {
    }
}
