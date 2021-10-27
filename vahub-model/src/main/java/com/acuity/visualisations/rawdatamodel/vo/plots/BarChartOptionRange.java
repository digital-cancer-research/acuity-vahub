package com.acuity.visualisations.rawdatamodel.vo.plots;

import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Date;

import static com.acuity.visualisations.rawdatamodel.util.Constants.DOUBLE_DASH;

@EqualsAndHashCode
public class BarChartOptionRange<T extends Comparable<T>> implements Comparable<BarChartOptionRange<T>> {
    public static final BarChartOptionRange EMPTY = new BarChartOptionRange<>(null, null, null);
    @Getter
    private final T left;
    @Getter
    private final T right;
    private String prefix;

    private static final String DATE_DELIMITER = " - ";
    private static final String GENERIC_DELIMITER = "-";

    public BarChartOptionRange(T left, T right) {
        this.left = left;
        this.right = right;
    }

    public BarChartOptionRange(T left, T right, String prefix) {
        this.left = left;
        this.right = right;
        this.prefix = prefix;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> BarChartOptionRange<T> empty() {
        return EMPTY;
    }

    public static <T extends Comparable<T>> BarChartOptionRange<T> newEmpty() {
        return new BarChartOptionRange(null, null);
    }

    public BarChartOptionRange withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public String toString() {
        if (left == null && right == null) {
            return prefix == null ? Attributes.DEFAULT_EMPTY_VALUE : prefix + DOUBLE_DASH + Attributes.DEFAULT_EMPTY_VALUE;
        }
        if (left instanceof Date) {
            String str = String.join(
                    DATE_DELIMITER,
                    DaysUtil.toString((Date) left, DaysUtil.DMMMY).toUpperCase(),
                    DaysUtil.toString((Date) right, DaysUtil.DMMMY).toUpperCase());
            return prefix == null ? str : prefix + DOUBLE_DASH + str;
        } else if (left instanceof Double) {
            Object leftObj = ((Double) left).doubleValue() == ((Double) left).longValue() ? ((Double) left).longValue() : left;
            Object rightObj = ((Double) right).doubleValue() == ((Double) right).longValue() ? ((Double) right).longValue() : right;
            String str = leftObj + GENERIC_DELIMITER + rightObj;
            return prefix == null ? str : prefix + DOUBLE_DASH + str;
        } else {
            String str = left + GENERIC_DELIMITER + right;
            return prefix == null ? str : prefix + DOUBLE_DASH + str;
        }
    }

    @Override
    public int compareTo(BarChartOptionRange<T> o) {
        if ((this.getLeft() == null && this.getRight() == null) && (o.getLeft() == null && o.getRight() == null)) {
            return 0;
        }
        if (this.getLeft() == null && this.getRight() == null) {
            return 1;
        }
        if (o.getLeft() == null && o.getRight() == null) {
            return -1;
        }
        return new CompareToBuilder().append(getLeft(), o.getLeft())
                .append(getRight(), o.getRight()).toComparison();
    }
}
