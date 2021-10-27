package com.acuity.visualisations.rawdatamodel.dataset.info.vo;

import com.acuity.va.security.acl.domain.AcuityObjectIdentity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
@ToString
public class CombinedStudyInfo<T extends AcuityObjectIdentity> implements Serializable {

    private List<T> roisWithPermission;
    private Set<StudySelectionDatasetInfo> studySelectionDatasetInfo;
    private Set<StudyWarnings> studyWarnings;
    private Map<String, Long> counts;
}
