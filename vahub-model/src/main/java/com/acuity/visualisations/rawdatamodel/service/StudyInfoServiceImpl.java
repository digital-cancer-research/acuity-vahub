package com.acuity.visualisations.rawdatamodel.service;

import com.acuity.visualisations.rawdatamodel.dao.StudyInfoRepository;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.AcuityObjectIdentityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

@Service
public class StudyInfoServiceImpl implements StudyInfoService {
    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Override
    public Optional<StudyInfo> getStudyInfo(Datasets datasets) {
        return datasets.getDatasets().stream()
                .map(AcuityObjectIdentityImpl::getId)
                .map(studyInfoRepository::getRawData)
                .flatMap(Collection::stream).max(Comparator.comparing(StudyInfo::getLastUpdatedDate));
    }
}
