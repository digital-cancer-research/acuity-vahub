package com.acuity.visualisations.rawdatamodel.service.compatibility;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

import static com.acuity.visualisations.rawdatamodel.util.Constants.NONE;


@Service
public class ExposureLineChartColoringService extends CategoryColoringService {

    @Override
    protected boolean isValid(Triple<Integer, Integer, Integer> rgb) {
        return super.isValid(rgb) && isNotRed(rgb);
    }

    /**
     * If the arithmetic mean is 'per analyte', the 'colour By' panel disappears, color by value is "None"
     * and plots must be coloured with one colour
     */
    @Override
    public String getColor(Object colorByValue, Object colorByOption) {
        return  NONE.equals(colorByValue)
                ? Colors.SKYBLUE.getCode()
                : super.getColor(colorByValue, colorByOption);
    }
}
