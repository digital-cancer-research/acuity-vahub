package com.acuity.visualisations.rawdatamodel.vo.biomarker;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerMutation.SOMATIC_STATUS_KNOWN_COMPARATOR;
import static com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerMutation.SOMATIC_STATUS_LIKELY_COMPARATOR;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Builder
@EqualsAndHashCode
@Getter
public class BiomarkerData implements Serializable {

    public static final String SOMATIC_STATUS_KNOWN = "known";
    public static final String SOMATIC_STATUS_LIKELY = "likely";

    private String subjectCode;
    private String gene;
    private List<BiomarkerParameters> biomarkerParameters;
    private Integer totalNumberOfAlterations;

    @JsonIgnore
    public String getPriorityMutation() {
        Map<String, List<String>> mutationsByStatus = biomarkerParameters.stream()
                .collect(groupingBy(BiomarkerParameters::getSomaticStatus, mapping(BiomarkerParameters::getMutation, toList())));
        final boolean isSomaticStatusKnown = mutationsByStatus.keySet().contains(SOMATIC_STATUS_KNOWN);
        if (!isSomaticStatusKnown && !mutationsByStatus.keySet().contains(SOMATIC_STATUS_LIKELY)) {
            return "";
        }
        final Comparator<BiomarkerMutation> c = isSomaticStatusKnown ? SOMATIC_STATUS_KNOWN_COMPARATOR : SOMATIC_STATUS_LIKELY_COMPARATOR;
        final List<String> mutationsForSomaticStatus = mutationsByStatus.get(isSomaticStatusKnown ? SOMATIC_STATUS_KNOWN : SOMATIC_STATUS_LIKELY);
        return mutationsForSomaticStatus.stream()
                .min((o1, o2) -> c.compare(BiomarkerMutation.getMutationByName(o1), BiomarkerMutation.getMutationByName(o2)))
                .orElse("");
    }

}
