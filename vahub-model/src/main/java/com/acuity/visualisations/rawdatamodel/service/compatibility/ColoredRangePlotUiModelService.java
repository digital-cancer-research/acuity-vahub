package com.acuity.visualisations.rawdatamodel.service.compatibility;

import com.acuity.visualisations.rawdatamodel.vo.compatibility.ColoredRangeChartSeries;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputRangeChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.RangeChartSeries;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColoredRangePlotUiModelService extends RangePlotUiModelService {
    private final RangePlotColoringService coloringService;

    public ColoredRangePlotUiModelService(RangePlotColoringService coloringService) {
        this.coloringService = coloringService;
    }

    @Override
    protected RangeChartSeries rangeChartSeries(String name, List<OutputRangeChartEntry> entries) {
        return new ColoredRangeChartSeries(name, entries, coloringService.getColor(name));
    }
}
