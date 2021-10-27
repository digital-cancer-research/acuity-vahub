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

package com.acuity.visualisations.rawdatamodel.service.compatibility;

import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import org.junit.Test;

import java.util.List;

import static com.acuity.visualisations.rawdatamodel.service.compatibility.BiomarkerHeatMapColoringService.MUTATION_COLORS;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.BLACK;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.BLUE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.DIMGRAY;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.GREEN;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.ORANGE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.PINK;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.RED;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.SADDLEBROWN;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.SKYBLUE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.YELLOW;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.BRIGHTPURPLE;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class BiomarkerHeatMapColoringServiceTest {

    @Test
    public void testGetColor() {

        BiomarkerHeatMapColoringService service = new BiomarkerHeatMapColoringService();

        List<Biomarker> biomarkers = newArrayList(
                new Biomarker(BiomarkerRaw.builder().gene("gene1").mutation("Amplification").build(), Subject.builder().
                        subjectId("subj1id").subjectCode("subj1").build()),
                new Biomarker(BiomarkerRaw.builder().gene("gene2").mutation("Gain").build(), Subject.builder().
                        subjectId("subj2id").subjectCode("subj2").build()),
                new Biomarker(BiomarkerRaw.builder().gene("gene3").mutation("Rearrangement").build(), Subject.builder().
                        subjectId("subj3id").subjectCode("subj3").build()),
                new Biomarker(BiomarkerRaw.builder().gene("gene4").mutation("Deletion").build(), Subject.builder().
                        subjectId("subj4id").subjectCode("subj4").build()),
                new Biomarker(BiomarkerRaw.builder().gene("gene5").mutation("Nonsynonymous mutation").build(), Subject.builder().
                        subjectId("subj5id").subjectCode("subj5").build()),
                new Biomarker(BiomarkerRaw.builder().gene("gene6").mutation("InDel").build(), Subject.builder().
                        subjectId("subj6id").subjectCode("subj6").build()),
                new Biomarker(BiomarkerRaw.builder().gene("gene6").mutation("Splice").build(), Subject.builder().
                        subjectId("subj6id").subjectCode("subj6").build()),
                new Biomarker(BiomarkerRaw.builder().gene("gene7").mutation("Other").build(), Subject.builder().
                        subjectId("subj7id").subjectCode("subj7").build()),
                new Biomarker(BiomarkerRaw.builder().gene("gene8").mutation("Not recognised").build(), Subject.builder().
                        subjectId("subj8id").subjectCode("subj8").build()),
                new Biomarker(BiomarkerRaw.builder().gene("gene9").mutation("Truncating").build(), Subject.builder().
                        subjectId("subj9id").subjectCode("subj9").build()),
                new Biomarker(BiomarkerRaw.builder().gene("gene10").mutation("Promoter").build(), Subject.builder().
                        subjectId("subj10id").subjectCode("subj10").build())
        );

        List<String> colors = biomarkers.stream().map(service::getColor).collect(toList());
        assertThat(colors).hasSize(11);
        assertThat(colors).containsAll(MUTATION_COLORS.values().stream().map(Colors::getCode).collect(toList()));
        assertThat(colors).containsExactlyElementsOf(newArrayList(RED, PINK, YELLOW, BLUE, GREEN, SADDLEBROWN, ORANGE, DIMGRAY, SKYBLUE, BLACK, BRIGHTPURPLE).stream()
                .map(Colors::getCode).collect(toList()));
    }
}
