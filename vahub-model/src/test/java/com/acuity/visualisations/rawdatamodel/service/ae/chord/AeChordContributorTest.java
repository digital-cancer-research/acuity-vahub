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

import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

public class AeChordContributorTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Subject SUBJECT1 = Subject.builder().subjectId("sid1")
            .subjectCode("E01")
            .firstTreatmentDate(toDate("01.08.2015"))
            .build();

    private Set<Ae> startEvents = newHashSet(new Ae(AeRaw.builder().id("id11")
                    .aeSeverities(newArrayList(AeSeverityRaw.builder()
                            .startDate(toDate("15.08.2015"))
                            .endDate(toDate("20.08.2015")).build()))
                    .build(), SUBJECT1),
            new Ae(AeRaw.builder().id("id12")
                    .aeSeverities(newArrayList(AeSeverityRaw.builder()
                            .startDate(toDate("10.08.2015"))
                            .endDate(toDate("25.08.2015")).build()))
                    .build(), SUBJECT1));
    private Set<Ae> endEvents = newHashSet(new Ae(AeRaw.builder().id("id21")
                    .aeSeverities(newArrayList(AeSeverityRaw.builder()
                            .startDate(toDate("11.08.2015"))
                            .endDate(toDate("16.08.2015")).build()))
                    .build(), SUBJECT1),
            new Ae(AeRaw.builder().id("id22")
                    .aeSeverities(newArrayList(AeSeverityRaw.builder()
                            .startDate(toDate("12.08.2015"))
                            .endDate(toDate("14.08.2015")).build()))
                    .build(), SUBJECT1));
    private AeChordContributor aeChordContributorWithDates = new AeChordContributor(startEvents, endEvents);

    @Test
    public void testGetDaysOnStudyAtStartA() {
        softly.assertThat(aeChordContributorWithDates.getDaysOnStudyAtStartA()).isEqualTo(9);
    }

    @Test
    public void testGetDaysOnStudyAtEndA() {
        softly.assertThat(aeChordContributorWithDates.getDaysOnStudyAtEndA()).isEqualTo(24);
    }

    @Test
    public void testGetDaysOnStudyAtStartB() {
        softly.assertThat(aeChordContributorWithDates.getDaysOnStudyAtStartB()).isEqualTo(10);
    }

    @Test
    public void testGetDaysOnStudyAtEndB() {
        softly.assertThat(aeChordContributorWithDates.getDaysOnStudyAtEndB()).isEqualTo(15);
    }

    @Test
    public void testGetPtLinks() {
        AeChordContributor aeChordContributorWithPt = getAeChordContributorWithPtHltSoc();
        softly.assertThat(aeChordContributorWithPt.getPtLinks()).isEqualTo("PT1 (A) to PT2 (B)");
    }

    @Test
    public void testGetPtLinksPtEmpty() {
        AeChordContributor aeChordContributorWithoutPt = getAeChordContributorWithPtHltSocEmpty();
        softly.assertThat(aeChordContributorWithoutPt.getPtLinks()).isEqualTo("(Empty) (A) to (Empty) (B)");
    }

    @Test
    public void testGetHltLinks() {
        AeChordContributor aeChordContributorWithHlt = getAeChordContributorWithPtHltSoc();
        softly.assertThat(aeChordContributorWithHlt.getHltLinks()).isEqualTo("HLT1 (A) to HLT2 (B)");
    }

    @Test
    public void testGetHltLinksPtEmpty() {
        AeChordContributor aeChordContributorWithoutHlt = getAeChordContributorWithPtHltSocEmpty();
        softly.assertThat(aeChordContributorWithoutHlt.getHltLinks()).isEqualTo("(Empty) (A) to (Empty) (B)");
    }

    @Test
    public void testGetSocLinks() {
        AeChordContributor aeChordContributorWithSoc = getAeChordContributorWithPtHltSoc();
        softly.assertThat(aeChordContributorWithSoc.getSocLinks()).isEqualTo("SOC1 (A) to SOC2 (B)");
    }

    @Test
    public void testGetSocLinksPtEmpty() {
        AeChordContributor aeChordContributorWithoutSoc = getAeChordContributorWithPtHltSocEmpty();
        softly.assertThat(aeChordContributorWithoutSoc.getSocLinks()).isEqualTo("(Empty) (A) to (Empty) (B)");
    }

    @Test
    public void testGetCausalityA() {

        AeChordContributor aeChordContributorWithCausality = getAeChordContributorWithCausality();
        softly.assertThat(aeChordContributorWithCausality.getCausalityA())
                .isEqualTo("Drug1: No, Drug2: Yes, Drug3: Yes, Drug4: Yes");
    }

    @Test
    public void testGetCausalityB() {

        AeChordContributor aeChordContributorWithCausality = getAeChordContributorWithCausality();
        softly.assertThat(aeChordContributorWithCausality.getCausalityB())
                .isEqualTo("Drug1: No, Drug2: Yes, Drug3: Yes, Drug4: Yes");
    }

    private AeChordContributor getAeChordContributorWithCausality() {
        Map<String, String> causality1 = new HashMap<>();
        causality1.put("Drug1", "No");
        causality1.put("Drug2", "Yes");
        causality1.put("Drug3", "No");
        causality1.put("Drug4", "Yes");

        Map<String, String> causality2 = new HashMap<>();
        causality2.put("Drug1", "No");
        causality2.put("Drug2", "No");
        causality2.put("Drug3", "Yes");
        causality2.put("Drug4", "Yes");

        Set<Ae> startEvents = newHashSet(new Ae(AeRaw.builder().id("id11").drugsCausality(causality1).build(), SUBJECT1),
                new Ae(AeRaw.builder().id("id12").drugsCausality(causality2).build(), SUBJECT1));
        Set<Ae> endEvents = newHashSet(new Ae(AeRaw.builder().id("id21").drugsCausality(causality1).build(), SUBJECT1),
                new Ae(AeRaw.builder().id("id22").drugsCausality(causality2).build(), SUBJECT1));
        return new AeChordContributor(startEvents, endEvents);
    }

    private AeChordContributor getAeChordContributorWithPtHltSoc() {
        Set<Ae> startEvents = newHashSet(new Ae(AeRaw.builder().id("id11").pt("PT1").hlt("HLT1").soc("SOC1").build(), SUBJECT1),
                new Ae(AeRaw.builder().id("id12").pt("PT1").hlt("HLT1").soc("SOC1").build(), SUBJECT1));
        Set<Ae> endEvents = newHashSet(new Ae(AeRaw.builder().id("id21").pt("PT2").hlt("HLT2").soc("SOC2").build(), SUBJECT1),
                new Ae(AeRaw.builder().id("id22").pt("PT2").hlt("HLT2").soc("SOC2").build(), SUBJECT1));
        return new AeChordContributor(startEvents, endEvents);
    }

    private AeChordContributor getAeChordContributorWithPtHltSocEmpty() {
        Set<Ae> startEvents = newHashSet(new Ae(AeRaw.builder().id("id11").build(), SUBJECT1),
                new Ae(AeRaw.builder().id("id12").build(), SUBJECT1));
        Set<Ae> endEvents = newHashSet(new Ae(AeRaw.builder().id("id21").build(), SUBJECT1),
                new Ae(AeRaw.builder().id("id22").build(), SUBJECT1));
        return new AeChordContributor(startEvents, endEvents);
    }

}
