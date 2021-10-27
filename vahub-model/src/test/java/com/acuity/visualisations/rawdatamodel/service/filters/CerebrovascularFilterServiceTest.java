package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.dataproviders.CerebrovascularDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.CerebrovascularFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.vo.CerebrovascularRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class CerebrovascularFilterServiceTest {

    @InjectMocks
    private CerebrovascularFilterService cerebrovascularFilterService;
    @Mock
    private CerebrovascularDatasetsDataProvider cerebrovascularDatasetsDataProvider;
    @Mock
    private PopulationRawDataFilterService subjectService;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();


    @Test
    public void shouldGetAvailableFiltersWithEmptyFilters() throws Exception {
        //Given
        List<Cerebrovascular> events = createListOfFour();
        final List<Subject> subjects = events.stream().map(e -> e.getSubject()).collect(Collectors.toList());
        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(subjects, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(subjects, subjects);

        when(subjectService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        //When
        CerebrovascularFilters result = (CerebrovascularFilters) cerebrovascularFilterService.getAvailableFilters(events,
                CerebrovascularFilters.empty(), subjects, PopulationFilters.empty());

        //Then
        softly.assertThat(result.getAeNumber().getValues()).containsExactlyInAnyOrder("E01-213", "E02-213", "E03-21", "E04-21");
        softly.assertThat(result.getComment().getValues()).containsExactlyInAnyOrder("Comment", "Comment2", "Comment3", "Comment4");
        softly.assertThat(result.getEventType().getValues().size()).isEqualTo(1);
        softly.assertThat(result.getEventType().getValues().contains(null)).isTrue();
        softly.assertThat(result.getEventTerm().getValues().size()).isEqualTo(1);
        softly.assertThat(result.getEventTerm().getValues().contains(null)).isTrue();
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetAvailableFilters() throws Exception {
        //Given
        List<Cerebrovascular> events = createListOfFour();
        final List<Subject> subjects = events.stream().map(e -> e.getSubject()).collect(Collectors.toList());
        CerebrovascularFilters filters = new CerebrovascularFilters();
        filters.setAeNumber(new SetFilter<>(newHashSet("E03-21")));

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(subjects, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(subjects, subjects);

        when(subjectService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        //When
        CerebrovascularFilters result =
                (CerebrovascularFilters) cerebrovascularFilterService.getAvailableFilters(events, filters, subjects, PopulationFilters.empty());

        //Then
        softly.assertThat(result.getAeNumber().getValues()).containsExactlyInAnyOrder("E03-21");
        softly.assertThat(result.getComment().getValues()).containsExactlyInAnyOrder("Comment3");
        softly.assertThat(result.getEventType().getValues().size()).isEqualTo(1);
        softly.assertThat(result.getEventType().getValues().contains(null)).isTrue();
        softly.assertThat(result.getEventTerm().getValues().size()).isEqualTo(1);
        softly.assertThat(result.getEventTerm().getValues().contains(null)).isTrue();
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetAvailableFiltersImplWithEmptyFilters() {
        //Given
        List<Cerebrovascular> cerebrovasculars = createListOfFour();
        FilterQuery<Cerebrovascular> query =
                new FilterQuery<>(cerebrovasculars, new CerebrovascularFilters(), newArrayList(), new PopulationFilters());
        FilterResult<Cerebrovascular> filterResult = new FilterResult<>(query);
        filterResult.withResults(cerebrovasculars, cerebrovasculars);

        //When
        CerebrovascularFilters result = cerebrovascularFilterService.getAvailableFiltersImpl(filterResult);

        //Then
        softly.assertThat(result.getAeNumber().getValues()).containsExactlyInAnyOrder("E01-213", "E02-213", "E03-21", "E04-21");
        softly.assertThat(result.getComment().getValues()).containsExactlyInAnyOrder("Comment", "Comment2", "Comment3", "Comment4");
        softly.assertThat(result.getEventType().getValues().size()).isEqualTo(1);
        softly.assertThat(result.getEventType().getValues().contains(null)).isTrue();
        softly.assertThat(result.getEventTerm().getValues().size()).isEqualTo(1);
        softly.assertThat(result.getEventTerm().getValues().contains(null)).isTrue();
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetAvailableFiltersImpl() {
        //Given
        List<Cerebrovascular> events = createListOfFour();

        CerebrovascularFilters filters = new CerebrovascularFilters();
        filters.setAeNumber(new SetFilter<>(newHashSet("E01-213")));

        FilterQuery<Cerebrovascular> query =
                new FilterQuery<>(events, filters, newArrayList(), new PopulationFilters());
        FilterResult<Cerebrovascular> filterResult = new FilterResult<>(query);
        filterResult.withResults(events, events);

        final List<Subject> subjects = events.stream().map(e -> e.getSubject()).collect(Collectors.toList());

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(subjects, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(subjects, subjects);

        when(subjectService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        //When
        final FilterResult<Cerebrovascular> res = cerebrovascularFilterService.query(query);
        CerebrovascularFilters result = cerebrovascularFilterService.getAvailableFiltersImpl(res);

        //Then
        softly.assertThat(result.getAeNumber().getValues()).containsExactlyInAnyOrder("E01-213");
        softly.assertThat(result.getComment().getValues()).containsExactlyInAnyOrder("Comment");
        softly.assertThat(result.getEventType().getValues().size()).isEqualTo(1);
        softly.assertThat(result.getEventType().getValues().contains(null)).isTrue();
        softly.assertThat(result.getEventTerm().getValues().size()).isEqualTo(1);
        softly.assertThat(result.getEventTerm().getValues().contains(null)).isTrue();
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    private List<Cerebrovascular> createListOfFour() {
        Cerebrovascular cerebrovascular1 = new Cerebrovascular(CerebrovascularRaw.builder().aeNumber(213).comment("Comment").build(),
                Subject.builder().subjectId("1").subjectCode("E01").build());
        Cerebrovascular cerebrovascular2 = new Cerebrovascular(CerebrovascularRaw.builder().aeNumber(213).comment("Comment2").build(),
                Subject.builder().subjectId("2").subjectCode("E02").build());
        Cerebrovascular cerebrovascular3 = new Cerebrovascular(CerebrovascularRaw.builder().aeNumber(21).comment("Comment3").build(),
                Subject.builder().subjectId("3").subjectCode("E03").build());
        Cerebrovascular cerebrovascular4 = new Cerebrovascular(CerebrovascularRaw.builder().aeNumber(21).comment("Comment4").build(),
                Subject.builder().subjectId("4").subjectCode("E04").build());
        return newArrayList(cerebrovascular1, cerebrovascular2, cerebrovascular3, cerebrovascular4);
    }
}
