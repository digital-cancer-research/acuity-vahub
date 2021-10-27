package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dao.SeriousAeRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.SeriousAeRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SeriousAe;
import com.acuity.va.security.acl.domain.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SeriousAeDatasetsDataProvider
        extends SubjectAwareDatasetsDataProvider<SeriousAeRaw, SeriousAe> {

    @Autowired
    private SeriousAeRepository seriousAeRepository;

    @Override
    protected Collection<SeriousAeRaw> getData(Dataset dataset) {
        return dataProvider.getData(SeriousAeRaw.class, dataset, ds -> {
            Collection<SeriousAeRaw> rawData = seriousAeRepository.getRawData(ds.getId());
            Map<String, List<SeriousAeRaw.SeriousAeSeverityStartEndDates>> seriousAeDatesGroupedById = seriousAeRepository
                    .getAeSeverityDates(dataset.getId()).stream()
                    .collect(Collectors.groupingBy(SeriousAeRaw.SeriousAeSeverityStartEndDates::getSeriousAeId));
            return rawData.stream().map(sae -> {
                List<SeriousAeRaw.SeriousAeSeverityStartEndDates> saeDates = seriousAeDatesGroupedById.get(sae.getId());
                return saeDates != null
                        ? sae.toBuilder()
                        .startDate(saeDates.stream()
                                .filter(e -> e.getSeverityStartDate() != null)
                                .map(SeriousAeRaw.SeriousAeSeverityStartEndDates::getSeverityStartDate)
                                .min(Date::compareTo)
                                .orElse(null))
                        .endDate(saeDates.stream()
                                .filter(e -> e.getSeverityEndDate() != null)
                                .map(SeriousAeRaw.SeriousAeSeverityStartEndDates::getSeverityEndDate)
                                .max(Date::compareTo)
                                .orElse(null))
                        .build()
                        : sae;
            }).collect(Collectors.toList());
        });
    }

    @Override
    protected Class<SeriousAeRaw> rawDataClass() {
        return SeriousAeRaw.class;
    }

    @Override
    protected SeriousAe getWrapperInstance(SeriousAeRaw event, Subject subject) {
        return new SeriousAe(event, subject);
    }
}
