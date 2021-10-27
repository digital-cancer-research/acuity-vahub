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

import {TestBed, inject} from '@angular/core/testing';
import {Router} from '@angular/router';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {Location} from '@angular/common';
import {SpyLocation} from '@angular/common/testing';
import * as  _ from 'lodash';

import {SelectedFiltersComponent} from './SelectedFiltersComponent';
import {SelectedFiltersModel} from './SelectedFiltersModel';
import {PopulationFiltersModel} from '../dataTypes/population/PopulationFiltersModel';
import {CohortFiltersModel} from '../dataTypes/cohort/CohortFiltersModel';
import {CohortEditorService} from '../../plugins/cohorteditor/services/CohortEditorService';
import {FilterReloadService} from '../../plugins/cohorteditor/services/FilterReloadService';
import {AesFiltersModel} from '../dataTypes/aes/AesFiltersModel';
import {FilterEventService, ListFilterItemModel} from '../module';
import {TimelineTrackService} from '../../plugins/timeline/config/trackselection/TimelineTrackService';
import {
    MockRouter, MockFilterEventService, MockFiltersUtils, MockSessionEventService,
    MockFilterHttpService, MockTrellisingDispatcher
} from '../../common/MockClasses';
import {FilterId} from '../../common/trellising/store/ITrellising';
import {DatasetViews} from '../../security/DatasetViews';
import {StudyService} from '../../common/StudyService';
import {FiltersUtils} from '../utils/FiltersUtils';
import {FILTER_TYPE} from '../components/dtos';
import {TimelineDispatcher} from '../../plugins/timeline/store/dispatcher/TimelineDispatcher';
import {TrellisingDispatcher} from '../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {SessionEventService} from '../../session/event/SessionEventService';
import {FilterHttpService} from '../http/FilterHttpService';
import {Store} from '@ngrx/store';

