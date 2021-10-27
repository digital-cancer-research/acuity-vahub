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

import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import org.apache.commons.lang3.tuple.Triple;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.IntStream;

import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.COLORS;
import static java.util.stream.Collectors.toList;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class ExposureLineChartColoringServiceTest {

    @Autowired
    private ExposureLineChartColoringService exposureLineChartColoringService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private String cat1 = "cat1";
    private String cat2 = "cat2";

    @Test
    public void testGetColorVariousCategories() {

        List<String> colorByValues = IntStream.rangeClosed(0, 50).boxed().map(n -> "v" + n).collect(toList());

        List<String> colorsCategory1 = colorByValues.stream()
                .map(v -> exposureLineChartColoringService.getColor(v, cat1)).collect(toList());

        List<String> colorsCategory2 = colorByValues.stream()
                .map(v -> exposureLineChartColoringService.getColor(v, cat2)).collect(toList());

        for (int i = 0; i < COLORS.length; i++) {
            softly.assertThat(colorsCategory1.get(i)).isEqualTo(COLORS[i]);
            softly.assertThat(colorsCategory2.get(i)).isEqualTo(COLORS[i]);
        }

        colorsCategory1.stream().skip(COLORS.length).forEach(color ->
            softly.assertThat(exposureLineChartColoringService.isValid(hex2Rgb(color))).isTrue());
        colorsCategory2.stream().skip(COLORS.length).forEach(color ->
            softly.assertThat(exposureLineChartColoringService.isValid(hex2Rgb(color))).isTrue());

    }

    @Test
    public void testGetColorDefaultCategories() {
        softly.assertThat(exposureLineChartColoringService.getColor("None", cat1))
                .isEqualTo(ColoringService.Colors.SKYBLUE.getCode());
        softly.assertThat(exposureLineChartColoringService.getColor("None", cat2))
                .isEqualTo(ColoringService.Colors.SKYBLUE.getCode());
    }


    private static Triple<Integer, Integer, Integer> hex2Rgb(String colorStr) {
        return Triple.of(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }
}
