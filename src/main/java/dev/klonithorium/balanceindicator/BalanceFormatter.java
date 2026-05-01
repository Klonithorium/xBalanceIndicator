package dev.klonithorium.balanceindicator;

import org.bukkit.configuration.file.FileConfiguration;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public record BalanceFormatter(
        String positiveFormat,
        String negativeFormat,
        String zeroFormat,
        String positiveSign,
        String negativeSign,
        String zeroSign,
        int decimals,
        boolean grouping,
        boolean stripTrailingZeros,
        String firstCheckValue,
        boolean ignoreFirstChange
) {
    public static BalanceFormatter fromConfig(FileConfiguration config) {
        int configuredDecimals = Math.max(0, config.getInt("number.decimals", 0));
        String firstCheck = config.getString("behavior.first-check", "zero");

        return new BalanceFormatter(
                config.getString("format.positive", "{sign}{amount}"),
                config.getString("format.negative", "{sign}{amount}"),
                config.getString("format.zero", "0"),
                config.getString("format.positive-sign", "+"),
                config.getString("format.negative-sign", "-"),
                config.getString("format.zero-sign", ""),
                configuredDecimals,
                config.getBoolean("number.grouping", false),
                config.getBoolean("number.strip-trailing-zeros", true),
                firstCheck != null && firstCheck.equalsIgnoreCase("blank") ? "" : "0",
                config.getBoolean("behavior.ignore-first-change", true)
        );
    }

    public String format(double change) {
        if (isZero(change)) {
            return apply(zeroFormat, zeroSign, 0);
        }

        if (change > 0) {
            return apply(positiveFormat, positiveSign, change);
        }

        return apply(negativeFormat, negativeSign, Math.abs(change));
    }

    private String apply(String format, String sign, double amount) {
        return format
                .replace("{sign}", sign)
                .replace("{amount}", formatNumber(amount));
    }

    private String formatNumber(double amount) {
        DecimalFormat decimalFormat = new DecimalFormat(pattern(), DecimalFormatSymbols.getInstance(Locale.US));
        decimalFormat.setGroupingUsed(grouping);
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);

        String formatted = decimalFormat.format(amount);
        if (!stripTrailingZeros || decimals == 0 || !formatted.contains(".")) {
            return formatted;
        }

        while (formatted.endsWith("0")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }

        return formatted;
    }

    private String pattern() {
        StringBuilder pattern = new StringBuilder(grouping ? "#,##0" : "0");
        if (decimals > 0) {
            pattern.append(".");
            pattern.append("0".repeat(decimals));
        }
        return pattern.toString();
    }

    private boolean isZero(double change) {
        return Math.abs(change) < 0.0000001;
    }
}
