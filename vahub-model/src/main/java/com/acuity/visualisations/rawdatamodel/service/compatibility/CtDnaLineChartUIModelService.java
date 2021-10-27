package com.acuity.visualisations.rawdatamodel.service.compatibility;

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.GroupByOptionAndParams;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.ColorbyCategoriesUtil;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CtDnaLineChartUIModelService extends LineChartUIModelService<CtDna, CtDnaGroupByOptions> {

    @Autowired
    private CtDnaLineChartColoringService coloringService;

    @Override
    protected String getColor(Object colorByValue, Datasets datasets,
                              GroupByOptionAndParams<CtDna, CtDnaGroupByOptions> colorByOption) {

        String datasetColorByOption = ColorbyCategoriesUtil.getDatasetColorByOption(datasets,
                Optional.ofNullable(colorByOption)
                .map(o -> o.getGroupByOption().toString())
                .orElse(null));
        return coloringService.getColor(colorByValue, datasetColorByOption);
    }

    public void generateColors(Datasets datasets, List<TrellisOptions<CtDnaGroupByOptions>> colorByOptions) {
        colorByOptions
                .forEach(colorByOption -> colorByOption.getTrellisOptions()
                        .forEach(opt -> getColor(opt, datasets,
                                colorByOption.getTrellisedBy().getGroupByOptionAndParams())));
    }
}
