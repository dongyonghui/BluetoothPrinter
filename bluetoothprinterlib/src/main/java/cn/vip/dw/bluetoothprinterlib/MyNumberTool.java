package cn.vip.dw.bluetoothprinterlib;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class MyNumberTool {
    public static final String DEFAULT_FORMAT = "default";
    private static final int STYLE_NUMBER = 0;
    private static final int STYLE_CURRENCY = 1;
    private static final int STYLE_PERCENT = 2;
    private static final int STYLE_INTEGER = 4;
    /**
     * 固定保留两位小数
     */
    public final static DecimalFormat TWO_PRICE_COUNT = new DecimalFormat("0.00");
    public MyNumberTool() {
    }

    public Locale getLocale() {
        return Locale.getDefault();
    }

    public String getFormat() {
        return "default";
    }

    public String format(Object obj) {
        return this.format(this.getFormat(), obj);
    }

    public String currency(Object obj) {
        return this.format("currency", obj);
    }

    public String integer(Object obj) {
        return this.format("integer", obj);
    }

    public String number(Object obj) {
        return this.format("number", obj);
    }

    public String percent(Object obj) {
        return this.format("percent", obj);
    }

    public String format(String format, Object obj) {
        return this.format(format, obj, this.getLocale());
    }

    public String format(String format, Object obj, Locale locale) {
        Number number = this.toNumber(obj);
        if (null != number && "currency".equalsIgnoreCase(format)) {
            return TWO_PRICE_COUNT.format(number.doubleValue());
        }
        NumberFormat nf = this.getNumberFormat(format, locale);
        return number != null && nf != null ? nf.format(number) : null;
    }

    public NumberFormat getNumberFormat(String format, Locale locale) {
        if (format == null) {
            return null;
        } else {
            Object nf = null;
            int style = this.getStyleAsInt(format);
            if (style < 0) {
                nf = new DecimalFormat(format, new DecimalFormatSymbols(locale));
            } else {
                nf = this.getNumberFormat(style, locale);
            }

            return (NumberFormat) nf;
        }
    }

    protected NumberFormat getNumberFormat(int numberStyle, Locale locale) {
        try {
            NumberFormat suppressed;
            switch (numberStyle) {
                case 0:
                    suppressed = NumberFormat.getNumberInstance(locale);
                    break;
                case 1:
                    suppressed = NumberFormat.getCurrencyInstance(locale);
                    break;
                case 2:
                    suppressed = NumberFormat.getPercentInstance(locale);
                    break;
                case 3:
                default:
                    suppressed = null;
                    break;
                case 4:
                    suppressed = this.getIntegerInstance(locale);
            }

            return suppressed;
        } catch (Exception var4) {
            return null;
        }
    }

    private NumberFormat getIntegerInstance(Locale locale) {
        DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        format.setMaximumFractionDigits(0);
        format.setDecimalSeparatorAlwaysShown(false);
        format.setParseIntegerOnly(true);
        return format;
    }

    protected int getStyleAsInt(String style) {
        return style != null && style.length() >= 6 && style.length() <= 8 ? (style.equalsIgnoreCase("default") ? 0 : (style.equalsIgnoreCase("number") ? 0 : (style.equalsIgnoreCase("currency") ? 1 : (style.equalsIgnoreCase("percent") ? 2 : (style.equalsIgnoreCase("integer") ? 4 : -1))))) : -1;
    }

    public Number toNumber(Object obj) {
        return this.toNumber(this.getFormat(), obj, this.getLocale());
    }

    public Number toNumber(String format, Object obj) {
        return this.toNumber(format, obj, this.getLocale());
    }

    public Number toNumber(String format, Object obj, Locale locale) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Number) {
            return (Number) obj;
        } else {
            try {
                NumberFormat e = this.getNumberFormat(format, locale);
                return e.parse(String.valueOf(obj));
            } catch (Exception var5) {
                return null;
            }
        }
    }
}
