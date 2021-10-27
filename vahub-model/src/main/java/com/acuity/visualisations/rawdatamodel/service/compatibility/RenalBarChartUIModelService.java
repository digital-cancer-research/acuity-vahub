package com.acuity.visualisations.rawdatamodel.service.compatibility;

import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.service.plots.vo.BarChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.AlphanumEmptyLastComparator;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;

@Service
public class RenalBarChartUIModelService extends ColoredBarChartUIModelService {

    public RenalBarChartUIModelService(@Autowired BarChartColoringService coloringService) {
        super(coloringService);
    }

    @Override
    <T extends BarChartData> Comparator<T> getColorByOptionComparator() {
        return (o1, o2) -> AlphanumEmptyLastComparator.getInstance().compare(o2.getName().toString(), o1.getName().toString());
    }

    @Override
    <T extends HasStringId & HasSubject, G extends Enum<G> & GroupByOption<T>> List<String> sortCategories(
            Map<GroupByKey<T, G>, BarChartCalculationObject<T>> barChart, CountType countType) {
        return barChart.entrySet().stream().map(e -> {
            Object category = e.getKey().getValue(X_AXIS) instanceof Map
                    ? ((Map) e.getKey().getValue(X_AXIS)).keySet().iterator().next()
                    : e.getKey().getValue(X_AXIS);
            return Objects.toString(getDefaultedGroupName(category));
        }).distinct().sorted(AlphanumEmptyLastComparator.getInstance()).collect(Collectors.toList());
    }
}
