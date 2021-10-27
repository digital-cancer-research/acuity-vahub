package com.acuity.visualisations.rawdatamodel.service.compatibility;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.BLACK;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.BLUE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.GRAY;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.PURPLE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.RED;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.WHITE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.YELLOW;

@Service
public class TumourChartColoringService extends ColoringService {

    protected static final Map<Object, Colors> ASSESSMENT_RESPONSE_COLORS = new HashMap<>();

    static {
        ASSESSMENT_RESPONSE_COLORS.put("Missing Target Lesions", YELLOW);
        ASSESSMENT_RESPONSE_COLORS.put("Partial Response", BLUE);
        ASSESSMENT_RESPONSE_COLORS.put("Stable Disease", GRAY);
        ASSESSMENT_RESPONSE_COLORS.put("Progressive Disease", RED);
        ASSESSMENT_RESPONSE_COLORS.put("Not Evaluable", BLACK);
        ASSESSMENT_RESPONSE_COLORS.put("No Assessment", BLACK);
        ASSESSMENT_RESPONSE_COLORS.put("Complete Response", PURPLE);
        ASSESSMENT_RESPONSE_COLORS.put("No Evidence of Disease", PURPLE);
    }

    public String getColor(Object value) {
        return ASSESSMENT_RESPONSE_COLORS.getOrDefault(value, WHITE).getCode();
    }

}
