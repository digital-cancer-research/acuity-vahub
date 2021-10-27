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

package com.acuity.visualisations.rawdatamodel.trellis;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.EmptyBin;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.IntBin;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.CvotEndpointRaw;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;

public class CvotEndpointDatasetsBinnedAttributesTest {

    public static final Subject SUBJECT1;

    static {
        final HashMap<String, Date> drugFirstDoseDate1 = new HashMap<>();
        drugFirstDoseDate1.put("drug1", DateUtils.toDate("01.08.2015"));
        drugFirstDoseDate1.put("drug2", DateUtils.toDate("01.10.2015"));
        SUBJECT1 = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
                .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
                .drugFirstDoseDate(drugFirstDoseDate1).build();
    }

    public static final CvotEndpoint EVENT1 = new CvotEndpoint(CvotEndpointRaw.builder()
            .startDate(DateUtils.toDate("01.09.2015")).aeNumber(1).category1("cat1").subjectId("sid1").build(), SUBJECT1);
    public static final CvotEndpoint EVENT2 = new CvotEndpoint(CvotEndpointRaw.builder()
            .startDate(DateUtils.toDate("01.11.2015")).aeNumber(1).category1("cat1").subjectId("sid1").build(), SUBJECT1);
    public static final CvotEndpoint EVENT3 = new CvotEndpoint(CvotEndpointRaw.builder()
            .startDate(null).aeNumber(1).category1("cat1").subjectId("sid1").build(), SUBJECT1);

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldCalc1BinDaysSinceFirstDose() throws Exception {
        final Object bin1 = Attributes.get(
                CvotEndpointGroupByOptions.START_DATE.getAttribute(
                        GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 1)
                                .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE).build()), EVENT1);
        System.out.println(bin1);
        softly.assertThat(bin1.toString()).isEqualTo("31");

        final Object bin2 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE).build()), EVENT2);
        System.out.println(bin2);

        softly.assertThat(bin2.toString()).isEqualTo("92");
        final Object bin3 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE).build()), EVENT3);
        System.out.println(bin3);
        softly.assertThat(bin3.toString()).isEqualTo("(Empty)");
    }

    @Test
    public void shouldCalcNullBinDaysSinceFirstDose() throws Exception {
        final Object bin1 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, null)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE).build()), EVENT1);
        System.out.println(bin1);
        softly.assertThat(bin1.toString()).isEqualTo("31");

        final Object bin2 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, null)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE).build()), EVENT2);
        System.out.println(bin2);
        softly.assertThat(bin2.toString()).isEqualTo("92");

        final Object bin3 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, null)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE).build()), EVENT3);
        System.out.println(bin3);
        softly.assertThat(bin3.toString()).isEqualTo("(Empty)");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldCalc7BinDaysSinceFirstDose() throws Exception {
        final Object bin1 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 7)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE).build()), EVENT1);
        System.out.println(bin1);
        softly.assertThat(bin1).isInstanceOf(IntBin.class);
        softly.assertThat(bin1.toString()).isEqualTo("28 - 34");
        final Object bin2 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 7)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE).build()), EVENT2);
        softly.assertThat(bin2).isInstanceOf(IntBin.class);
        softly.assertThat(bin2.toString()).isEqualTo("91 - 97");
        final Object bin3 =  Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 7)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE).build()), EVENT3);
        System.out.println(bin3);
        softly.assertThat(bin3).isInstanceOf(EmptyBin.class);
        softly.assertThat(bin3.toString()).isEqualTo("(Empty)");
    }

    @Test
    public void shouldCalc7BinDaysSinceFirstDoseByDrug() throws Exception {
        final Object bin11 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 7).with(GroupByOption.Param.DRUG_NAME, "drug1")
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE_OF_DRUG).build()), EVENT1);
        System.out.println(bin11);
        softly.assertThat(bin11.toString()).isEqualTo("28 - 34");
        final Object bin12 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 7).with(GroupByOption.Param.DRUG_NAME, "drug2")
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE_OF_DRUG).build()), EVENT1);
        System.out.println(bin12);
        softly.assertThat(bin12.toString()).isEqualTo("-35 - -29");
        final Object bin21 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 7).with(GroupByOption.Param.DRUG_NAME, "drug1")
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE_OF_DRUG).build()), EVENT2);
        System.out.println(bin21);
        softly.assertThat(bin21.toString()).isEqualTo("91 - 97");
        final Object bin22 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 7).with(GroupByOption.Param.DRUG_NAME, "drug2")
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE_OF_DRUG).build()), EVENT2);
        System.out.println(bin22);
        softly.assertThat(bin22.toString()).isEqualTo("28 - 34");
        final Object bin31 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 7).with(GroupByOption.Param.DRUG_NAME, "drug1")
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE_OF_DRUG).build()), EVENT3);
        System.out.println(bin31);
        softly.assertThat(bin31.toString()).isEqualTo("(Empty)");
        final Object bin32 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 7).with(GroupByOption.Param.DRUG_NAME, "drug2")
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE_OF_DRUG).build()), EVENT3);
        System.out.println(bin32);
        softly.assertThat(bin32.toString()).isEqualTo("(Empty)");
    }

    @Test
    public void shouldCalc1BinStartDate() throws Exception {
        final Object bin1 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 1).build()), EVENT1);
        System.out.println(bin1);
        softly.assertThat(bin1.toString()).isEqualTo("2015-09-01");
        final Object bin2 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 1).build()), EVENT2);
        System.out.println(bin2);
        softly.assertThat(bin2.toString()).isEqualTo("2015-11-01");
        final Object bin3 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 1).build()), EVENT3);
        System.out.println(bin3);
        softly.assertThat(bin3.toString()).isEqualTo("(Empty)");
    }

    @Test
    public void shouldCalcNullBinStartDate() throws Exception {
        final Object bin1 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, null).build()), EVENT1);
        System.out.println(bin1);
        softly.assertThat(bin1.toString()).isEqualTo("2015-09-01");
        final Object bin2 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, null).build()), EVENT2);
        System.out.println(bin2);
        softly.assertThat(bin2.toString()).isEqualTo("2015-11-01");
        final Object bin3 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, null).build()), EVENT3);
        System.out.println(bin3);
        softly.assertThat(bin3.toString()).isEqualTo("(Empty)");
    }

    @Test
    public void shouldCalc7BinStartDate() throws Exception {
        final Object bin1 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 7).build()), EVENT1);
        System.out.println(bin1);
        softly.assertThat(bin1.toString()).isEqualTo("2015-08-27 - 2015-09-02");
        final Object bin2 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 7).build()), EVENT2);
        System.out.println(bin2);
        softly.assertThat(bin2.toString()).isEqualTo("2015-10-29 - 2015-11-04");
        final Object bin3 = Attributes.get(CvotEndpointGroupByOptions.START_DATE.getAttribute(
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 7).build()), EVENT3);
        System.out.println(bin3);
        softly.assertThat(bin3.toString()).isEqualTo("(Empty)");
    }

}
