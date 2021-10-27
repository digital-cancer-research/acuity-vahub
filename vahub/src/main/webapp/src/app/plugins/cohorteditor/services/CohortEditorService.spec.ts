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
import {HttpClientTestingModule} from '@angular/common/http/testing';

import {CohortEditorService} from './CohortEditorService';
import {SessionEventService} from '../../../session/module';
import {AesFiltersModel, FilterEventService, PopulationFiltersModel} from '../../../filters/module';
import {
    MockDatasetViews,
    MockFilterEventService,
    MockFilterHttpService,
    MockFilterModel,
    MockFilterReloadService,
    MockSessionEventService,
    MockTimelineDispatcher,
    MockTrellisingDispatcher
} from '../../../common/MockClasses';
import {TrellisingDispatcher} from '../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {TimelineDispatcher} from '../../timeline/store/dispatcher/TimelineDispatcher';
import {FilterReloadService} from './FilterReloadService';
import {SaveCohortDto} from '../dto/SaveCohortDto';
import {FilterHttpService} from '../../../filters/http/FilterHttpService';
import {DatasetViews} from '../../../security/DatasetViews';
import {Observable} from 'rxjs/Observable';

describe('GIVEN CohortEditorService', () => {

    class MockPopulationFiltersModel {
        transformFiltersToServer(): void {

        }
    }

    class MockAesFiltersModel {
        transformFiltersToServer(): void {

        }
    }

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                CohortEditorService,
                HttpClient,
                {provide: PopulationFiltersModel, useClass: MockFilterModel},
                {provide: FilterReloadService, useClass: MockFilterReloadService},
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {provide: SessionEventService, useClass: MockSessionEventService},
                {provide: TrellisingDispatcher, useClass: MockTrellisingDispatcher},
                {provide: TimelineDispatcher, useClass: MockTimelineDispatcher}
            ]
        });
    });

    beforeEach(inject([SessionEventService], (service: SessionEventService) => {
        service.currentSelectedDatasets = <any> [{
            id: 99
        }];
    }));

    describe('WHEN a list of cohorts is requested from the server', () => {
        it('THEN a list of saved cohorts is returned', inject([HttpClient, CohortEditorService],
            (httpClient: HttpClient, service: CohortEditorService) => {
                service['session']['userDetails'] = {
                    userId: 'a user'
                };
                spyOn(httpClient, 'post').and.returnValue(Observable.of(getMockSavedFilterResponse()));

                service.getCohorts().subscribe((cohorts) => {
                    expect(cohorts).toEqual(getMockSavedFilterResponse());
                });
            }));
    });

    describe('WHEN a cohort is saved', () => {
        it('THEN the list is returned', inject([HttpClient, CohortEditorService],
            (httpClient: HttpClient, service: CohortEditorService) => {
                service['session']['userDetails'] = {
                    userId: 'a user'
                };
                spyOn(httpClient, 'post').and.returnValue(Observable.of(getMockSavedFilterResponse()));

                const dto = new SaveCohortDto(1, 'new cohort name', [], 101, 102,
                    <PopulationFiltersModel> new MockPopulationFiltersModel(), <AesFiltersModel> new MockAesFiltersModel());
                service.saveCohort(dto).subscribe((cohorts) => {
                    expect(cohorts).toEqual(getMockSavedFilterResponse());
                });
            }));
    });

    describe('WHEN an applied cohort is changed', () => {
        it('THEN the applied cohort is updated',
            inject([HttpClient, CohortEditorService, FilterEventService, PopulationFiltersModel],
                (httpClient: HttpClient, service: CohortEditorService, filterEventService: FilterEventService,
                 populationFiltersModel: PopulationFiltersModel) => {
                    service['session']['userDetails'] = {
                        userId: 'a user'
                    };
                    spyOn(populationFiltersModel, 'removeCohortFilter');
                    spyOn(populationFiltersModel, 'addCohortFilter');
                    spyOn(populationFiltersModel, 'toggleSubjectIdFilterVisibility');

                    spyOn(httpClient, 'post').and.returnValue(Observable.of(getMockSubjectListResponse()));

                    filterEventService.populationFilter.subscribe(() => {});
                    filterEventService.populationFilterSubjectCount.subscribe(() => {});

                    service.applyCohort(1, 'cohort 1').subscribe((cohorts) => {
                        expect(populationFiltersModel.removeCohortFilter).toHaveBeenCalledWith(['cohort 1'], false);
                        expect(populationFiltersModel.addCohortFilter).toHaveBeenCalledWith(getMockSubjectListResponse(), 'cohort 1');
                        expect(populationFiltersModel.addCohortFilter).toHaveBeenCalledWith(getMockSubjectListResponse(), 'cohort 1');
                    });
                }));
    });

    describe('WHEN a cohort is deleted', () => {
        it('THEN a list of saved cohorts is returned', inject([HttpClient, CohortEditorService, FilterEventService],
            (httpClient: HttpClient, service: CohortEditorService, filterEventService: FilterEventService) => {
                service['session']['userDetails'] = {
                    userId: 'a user'
                };
                spyOn(filterEventService, 'setPopulationFilter');
                spyOn(httpClient, 'post').and.returnValue(Observable.of(getMockSavedFilterResponse()));

                service.deleteCohort(123, 'a cohort').subscribe((cohorts) => {
                    expect(cohorts).toEqual(getMockSavedFilterResponse());
                });
            }));

        it('THEN the cohort is removed from the global population filters',
            inject([HttpClient, CohortEditorService, PopulationFiltersModel, FilterEventService],
                (httpClient: HttpClient, service: CohortEditorService, populationFiltersModel: PopulationFiltersModel,
                 filterEventService: FilterEventService) => {
                    populationFiltersModel.addCohortFilter(['subj-1'], 'a cohort');
                    spyOn(filterEventService, 'setPopulationFilter');

                    service.deleteCohort(123, 'a cohort').subscribe((cohorts) => {
                        expect(populationFiltersModel.removeCohortFilter).toHaveBeenCalled();
                    });
                }));

        it('THEN the global population filters are reset',
            inject([CohortEditorService, PopulationFiltersModel, FilterReloadService],
                (service: CohortEditorService, populationFiltersModel: PopulationFiltersModel,
                 filterReloadService: FilterReloadService) => {
                    populationFiltersModel.addCohortFilter(['subj-1'], 'a cohort');
                    spyOn(filterReloadService, 'resetFilters');

                    service.deleteCohort(123, 'a cohort').subscribe((cohorts) => {
                        expect(filterReloadService.resetFilters).toHaveBeenCalledWith();
                    });
                }));
    });

    describe('WHEN renaming an applied cohort', () => {
        it('THEN the cohort is renamed in the population filters',
            inject([CohortEditorService, PopulationFiltersModel],
                (service: CohortEditorService, populationFiltersModel: PopulationFiltersModel) => {
                    spyOn(populationFiltersModel, 'renameCohortIfApplied');
                    service.renameCohortIfApplied('a cohort', 'renamed cohort');
                    expect(populationFiltersModel.renameCohortIfApplied).toHaveBeenCalled();
                }));
    });

    describe('WHEN checking whether to rename a cohort', () => {
        it('THEN the cohort is not renamed if has not been applied',
            inject([CohortEditorService, PopulationFiltersModel],
                (service: CohortEditorService, populationFiltersModel: PopulationFiltersModel) => {
                    const newName = 'renamed cohort';
                    populationFiltersModel.addCohortFilter(['subj-1'], 'a cohort');

                    service.renameCohortIfApplied(undefined, newName);

                    // An exception should be thrown if this fails
                }));
    });
});

function getMockSavedFilterResponse(): any {
    return [{
        savedFilter: {
            id: 1,
            datasetsObject: {
                datasets: [{
                    id: 99
                }]
            }
        }
    }];
}

function getMockSubjectListResponse(): string[] {
    return ['subj-1', 'subj-2'];
}
