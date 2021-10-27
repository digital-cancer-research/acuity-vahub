package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.BiomarkerFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.event.BiomarkerService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.BiomarkerHeatMapColoringService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.BiomarkerGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.util.AlphanumComparator;
import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputHeatMapData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputHeatMapEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedHeatMap;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_2_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.BLACK;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.BLUE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.DIMGRAY;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.GREEN;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.ORANGE;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.groups.Tuple.tuple;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityBiomarkerServiceITCase {
    @Autowired
    private BiomarkerService biomarkerService;

    @Autowired
    private BiomarkerHeatMapColoringService biomarkerColoringService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private ChartGroupByOptionsFiltered<Biomarker, BiomarkerGroupByOptions> getHeatMapSettings() {
        ChartGroupByOptions<Biomarker, BiomarkerGroupByOptions> settings = ChartGroupByOptions.<Biomarker, BiomarkerGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                        BiomarkerGroupByOptions.GENE_PERCENTAGE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, BiomarkerGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.VALUE, BiomarkerGroupByOptions.BIOMARKER_DATA.getGroupByOptionAndParams())
                .build();
        return ChartGroupByOptionsFiltered.builder(settings).build();
    }

    private ChartGroupByOptionsFiltered<Biomarker, BiomarkerGroupByOptions> getHeatMapSelectionSettings() {
        ChartGroupByOptions<Biomarker, BiomarkerGroupByOptions> settings = ChartGroupByOptions.<Biomarker, BiomarkerGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                        BiomarkerGroupByOptions.GENE_PERCENTAGE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, BiomarkerGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .build();
        return ChartGroupByOptionsFiltered.builder(settings).build();
    }

    @Test
    public void shouldGetBiomarkerForHeatmapWithEmptyFilters() {
        List<TrellisedHeatMap<Biomarker, BiomarkerGroupByOptions>> heatMap =
                biomarkerService.getBiomarkerHeatMap(DUMMY_2_ACUITY_DATASETS, getHeatMapSettings(), BiomarkerFilters.empty(), PopulationFilters.empty());

        OutputHeatMapData data = heatMap.get(0).getData();

        softly.assertThat(data.getYCategories().size()).isEqualTo(39);
        softly.assertThat(data.getYCategories()).startsWith("1% ZNF217"); // bottom row
        softly.assertThat(data.getYCategories()).endsWith("8% TP53"); // top row

        softly.assertThat(data.getXCategories().size()).isEqualTo(31);
        softly.assertThat(data.getXCategories().get(0)).isEqualTo("E000010010");

        final List<OutputHeatMapEntry> e000010010entries = data.getEntries().stream()
                .filter(e -> e.getX() == data.getXCategories().indexOf("E000010010")).collect(Collectors.toList());
        softly.assertThat(e000010010entries).hasSize(6);
        softly.assertThat(e000010010entries)
                .extracting("value.subjectCode", "value.gene", "value.totalNumberOfAlterations", "y", "color")
                .containsExactlyInAnyOrder(tuple("E000010010", "PTCH2", 1, data.getYCategories().indexOf("1% PTCH2"), GREEN.getCode()),
                        tuple("E000010010", "APC", 1, data.getYCategories().indexOf("1% APC"), BLACK.getCode()),
                        tuple("E000010010", "RB1", 1, data.getYCategories().indexOf("2% RB1"), BLACK.getCode()),
                        tuple("E000010010", "ERBB2", 1, data.getYCategories().indexOf("2% ERBB2"), GREEN.getCode()),
                        tuple("E000010010", "TERT", 1, data.getYCategories().indexOf("6% TERT"), DIMGRAY.getCode()),
                        tuple("E000010010", "TP53", 1, data.getYCategories().indexOf("8% TP53"), BLACK.getCode())
                );
        softly.assertThat(e000010010entries.stream().map(b -> ((BiomarkerData) b.getValue()).getBiomarkerParameters())
                .flatMap(Collection::stream).collect(Collectors.toList())).extracting(
                "mutation", "somaticStatus", "aminoAcidChange", "copyNumberAlterationCopyNumber", "alleleFrequency")
                .containsExactlyInAnyOrder(tuple("Nonsynonymous mutation", "known", "E583K", null, 41),
                        tuple("Truncating", "likely", "S851fs*7", null, 64),
                        tuple("Truncating", "likely", "V144fs*9", null, 35),
                        tuple("Nonsynonymous mutation", "known", "S310Y", null, 40),
                        tuple("Other", "known", "promoter -124C>T", null, 58),
                        tuple("Truncating", "known", "Q331*", null, 77)
                );

        final List<OutputHeatMapEntry> e0000100128entries = data.getEntries().stream()
                .filter(e -> e.getX() == data.getXCategories().indexOf("E0000100128")).collect(Collectors.toList());
        softly.assertThat(e0000100128entries).hasSize(2);
        softly.assertThat(e0000100128entries).extracting("value.subjectCode", "value.gene",
                "value.totalNumberOfAlterations", "y", "color")
                .containsExactlyInAnyOrder(tuple("E0000100128", "CDKN2B", 1, data.getYCategories().indexOf("4% CDKN2B"), BLUE.getCode()),
                        tuple("E0000100128", "CDKN2A", 1, data.getYCategories().indexOf("4% CDKN2A"), BLUE.getCode())
                );
        softly.assertThat(e0000100128entries.stream().map(b -> ((BiomarkerData) b.getValue()).getBiomarkerParameters())
                .flatMap(Collection::stream).collect(Collectors.toList())).extracting(
                "mutation", "somaticStatus", "aminoAcidChange", "copyNumberAlterationCopyNumber", "alleleFrequency")
                .containsExactlyInAnyOrder(tuple("Deletion", "known", "-", 0, null), tuple("Deletion", "known", "-", 0, null)
                );

        data.getEntries().forEach(e -> {
            softly.assertThat(biomarkerColoringService.getColor(e.getValue().toString())).isNotNull();
        });
    }

    @Test
    public void shouldGetBiomarkerForHeatmapWithBiomarkerFilter() {
        BiomarkerFilters filters = new BiomarkerFilters();
        filters.setGene(new SetFilter<>(newArrayList("RB1")));
        List<TrellisedHeatMap<Biomarker, BiomarkerGroupByOptions>> heatMap =
                biomarkerService.getBiomarkerHeatMap(DUMMY_2_ACUITY_DATASETS, getHeatMapSettings(), filters, PopulationFilters.empty());

        OutputHeatMapData data = heatMap.get(0).getData();
        softly.assertThat(data.getXCategories().size()).isEqualTo(2);
        softly.assertThat(data.getXCategories().get(0)).isEqualTo("E000010010");
        softly.assertThat(data.getYCategories().size()).isEqualTo(1);
        softly.assertThat(data.getYCategories().get(0)).isEqualTo("2% RB1");
        softly.assertThat(data.getEntries().size()).isEqualTo(2);
    }

    @Test
    public void shouldGetBiomarkerForHeatmapWithPopulationFilter() {
        PopulationFilters filters = new PopulationFilters();
        filters.setAge(new RangeFilter<>(20, 30));
        List<TrellisedHeatMap<Biomarker, BiomarkerGroupByOptions>> heatMap =
                biomarkerService.getBiomarkerHeatMap(DUMMY_2_ACUITY_DATASETS, getHeatMapSettings(), BiomarkerFilters.empty(), filters);

        OutputHeatMapData data = heatMap.get(0).getData();
        softly.assertThat(data.getXCategories().size()).isEqualTo(7);
        softly.assertThat(data.getXCategories()
                .containsAll(newArrayList("E000010025", "E0000100105", "E0000100151", "E0000100242",
                        "E0000100271", "E0000100274", "E0000100276"))).isTrue();
        softly.assertThat(data.getYCategories().size()).isEqualTo(8);
        softly.assertThat(data.getEntries().size()).isEqualTo(11);
    }

    @Test
    public void shouldGetBiomarkerForHeatmapAscOrderedByX() {
        ChartGroupByOptions<Biomarker, BiomarkerGroupByOptions> settings = getHeatMapSettings().getSettings();

        List<TrellisedHeatMap<Biomarker, BiomarkerGroupByOptions>> heatMap =
                biomarkerService.getBiomarkerHeatMap(DUMMY_2_ACUITY_DATASETS,
                        ChartGroupByOptionsFiltered.builder(settings).build(),
                        BiomarkerFilters.empty(), PopulationFilters.empty());
        OutputHeatMapData data = heatMap.get(0).getData();
        Comparator<String> cmp = new AlphanumComparator<>();

        List<String> xCategories = data.getXCategories();
        for (int i = 1; i < data.getXCategories().size(); i++) {
            softly.assertThat(cmp.compare(xCategories.get(i - 1), xCategories.get(i)) <= 0).isTrue();
        }
    }

    // Y categories must be sorted on the plot by percentage DESC, than by gene name ASC;
    // Y categories are displayed from bottom to top, that's why sorting order is reversed
    @Test
    public void shouldGetBiomarkerForHeatmapOrderedByYByPercentageDescGeneAsc() {
        List<TrellisedHeatMap<Biomarker, BiomarkerGroupByOptions>> heatMap =
                biomarkerService.getBiomarkerHeatMap(DUMMY_2_ACUITY_DATASETS, getHeatMapSettings(), BiomarkerFilters.empty(), PopulationFilters.empty());
        OutputHeatMapData data = heatMap.get(0).getData();
        List<String> yCategories = data.getYCategories();

        softly.assertThat(yCategories).hasSize(39);
        softly.assertThat(yCategories.get(0)).isEqualTo("1% ZNF217");
        softly.assertThat(yCategories.get(38)).isEqualTo("8% TP53");
    }

    @Test
    public void shouldRoundGenePercentage() {
        PopulationFilters filters = new PopulationFilters();
        filters.setAge(new RangeFilter<>(10, 70));
        List<TrellisedHeatMap<Biomarker, BiomarkerGroupByOptions>> heatMap =
                biomarkerService.getBiomarkerHeatMap(DUMMY_2_ACUITY_DATASETS, getHeatMapSettings(), BiomarkerFilters.empty(), filters);
        OutputHeatMapData data = heatMap.get(0).getData();
        List<String> yCategories = data.getYCategories();

        softly.assertThat(yCategories).hasSize(39);
        softly.assertThat(data.getXCategories()).hasSize(31);
        softly.assertThat(yCategories.get(0)).isEqualTo("1% ZNF217"); // round down
        softly.assertThat(yCategories.get(35)).isEqualTo("4% CDKN2B"); // round up
    }


    @Test
    public void shouldHandleMultimutations() {
        BiomarkerFilters filters = new BiomarkerFilters();
        // subject E0000100200 has multimutation in gene ATM
        filters.setGene(new SetFilter<>(newArrayList("ATM")));
        List<TrellisedHeatMap<Biomarker, BiomarkerGroupByOptions>> heatMap =
                biomarkerService.getBiomarkerHeatMap(DUMMY_2_ACUITY_DATASETS, getHeatMapSettings(), filters, PopulationFilters.empty());

        OutputHeatMapData data = heatMap.get(0).getData();
        softly.assertThat(data.getXCategories()).containsExactly("E0000100200");
        softly.assertThat(data.getYCategories()).containsExactly("1% ATM");

        softly.assertThat(data.getEntries()).hasSize(1);

        softly.assertThat(data.getEntries())
                .extracting("value.subjectCode", "value.gene", "value.totalNumberOfAlterations", "color", "x", "y", "name")
                .contains(tuple("E0000100200", "ATM", 2, ORANGE.getCode(), 0, 0, "Splice"));
        softly.assertThat(data.getEntries().stream().map(b -> ((BiomarkerData) b.getValue()).getBiomarkerParameters())
                .flatMap(Collection::stream).collect(Collectors.toList())).extracting(
                "mutation", "somaticStatus", "aminoAcidChange", "copyNumberAlterationCopyNumber", "alleleFrequency")
                .containsExactlyInAnyOrder(tuple("Splice", "likely", "splice site 6976-2A>G", null, 46),
                        tuple("Truncating", "likely", "V997fs*2", null, 15));
    }

    private static class BiomarkerSelectionItem extends ChartSelectionItem<Biomarker, BiomarkerGroupByOptions> implements Comparable<BiomarkerSelectionItem> {

        BiomarkerSelectionItem(Map<BiomarkerGroupByOptions, Object> selectedTrellises, Map<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems) {
            super(selectedTrellises, selectedItems);
        }

        @Override
        public int compareTo(BiomarkerSelectionItem o) {
            if (o == null) {
                return -1;
            }
            if (this.equals(o)) {
                return 0;
            }
            return this.hashCode() > o.hashCode() ? 1 : -1;
        }
    }

    @Test
    public void shouldAcquireSelectionDetails() throws JsonProcessingException {

        ChartGroupByOptions<Biomarker, BiomarkerGroupByOptions> settings =
                new ChartGroupByOptions<>(getHeatMapSelectionSettings().getSettings().getOptions(), Collections.emptySet());
        Set<ChartSelectionItem<Biomarker, BiomarkerGroupByOptions>> items = new TreeSet<>();
        ChartSelection<Biomarker, BiomarkerGroupByOptions, ChartSelectionItem<Biomarker, BiomarkerGroupByOptions>> selection =
                new ChartSelection<>(settings, items);

        SelectionDetail selectionDetail =
                biomarkerService.getSelectionDetails(DUMMY_2_ACUITY_DATASETS, BiomarkerFilters.empty(), PopulationFilters.empty(), selection);
        softly.assertThat(selectionDetail.getEventIds()).hasSize(0);
        softly.assertThat(selectionDetail.getSubjectIds()).hasSize(0);
        softly.assertThat(selectionDetail.getTotalEvents()).isEqualTo(75);
        softly.assertThat(selectionDetail.getTotalSubjects()).isEqualTo(124);
        // trying to obtain selection
        Map<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, "E0000100213");
        selectedItems.put(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, "2% RB1");
        ChartSelectionItem<Biomarker, BiomarkerGroupByOptions> item = new BiomarkerSelectionItem(Collections.emptyMap(), selectedItems);
        items.add(item);

        selection = ChartSelection.of(settings, items);
        selectionDetail = biomarkerService.getSelectionDetails(DUMMY_2_ACUITY_DATASETS, BiomarkerFilters.empty(), PopulationFilters.empty(), selection);

        softly.assertThat(selectionDetail.getEventIds()).hasSize(1);
        softly.assertThat(selectionDetail.getSubjectIds()).hasSize(1);
        softly.assertThat(selectionDetail.getTotalEvents()).isEqualTo(75);
        softly.assertThat(selectionDetail.getTotalSubjects()).isEqualTo(124);
    }
}
