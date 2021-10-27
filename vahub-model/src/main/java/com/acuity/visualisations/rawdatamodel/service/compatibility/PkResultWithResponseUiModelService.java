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

import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import com.acuity.visualisations.rawdatamodel.vo.plots.BoxplotCalculationObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw.Response.COMPLETE_RESPONSE;
import static com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw.Response.MISSING_TARGET_LESIONS_RESPONSE;
import static com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw.Response.NOT_EVALUABLE;
import static com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw.Response.NO_ASSESSMENT;
import static com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw.Response.NO_EVIDANCE_OF_DISEASE;
import static com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw.Response.PARTIAL_RESPONSE;
import static com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw.Response.PROGRESSIVE_DISEASE;
import static com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw.Response.STABLE_DISEASE;


@Service
public class PkResultWithResponseUiModelService extends BoxPlotUiModelService {

    public static final List<String> OVERALL_RESPONSES_SORTED = Stream.of(
            COMPLETE_RESPONSE, PARTIAL_RESPONSE, STABLE_DISEASE, PROGRESSIVE_DISEASE,
            NOT_EVALUABLE, NO_ASSESSMENT, MISSING_TARGET_LESIONS_RESPONSE, NO_EVIDANCE_OF_DISEASE)
            .map(AssessmentRaw.Response::getName)
            .collect(Collectors.toList());

    @Override
    List<?> getXCategories(Set<Object> categories) {
        return OVERALL_RESPONSES_SORTED;
    }

    @Override
    Predicate<Object> getXCategoriesFilter(Map<Object, BoxplotCalculationObject> mapByXCategory) {
        return xAxisValue -> true;
    }

    @Override
    boolean isNeededToShowEmptyCategories() {
        return false;
    }
}
