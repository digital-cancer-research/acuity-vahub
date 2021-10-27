package com.acuity.visualisations.rawdatamodel.service.compatibility;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

@Service
public class CtDnaLineChartColoringService extends CategoryColoringService {

    @Override
    protected boolean isValid(Triple<Integer, Integer, Integer> rgb) {
        return super.isValid(rgb) && isNotGreen(rgb) && isNotRed(rgb);
    }

    @Override
    protected String[] getStandardColors() {
        return COLORS_NO_GREEN;
    }
}
