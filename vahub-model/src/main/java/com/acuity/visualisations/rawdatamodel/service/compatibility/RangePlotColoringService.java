package com.acuity.visualisations.rawdatamodel.service.compatibility;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RangePlotColoringService extends ColoringService {
    public static final String LIGHT_BLUE = "#88CCEE";
    public static final String BLUE = "#00AAFF";
    private final Map<String, String> colorsByName = new ConcurrentHashMap<>();

    private String getColorFromMap(String value) {
        return this.colorsByName.computeIfAbsent(value.toLowerCase(), v -> COLORS[this.colorsByName.size() % COLORS.length]);
    }

    public String getColor(String value) {
        String color;
        switch ((value == null ? "" : value).toLowerCase()) {
            case "all":
                color = LIGHT_BLUE;
                break;
            case "placebo":
                color = BLUE;
                break;
            default:
                color = getColorFromMap(value);
                break;
        }
        return color;
    }
}
