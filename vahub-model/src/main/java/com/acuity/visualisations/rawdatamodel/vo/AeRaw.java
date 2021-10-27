/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DodUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.acuity.visualisations.rawdatamodel.statistics.collectors.DateSummaryStatisticsCollector.toDateSummaryStatistics;
import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType.DETECT;
import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType.ACUITY;
import static com.google.common.collect.Lists.newArrayList;

@EqualsAndHashCode(of = "id")
@ToString
@Getter
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@AcuityEntity(version = 8)
public final class AeRaw implements HasStringId, HasSubjectId, Serializable {

    private String id;
    private String subjectId;
    private String eventId;
    @Column(columnName = "preferredTerm", order = 1, displayName = "Preferred term")
    @Column(columnName = "preferredTerm", order = 1, displayName = "Preferred term", type = Column.Type.AML)
    private String pt;
    @Column(columnName = "highLevelTerm", order = 2, displayName = "High level term")
    @Column(columnName = "highLevelTerm", order = 2, displayName = "High level term", type = Column.Type.AML)
    private String hlt;
    @Column(columnName = "systemOrganClass", order = 3, displayName = "System organ class")
    @Column(columnName = "systemOrganClass", order = 3, displayName = "System organ class", type = Column.Type.AML)
    private String soc;
    @Column(columnName = "description", order = 15, displayName = "Description")
    @Column(columnName = "description", order = 15, displayName = "Description", type = Column.Type.AML)
    private String text;
    @Column(columnName = "comment", order = 16, displayName = "Comment", datasetType = ACUITY)
    private String comment;
    @Column(columnName = "serious", order = 12, displayName = "Serious")
    @Column(columnName = "serious", order = 12, displayName = "Serious", type = Column.Type.AML)
    private String serious;
    @Column(columnName = "outcome", order = 19, displayName = "Outcome")
    private String outcome;
    @Column(columnName = "requiredTreatment", order = 20, displayName = "Required treatment")
    @Column(columnName = "requiredTreatment", order = 20, displayName = "Required treatment", type = Column.Type.AML)
    private String requiredTreatment;
    @Column(columnName = "treatmentEmergent", order = 18, displayName = "Treatment emergent")
    private String treatmentEmergent;
    @Column(columnName = "requiresOrProlongsHospitalisation", order = 17, displayName = "Requires or prolongs hospitalisation")
    private String requiresHospitalisation;
    @Column(columnName = "causedSubjectWithdrawal", order = 21, displayName = "Caused subject withdrawal")
    private String causedSubjectWithdrawal;

    private Integer duration;
    @Builder.Default
    private Boolean calcDurationIfNull = true;

    //  detect
    private Boolean usedInTfl;
    @Column(columnName = "daysFromPreviousDoseToAEStart", order = 11, displayName = "Days from previous dose to ae start", datasetType = DETECT)
    private Integer daysFromPrevDoseToStart;
    private String studyPeriod;
    @Column(columnName = "actionTaken", order = 13, displayName = "Action taken", datasetType = DETECT)
    private String actionTaken;
    @Column(columnName = "causality", order = 14, displayName = "Causality", datasetType = DETECT)
    private String causality;
    //  end detect


    //  acuity
    @Column(columnName = "doseLimitingToxicity", order = 22, displayName = "Dose limiting toxicity", datasetType = ACUITY)
    @Column(columnName = "doseLimitingToxicity", order = 22, displayName = "Dose limiting toxicity", datasetType = ACUITY, type = Column.Type.AML)
    private String doseLimitingToxicity;
    @Column(columnName = "timePointOfDoseLimitingToxicity", order = 23, displayName = "Time point of dose limiting toxicity", datasetType = ACUITY)
    private String timepoint;
    @Column(columnName = "immuneMediatedAE", order = 24, displayName = "Immune mediated ae", datasetType = ACUITY)
    @Column(columnName = "immuneMediatedAE", order = 24, displayName = "Immune mediated ae", datasetType = ACUITY, type = Column.Type.AML)
    private String immuneMediated;
    @Column(columnName = "infusionReactionAE", order = 25, displayName = "Infusion reaction ae", datasetType = ACUITY)
    private String infusionReaction;
    @Column(columnName = "suspectedEndpoint", order = 26, displayName = "Suspected endpoint", datasetType = ACUITY)
    private String suspectedEndpoint;
    @Column(columnName = "suspectedEndpointCategory", order = 27, displayName = "Suspected endpoint category", datasetType = ACUITY)
    private String suspectedEndpointCategory;
    private Integer aeNumber;
    @Column(columnName = "aeOfSpecialInterest", order = 28, displayName = "Ae of special interest", datasetType = ACUITY)
    @Column(columnName = "aeOfSpecialInterest", order = 28, displayName = "Ae of special interest", datasetType = ACUITY, type = Column.Type.AML)
    private String aeOfSpecialInterest;

