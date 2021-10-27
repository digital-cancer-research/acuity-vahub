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

package com.acuity.visualisations.rawdatamodel.trellis.extractor;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.VisitDescription;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.extractor.VisitDescriptionValueExtractor;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

public class VisitDescriptionValueExtractorTest {

    private VisitDescriptionValueExtractor extractor = new VisitDescriptionValueExtractor();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldExtractIntegerValue() {
        //Given
        VisitDescription visitDescription = new VisitDescription("visit 10");

        //When
        Integer result = extractor.extractFrom(visitDescription);

        //Then
        softly.assertThat(result).isEqualTo(10);
    }

    @Test
    public void shouldExtractNegativeIntegerValue() {
        //Given
        VisitDescription visitDescription = new VisitDescription("visit -1");

        //When
        Integer result = extractor.extractFrom(visitDescription);

        //Then
        softly.assertThat(result).isEqualTo(-1);
    }

    @Test
    public void shouldntExtractValue() {
        //Given
        VisitDescription visitDescription = new VisitDescription("some text");

        //When
        Integer result = extractor.extractFrom(visitDescription);

        //Then
        softly.assertThat(result).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void shouldntFailWithoutValue() {
        //Given
        VisitDescription visitDescription = new VisitDescription("");

        //When
        Integer result = extractor.extractFrom(visitDescription);

        //Then
        softly.assertThat(result).isEqualTo(Integer.MAX_VALUE);
    }
}
