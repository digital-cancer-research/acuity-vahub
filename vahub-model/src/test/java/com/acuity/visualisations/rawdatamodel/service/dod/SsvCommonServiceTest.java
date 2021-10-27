package com.acuity.visualisations.rawdatamodel.service.dod;

import com.acuity.visualisations.rawdatamodel.generators.SubjectGenerator;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class SsvCommonServiceTest {

    private SsvCommonService ssvCommonService = new SsvCommonService();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetSsv() throws Exception {
        final List<Subject> subjects = SubjectGenerator.generateSubjectListOfFiveWithSubjectIds();
        final List<Map<String, String>> ssvData = ssvCommonService.getColumnData(DatasetType.ACUITY, subjects);
        softly.assertThat(ssvData).hasSize(subjects.size());
        for (int i = 0; i < subjects.size(); i++) {
            final Subject subject = subjects.get(i);
            final Map<String, String> dod = ssvData.get(i);
            softly.assertThat(subject.getSex()).isEqualTo(dod.get("sex"));
            softly.assertThat(subject.getRace()).isEqualTo(dod.get("race"));
            softly.assertThat(subject.getAge().toString()).isEqualTo(dod.get("age"));
            softly.assertThat(Double.parseDouble(subject.getWeight().toString())).isEqualTo(Double.parseDouble(dod.get("weight")));
            softly.assertThat(Double.parseDouble(subject.getHeight().toString())).isEqualTo(Double.parseDouble(dod.get("height")));
        }
    }

    @Test
    public void shouldGetSsvColumns() throws Exception {

        final Map<String, String> ssvColumns = ssvCommonService.getColumns(DatasetType.ACUITY, Subject.class);
        softly.assertThat(ssvColumns.keySet()).containsExactly(
                "sex",
                "race",
                "age",
                "weight",
                "height");
        softly.assertThat(ssvColumns.values()).containsExactly(
                "Sex",
                "Race",
                "Age",
                "Weight",
                "Height");
    }

}
