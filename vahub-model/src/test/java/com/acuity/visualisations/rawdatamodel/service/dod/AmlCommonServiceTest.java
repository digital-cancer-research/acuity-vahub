package com.acuity.visualisations.rawdatamodel.service.dod;

import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.generators.AEGenerator.AE1;
import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import static com.google.common.collect.Lists.newArrayList;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AmlCommonServiceTest {
    
    private final static String[] ACUITY_COLUMNS_KEYS = {"studyId", "studyPart", "subjectId", "preferredTerm", "highLevelTerm",
            "systemOrganClass", "specialInterestGroup", "maxSeverity", "startDate", "endDate", "daysOnStudyAtAEStart", "daysOnStudyAtAEEnd",
            "duration", "serious", "actionTaken", "causality", "description", "requiredTreatment", "doseLimitingToxicity",
            "immuneMediatedAE", "aeOfSpecialInterest"};
    private final static String[] ACUITY_COLUMNS_NAMES = {"Study id", "Study part", "Subject id", "Preferred term",
            "High level term", "System organ class", "Special interest group", "Max severity", "Start date", "End date",
            "Days on study at ae start", "Days on study at ae end", "Duration", "Serious", "Action taken", "Causality",
            "Description", "Required treatment", "Dose limiting toxicity", "Immune mediated ae",
            "Ae of special interest"};
    private final static String[] DETECT_COLUMNS_KEYS = {"studyId", "studyPart", "subjectId", "preferredTerm",
            "highLevelTerm", "systemOrganClass", "specialInterestGroup", "maxSeverity", "startDate", "endDate", "daysOnStudyAtAEStart",
            "daysOnStudyAtAEEnd", "duration", "serious", "description", "requiredTreatment"};
    private final static String[] DETECT_COLUMNS_NAMES = {"Study id", "Study part", "Subject id", "Preferred term",
            "High level term", "System organ class", "Special interest group", "Max severity", "Start date", "End date",
            "Days on study at ae start", "Days on study at ae end", "Duration", "Serious", "Description",
            "Required treatment"};

    private AmlCommonService amlCommonService = new AmlCommonService();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetAcuityAMLData() throws Exception {
        final List<Map<String, String>> amlData = amlCommonService.getColumnData(DatasetType.ACUITY, newArrayList(AE1), false);
        softly.assertThat(amlData.get(0)).hasSize(ACUITY_COLUMNS_KEYS.length);
    }

    @Test
    public void shouldGetDetectAMLData() throws Exception {
        final List<Map<String, String>> amlData = amlCommonService.getColumnData(DatasetType.DETECT, newArrayList(AE1), false);
        softly.assertThat(amlData.get(0)).hasSize(DETECT_COLUMNS_KEYS.length );
    }

    @Test
    public void shouldGetAmlAcuityColumns() {
        final Map<String, String> amlColumns = amlCommonService.getColumns(DatasetType.ACUITY, Ae.class, AeRaw.class);
        softly.assertThat(amlColumns.keySet()).containsExactly(ACUITY_COLUMNS_KEYS);
        softly.assertThat(amlColumns.values()).containsExactly(ACUITY_COLUMNS_NAMES);
    }

    @Test
    public void shouldGetAmlDetectColumns() {
        final Map<String, String> amlColumns = amlCommonService.getColumns(DatasetType.DETECT, Ae.class, AeRaw.class);
        softly.assertThat(amlColumns.keySet()).containsExactly(DETECT_COLUMNS_KEYS);
        softly.assertThat(amlColumns.values()).containsExactly(DETECT_COLUMNS_NAMES);
    }

}
