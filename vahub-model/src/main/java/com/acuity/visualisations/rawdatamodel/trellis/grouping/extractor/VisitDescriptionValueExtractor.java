package com.acuity.visualisations.rawdatamodel.trellis.grouping.extractor;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.VisitDescription;

public class VisitDescriptionValueExtractor implements ValueExtractor<VisitDescription, Integer> {
    /**
     * Returns an {@link Integer} number that's situated on the second position in visitDescription string (e.g. 'visit 2')
     * or {@link Integer#MAX_VALUE} if there is not a number (e.g. 'Baseline').
     *
     * @param object visit description object to extract number values from description string
     * @return a list within two integer to compare {@param object}
     */
    @Override
    public Integer extractFrom(VisitDescription object) {
        String[] descWords = object.getVisitDescription().split(" ");
        String value = descWords.length > 1 ? descWords[1] : null;
        int intValue;
        try {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            intValue = Integer.MAX_VALUE;
        }
        return intValue;
    }
}
