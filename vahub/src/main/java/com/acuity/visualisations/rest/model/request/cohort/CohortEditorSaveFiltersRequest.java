package com.acuity.visualisations.rest.model.request.cohort;

import com.acuity.visualisations.cohorteditor.vo.SavedFilterVO;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CohortEditorSaveFiltersRequest extends DatasetsRequest {

    @NotNull
    private SavedFilterVO savedFilterVO;
}
