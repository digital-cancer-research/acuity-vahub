package com.acuity.visualisations.rawdatamodel.service;

import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.va.security.acl.domain.Datasets;

import java.util.Optional;

public interface StudyInfoService {
    Optional<StudyInfo> getStudyInfo(Datasets dataset);
}
