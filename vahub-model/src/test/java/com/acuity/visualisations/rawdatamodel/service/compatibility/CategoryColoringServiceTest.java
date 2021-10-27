package com.acuity.visualisations.rawdatamodel.service.compatibility;

import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.IntStream;

import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.COLORS;
import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE;
import static com.acuity.visualisations.rawdatamodel.util.Constants.ALL;
import static java.util.stream.Collectors.toList;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class CategoryColoringServiceTest {
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    @Qualifier("aeChordDiagramColoringService")
    CategoryColoringService categoryColoringService;

    @Test
   public void testGetColorByCategories(){
        String cat1 = "cat1";
        String cat2 = "cat2";

        List<String> colorByValues = IntStream.rangeClosed(0, 40).boxed().map(n -> "v" + n).collect(toList());

        List<String> colorsCategory1 = colorByValues.stream()
                .map(v -> categoryColoringService.getColor(v, cat1)).collect(toList());

        List<String> colorsCategory2 = colorByValues.stream()
                .map(v -> categoryColoringService.getColor(v, cat2)).collect(toList());

        for(int i = 0; i < COLORS.length; i++) {
            softly.assertThat(colorsCategory1.get(i)).isEqualTo(colorsCategory2.get(i));
        }
    }

    @Test
    public void testGetDefaultColors() {
        String category = "cat";
        softly.assertThat(categoryColoringService.getColor(DEFAULT_EMPTY_VALUE, category))
                .isEqualTo(ColoringService.Colors.GRAY.getCode());
        softly.assertThat(categoryColoringService.getColor(ALL, category))
                .isEqualTo(ColoringService.Colors.LIGHTSEAGREEN.getCode());
    }


}
