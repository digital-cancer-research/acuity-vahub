package com.acuity.visualisations.rawdatamodel.util;

import net.logstash.logback.encoder.org.apache.commons.lang.ObjectUtils;
import se.sawano.java.text.AlphanumericComparator;

import java.util.Comparator;

public class AlphanumComparator<T extends Comparable<T>> implements Comparator<T> {

    private static Comparator<CharSequence> alphanumericComparator = new AlphanumericComparator();

    @Override
    public int compare(T obj1, T obj2) {
        if (!(obj1 instanceof String) || !(obj2 instanceof String)) {
            return ObjectUtils.compare(obj1, obj2, true);
        }

        CharSequence str1 = (String) obj1;
        CharSequence str2 = (String) obj2;

        return alphanumericComparator.compare(str1, str2);
    }
}
