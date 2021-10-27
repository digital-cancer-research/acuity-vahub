package com.acuity.visualisations.rawdatamodel.service.compatibility;


import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TooltipInfoOutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @deprecated just a temporary solution waiting for refactoring
 */
@Service
@Deprecated
public class ConmedRangedColoredBarChartUIModelService extends RangedColoredBarChartUIModelService {

    public ConmedRangedColoredBarChartUIModelService(BarChartColoringService coloringService) {
        super(coloringService);
    }

    @Override
    protected OutputBarChartEntry mapToOutputBarchartEntry(BarChartEntry barChartEntry,
                                                           Map<String, Integer> categoriesWithIndexes,
                                                           Supplier<Double> totalSubjectsCountSupplier) {
        String atcText = ((Conmed) barChartEntry.getEventSet().iterator().next()).getEvent().getAtcText();
        return new TooltipInfoOutputBarChartEntry(barChartEntry,
                categoriesWithIndexes.get(barChartEntry.getCategory().toString()) + 1,
                ImmutableMap.of(
                        "percentOfSubjects", 100 * barChartEntry.getTotalSubjects().doubleValue() / totalSubjectsCountSupplier.get(),
                        "atcText", atcText == null ? "" : atcText));
    }
}
