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

import com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.TumourTherapyGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.AlphanumEmptyLastComparator;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE_TO_LOWER_CASE;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.weeksBetween;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy.CHEMOTHERAPY;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy.RADIOTHERAPY_LABEL;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public final class TumourTherapy implements HasStartEndDate, HasStringId, HasSubject {

    private Date startDate;
    private Date endDate;
    private String subjectId;
    private Subject subject;
    private Set<String> drugs = new HashSet<>();
    private String color = ColoringService.Colors.LIGHTSEAGREEN.getCode();
    private String name = "";
    private List<DrugDose> doses = new ArrayList<>();
    private Collection<Chemotherapy> previousChemoTherapies = new HashSet<>();
    private Collection<Radiotherapy> previousRadioTherapies = new HashSet<>();
    private boolean noStartDate;

    /**
     * Need to implement HasStringId interface to enable implementing SimpleSelectionSupportService by
     * TumourColumnRangeSelectionSupportService,
     * but there is no real need in having an id for this class
     */
    @Override
    public String getId() {
        throw new NotImplementedException("TumourTherapy is a class to store aggregated information");
    }

    public enum Attributes implements GroupByOption<TumourTherapy> {

        SUBJECT(EntityAttribute.attribute("subject", (TumourTherapy t) -> t.getSubject().getSubjectCode())),
        START_DATE(EntityAttribute.attribute("startDate", TumourTherapy::getStartDate)),
        // start date can be null
        WEEKS_TO_START_DATE(EntityAttribute.attribute("weeksToStartDate",
                t -> {
                    OptionalInt weeks = weeksBetween(t.getSubject().getFirstTreatmentDate(), t.getStartDate());
                    return weeks.isPresent() ? weeks.getAsInt() : null;
                })),
        END_DATE(EntityAttribute.attribute("endDate", TumourTherapy::getEndDate)),
        // end date cannot be null
        WEEKS_TO_END_DATE(EntityAttribute.attribute("weeksToEndDate",
                t -> weeksBetween(t.getSubject().getFirstTreatmentDate(), t.getEndDate()).orElse(0))),
        MOST_RECENT_THERAPY(EntityAttribute.attribute("mostRecentTherapy",
                (TumourTherapy t) -> TumourTherapyGroupByOptions.MOST_RECENT_THERAPY)),
        ALL_PRIOR_THERAPIES(EntityAttribute.attribute("allPriorTherapies",
                (TumourTherapy t) -> TumourTherapyGroupByOptions.ALL_PRIOR_THERAPIES));

        @Getter
        private final EntityAttribute<TumourTherapy> attribute;

        Attributes(EntityAttribute<TumourTherapy> attribute) {
            this.attribute = attribute;
        }
    }


    public TumourTherapy(Date startDate, Date endDate, Subject subject) {
        this.startDate = startDate == null ? null : DaysUtil.truncLocalTime(startDate);

        if (subject.getDateOfWithdrawal() != null) {
            endDate = DaysUtil.getMinDate(endDate, subject.getDateOfWithdrawal());
        }
        if (subject.getDateOfDeath() != null) {
            endDate = DaysUtil.getMinDate(endDate, subject.getDateOfDeath());
        }
        this.endDate = endDate == null ? null : DaysUtil.truncLocalTime(endDate);

        this.subjectId = subject.getSubjectId();
        this.subject = subject;
    }

    public static <T extends HasSubject & HasStartEndDate> TumourTherapy from(T otherTherapy) {
        return new TumourTherapy(otherTherapy.getStartDate(), otherTherapy.getEndDate(), otherTherapy.getSubject());
    }

    public static TumourTherapy from(Subject s) {
        return new TumourTherapy(s.getFirstTreatmentDate(), s.getLastTreatmentDate(), s);
    }

    public List<String> getTherapiesTooltip() {
        List<String> therapies = new ArrayList<>();
        if (!previousRadioTherapies.isEmpty()) {
            therapies.add(RADIOTHERAPY_LABEL);
        }
        if (!previousChemoTherapies.isEmpty()) {
            therapies.add(CHEMOTHERAPY + ": " + String.join(", ", previousChemoTherapies.stream()
                    .map(ch -> com.acuity.visualisations.rawdatamodel.util.Attributes
                            .defaultNullableValue(ch.getEvent().getPreferredMed(), DEFAULT_EMPTY_VALUE_TO_LOWER_CASE).toString())
                    .sorted().collect(Collectors.toCollection(() -> new TreeSet<>(AlphanumEmptyLastComparator.getInstance())))));
        }
        if (!drugs.isEmpty()) {
            therapies.add(String.join(", ", drugs));
        }
        return therapies;
    }
}
