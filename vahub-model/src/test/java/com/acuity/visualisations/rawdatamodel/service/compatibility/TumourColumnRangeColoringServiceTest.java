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