    @Builder.Default
    private Map<String, String> drugsCausality = new HashMap<>();
    //  end acuity

    @Builder.Default
    private List<AeSeverityRaw> aeSeverities = new ArrayList<>();

    @Builder.Default
    private List<String> specialInterestGroups = newArrayList();

    @Column(columnName = "specialInterestGroup", order = 4, displayName = "Special interest group")
    @Column(columnName = "specialInterestGroup", order = 4, displayName = "Special interest group", type = Column.Type.AML)
    public String getSpecialInterestGroupsAsString() {
        return DodUtil.toString(specialInterestGroups, ", ");
    }


    public Map<String, String> getDrugsCausality() {
        return getUnmodifiableMap(drugsCausality);
    }

    private static <K, V> Map<K, V> getUnmodifiableMap(Map<K, V> map) {
        return map == null ? null : Collections.unmodifiableMap(map);
    }

    public List<AeSeverityRaw> getAeSeverities() {
        return aeSeverities;
    }

    /**
     * This method returns groups of drugActionTaken entries.
     * Entries grouped by drug name and sorted by date within every single group
     */
    public Multimap<String, String> getDrugsActionTaken() {
        return aeSeverities.stream()
                .filter(sev -> sev.getDrugsActionTaken() != null)
                .sorted(Comparator.comparing(AeSeverityRaw::getStartDate))
                .flatMap(sev -> sev.getDrugsActionTaken().entrySet().stream())
                .distinct()
                .collect(ArrayListMultimap::create,
                        (multimap, entry) -> multimap.put(entry.getKey(), entry.getValue()),
                        (m1, m2) -> m1.putAll(m2));
    }

    /**
     * Raw data from db has:
     * <pre>
     * Ae (1) -< AeSeverities (3)
     *
     * So for perIncidence we use Ae -> Max(AeSeverities) so we have AeSeverities >=1. Ae (1) -< AeSeverities (1)
     * So for perSeverityChange we create an Ae for every AeSeverities so we have AeSeverities =1. Ae (3) -< AeSeverities (1)
     *
     * For AeDetailLevel.perIncidence the aeSeverities array will be >=1
     * For AeDetailLevel.perSeverityChange the aeSeverities array will be 1
     * </pre>
     */
    public Date getMinStartDate() {
        return getAeSeverities().stream().map(AeSeverityRaw::getStartDate).collect(toDateSummaryStatistics()).getMin();
    }

    public Date getMaxEndDate() {
        return getAeSeverities().stream().map(AeSeverityRaw::getEndDate).collect(toDateSummaryStatistics()).getMax();
    }

    @Column(columnName = "maxSeverity", order = 5, displayName = "Max severity")
    @Column(columnName = "maxSeverity", order = 5, displayName = "Max severity", type = Column.Type.AML)
    public String getMaxAeSeverity() {
        AeSeverity maxAeSeverity = getMaxSeverity();
        if (maxAeSeverity != null) {
            return maxAeSeverity.getWebappSeverity();
        } else {
            return null;
        }
    }

    public Integer getMaxAeSeverityNum() {
        AeSeverity maxAeSeverity = getMaxSeverity();
        if (maxAeSeverity != null) {
            return maxAeSeverity.getSeverityNum();
        } else {
            return null;
        }
    }

    public AeSeverity getMaxSeverity() {
        return getAeSeverities().stream().
                map(AeSeverityRaw::getSeverity).
                filter(Objects::nonNull).
                max(Comparator.comparing(AeSeverity::getSeverityNum))
                .orElse(null);
    }
}
