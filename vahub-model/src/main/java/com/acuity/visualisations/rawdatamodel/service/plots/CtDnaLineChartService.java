package com.acuity.visualisations.rawdatamodel.service.plots;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna.NO_MUTATIONS_DETECTED_VALUE;

/**
 * Service to build a line chart for CtDna plot
 */
@Service
public class CtDnaLineChartService extends LineChartService<CtDna, CtDnaGroupByOptions> {

    /**
     * Series that contain only VAF <= 0.002 or 'no mutations detected' must be excluded
     */
    @Override
    protected boolean isSeriesValid(List<CtDna> events) {
        return events.stream().anyMatch(e -> e.getEvent().getReportedVafCalculated() > NO_MUTATIONS_DETECTED_VALUE);
    }
}
