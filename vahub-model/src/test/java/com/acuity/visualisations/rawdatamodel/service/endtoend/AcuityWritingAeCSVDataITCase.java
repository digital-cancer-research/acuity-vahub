package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_SMALL_ACUITY_DATASETS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityWritingAeCSVDataITCase {

    @Autowired
    private AeService aeService;

    @Test
    public void shouldWriteCorrectColumns() throws IOException {
        // Given
        Writer writer = new StringWriter();

        // When
        aeService.writeAllDetailsOnDemandCsv(DUMMY_SMALL_ACUITY_DATASETS, writer, AeFilters.empty(), PopulationFilters.empty());

        // Then
        String[] firstLine = writer.toString().split("\n")[0].split(",");
        assertThat(firstLine).containsExactly(
                "Study id",
                "Study part",
                "Subject id",
                "Preferred term",
                "High level term",
                "System organ class",
                "Special interest group",
                "Max severity",
                "Start date",
                "End date",
                "Days on study at ae start",
                "Days on study at ae end",
                "Duration",
                "Serious",
                "Action taken",
                "Causality",
                "Description",
                "Outcome",
                "Required treatment",
                "Caused subject withdrawal",
                "Dose limiting toxicity",
                "Time point of dose limiting toxicity",
                "Immune mediated ae",
                "Infusion reaction ae");
    }

    @Test
    public void shouldWriteCorrectDateFormat() throws IOException {
        //Given
        Writer writer = new StringWriter();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        //When
        aeService.writeAllDetailsOnDemandCsv(DUMMY_SMALL_ACUITY_DATASETS, writer, AeFilters.empty(), PopulationFilters.empty());

        //Then
        //bad trick, fails on rows having commas in "quoted" column values like "RESPIRATORY, THORACIC AND MEDIASTINAL DISORDERS"
        String[] data = writer.toString().split("\n")[5].split(",");
        try {
            dateFormat.parse(data[8]);
            dateFormat.parse(data[9]);
        } catch (ParseException e) {
            fail("Wrong date format.");
        }
    }

    @Test
    public void shouldWriteAesTableToCsvWithCorrectHeaders() {
        // Given
        Writer writer = new StringWriter();

        //When
        aeService.writeAesTableToCsv(DUMMY_ACUITY_DATASETS, AeGroupByOptions.PT, AeFilters.empty(), PopulationFilters.empty(), writer);

        //Then
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            String[] line = writer.toString().split("\n")[i].split(",");
            for (String str : line) {
                headers.add(str);
            }
        }
        assertThat(headers).isNotEmpty();
        assertThat(headers).containsExactly("Term",
                "Max. severity grade experienced",
                "\"All",
                "Number of subjects\"");
    }

    @Test
    public void shouldWriteAesTableToCsvWithCorrectData() {
        // Given
        Writer writer = new StringWriter();

        //When
        aeService.writeAesTableToCsv(DUMMY_ACUITY_DATASETS, AeGroupByOptions.PT, AeFilters.empty(), PopulationFilters.empty(), writer);

        //Then
        List<String> headers = new ArrayList<>();
        for (int i = 2; i < 13; i++) {
            String[] line = writer.toString().split("\n")[i].split(",");
            for (String str : line) {
                headers.add(str);
            }
        }
        assertThat(headers).isNotEmpty();
        assertThat(headers).containsExactly("ABDOMINAL DISCOMFORT",
                "",
                "CTC Grade 1",
                "1 (0.81%)",
                "",
                "No incidence",
                "123 (99.19%)",
                "ABDOMINAL DISTENSION",
                "",
                "CTC Grade 2",
                "1 (0.81%)",
                "",
                "No incidence",
                "123 (99.19%)",
                "ABDOMINAL PAIN",
                "",
                "CTC Grade 1",
                "12 (9.68%)",
                "",
                "CTC Grade 2",
                "3 (2.42%)",
                "",
                "CTC Grade 3",
                "1 (0.81%)",
                "",
                "No incidence",
                "108 (87.10%)");
    }
}
