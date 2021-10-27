package com.acuity.visualisations.cohorteditor.vo;

import com.acuity.visualisations.cohorteditor.entity.SavedFilter;
import com.acuity.visualisations.cohorteditor.entity.SavedFilterInstance;
import java.io.Serializable;
import java.util.List;
import lombok.Data;


import static com.google.common.collect.Lists.newArrayList;

@Data
public class SavedFilterVO implements Serializable {
    private SavedFilter savedFilter;
    private List<SavedFilterInstance> cohortFilters = newArrayList();
    private List<UserVO> sharedWith = newArrayList();

    public SavedFilter getSavedFilter() {
        if (savedFilter != null) {
            SavedFilter savedFilterSlim = new SavedFilter(savedFilter.getName(), savedFilter.getCreatedDate(), savedFilter.getOwner());
            savedFilterSlim.setOperator(savedFilter.getOperator());
            savedFilterSlim.setId(savedFilter.getId());
            savedFilterSlim.setDatasetId(savedFilter.getDatasetId());
            savedFilterSlim.setDatasetClass(savedFilter.getDatasetClass());
            return savedFilterSlim;
        }
        return null;
    }
}
