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

package com.acuity.visualisations.rawdatamodel.service.dod;

import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.CardiacRaw;
import com.acuity.visualisations.rawdatamodel.vo.LungFunctionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * See also {@link DoDCommonServiceTest}
 */
public class CommonTableServiceTest {

    private static final String SUBJECT_ID_FIELD = "subjectId";
    private static final String MEASUREMENT_TIME_POINT = "measurementTimePoint";
    private static final String REASON_NO_SINUS_RHYTHM = "reasonNoSinusRhythm";
    private CommonTableService service = new CommonTableService() {
        @Override
        protected Column.Type getType() {
            return Column.Type.DOD;
        }
    };

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void subjectIsSortedBySubjectId() {
        Class<Subject> eventClass = Subject.class;
        String columnName = SUBJECT_ID_FIELD;
        CommonTableService.ColumnMetadata expected = CommonTableService.ColumnMetadata.builder()
                .columnName(columnName)
                .defaultSortBy(true)
                .defaultSortOrder(0.0)
                .defaultSortReversed(false)
                .build();

        CommonTableService.ColumnMetadata classColumnMetadata =
                service.getClassColumnMetadataImpl(eventClass, Column.DatasetType.ACUITY, Column.Type.DOD).get(columnName);
        softly.assertThat(classColumnMetadata.getColumnName()).isEqualTo(expected.getColumnName());
        softly.assertThat(classColumnMetadata.isDefaultSortBy()).isEqualTo(expected.isDefaultSortBy());
        softly.assertThat(classColumnMetadata.getDefaultSortOrder()).isEqualTo(expected.getDefaultSortOrder());
        softly.assertThat(classColumnMetadata.isDefaultSortReversed()).isEqualTo(expected.isDefaultSortReversed());
    }

    @Test
    public void lungFunctionIsSortedByMeasurementTimePoint() {
        Class<LungFunction> eventClass = LungFunction.class;
        String columnName = MEASUREMENT_TIME_POINT;
        CommonTableService.ColumnMetadata expected = CommonTableService.ColumnMetadata.builder()
                .columnName(columnName)
                .defaultSortBy(true)
                .defaultSortOrder(1.0)
                .defaultSortReversed(false)
                .build();

        CommonTableService.ColumnMetadata classColumnMetadata =
                service.getClassColumnMetadataImpl(eventClass, Column.DatasetType.ACUITY, Column.Type.DOD).get(columnName);
        softly.assertThat(classColumnMetadata.getColumnName()).isEqualTo(expected.getColumnName());
        softly.assertThat(classColumnMetadata.isDefaultSortBy()).isEqualTo(expected.isDefaultSortBy());
        softly.assertThat(classColumnMetadata.getDefaultSortOrder()).isEqualTo(expected.getDefaultSortOrder());
        softly.assertThat(classColumnMetadata.isDefaultSortReversed()).isEqualTo(expected.isDefaultSortReversed());
    }

    @Test
    public void getDefaultSortBy() {
        LungFunction lungFunctionEvent = new LungFunction(LungFunctionRaw.builder().build(), Subject.builder().build());

        ArrayList<SortAttrs> actual = service.getDefaultSortBy(Column.DatasetType.ACUITY, Collections.singletonList(lungFunctionEvent), Column.Type.DOD);

        assertThat(actual).extracting("sortBy").containsExactly(SUBJECT_ID_FIELD, MEASUREMENT_TIME_POINT);
    }

    @Test
    public void whenColumnNameIsEmptyFieldNameIsUsed() {
        CardiacRaw cardiacRaw = CardiacRaw.builder().build();

        Map<String, CommonTableService.ColumnMetadata> classColumnMetadata = service.getClassColumnMetadataImpl(cardiacRaw, Column.DatasetType.ACUITY, Column.Type.DOD);

        assertThat(classColumnMetadata.get(REASON_NO_SINUS_RHYTHM).getColumnName()).isEqualTo(REASON_NO_SINUS_RHYTHM);
    }
}
