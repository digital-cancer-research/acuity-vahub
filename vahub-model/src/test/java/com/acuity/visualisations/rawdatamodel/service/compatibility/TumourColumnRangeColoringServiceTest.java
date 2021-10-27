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
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.COLORS_NO_GREEN;
import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE;
import static com.acuity.visualisations.rawdatamodel.util.Constants.ALL;
import static com.acuity.visualisations.rawdatamodel.util.Constants.DEFAULT_GROUP;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class TumourColumnRangeColoringServiceTest {

    @Autowired
    TumourColumnRangeColoringService tumourColumnRangeColoringService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testGetColor() {
        String category = "cat";
        softly.assertThat(tumourColumnRangeColoringService.getColor(DEFAULT_EMPTY_VALUE, category))
                .isEqualTo(AeColoringService.BLUE);
        softly.assertThat(tumourColumnRangeColoringService.getColor(ALL, category))
                .isEqualTo(ColoringService.Colors.LIGHTSEAGREEN.getCode());
        softly.assertThat(tumourColumnRangeColoringService.getColor(DEFAULT_GROUP, category))
                .isEqualTo(COLORS_NO_GREEN[9]);
        softly.assertThat(tumourColumnRangeColoringService.getColor("otherValue", category))
                .isEqualTo(COLORS_NO_GREEN[0]);

    }

}
