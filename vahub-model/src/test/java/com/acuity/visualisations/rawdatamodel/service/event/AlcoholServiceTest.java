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

package com.acuity.visualisations.rawdatamodel.service.event;

import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import com.acuity.visualisations.rawdatamodel.dataproviders.AlcoholDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.AlcoholRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Alcohol;
import com.acuity.va.security.acl.domain.Datasets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AlcoholServiceTest {
    @Autowired
    private AlcoholService alcoholService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private AlcoholDatasetsDataProvider alcoholDatasetsDataProvider;

    private DoDCommonService doDCommonService = new DoDCommonService();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final String SUBJECT_ID = "sid1";
    private static final String STUDY_CODE = "code1";
    private static final String STUDY_PART = "A";

    private static final String SUBSTANCE_CATEGORY = "category1";
    private static final String SUBSTANCE_USE_OCCURRENCE = "1";
    private static final String SUBSTANCE_TYPE = "2";
    private static final String OTHER_SUBSTANCE_TYPE_SPEC = "Malt";
    private static final String SUBSTANCE_TYPE_USE_OCCURRENCE = "1";
    private static final String FREQUENCY = "Every week";
    private static final String SUBSTANCE_CONSUMPTION = "2000.1";
    private static final String START_DATE_STRING = "2015-08-03T00:00:00";
    private static final Date START_DATE = DateUtils.toDateTime(START_DATE_STRING);
    private static final String END_DATE_STRING = "2015-08-08T00:00:00";
    private static final Date END_DATE = DateUtils.toDateTime(END_DATE_STRING);

    private Subject SUBJECT1 = Subject.builder().subjectId(SUBJECT_ID).subjectCode(SUBJECT_ID).clinicalStudyCode(STUDY_CODE).studyPart(STUDY_PART)
            .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("02.08.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2016")).build();
    private Alcohol ALCOHOL1 = new Alcohol(AlcoholRaw.builder().substanceCategory(SUBSTANCE_CATEGORY).substanceUseOccurrence(SUBSTANCE_USE_OCCURRENCE)
            .substanceType(SUBSTANCE_TYPE).otherSubstanceTypeSpec(OTHER_SUBSTANCE_TYPE_SPEC).frequency(FREQUENCY)
            .substanceTypeUseOccurrence(SUBSTANCE_TYPE_USE_OCCURRENCE).substanceConsumption(Double.valueOf(SUBSTANCE_CONSUMPTION)).startDate(START_DATE)
            .endDate(END_DATE).build(), SUBJECT1);

    @Test
    public void shouldGetAcuityDetailsOnDemandColumnsInCorrectOrder() {
        when(alcoholDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(ALCOHOL1));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT1));

        Map<String, String> columns = doDCommonService.getDoDColumns(Column.DatasetType.ACUITY, Collections.singletonList(ALCOHOL1));

        softly.assertThat(columns.keySet())
                .containsExactly("studyId", "studyPart", "subjectId", "substanceCategory", "substanceUseOccurrence", "substanceType",
                        "otherSubstanceTypeSpec", "substanceTypeUseOccurrence", "substanceConsumption", "frequency", "startDate", "endDate");

        softly.assertThat(columns.values())
                .containsExactly("Study id", "Study part", "Subject id", "Substance category", "Substance use occurrence", "Type of substance",
                        "Other substance type specification", "Substance type use occurrence", "Substance consumption", "Frequency", "Start date",
                        "End date");
    }

    @Test
    public void shouldGetAcuityDetailsOnDemandData() {
        when(alcoholDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(ALCOHOL1));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT1));

        List<Map<String, String>> result = doDCommonService.getColumnData(Column.DatasetType.ACUITY, Collections.singletonList(ALCOHOL1));
        softly.assertThat(result)
                .hasSize(1)
                .extracting("studyId", "studyPart", "subjectId", "substanceCategory", "substanceUseOccurrence", "substanceType",
                        "otherSubstanceTypeSpec", "substanceTypeUseOccurrence", "frequency", "substanceConsumption", "startDate", "endDate")
                .contains(tuple(STUDY_CODE, STUDY_PART, SUBJECT_ID, SUBSTANCE_CATEGORY, SUBSTANCE_USE_OCCURRENCE, SUBSTANCE_TYPE,
                        OTHER_SUBSTANCE_TYPE_SPEC, SUBSTANCE_TYPE_USE_OCCURRENCE, FREQUENCY, SUBSTANCE_CONSUMPTION, START_DATE_STRING,
                        END_DATE_STRING));
    }
}
