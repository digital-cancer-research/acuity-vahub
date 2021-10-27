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

package com.acuity.visualisations.rawdatamodel.service.ae.chord;

import com.acuity.visualisations.rawdatamodel.util.AlphanumEmptyLastComparator;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DodUtil;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverity;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import static com.acuity.visualisations.rawdatamodel.statistics.collectors.DateSummaryStatisticsCollector.toDateSummaryStatistics;
import static com.acuity.visualisations.rawdatamodel.util.Attributes.defaultNullableValue;
import static com.acuity.visualisations.rawdatamodel.util.Constants.NO;
import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;
import static java.util.stream.Collectors.toMap;

@EqualsAndHashCode
@Getter
public class AeChordContributor {
    private Set<Ae> startEvents;
    private Set<Ae> endEvents;

    private static final String LINKS_PATTERN = "%s (A) to %s (B)";

    public AeChordContributor(Set<Ae> startEvents, Set<Ae> endEvents) {
        Assert.isTrue(startEvents != null && !startEvents.isEmpty(), "Start events must not be empty");
        Assert.isTrue(endEvents != null && !endEvents.isEmpty(), "End events must not be empty");
        this.startEvents = startEvents;
        this.endEvents = endEvents;
    }

    @Column(columnName = "studyId", order = -3, displayName = "Study id")
    public String getStudyId() {
        return anyStart().getSubject().getClinicalStudyCode();
    }

    @Column(columnName = "studyPart", order = -2, displayName = "Study part")
    public String getStudyPart() {
        return anyStart().getStudyPart();
    }

    @Column(columnName = "subjectId", order = -1, displayName = "Subject id", defaultSortBy = true)
    public String getRawSubjectCode() {
        return anyStart().getSubject().getRawSubject();
    }

    @Column(columnName = "ptLinks", order = 1, displayName = "Preferred term links")
    public String getPtLinks() {
        return String.format(LINKS_PATTERN, defaultNullableValue(anyStart().getEvent().getPt()),
                defaultNullableValue(anyEnd().getEvent().getPt()));
    }

    @Column(columnName = "hltLinks", order = 2, displayName = "High level term links")
    public String getHltLinks() {
        return String.format(LINKS_PATTERN, defaultNullableValue(anyStart().getEvent().getHlt()),
                defaultNullableValue(anyEnd().getEvent().getHlt()));
    }

    @Column(columnName = "socLinks", order = 3, displayName = "System organ class links")
    public String getSocLinks() {
        return String.format(LINKS_PATTERN, defaultNullableValue(anyStart().getEvent().getSoc()),
                defaultNullableValue(anyEnd().getEvent().getSoc()));
    }

    @Column(columnName = "seriousA", order = 4, displayName = "Serious (A)")
    public String getSeriousA() {
        return startEvents.stream().map(e -> e.getEvent().getSerious()).filter(YES::equalsIgnoreCase).findAny().orElse(NO);
    }

    @Column(columnName = "seriousB", order = 5, displayName = "Serious (B)")
    public String getSeriousB() {
        return endEvents.stream().map(e -> e.getEvent().getSerious()).filter(YES::equalsIgnoreCase).findAny().orElse(NO);
    }

    @Column(columnName = "maxSeverityA", order = 6, displayName = "Max severity (A)")
    public String getMaxSeverityA() {
        return getMaxSeverityForEvents(startEvents);
    }

    @Column(columnName = "maxSeverityB", order = 7, displayName = "Max severity (B)")
    public String getMaxSeverityB() {
        return getMaxSeverityForEvents(endEvents);
    }

    @Column(columnName = "startDateA", order = 8, displayName = "Start date (A)")
    public Date getStartDateA() {
        return startEvents.stream().map(Ae::getStartDate).collect(toDateSummaryStatistics()).getMin();
    }

    @Column(columnName = "endDateA", order = 9, displayName = "End date (A)")
    public Date getEndDateA() {
        return startEvents.stream().map(Ae::getEndDate).collect(toDateSummaryStatistics()).getMax();
    }

    @Column(columnName = "daysOnStudyAtStartA", order = 10, displayName = "Days on study at ae start (A)")
    public Integer getDaysOnStudyAtStartA() {
        return startEvents.stream().map(Ae::getDaysOnStudyAtStart)
                .min(Comparator.nullsLast(Comparator.naturalOrder())).orElse(null);
    }

    @Column(columnName = "daysOnStudyAtEndA", order = 11, displayName = "Days on study at ae end (A)")
    public Integer getDaysOnStudyAtEndA() {
        return startEvents.stream().map(Ae::getDaysOnStudyAtEnd)
                .max(Comparator.nullsLast(Comparator.naturalOrder())).orElse(null);
    }

    @Column(columnName = "startDateB", order = 12, displayName = "Start date (B)")
    public Date getStartDateB() {
        return endEvents.stream().map(Ae::getStartDate).collect(toDateSummaryStatistics()).getMin();
    }

    @Column(columnName = "endDateB", order = 13, displayName = "End date (B)")
    public Date getEndDateB() {
        return endEvents.stream().map(Ae::getEndDate).collect(toDateSummaryStatistics()).getMax();
    }

    @Column(columnName = "daysOnStudyAtStartB", order = 14, displayName = "Days on study at ae start (B)")
    public Integer getDaysOnStudyAtStartB() {
        return endEvents.stream().map(Ae::getDaysOnStudyAtStart)
                .min(Comparator.nullsLast(Comparator.naturalOrder())).orElse(null);
    }

    @Column(columnName = "daysOnStudyAtEndB", order = 15, displayName = "Days on study at ae end (B)")
    public Integer getDaysOnStudyAtEndB() {
        return endEvents.stream().map(Ae::getDaysOnStudyAtEnd)
                .max(Comparator.nullsLast(Comparator.naturalOrder())).orElse(null);
    }

    @Column(columnName = "causalityA", order = 16, displayName = "Causality (A)")
    public String getCausalityA() {
        return getCausalityAsStringForEvents(startEvents);
    }

    @Column(columnName = "causalityB", order = 17, displayName = "Causality (B)")
    public String getCausalityB() {
        return getCausalityAsStringForEvents(endEvents);
    }

    private Ae anyStart() {
        return startEvents.stream().findAny().get();
    }

    private Ae anyEnd() {
        return endEvents.stream().findAny().get();
    }

    private String getMaxSeverityForEvents(Set<Ae> events) {
        return events.stream().map(e -> e.getEvent().getAeSeverities())
                .flatMap(Collection::stream)
                .map(AeSeverityRaw::getSeverity)
                .filter(Objects::nonNull)
                .max(Comparator.comparing(AeSeverity::getSeverityNum))
                .map(AeSeverity::getWebappSeverity)
                .orElse(null);
    }

    private String getCausalityAsStringForEvents(Set<Ae> events) {
        final Map<String, String> causalities = events.stream().map(e -> e.getEvent().getDrugsCausality())
                .flatMap(e -> e.entrySet().stream())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (v1, v2) -> (YES.equals(v1) || YES.equals(v2))
                                ? YES : NO,
                        () -> new TreeMap<>(AlphanumEmptyLastComparator.getInstance())));
        return DodUtil.toString(causalities, ": ", ", ");
    }
}
