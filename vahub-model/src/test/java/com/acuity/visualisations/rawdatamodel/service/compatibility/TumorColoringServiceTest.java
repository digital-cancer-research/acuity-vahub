package com.acuity.visualisations.rawdatamodel.service.compatibility;

import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.BLACK;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.BLUE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.GRAY;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.PURPLE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.RED;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.WHITE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.YELLOW;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class TumorColoringServiceTest {

    @Autowired
    TumourChartColoringService tumourChartColoringService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testGetColor() {
        softly.assertThat(tumourChartColoringService.getColor("Missing Target Lesions")).isEqualTo(YELLOW.getCode());
        softly.assertThat(tumourChartColoringService.getColor("Partial Response")).isEqualTo(BLUE.getCode());
        softly.assertThat(tumourChartColoringService.getColor("Stable Disease")).isEqualTo(GRAY.getCode());
        softly.assertThat(tumourChartColoringService.getColor("Progressive Disease")).isEqualTo(RED.getCode());
        softly.assertThat(tumourChartColoringService.getColor("Not Evaluable")).isEqualTo(BLACK.getCode());
        softly.assertThat(tumourChartColoringService.getColor("No Assessment")).isEqualTo(BLACK.getCode());
        softly.assertThat(tumourChartColoringService.getColor("Complete Response")).isEqualTo(PURPLE.getCode());
        softly.assertThat(tumourChartColoringService.getColor("No Evidence of Disease")).isEqualTo(PURPLE.getCode());

        softly.assertThat(tumourChartColoringService.getColor("Something else")).isEqualTo(WHITE.getCode());
    }

}
