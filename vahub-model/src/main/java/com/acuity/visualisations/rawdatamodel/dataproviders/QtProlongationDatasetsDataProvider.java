package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.QtProlongationRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.QtProlongation;
import com.acuity.va.security.acl.domain.Dataset;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.daysBetween;

@Component
public class QtProlongationDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<QtProlongationRaw, QtProlongation> {

    @Override
    protected Collection<QtProlongationRaw> getData(Dataset dataset) {
        return dataProvider.getData(QtProlongationRaw.class, dataset, ds -> rawDataRepository.getRawData(ds.getId()).stream().map(qt -> {
            OptionalInt daysOnStudy = daysBetween(qt.getDoseFirstDate(), qt.getMeasurementTimePoint());
            return qt.toBuilder().daysOnStudy(daysOnStudy.isPresent() ? daysOnStudy.getAsInt() : null).build();
        }).collect(Collectors.toList()));
    }

    @Override
    protected QtProlongation getWrapperInstance(QtProlongationRaw event, Subject subject) {
        return new QtProlongation(event, subject);
    }

    @Override
    protected Class<QtProlongationRaw> rawDataClass() {
        return QtProlongationRaw.class;
    }
}
