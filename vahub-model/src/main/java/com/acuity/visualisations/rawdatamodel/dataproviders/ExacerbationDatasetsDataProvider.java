package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.ExacerbationRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Constants.NO;
import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;

@Component
public class ExacerbationDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<ExacerbationRaw, Exacerbation> {

    @Override
    protected Exacerbation getWrapperInstance(ExacerbationRaw event, Subject subject) {
        return new Exacerbation(event, subject);
    }

    @Override
    protected Class<ExacerbationRaw> rawDataClass() {
        return ExacerbationRaw.class;
    }

    @Override
    protected Collection<ExacerbationRaw> getData(Dataset dataset) {
        return dataProvider.getData(ExacerbationRaw.class, dataset, (Dataset ds) -> {
            final Map<String, Subject> subjects = getPopulationDatasetsDataProvider().loadData(new Datasets(dataset))
                    .stream().collect(Collectors.toMap(Subject::getSubjectId, s -> s));
            final Collection<ExacerbationRaw> rawData = rawDataRepository.getRawData(ds.getId());
            return rawData.stream().map(exRaw -> {
                Subject subject = subjects.get(exRaw.getSubjectId());
                OptionalInt daysAtStudyStart = DaysUtil.daysBetween(subject.getFirstTreatmentDate(), exRaw.getStartDate());
                OptionalInt daysAtStudyEnd = DaysUtil.daysBetween(subject.getFirstTreatmentDate(), exRaw.getEndDate());
                OptionalInt duration = DaysUtil.daysBetween(exRaw.getStartDate(), exRaw.getEndDate());
                ExacerbationRaw.ExacerbationRawBuilder exBuilder = exRaw.toBuilder();
                return exBuilder.daysOnStudyAtStart(daysAtStudyStart.isPresent() ? daysAtStudyStart.getAsInt() : null)
                        .daysOnStudyAtEnd(daysAtStudyEnd.isPresent() ? daysAtStudyEnd.getAsInt() : null)
                        .duration(duration.isPresent() ? duration.getAsInt() + 1 : null)
                        .startPriorToRandomisation(before(exRaw.getStartDate(), subject.getDateOfRandomisation()))
                        .endPriorToRandomisation(before(exRaw.getEndDate(), subject.getDateOfRandomisation()))
                        .build();
            }).collect(Collectors.toList());
        });
    }

    private String before(Date date1, Date date2) {
        return (date1 == null || date2 == null) ? null : date1.before(date2) ? YES : NO;
    }
}