describe('GIVEN SelectedFilterComponent', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                TimelineTrackService,
                CohortEditorService,
                FilterReloadService,
                CohortFiltersModel,
                TimelineDispatcher,
                PopulationFiltersModel,
                {provide: SessionEventService, useClass: MockSessionEventService},
                {provide: FiltersUtils, useClass: MockFiltersUtils},
                {provide: Location, useClass: SpyLocation},
                {provide: Router, useClass: MockRouter},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {provide: Store},
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                {provide: StudyService, useClass: StudyService},
                {provide: DatasetViews, useClass: DatasetViews, deps: [StudyService]},
                {provide: SelectedFiltersModel, useClass: SelectedFiltersModel},
                {provide: AesFiltersModel, useClass: AesFiltersModel, deps: [FilterEventService]},
                {provide: TrellisingDispatcher, useClass: MockTrellisingDispatcher},
                {
                    provide: SelectedFiltersComponent, useClass: SelectedFiltersComponent,
                    deps: [Router, FilterEventService,
                        SelectedFiltersModel, TimelineTrackService, DatasetViews, Location, FiltersUtils
                    ]
                }
            ]
        });
    });

    describe('WHEN on the Timeline page', () => {
        it('THEN the number of events is not hidden AND the event filter widget is visible',
            inject([SelectedFiltersComponent], (selectedFiltersComponent: SelectedFiltersComponent) => {
                spyOn(selectedFiltersComponent['location'], 'path').and.returnValue('plugins/timeline');
                selectedFiltersComponent.setEventFilterWidgetVisibility();

                expect(selectedFiltersComponent.showNumberOfEvents).toBe(false);
                expect(selectedFiltersComponent.showEventFilterWidget).toBe(true);
            })
        );
    });

    describe('WHEN on the Single Subject view', () => {
        describe('AND on the summary page', () => {
            it('THEN the number of events is hidden AND the event filter widget is hidden',
                inject([SelectedFiltersComponent], (selectedFiltersComponent: SelectedFiltersComponent) => {
                    spyOn(selectedFiltersComponent['location'], 'path').and.returnValue('plugins/singlesubject/summary-tab');
                    selectedFiltersComponent.setEventFilterWidgetVisibility();

                    expect(selectedFiltersComponent.showNumberOfEvents).toBe(false);
                    expect(selectedFiltersComponent.showEventFilterWidget).toBe(false);
                })
            );
        });

        describe('AND not on the summary page', () => {
            it('THEN the number of events is hidden AND the event filter widget is hidden',
                inject([SelectedFiltersComponent], (selectedFiltersComponent: SelectedFiltersComponent) => {
                    spyOn(selectedFiltersComponent['location'], 'path').and.returnValue('plugins/singlesubject/aes');
                    selectedFiltersComponent.setEventFilterWidgetVisibility();

                    expect(selectedFiltersComponent.showNumberOfEvents).toBe(false);
                    expect(selectedFiltersComponent.showEventFilterWidget).toBe(true);
                })
            );
        });
    });

    describe('WHEN not on the Timeline page, Single Subject page or Lung Function page', () => {
        describe('AND not on the Population summary page', () => {
            it('THEN the number of events is visible AND the event filter widget is visible',
                inject([SelectedFiltersComponent], (selectedFiltersComponent: SelectedFiltersComponent) => {
                    spyOn(selectedFiltersComponent['location'], 'path').and.returnValue('plugins/aes/subject-counts');
                    selectedFiltersComponent.setEventFilterWidgetVisibility();

                    expect(selectedFiltersComponent.showNumberOfEvents).toBe(true);
                    expect(selectedFiltersComponent.showEventFilterWidget).toBe(true);
                })
            );
        });

        describe('AND on the Population summary page', () => {
            it('THEN the event filter widget is not visible AND the event filter widget is hidden',
                inject([SelectedFiltersComponent], (selectedFiltersComponent: SelectedFiltersComponent) => {
                    spyOn(selectedFiltersComponent['location'], 'path').and.returnValue('plugins/population-summary/summary-plot');
                    selectedFiltersComponent.setEventFilterWidgetVisibility();

                    expect(selectedFiltersComponent.showNumberOfEvents).toBe(true);
                    expect(selectedFiltersComponent.showEventFilterWidget).toBe(false);
                })
            );
        });

        describe('AND on the Cohort Editor page', () => {
            it('THEN the event filter widget is visible',
                inject([SelectedFiltersComponent], (selectedFiltersComponent: SelectedFiltersComponent) => {
                    spyOn(selectedFiltersComponent['location'], 'path').and.returnValue('plugins/cohort-editor');
                    selectedFiltersComponent.setEventFilterWidgetVisibility();

                    expect(selectedFiltersComponent.showNumberOfEvents).toBe(false);
                    expect(selectedFiltersComponent.showEventFilterWidget).toBe(true);
                })
            );
        });
    });

    describe('WHEN the population filters change', () => {

        describe('AND no subjects are filtered out', () => {

            it('THEN the subject total is displayed in the population widget',
                inject([SelectedFiltersComponent, FilterEventService, DatasetViews],
                    (selectedFiltersComponent: SelectedFiltersComponent, filterEventService: FilterEventService,
                     datasetViews: DatasetViews) => {

                        spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('labs');
                        spyOn(datasetViews, 'getItemsCount').and.returnValue(10);

                        selectedFiltersComponent.ngOnInit();

                        filterEventService.populationFilterSubjectCount.next(10);

                        const result = selectedFiltersComponent.selectedFiltersModel.numberOfSubjects;

                        expect(result).toEqual('(All) 10 of 10');

                    })
            );

            describe('AND on the Cohort Editor page', () => {
                it('THEN the subject total is displayed in the population widget',
                    inject([SelectedFiltersComponent, FilterEventService, DatasetViews],
                        (selectedFiltersComponent: SelectedFiltersComponent, filterEventService: FilterEventService,
                         datasetViews: DatasetViews) => {

                            spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('labs');
                            spyOn(datasetViews, 'getItemsCount').and.returnValue(10);
                            selectedFiltersComponent.selectedFiltersModel.totalNumberOfSubjects = 9;

                            selectedFiltersComponent.ngOnInit();

                            filterEventService.populationFilterSubjectCount.next(10);

                            const result = selectedFiltersComponent.selectedFiltersModel.numberOfSubjects;

                            expect(result).toEqual('(All) 10 of 10');

                        })
                );
            });
        });

        describe('AND subjects are filtered out', () => {

            it('THEN the correct number of subjects is displayed in the population widget',
                inject([SelectedFiltersComponent, FilterEventService, DatasetViews],
                    (selectedFiltersComponent: SelectedFiltersComponent, filterEventService: FilterEventService,
                     datasetViews: DatasetViews) => {
                        spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('population');
                        spyOn(datasetViews, 'getItemsCount').and.returnValue(10);
                        selectedFiltersComponent.selectedFiltersModel.selectedPopulationFilters = [{subjectIds: ['subj-1']}];

                        selectedFiltersComponent.ngOnInit();

                        filterEventService.populationFilterSubjectCount.next(8);

                        const result = selectedFiltersComponent.selectedFiltersModel.numberOfSubjects;

                        expect(result).toEqual('8 of 10');
                    })
            );
        });
    });

    describe('WHEN the event filters change', () => {

        describe('AND no events are filtered out', () => {

            it('THEN the subject total is displayed in the event widget',
                inject([SelectedFiltersComponent, FilterEventService, DatasetViews],
                    (selectedFiltersComponent: SelectedFiltersComponent, filterEventService: FilterEventService,
                     datasetViews: DatasetViews) => {
                        spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('labs');
                        spyOn(datasetViews, 'getItemsCount').and.returnValue(10);

                        selectedFiltersComponent.ngOnInit();

                        filterEventService.eventFilterEventCount.next(10);

                        const result = selectedFiltersComponent.selectedFiltersModel.numberOfEvents;

                        expect(result).toEqual('(All) 10 of 10');
                    })
            );
        });

        describe('AND events are filtered out', () => {

            it('THEN the correct number of events is displayed in the event widget',
                inject([SelectedFiltersComponent, FilterEventService, DatasetViews],
                    (selectedFiltersComponent: SelectedFiltersComponent, filterEventService: FilterEventService,
                     datasetViews: DatasetViews) => {
                        spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('labs');
                        spyOn(datasetViews, 'getItemsCount').and.returnValue(10);

                        selectedFiltersComponent.ngOnInit();

                        filterEventService.eventFilterEventCount.next(8);

                        const result = selectedFiltersComponent.selectedFiltersModel.numberOfEvents;

                        expect(result).toEqual('8 of 10');
                    })
            );
        });
    });

    describe('WHEN filter details are toggled', () => {

        it('THEN filter details visibility is changed',
            inject([SelectedFiltersComponent],
                (selectedFiltersComponent: SelectedFiltersComponent) => {
                    spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('labs');

                    selectedFiltersComponent.ngOnInit();
                    const currentFilter = {
                        key: 'soc',
                        type: 1,
                        displayValues: ['ALBUMIN'],
                        values: ['ALBUMIN'],
                        displayName: 'SOC',
                        joinedFilterValues: 'ALBUMIN',
                        currentState: 'TIMELINE',
                        isEventFilter: false
                    };
                    const event = {
                        currentTarget: {
                            getBoundingClientRect: (): any => {
                                return {
                                    top: 10,
                                    left: 20,
                                    bottom: 30,
                                    right: 40,
                                    height: 50
                                };
                            }
                        }
                    };
                    selectedFiltersComponent.toggleFilterDetails(currentFilter, event, false);

                    expect(selectedFiltersComponent.filterDetailsVisible).toBeTruthy();
                    expect(selectedFiltersComponent.selectedFiltersModel.consideredFilter).toEqual(currentFilter);
                    expect(selectedFiltersComponent.detailsOffset).toEqual({top: 60, left: 20});
                })
        );
    });

    describe('WHEN filter details are closed', () => {

        it('THEN filter details visibility is changed to false',
            inject([SelectedFiltersComponent],
                (selectedFiltersComponent: SelectedFiltersComponent) => {
                    spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('labs');

                    selectedFiltersComponent.ngOnInit();
                    selectedFiltersComponent.closeDetails();
                    expect(selectedFiltersComponent.filterDetailsVisible).toBeFalsy();
                }));
    });

    describe('WHEN population filters panel is toggled', () => {
        describe('AND population widget is not expandable', () => {
            it('THEN population filter is not toggled',
                inject([SelectedFiltersComponent],
                    (selectedFiltersComponent: SelectedFiltersComponent) => {
                        spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('labs');

                        selectedFiltersComponent.ngOnInit();
                        selectedFiltersComponent.populationExpandCollapseEnabled = false;
                        const filterState = selectedFiltersComponent.populationFiltersCollapsed;
                        selectedFiltersComponent.togglePopulationFilters();
                        expect(selectedFiltersComponent.populationFiltersCollapsed).toEqual(filterState);
                    }));
        });
        describe('AND population widget is expandable', () => {
            describe('AND population widget is collapsed', () => {
                it('THEN population filter is toggled',
                    inject([SelectedFiltersComponent],
                        (selectedFiltersComponent: SelectedFiltersComponent) => {
                            spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('labs');

                            selectedFiltersComponent.ngOnInit();
                            selectedFiltersComponent.populationExpandCollapseEnabled = true;
                            selectedFiltersComponent.populationFiltersCollapsed = false;
                            const filterState = selectedFiltersComponent.populationFiltersCollapsed;
                            selectedFiltersComponent.togglePopulationFilters();
                            expect(selectedFiltersComponent.populationFiltersCollapsed).not.toEqual(filterState);
                            expect(selectedFiltersComponent.populationWidgetHeight).toEqual(48);
                        }));
            });
            describe('AND population widget is not collapsed', () => {
                it('THEN population filter is toggled',
                    inject([SelectedFiltersComponent],
                        (selectedFiltersComponent: SelectedFiltersComponent) => {
                            spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('labs');
                            spyOn(selectedFiltersComponent, 'getHeight').and.returnValue(80);

                            selectedFiltersComponent.ngOnInit();
                            selectedFiltersComponent.populationExpandCollapseEnabled = true;
                            selectedFiltersComponent.populationFiltersCollapsed = true;
                            const filterState = selectedFiltersComponent.populationFiltersCollapsed;
                            selectedFiltersComponent.togglePopulationFilters();
                            expect(selectedFiltersComponent.populationFiltersCollapsed).not.toEqual(filterState);
                            expect(selectedFiltersComponent.populationWidgetHeight).toEqual(80);
                        }));
            });
        });
    });

    describe('WHEN event filters panel is toggled', () => {
        describe('AND event widget is not expandable', () => {
            it('THEN event filter is not toggled',
                inject([SelectedFiltersComponent],
                    (selectedFiltersComponent: SelectedFiltersComponent) => {
                        spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('labs');

                        selectedFiltersComponent.ngOnInit();
                        selectedFiltersComponent.eventExpandCollapseEnabled = false;
                        const filterState = selectedFiltersComponent.eventFiltersCollapsed;
                        selectedFiltersComponent.toggleEventFilters();
                        expect(selectedFiltersComponent.eventFiltersCollapsed).toEqual(filterState);
                    }));
        });
        describe('AND event widget is expandable', () => {
            describe('AND event widget is collapsed', () => {
                it('THEN event filter is toggled',
                    inject([SelectedFiltersComponent],
                        (selectedFiltersComponent: SelectedFiltersComponent) => {
                            spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('labs');

                            selectedFiltersComponent.ngOnInit();
                            selectedFiltersComponent.eventExpandCollapseEnabled = true;
                            selectedFiltersComponent.eventFiltersCollapsed = false;
                            const filterState = selectedFiltersComponent.eventFiltersCollapsed;
                            selectedFiltersComponent.toggleEventFilters();
                            expect(selectedFiltersComponent.eventFiltersCollapsed).not.toEqual(filterState);
                            expect(selectedFiltersComponent.eventWidgetHeight).toEqual(48);
                        }));
            });
            describe('AND event widget is not collapsed', () => {
                it('THEN event filter is toggled',
                    inject([SelectedFiltersComponent],
                        (selectedFiltersComponent: SelectedFiltersComponent) => {
                            spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('labs');

                            spyOn(selectedFiltersComponent, 'getHeight').and.returnValue(80);

                            selectedFiltersComponent.ngOnInit();
                            selectedFiltersComponent.eventExpandCollapseEnabled = true;
                            selectedFiltersComponent.eventFiltersCollapsed = true;
                            const filterState = selectedFiltersComponent.eventFiltersCollapsed;
                            selectedFiltersComponent.toggleEventFilters();
                            expect(selectedFiltersComponent.eventFiltersCollapsed).not.toEqual(filterState);
                            expect(selectedFiltersComponent.eventWidgetHeight).toEqual(80);
                        }));
            });
        });
    });

    describe('WHEN remove filter item is clicked', () => {
        describe('AND population filter item is clicked', () => {
            it('THEN filter item is removed from selected population filters',
                inject([SelectedFiltersComponent, PopulationFiltersModel, FiltersUtils],
                    (selectedFiltersComponent: SelectedFiltersComponent,
                     populationFiltersModel: PopulationFiltersModel,
                     filtersUtils: FiltersUtils) => {
                        const filtersItem = {
                            key: 'studyIdentifier',
                            filterId: FilterId.POPULATION
                        };
                        spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('labs');
                        spyOn(filtersUtils, 'getFilterModelById').and.returnValue(populationFiltersModel);

                        spyOn(populationFiltersModel, 'getFilters');

                        selectedFiltersComponent.ngOnInit();
                        selectedFiltersComponent.selectedFiltersModel.selectedPopulationFilters = [filtersItem];
                        selectedFiltersComponent.removeFilterItem(filtersItem, true);
                        expect(selectedFiltersComponent.selectedFiltersModel.selectedPopulationFilters).toEqual([]);
                    }));
        });
        describe('AND a cohort filter item is clicked', () => {
            let filtersItem;

            beforeEach(inject([SelectedFiltersComponent, CohortFiltersModel, PopulationFiltersModel, FiltersUtils],
                (selectedFiltersComponent: SelectedFiltersComponent, cohortFiltersModel: CohortFiltersModel,
                 populationFiltersModel: PopulationFiltersModel, filtersUtils: FiltersUtils) => {
                    const cohortName = 'cohort name';
                    filtersItem = {
                        key: PopulationFiltersModel.COHORT_EDITOR_KEY + '--' + cohortName,
                        filterId: FilterId.POPULATION,
                        type: FILTER_TYPE.COHORT_EDITOR
                    };

                    (<ListFilterItemModel>_.find(populationFiltersModel.itemsModels,
                        {key: PopulationFiltersModel.SUBJECT_IDS_KEY})).selectedValues = ['subj-1'];
                    populationFiltersModel.addCohortFilter(['subj-1'], cohortName);

                    spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('labs');
                    spyOn(filtersUtils, 'getFilterModelById').and.callFake((filterId): any => {
                        if (filterId === FilterId.POPULATION) {
                            return <any>populationFiltersModel;
                        } else {
                            return <any>cohortFiltersModel;
                        }
                    });
                    spyOn(cohortFiltersModel, 'removeCohort');

                    spyOn(populationFiltersModel, 'getFilters');

                    selectedFiltersComponent.ngOnInit();
                    selectedFiltersComponent.selectedFiltersModel.selectedPopulationFilters = [filtersItem];
                    selectedFiltersComponent.removeFilterItem(filtersItem, true);
                }));

            it('THEN filter item is removed from selected population filters',
                inject([SelectedFiltersComponent], (selectedFiltersComponent: SelectedFiltersComponent) => {
                    expect(selectedFiltersComponent.selectedFiltersModel.selectedPopulationFilters).toEqual([]);
                }));

            it('THEN filter item is removed from the cohort filters menu',
                inject([CohortFiltersModel], (cohortFiltersModel: CohortFiltersModel) => {
                    expect(cohortFiltersModel.removeCohort).toHaveBeenCalled();
                }));

            it('THEN the subject ID filter is cleared', inject([SelectedFiltersComponent, PopulationFiltersModel],
                (selectedFiltersComponent: SelectedFiltersComponent, populationFiltersModel: PopulationFiltersModel) => {
                    const subjectIdFilter = (<ListFilterItemModel>_.find(populationFiltersModel.itemsModels,
                        {key: PopulationFiltersModel.SUBJECT_IDS_KEY})).selectedValues;
                    expect(subjectIdFilter).toEqual([]);
                }));
        });

        describe('AND event filter item is clicked', () => {
            it('THEN filter item is removed from selected event filters',
                inject([SelectedFiltersComponent, AesFiltersModel, FiltersUtils],
                    (selectedFiltersComponent: SelectedFiltersComponent, aesFiltersModel: AesFiltersModel,
                     filtersUtils: FiltersUtils) => {
                        const filtersItem = {
                            key: 'pt',
                            filterId: FilterId.AES
                        };
                        spyOn(selectedFiltersComponent, 'getEventTypeFromUrl').and.returnValue('labs');
                        spyOn(filtersUtils, 'getFilterModelById').and.returnValue(aesFiltersModel);
                        spyOn(aesFiltersModel, 'getFilters');

                        selectedFiltersComponent.ngOnInit();
                        selectedFiltersComponent.selectedFiltersModel.selectedEventFilters = [filtersItem];
                        selectedFiltersComponent.removeFilterItem(filtersItem, false);
                        expect(selectedFiltersComponent.selectedFiltersModel.selectedEventFilters).toEqual([]);
                    }));
        });
    });
});
