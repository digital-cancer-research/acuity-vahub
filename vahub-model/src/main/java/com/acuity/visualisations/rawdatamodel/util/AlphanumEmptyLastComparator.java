package com.acuity.visualisations.rawdatamodel.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE;

/**
 * Created by knml167 on 6/27/2017.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AlphanumEmptyLastComparator extends AlphanumComparator<String> {

    private static final AlphanumEmptyLastComparator INSTANCE = new AlphanumEmptyLastComparator();

    public static AlphanumEmptyLastComparator getInstance() {
        return INSTANCE;
    }

    @Override
    public int compare(String o1, String o2) {
        if (DEFAULT_EMPTY_VALUE.equalsIgnoreCase(o1) && DEFAULT_EMPTY_VALUE.equalsIgnoreCase(o2)) {
            return 0;
        }
        if (DEFAULT_EMPTY_VALUE.equalsIgnoreCase(o1)) {
            return 1;
        }
        if (DEFAULT_EMPTY_VALUE.equalsIgnoreCase(o2)) {
            return -1;
        }
        return super.compare(o1, o2);
    }
}
