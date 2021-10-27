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

import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.BLACK;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.BLUE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.BRIGHTPURPLE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.DIMGRAY;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.GREEN;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.ORANGE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.PINK;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.RED;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.SADDLEBROWN;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.SKYBLUE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.WHITE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.YELLOW;
import static com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerMutation.AMPLIFICATION_MUTATION;
import static com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerMutation.DELETION_MUTATION;
import static com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerMutation.GAIN_MUTATION;
import static com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerMutation.INDEL_MUTATION;
import static com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerMutation.NONSYNONYMOUS_MUTATION;
import static com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerMutation.NOT_RECOGNISED_MUTATION;
import static com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerMutation.OTHER_MUTATION;
import static com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerMutation.PROMOTER;
import static com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerMutation.REARRANGEMENT_MUTATION;
import static com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerMutation.SPLICE_MUTATION;
import static com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerMutation.TRUNCATING_MUTATION;

@Service
public class BiomarkerHeatMapColoringService extends ColoringService {

    protected static final Map<Object, Colors> MUTATION_COLORS = new HashMap<>();

    static {
        MUTATION_COLORS.put(AMPLIFICATION_MUTATION.getName(), RED);
        MUTATION_COLORS.put(GAIN_MUTATION.getName(), PINK);
        MUTATION_COLORS.put(REARRANGEMENT_MUTATION.getName(), YELLOW);
        MUTATION_COLORS.put(DELETION_MUTATION.getName(), BLUE);
        MUTATION_COLORS.put(NONSYNONYMOUS_MUTATION.getName(), GREEN);
        MUTATION_COLORS.put(INDEL_MUTATION.getName(), SADDLEBROWN);
        MUTATION_COLORS.put(SPLICE_MUTATION.getName(), ORANGE);
        MUTATION_COLORS.put(TRUNCATING_MUTATION.getName(), BLACK);
        MUTATION_COLORS.put(OTHER_MUTATION.getName(), DIMGRAY);
        MUTATION_COLORS.put(NOT_RECOGNISED_MUTATION.getName(), SKYBLUE);
        MUTATION_COLORS.put(PROMOTER.getName(), BRIGHTPURPLE);
    }

    public String getColor(Biomarker element) {
        return getColor(element.getEvent().getMutation());

    }

    public String getColor(Object category) {
        return MUTATION_COLORS.getOrDefault(category, WHITE).getCode();
    }
}
