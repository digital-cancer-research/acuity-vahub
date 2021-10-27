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
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {Observable} from 'rxjs/Observable';

import {PopulationFiltersModel} from './PopulationFiltersModel';
import {CheckListFilterItemModel, CohortFilterItemModel, ListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {MockDatasetViews, MockFilterEventService, MockSessionEventService} from '../../../common/MockClasses';
import {SessionEventService} from '../../../session/event/SessionEventService';
import {SessionHttpService} from '../../../session/http/SessionHttpService';
import {DatasetViews} from '../../../security/DatasetViews';
import {StudyService} from '../../../common/StudyService';
import * as  _ from 'lodash';
import {Store} from '@ngrx/store';

class MockFilterHttpService {
    getPopulationFiltersObservable(path: string, selectedPopulationFilters: any): Observable<any> {
        return Observable.from(<any>{
            matchedItemsCount: 1900,
            sex: {
                values: ['M', 'F']
            },
            subjectId: {
                values: ['DummyData-1004573274', 'DummyData-1004573266']
            }
        });
    }
}

describe('GIVEN a PopulationFiltersModel class', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                StudyService,
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: SessionEventService, useClass: MockSessionEventService},
                {provide: Store},
                SessionHttpService,
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                {provide: FilterEventService, useClass: MockFilterEventService},
                PopulationFiltersModel
            ]
        });
    });

    describe('WHEN constructing', () => {

        it('THEN it should have instance var set', inject([PopulationFiltersModel], (populationFiltersModel: PopulationFiltersModel) => {
            expect(populationFiltersModel.itemsModels.length).toBe(41);
            expect(populationFiltersModel.hasInitData).toBe(false);

            expect((<ListFilterItemModel>populationFiltersModel.itemsModels[3]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>populationFiltersModel.itemsModels[3]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>populationFiltersModel.itemsModels[3]).includeEmptyValues).toEqual(true);

            expect((<CheckListFilterItemModel>populationFiltersModel.itemsModels[2]).availableValues).toEqual([]);
            expect((<CheckListFilterItemModel>populationFiltersModel.itemsModels[2]).selectedValues).toEqual([]);
            expect((<CheckListFilterItemModel>populationFiltersModel.itemsModels[2]).initialValues).toEqual([]);
            expect((<CheckListFilterItemModel>populationFiltersModel.itemsModels[2]).includeEmptyValues).toEqual(true);

            expect((<ListFilterItemModel>populationFiltersModel.itemsModels[1]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>populationFiltersModel.itemsModels[1]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>populationFiltersModel.itemsModels[1]).includeEmptyValues).toEqual(true);
        }));
    });

    describe('WHEN transforming model to server', () => {

        it('THEN it should be empty', inject([PopulationFiltersModel], (populationFiltersModel: PopulationFiltersModel) => {

            expect(populationFiltersModel.transformFiltersToServer()).toEqual({});
        }));
    });

    describe('WHEN getting name', () => {
        it('THEN it should send event to popbe set to pop', inject([PopulationFiltersModel],
            (populationFiltersModel: PopulationFiltersModel) => {

                expect(populationFiltersModel.getName()).toEqual('pop');
            }));
    });

    describe('WHEN emitEvents', () => {

        it('THEN it should be set filterEventService.setPopulationFilter',
            inject([FilterEventService, PopulationFiltersModel], (filterEventService: FilterEventService,
                                                                  populationFiltersModel: PopulationFiltersModel) => {
                const validator = jasmine.createSpyObj('validator', ['called']);

                filterEventService.populationFilter.subscribe(
                    (object: any) => {
                        validator.called(object);
                    }
                );

                populationFiltersModel.emitEvent({});

                expect(validator.called).toHaveBeenCalledWith({});
            })
        );
    });

    describe('WHEN transforming model from server', () => {

        it('THEN it should be set correctly', inject([PopulationFiltersModel], (populationFiltersModel: PopulationFiltersModel) => {

            populationFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                sex: {
                    values: ['M']
                },
                subjectId: {
                    values: ['DummyData-1004573274']
                }
            });
            const sex: any = _.find(populationFiltersModel.itemsModels, {key: 'sex'});
            expect(sex.availableValues).toEqual(['M']);
            expect(sex.selectedValues).toEqual(['M']);
            expect(sex.initialValues).toEqual(['M']);
            expect(sex.includeEmptyValues).toEqual(true);
            const studyIdentifier: any = _.find(populationFiltersModel.itemsModels, {key: 'subjectId'});

            expect(studyIdentifier.availableValues).toEqual(['DummyData-1004573274']);
            expect(studyIdentifier.selectedValues).toEqual([]);
            expect(studyIdentifier.includeEmptyValues).toEqual(true);
        }));

        it('THEN it should get selected subjects ids', inject([PopulationFiltersModel],
            (populationFiltersModel: PopulationFiltersModel) => {

                populationFiltersModel.transformFiltersFromServer({
                    matchedItemsCount: 1900,
                    sex: {
                        values: ['M']
                    },
                    subjectId: {
                        values: ['DummyData-1004573274']
                    }
                });
                const subjectId: any = _.find(populationFiltersModel.itemsModels, {key: 'subjectId'});
                subjectId.selectedValues = ['DummyData-1004573274'];

                expect(populationFiltersModel.getSelectedSubjectIds()).toEqual(['DummyData-1004573274']);
                expect(populationFiltersModel.getSelectedValues(PopulationFiltersModel.SUBJECT_IDS_KEY)).toEqual(['DummyData-1004573274']);
            }));
    });

    describe('WHEN transforming model to server', () => {

        beforeEach(inject([PopulationFiltersModel], (populationFiltersModel: PopulationFiltersModel) => {
            populationFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                sex: {
                    values: ['M', 'F']
                },
                subjectId: {
                    values: ['DummyData-1004573274', 'DummyData-1004573266']
                }
            });
        }));

        it('THEN it should be set correctly', inject([PopulationFiltersModel], (populationFiltersModel: PopulationFiltersModel) => {
            const subjectId: any = _.find(populationFiltersModel.itemsModels, {key: 'subjectId'});
            const sex: any = _.find(populationFiltersModel.itemsModels, {key: 'sex'});

            sex.selectedValues = ['M'];
            subjectId.selectedValues = ['DummyData-1004573274'];
            sex.appliedSelectedValues = ['M'];
            subjectId.appliedSelectedValues = ['DummyData-1004573274'];

            const serverObject = populationFiltersModel.transformFiltersToServer();

            expect(serverObject.sex.values).toEqual(['M']);
            expect(serverObject.subjectId.values).toEqual(['DummyData-1004573274']);
        }));

        describe('AND there are subjects in the subject ID filter', () => {
            it('THEN sets the subjects to the Cohort subjects', inject([PopulationFiltersModel],
                (populationFiltersModel: PopulationFiltersModel) => {
                    populationFiltersModel.addCohortFilter(['Subj-1', 'Subj-2'], 'a cohort');
                    const subjectId: ListFilterItemModel = <ListFilterItemModel> _.find(populationFiltersModel.itemsModels,
                        {key: PopulationFiltersModel.SUBJECT_IDS_KEY});

                    subjectId.selectedValues = ['DummyData-1004573274', 'DummyData-1004573266'];
                    subjectId.appliedSelectedValues = ['DummyData-1004573274', 'DummyData-1004573266'];

                    const serverObject = populationFiltersModel.transformFiltersToServer();

                    expect(serverObject.subjectId.values).toEqual(['Subj-1', 'Subj-2']);
                }));

            describe('AND there are multiple cohorts with an intersection of cohorts', () => {
                it('THEN sets the subjects to the intersection of both cohorts', inject([PopulationFiltersModel],
                    (populationFiltersModel: PopulationFiltersModel) => {
                        populationFiltersModel.addCohortFilter(['Subj-1', 'Subj-2', 'Subj-3'], 'a cohort');
                        populationFiltersModel.addCohortFilter(['Subj-1', 'Subj-3'], 'an other cohort');

                        const serverObject = populationFiltersModel.transformFiltersToServer();

                        expect(serverObject.subjectId.values).toEqual(['Subj-1', 'Subj-3']);
                    }));
            });

            describe('AND there are multiple cohorts without an intersection of cohorts', () => {
                it('THEN sets the subjects to the intersection of both cohorts', inject([PopulationFiltersModel],
                    (populationFiltersModel: PopulationFiltersModel) => {
                        populationFiltersModel.addCohortFilter(['Subj-1', 'Subj-2', 'Subj-3'], 'a cohort');
                        populationFiltersModel.addCohortFilter(['Subj-4', 'Subj-5'], 'an other cohort');

                        const serverObject = populationFiltersModel.transformFiltersToServer();

                        expect(serverObject.subjectId.values).toEqual([PopulationFiltersModel.NO_INTERSECT_OF_SUBJECTS]);
                    }));
            });
        });

        describe('AND there are no subjects in the subject ID filter', () => {
            it('THEN it should add cohort subjects to subject ID filter', inject([PopulationFiltersModel],
                (populationFiltersModel: PopulationFiltersModel) => {
                    populationFiltersModel.addCohortFilter(['Subj-1', 'Subj-2'], 'a cohort');
                    const subjectId: ListFilterItemModel = <ListFilterItemModel> _.find(populationFiltersModel.itemsModels,
                        {key: PopulationFiltersModel.SUBJECT_IDS_KEY});
                    const cohortSubjects: ListFilterItemModel = <ListFilterItemModel> _.find(populationFiltersModel.itemsModels,
                        {key: PopulationFiltersModel.COHORT_EDITOR_KEY});

                    subjectId.selectedValues = [];

                    const serverObject = populationFiltersModel.transformFiltersToServer();

                    expect(serverObject.subjectId.values).toEqual(['Subj-1', 'Subj-2']);
                }));
        });
    });

    describe('WHEN resetting model', () => {

        it('THEN it should be reset correctly', inject([PopulationFiltersModel], (populationFiltersModel: PopulationFiltersModel) => {

            populationFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                sex: {
                    values: ['M', 'F']
                },
                subjectId: {
                    values: ['DummyData-1004573274', 'DummyData-1004573266']
                }
            });

            populationFiltersModel.reset();

            const serverObject = populationFiltersModel.transformFiltersToServer();

            expect(serverObject).toEqual({});
            const subjectId: any = _.find(populationFiltersModel.itemsModels, {key: 'subjectId'});
            const sex: any = _.find(populationFiltersModel.itemsModels, {key: 'sex'});

            expect(subjectId.selectedValues).toEqual([]);
            expect(subjectId.includeEmptyValues).toEqual(true);

            expect(sex.availableValues).toEqual([]);
            expect(sex.selectedValues).toEqual([]);
            expect(sex.initialValues).toEqual([]);
            expect(sex.includeEmptyValues).toEqual(true);
        }));
    });

    describe('WHEN setting the cohort filter', () => {
        const subjectIds = ['subj-1', 'subj-2'];
        const cohortName = 'a cohort';
        let cohortFilter: CohortFilterItemModel;

        beforeEach(inject([PopulationFiltersModel], (populationFiltersModel: PopulationFiltersModel) => {
            populationFiltersModel.addCohortFilter(subjectIds, cohortName);
            cohortFilter = <CohortFilterItemModel> _.find(populationFiltersModel.itemsModels,
                (item) => item.key.indexOf(PopulationFiltersModel.COHORT_EDITOR_KEY) > -1);
        }));

        it('THEN the subject IDs are set', () => {
            expect(cohortFilter.selectedValues).toEqual(subjectIds);
        });

        it('THEN the cohort name is set', () => {
            expect(cohortFilter.displayName).toEqual(cohortName);
        });
    });

    describe('WHEN renaming the applied cohort', () => {
        const subjectIds = ['subj-1', 'subj-2'];
        const cohortName = 'a cohort';
        const newName = 'renamed cohort';
        let cohortFilter: CohortFilterItemModel;

        beforeEach(inject([PopulationFiltersModel], (populationFiltersModel: PopulationFiltersModel) => {
            populationFiltersModel.addCohortFilter(subjectIds, cohortName);
            cohortFilter = <CohortFilterItemModel> _.find(populationFiltersModel.itemsModels,
                (item) => item.key.indexOf(PopulationFiltersModel.COHORT_EDITOR_KEY) > -1);
        }));

        it('THEN the cohort is renamed in the population filters', inject([PopulationFiltersModel],
            (populationFiltersModel: PopulationFiltersModel) => {
                populationFiltersModel.addCohortFilter(['subj-1'], cohortName);

                // is called
                populationFiltersModel.renameCohortIfApplied(cohortName, newName);

                expect(populationFiltersModel.getCohortEditorFilters()[0].displayName).toBe(newName);
                expect(cohortFilter.displayName).toEqual(newName);
            }));
    });

    describe('WHEN removing cohhort filters', () => {
        const subjectIds = ['subj-1', 'subj-2'];
        const cohort1Name = '1 cohort';
        const cohort2Name = '2 cohort';

        beforeEach(inject([PopulationFiltersModel], (populationFiltersModel: PopulationFiltersModel) => {
            populationFiltersModel.addCohortFilter(subjectIds, cohort1Name);
            populationFiltersModel.addCohortFilter(subjectIds, cohort2Name);
            populationFiltersModel.removeCohortFilter([cohort1Name, cohort2Name], true);
        }));

        it('THEN the cohorts are removed from the population filter', inject([PopulationFiltersModel],
            (populationFiltersModel: PopulationFiltersModel) => {
                const cohortFilters = <CohortFilterItemModel[]> _.filter(populationFiltersModel.itemsModels,
                    (item) => item.key.indexOf(PopulationFiltersModel.COHORT_EDITOR_KEY) > -1);
                expect(cohortFilters.length).toEqual(0);
            }));

        it('THEN the subject ID filter is reset', inject([PopulationFiltersModel],
            (populationFiltersModel: PopulationFiltersModel) => {
                const subjectIdFilter = <ListFilterItemModel> _.find(populationFiltersModel.itemsModels,
                    {key: PopulationFiltersModel.SUBJECT_IDS_KEY});
                expect(subjectIdFilter.selectedValues.length).toBe(0);
            }));
    });

    describe('WHEN setAsPopulation is called', () => {
        let datasetViews;

        beforeEach(inject([DatasetViews], (_datasetViews: DatasetViews) => {
            datasetViews = _datasetViews;
        }));

        describe('AND selection is not empty', () => {
            it('THEN subjects population is set to filters and filters are sent to server',
                inject([FilterEventService, PopulationFiltersModel], (filterEventService: FilterEventService,
                                                                      populationFiltersModel: PopulationFiltersModel) => {
                    spyOn(filterEventService, 'setPopulationFilterSubjectCount');
                    spyOn(populationFiltersModel, 'getFilters');
                    spyOn(datasetViews, 'getSubjectsEcodesByIds').and.returnValue(['subjectEcode']);
                    populationFiltersModel.setAsPopulation(['subjectId']);
                    expect(filterEventService.setPopulationFilterSubjectCount).toHaveBeenCalledWith(1);
                })
            )
            ;
            it('THEN subjects population is set to filters and filters are sent to server',
                inject([FilterEventService, PopulationFiltersModel],
                    (filterEventService: FilterEventService, populationFiltersModel: PopulationFiltersModel) => {
                        spyOn(filterEventService, 'setPopulationFilterSubjectCount');
                        spyOn(populationFiltersModel, 'getFilters');
                        spyOn(datasetViews, 'getSubjectsEcodesByIds').and.returnValue(['subjectEcode']);
                        populationFiltersModel.setAsPopulation(['subjectId']);
                        expect(populationFiltersModel.getFilters).toHaveBeenCalledWith(true);
                    })
            );
        });
    });
});
