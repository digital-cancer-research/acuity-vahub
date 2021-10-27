package com.acuity.visualisations.cohorteditor.repository;

import com.acuity.visualisations.cohorteditor.entity.SavedFilter;
import com.acuity.visualisations.cohorteditor.vo.SavedFilterVO;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.transaction.annotation.Transactional;

public interface SavedFilterRepositoryCustom {

    @Transactional(readOnly = true)
    SavedFilter loadTreeById(Long id);
    
    @Transactional
    Long saveVO(Datasets datasets, SavedFilterVO savedFilterVO);
}
