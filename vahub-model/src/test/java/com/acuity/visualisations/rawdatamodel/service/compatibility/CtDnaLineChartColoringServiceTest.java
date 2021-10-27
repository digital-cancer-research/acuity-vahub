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

import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.COLORS_NO_GREEN;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class CtDnaLineChartColoringServiceTest {

    @Autowired
    private CtDnaLineChartColoringService ctDnaLineChartColoringService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testGetColorVariousCategories() {

        String cat1 = "cat1";
        String cat2 = "cat2";

        List<String> colorByValues = IntStream.rangeClosed(0, 40).boxed().map(n -> "v" + n).collect(toList());

        List<String> colorsCategory1 = colorByValues.stream()
                .map(v -> ctDnaLineChartColoringService.getColor(v, cat1)).collect(toList());

        List<String> colorsCategory2 = colorByValues.stream()
                .map(v -> ctDnaLineChartColoringService.getColor(v, cat2)).collect(toList());


        for(int i = 0; i < COLORS_NO_GREEN.length; i++) {
            softly.assertThat(colorsCategory1.get(i)).isEqualTo(COLORS_NO_GREEN[i]);
            softly.assertThat(colorsCategory2.get(i)).isEqualTo(COLORS_NO_GREEN[i]);
        }

        colorsCategory1.stream().skip(COLORS_NO_GREEN.length).forEach(color ->
                softly.assertThat(ctDnaLineChartColoringService.isValid(hex2Rgb(color))).isTrue());

        colorsCategory2.stream().skip(COLORS_NO_GREEN.length).forEach(color ->
                softly.assertThat(ctDnaLineChartColoringService.isValid(hex2Rgb(color))).isTrue());

    }

    @Test
    public void testIsValidGreen() {
        List<String> greenShades = newArrayList("#38C659", "#18E849", "#135723", "#2BA848", "#00FF00");
        greenShades.forEach(color -> {
            softly.assertThat(ctDnaLineChartColoringService.isValid(hex2Rgb(color)))
                    .isFalse();
        });
    }

    @Test
    public void testIsValidRed() {
        List<String> redShades = newArrayList("#F31F1F", "#D22929", "#F34040", "#CB1212", "#FF0000");
        redShades.forEach(color -> {
            softly.assertThat(ctDnaLineChartColoringService.isValid(hex2Rgb(color)))
                    .isFalse();
        });
    }

    @Test
    public void testIsValidFaint() {
        List<String> faintShades = newArrayList("#F2EBEB", "#EFF0F2", "#FBFDEB", "#E7F3F3", "#FFFFFF");
        faintShades.forEach(color -> {
            softly.assertThat(ctDnaLineChartColoringService.isValid(hex2Rgb(color)))
                    .isFalse();
        });
    }

    private static Triple<Integer, Integer, Integer> hex2Rgb(String colorStr) {
        return Triple.of(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }
}
