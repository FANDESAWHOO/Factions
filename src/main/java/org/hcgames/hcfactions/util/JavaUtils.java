package org.hcgames.hcfactions.util;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;



public class JavaUtils {
    private static CharMatcher CHAR_MATCHER_ASCII;
    private static Pattern UUID_PATTERN;
    @SuppressWarnings("unused")
	private static int DEFAULT_NUMBER_FORMAT_DECIMAL_PLACES = 5;

    public static Integer tryParseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static Double tryParseDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static boolean isUUID(String string) {
        return JavaUtils.UUID_PATTERN.matcher(string).find();
    }

    public static boolean isAlphanumeric(String string) {
        return JavaUtils.CHAR_MATCHER_ASCII.matchesAllOf((CharSequence) string);
    }

    public static boolean containsIgnoreCase(Iterable<? extends String> elements, String string) {
        for (String element : elements) {
            if (StringUtils.containsIgnoreCase((CharSequence) element, (CharSequence) string)) {
                return true;
            }
        }
        return false;
    }

    public static String format(Number number) {
        return format(number, 5);
    }

    public static String format(Number number, int decimalPlaces) {
        return format(number, decimalPlaces, RoundingMode.HALF_DOWN);
    }

    public static String format(Number number, int decimalPlaces, RoundingMode roundingMode) {
        Preconditions.checkNotNull((Object) number, (Object) "The number cannot be null");
        return new BigDecimal(number.toString()).setScale(decimalPlaces, roundingMode).stripTrailingZeros().toPlainString();
    }

    public static String andJoin(Collection<String> collection, boolean delimiterBeforeAnd) {
        return andJoin(collection, delimiterBeforeAnd, ", ");
    }

    public static String andJoin(Collection<String> collection, boolean delimiterBeforeAnd, String delimiter) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        List<String> contents = new ArrayList<String>(collection);
        String last = contents.remove(contents.size() - 1);
        StringBuilder builder = new StringBuilder(Joiner.on(delimiter).join((Iterable<String>) contents));
        if (delimiterBeforeAnd) {
            builder.append(delimiter);
        }
        return builder.append(" and ").append(last).toString();
    }

    public static long parse(String input) {
        if (input == null || input.isEmpty()) {
            return -1L;
        }
        long result = 0L;
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            if (Character.isDigit(c)) {
                number.append(c);
            } else {
                String str;
                if (Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
                    result += convert(Integer.parseInt(str), c);
                    number = new StringBuilder();
                }
            }
        }
        return result;
    }

	private static long convert(int value, char unit) {
		switch (unit) {
		case 'y': {
			return value * TimeUnit.DAYS.toMillis(365L);
		}
		case 'M': {
			return value * TimeUnit.DAYS.toMillis(30L);
		}
		case 'd': {
			return value * TimeUnit.DAYS.toMillis(1L);
		}
		case 'h': {
			return value * TimeUnit.HOURS.toMillis(1L);
		}
		case 'm': {
			return value * TimeUnit.MINUTES.toMillis(1L);
		}
		case 's': {
			return value * TimeUnit.SECONDS.toMillis(1L);
		}
		default: {
			return -1L;
		}
		}
	}

    static {
        CHAR_MATCHER_ASCII = CharMatcher.inRange('0', '9').or(CharMatcher.inRange('a', 'z')).or(CharMatcher.inRange('A', 'Z')).or(CharMatcher.WHITESPACE).precomputed();
        UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");
    }
}
