package com.acuity.visualisations.cohorteditor.repository;

import com.acuity.visualisations.cohorteditor.entity.SavedFilter;
import com.acuity.visualisations.cohorteditor.util.SavedFilterVOConverter;
import com.acuity.visualisations.cohorteditor.vo.SavedFilterVO;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;


public class SavedFilterRepositoryImpl implements SavedFilterRepositoryCustom {

    @Autowired
    private SavedFilterRepository savedFilterRepository;
    @Autowired
    private SavedFilterVOConverter savedFilterVOConverter;

    /*
     * Pre load collections, hibernate cant fetch join 2+ collections
     */
    @Override
    public SavedFilter loadTreeById(Long id) {
        SavedFilter savedFilter = savedFilterRepository.findOne(id);
        savedFilter.getInstances().size();
        savedFilter.getPermissions().size();

        return savedFilter;
    }

    /*
     * Update or insert
     */
    @Override
    public Long saveVO(Datasets datasets, SavedFilterVO savedFilterVO) {
        SavedFilter savedFilter = savedFilterVOConverter.fromVO(datasets, savedFilterVO);
        SavedFilter insertedSavedFilter = savedFilterRepository.save(savedFilter);
        return insertedSavedFilter.getId();
    }
}
