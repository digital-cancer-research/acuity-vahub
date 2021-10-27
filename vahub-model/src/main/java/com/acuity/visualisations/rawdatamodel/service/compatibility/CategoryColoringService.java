package com.acuity.visualisations.rawdatamodel.service.compatibility;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE_TO_LOWER_CASE;
import static com.acuity.visualisations.rawdatamodel.util.Constants.ALL_TO_LOWER_CASE;
import static com.acuity.visualisations.rawdatamodel.util.Constants.NULL;

public abstract class CategoryColoringService extends ColoringService {

    protected final Map<Object, Map<Object, String>> categoryColors = new ConcurrentHashMap<>();
    protected final Map<Object, AtomicInteger> categoryCounters = new ConcurrentHashMap<>();

    public String getColor(Object colorByValue, Object colorByOption) {
        String color;
        switch ((colorByValue == null ? "" : colorByValue.toString()).toLowerCase()) {
            case DEFAULT_EMPTY_VALUE_TO_LOWER_CASE:
                color = Colors.GRAY.getCode();
                break;
            case ALL_TO_LOWER_CASE:
                color = Colors.LIGHTSEAGREEN.getCode();
                break;
            default:
                color = getColorFromMap(colorByValue, colorByOption);
                break;
        }
        return color;
    }

    protected String getColorFromMap(Object colorByValue, Object colorByOption) {
        categoryColors.putIfAbsent(colorByOption, new ConcurrentHashMap<>());
        categoryCounters.putIfAbsent(colorByOption, new AtomicInteger());
        final Object categoryKey = colorByValue == null ? NULL : colorByValue.toString();
        return categoryColors.get(colorByOption).computeIfAbsent(categoryKey,
                arg -> {
                    AtomicInteger counter = categoryCounters.get(colorByOption);
                    return counter.intValue() < getStandardColors().length
                            ? getStandardColors()[counter.getAndIncrement()] : generateColor();
                });
    }

    protected String[] getStandardColors() {
        return COLORS;
    }
}
