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

import {inject, TestBed} from '@angular/core/testing';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {Store} from '@ngrx/store';

import {CohortFiltersModel} from './CohortFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {CohortEditorService} from '../../../plugins/cohorteditor/services/CohortEditorService';
import {FilterReloadService} from '../../../plugins/cohorteditor/services/FilterReloadService';
import {TimelineDispatcher} from '../../../plugins/timeline/store/dispatcher/TimelineDispatcher';
import {ListFilterItemModel} from '../../components/list/ListFilterItemModel';
import {SessionEventService} from '../../../session/event/SessionEventService';
import {MockFilterHttpService, MockHttpClient, MockSessionEventService} from '../../../common/MockClasses';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {DatasetViews} from '../../../security/DatasetViews';
import {TrellisingDispatcher} from '../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {TimelineConfigService} from '../../../common/trellising/store/services/TimelineConfigService';
import {LabsFiltersModel} from '../labs/LabsFiltersModel';
import {StudyService} from '../../../common/StudyService';

describe('GIVEN a CohortFiltersModel class', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                CohortFiltersModel,
                DatasetViews,
                CohortEditorService,
                LabsFiltersModel,
                StudyService,
                PopulationFiltersModel,
                FilterReloadService,
                FilterEventService,
                TimelineDispatcher,
                TrellisingDispatcher,
                TimelineConfigService,
                HttpClient,
                {provide: Store},
                {provide: SessionEventService, useClass: MockSessionEventService},
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                {provide: HttpClient, useClass: MockHttpClient}
            ]
        });
    });

    beforeEach(inject([CohortFiltersModel, CohortEditorService, PopulationFiltersModel],
        (cohortFiltersModel: CohortFiltersModel, cohortEditorService: CohortEditorService,
         populationFiltersModel: PopulationFiltersModel) => {
            spyOn(cohortEditorService, 'getCohorts').and.returnValue(Observable.of([
                {
                    savedFilter: {
                        name: 'cohort 1',
                        id: 99,
                    }
                },
                {
                    savedFilter: {
                        name: 'cohort 2',
                        id: 101,
                    }
                }
            ]));
            spyOn(populationFiltersModel, 'getFilters');

            cohortFiltersModel['_getFiltersImpl']();
        }));

    describe('WHEN initialising', () => {

        it('THEN should populate a list with user\'s cohorts', inject([CohortFiltersModel],
            (cohortFiltersModel: CohortFiltersModel) => {
                expect((<ListFilterItemModel> cohortFiltersModel.itemsModels[0]).availableValues).toEqual(['cohort 1', 'cohort 2']);
            }));
    });

    describe('WHEN adding a selected cohort ', () => {

        const cohortName = 'a cohort';

        beforeEach(inject([CohortFiltersModel, PopulationFiltersModel],
            (cohortFiltersModel: CohortFiltersModel, populationFiltersModel: PopulationFiltersModel) => {
                populationFiltersModel.addCohortFilter(['subj-1'], cohortName);
                cohortFiltersModel.addSelectedValue(cohortName);
            }));

        it('THEN should add to selected values', inject([CohortFiltersModel], (cohortFiltersModel: CohortFiltersModel) => {
            expect((<ListFilterItemModel> cohortFiltersModel.itemsModels[0]).selectedValues).toEqual([cohortName]);
        }));

        it('THEN should update number of selected filters', inject([CohortFiltersModel], (cohortFiltersModel: CohortFiltersModel) => {
            expect((<ListFilterItemModel> cohortFiltersModel.itemsModels[0]).numberOfSelectedFilters).toBe(1);
        }));

        it('THEN should not add cohorts that are already applied',
            inject([CohortFiltersModel], (cohortFiltersModel: CohortFiltersModel) => {
                cohortFiltersModel.addSelectedValue(cohortName);
                expect((<ListFilterItemModel> cohortFiltersModel.itemsModels[0]).selectedValues.length).toBe(1);
                expect((<ListFilterItemModel> cohortFiltersModel.itemsModels[0]).numberOfSelectedFilters).toBe(1);
            }));
    });

    describe('WHEN setting the values', () => {

        beforeEach(inject([CohortFiltersModel, PopulationFiltersModel],
            (cohortFiltersModel: CohortFiltersModel, populationFiltersModel: PopulationFiltersModel) => {
                const cohortsToSet = <any> [{
                    savedFilter: {
                        name: 'cohort 1',
                        id: 99,
                    }
                },
                    {
                        savedFilter: {
                            name: 'cohort 2',
                            id: 101,
                        }
                    }];
                populationFiltersModel.addCohortFilter(['subj-1'], 'cohort 1');
                cohortFiltersModel.setItems(cohortsToSet);
            }));

        it('THEN should update number of selected filters', inject([CohortFiltersModel], (cohortFiltersModel: CohortFiltersModel) => {
            expect((<ListFilterItemModel> cohortFiltersModel.itemsModels[0]).numberOfSelectedFilters).toBe(1);
        }));
    });

    describe('WHEN resetting', () => {

        it('THEN the cohorts are removed from the population filter', inject([CohortFiltersModel, PopulationFiltersModel],
            (cohortFiltersModel: CohortFiltersModel, populationFiltersModel: PopulationFiltersModel) => {
                populationFiltersModel.addCohortFilter(['subj-1'], 'a cohort name');
                cohortFiltersModel.itemsModels[0].setSelectedValues([]);

                cohortFiltersModel['_getFiltersImpl']();

                expect(populationFiltersModel.getCohortEditorFilters().length).toBe(0);
            }));
    });

    describe('WHEN applying selected cohorts', () => {
        it('THEN cohorts are added to the population filter', inject([CohortFiltersModel, CohortEditorService],
            (cohortFiltersModel: CohortFiltersModel, cohortEditorService: CohortEditorService) => {
                cohortFiltersModel.itemsModels[0].setSelectedValues({values: ['cohort 1']});
                spyOn(cohortEditorService, 'applyCohort').and.returnValue(Observable.of({}));

                cohortFiltersModel['_getFiltersImpl']();

                expect(cohortEditorService.applyCohort).toHaveBeenCalledWith(99, 'cohort 1');
            }));
    });

    describe('WHEN applying unselected cohorts', () => {
        it('THEN unselected cohorts are removed from the population filter',
            inject([CohortFiltersModel, CohortEditorService, PopulationFiltersModel],
                (cohortFiltersModel: CohortFiltersModel, cohortEditorService: CohortEditorService,
                 populationFiltersModel: PopulationFiltersModel) => {
                    populationFiltersModel.addCohortFilter(['subj-1'], 'cohort 1');
                    populationFiltersModel.addCohortFilter(['subj-2'], 'cohort 2');
                    cohortFiltersModel.itemsModels[0].setSelectedValues({values: ['cohort 1']});
                    spyOn(cohortEditorService, 'applyCohort');

                    cohortFiltersModel['_getFiltersImpl']();

                    expect(populationFiltersModel.getCohortEditorFilters().length).toBe(1);
                    expect(populationFiltersModel.getCohortEditorFilters()[0].displayName).toBe('cohort 1');
                }));
    });

});
