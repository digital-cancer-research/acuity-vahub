package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dao.PopulationRepository;
import com.acuity.visualisations.rawdatamodel.dao.StudyInfoRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class StudyInfoDataProvider extends DatasetsDataProvider<StudyInfo, StudyInfo> {

    @Autowired
    private StudyInfoRepository studyInfoRepository;
    @Autowired
    private PopulationRepository populationRepository;

    @Override
    @Cacheable(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
    public Collection<StudyInfo> loadData(Datasets datasets) {
        return super.loadData(datasets);
    }

    @Override
    protected Collection<StudyInfo> wrap(Datasets datasets, Collection<StudyInfo> events) {
        return events;
    }

    @Override
    // dataset can't have more than 1 corresponding study info, so in fact the collection as a result is here just to match the interface convention
    public Collection<StudyInfo> getData(Dataset dataset) {
        return dataProvider.getData(StudyInfo.class, dataset, ds -> {
                    Collection<StudyInfo> studiesInfo = studyInfoRepository.getRawData(dataset.getId());
                    if (!studiesInfo.isEmpty()) {
                        return studiesInfo.stream()
                                .map(studyInfo -> {
                                    long dosedSubjectsCount = populationRepository.getRawData(ds.getId()).stream()
                                            .filter(subject -> subject.getFirstTreatmentDate() != null)
                                            .count();
                                    return studyInfo.toBuilder().numberOfDosedSubjects(dosedSubjectsCount).build();
                                })
                                .collect(Collectors.toList());
                    } else {
                        return Collections.emptyList();
                    }
                }
        );
    }

    @Override
    protected Class<StudyInfo> rawDataClass() {
        return StudyInfo.class;
    }
}
